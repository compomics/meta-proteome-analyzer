#!/usr/bin/env perl
use strict;
use Pod::Usage;
use Getopt::Long;
use File::Basename;
use File::Temp qw(tempfile);


=head1 NAME

ncbinr2phenyxfasta.pl - tranfoorm ncbirn bank into fasta with annotated headers

=head1 DESCRIPTION

=head1 SYNOPSIS

ncbinr2phenyxfasta.pl --in=EST_others.fasta --out=/tmp/est_others.fasta --defaulttaxid=1 --taxo=/tmp/gi_taxid_nucl.dmp --sorted --norewind

=head1 ARGUMENTS


=head3 --in=infile.fasta

An input fasta file

=head3 --taxo=file

=head3 --out=outfile.fasta

A .fasta file [default is stdout]

=head1 OPTIONS

=head3 --sorted

The entries both in fasta and taxo file are sorted (there can be (few) miss sorting in the taxonomy file)

=head3 --defaulttaxid=int

A taxonomy to batttribute to entries where there is nor registrated taxonomy

=head3 --norewind

Disallow rewind of taxonomy file when a taxid is missing

=head3 --help

=head3 --man

=head3 --verbose

Setting an environment vartiable DO_NOT_DELETE_TEMP=1 will keep the temporay file after the script exit

=head1 EXAMPLE


=head1 COPYRIGHT

Copyright (C) 2004-2006  Geneva Bioinformatics www.genebio.com

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

=head1 AUTHORS

Djibril Ousmanou, 

Alexandre Masselot, www.genebio.com

=cut


my $nrFile;
my $outFile='-';
my ($nrFile, $taxodmpFile, $defaultTaxid, $sortedData, $isNoRewind, $noProgressBar, $help, $man, $verbose);


############### read command line options
if (!GetOptions(
		"in=s"=>\$nrFile,
		"taxo=s"=>\$taxodmpFile,
		"defaulttaxid=i"=>\$defaultTaxid,
		"out=s"=>\$outFile,
		"sorted"=>\$sortedData,
		"norewind"=>\$isNoRewind,
		
		"noprogressbar"=>\$noProgressBar,
		
		"help"=>\$help,
                "man" => \$man,
		"verbose"=>\$verbose,
	       )
    || $help || $man
   ){
  pod2usage(-verbose=>2, -exitval=>2) if(defined $man);
  pod2usage(-verbose=>1, -exitval=>2);
}

die "no nr fasta (--in=file)" unless $nrFile;
die "notaxo.dmp (--taxo=file)" unless $taxodmpFile;

my ($nextpgupdate, $readsize, $pg);


unless($sortedData){

  setupPG($nrFile);

  my %ac2taxid;
  open (FD,"<$nrFile") or die "cannot open for reading [$nrFile]:$!";
  while (<FD>) {
    $readsize+=length $_;
    $nextpgupdate=$pg->update($readsize) if $pg && $readsize>=$nextpgupdate;
    next unless /^>gi\|(\w+)/;
    my $gi=$1;
    $gi=~s/_.*//;
    $ac2taxid{$gi}=undef;
  }
  warn "nb of fasta entries=".scalar (keys %ac2taxid)."\n";
  close FD;

  setupPG($taxodmpFile);

  open (FD,"<$taxodmpFile") or die "cannot open for reading [$taxodmpFile]:$!";
  while (<FD>) {
    $readsize+=length $_;
    $nextpgupdate=$pg->update($readsize) if $pg && $readsize>=$nextpgupdate;
    chomp;
    my ($gi, $taxo)=split;
    next unless exists $ac2taxid{$gi};
    $ac2taxid{$gi}=$taxo;
  }
  close FD;

  my $nbnotaxid=0;
  foreach (keys %ac2taxid) {
    unless ($ac2taxid{$_}) {
      $nbnotaxid++;
      $ac2taxid{$_}=1;
    }
  }

  warn "no taxid found for $nbnotaxid/".scalar (keys %ac2taxid)." entries\n";
  warn "producing output file [$outFile]\n";
  setupPG($nrFile);
  open (FD, "<$nrFile") or die "cannot open for reading [$nrFile]:$!";
  open (OUT, ">$outFile") or die "cannot open for writing: [$outFile]: $!";
  local $/="\n>";
  while (<FD>) {
    $readsize+=length $_;
    $nextpgupdate=$pg->update($readsize) if $pg && $readsize>=$nextpgupdate;
    chomp;
    my ($head, $seq)=split /\n/, $_, 2;
    $head=~s/^>//;

    my $gi;


    if ($head=~s/^((?:\w+)\|(?:(\d+)\w*))\|(\S+)\|\s*(.*)/>$1 \\ID=$3 \\NCBITAXID=$ac2taxid{$2} \\DE=$4/) {
      $gi=$2;
      $gi=~s/_.*//;
      s/\cA/;/g;
    }

    $seq=~s/[^A-Z]//ig;
    next unless $seq=~/\S/;

    print OUT "$head\n$seq\n";
    #$ac2taxid{$gi}=undef if defined $gi;
  }
  close FD;
  close OUT;
} else {
  if($isNoRewind){
    my ($FDTMP, $ftmp)=tempfile(UNLINK=>1);
    my $cmd="sort -n $taxodmpFile";
    warn "sorting taxonomy: $cmd >$ftmp\n";
    unless (`sort --version` && open (FDSORTED, "$cmd|")){
      die "ask --rewind but cannot find 'sort command or execute '$cmd'";
    }
    while(<FDSORTED>){
      print $FDTMP $_;
    }
    close $FDTMP;
    close FDSORTED;
    $taxodmpFile=$ftmp;
  }


  setupPG($nrFile);
  open (FD, "<$nrFile") or die "cannot open for reading [$nrFile]:$!";
  open (FDTAXO,"<$taxodmpFile") or die "cannot open for reading [$taxodmpFile]:$!";

  open (OUT, ">$outFile") or die "cannot open for writing: [$outFile]: $!";
  local $/="\n>";
  my $hasRewindTaxo;
  my ($nbentries, $nbmisstaxo)=(0,0);
  while (<FD>) {
    $nbentries++;
    $hasRewindTaxo=$isNoRewind;
    $readsize+=length $_;
    $nextpgupdate=$pg->update($readsize) if $pg && $readsize>=$nextpgupdate;

    s/^>//;
    my ($line, $seq)=split /\n/, $_, 2;

    my $gi;
    if ($line=~s/^((?:\w+)\|(\w+))\|(\S+)\|\s*(.*)/>$1 \\ID=$3 \\NCBITAXID=__TAXID__ \\DE=$4/) {
      $gi=$2;
      $gi=~s/_.*//;
      $line=~s/\cA/;/g;
      #locate gi
      my $taxid;
      local $/="\n";
      while(<FDTAXO>){
	my ($gitmp, $taxo)=split;
	if(($gitmp+0)>($gi+0)){
	  last if($hasRewindTaxo);
	  $hasRewindTaxo=1;
	  unless ($isNoRewind){
	    warn "rewind at line $line\n" ;
	    close FDTAXO;
	    open (FDTAXO,"<$taxodmpFile") or die "cannot open for reading [$taxodmpFile]:$!";
	  }
	  next;
	}
	if($gitmp eq $gi){
	  $taxid=$taxo;
	  last;
	}
      }
      unless(defined $taxid){
	if(defined $defaultTaxid){
	  $nbmisstaxo++;
	  $taxid=$defaultTaxid;
	}else{
	  die "no taxid for gi=[$gi] in $taxodmpFile (force using --defaulttaxid=1)";
	}
      }
      $line=~s/NCBITAXID=__TAXID__/NCBITAXID=$taxid/;
    }
    $seq=~s/[^A-Z]//ig;
    next unless $seq=~/\S/;

    print OUT "$line\n$seq\n";
  }
  warn "nb miss taxo:$nbmisstaxo/$nbentries\n";
}
warn "completed\n";

sub setupPG{
  my $file=shift or die "no arg to setupPG";
  die "$file does not exist" unless -f $file;
  if ((!$noProgressBar)  && ($file ne '-')&& -t STDIN && -t STDOUT){
    require Term::ProgressBar;
    my $size=(stat $file)[7];
    $pg=Term::ProgressBar->new ({name=> "parsing ".basename($file),
				 count=>$size,
				 ETA=>'linear',
				 remove=>1
				});
    $nextpgupdate=0;
    $readsize=0;
  }
}
