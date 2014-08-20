#!/usr/bin/env perl
use strict;
use Carp;
use Pod::Usage;

=head1 NAME

uniprotdat2fasta.pl - converts uniprot native text format (.dat or .seq) into fasta file, reporting varsplic, signal, peptide, PTM, conflicts....

=head1 DESCRIPTION

Reads a uniprot .dat (sprot, trembl...) and converts it into a fasta file, expanding VAR_SPLIC, CHAIN, PEPT annotations.

FT annotation, such as MOD_RES, VARIANT... are kept in the fasta header coherently with the sequence modification (recomputed location).

=head1 SYNOPSIS

uniprotdata2fasta.pl --in=uniprot_sprot.dat --out=uniprot_sprot.fasta

=head1 ARGUMENTS


=head3 --in=file

A .dat file [default is stdin]

=head3 -out=file

A .fasta file [default is stdout]


=head1 OPTIONS

=head3 --noderived

Does not produce the derived sequences, i.e. the chains, peptides and isoforms (VAR_SEQ, CHAIN...). Equivalent to call with -skipchains -skippeptides -skipisoforms.

=head3 --skipchains

Does not produce the chains.

=head3 --skippeptides

Does not produce the peptides.

=head3 --skipisoforms

Does not produce the isoforms.

=head3 --skiporigsequence

By default the original sequence is always added to the fasta file and then new sequences (chains, etc.) are added provided they are different. To avoid having the original sequence and to have annotated features only, use this flag.

=head3 --shortname

The additional sequences have ACs that are obtained by appending a text to the original AC. For Mascot it might be necessary to configure the system to accept longer ACs than it does by default. To avoid this you can use this flag and peptides will be named AC_Pi, chains AC_Ci, and isoforms AC_Ii, where i is an integer starting at 0 for each type of variant.

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

Alexandre Masselot, www.genebio.com

=cut



use InSilicoSpectro::Databanks::DBEntryUniprot;
use SelectSaver;
use File::Basename;

use Getopt::Long;
my($datFile, $fastaFile, $noDerivedForm, $help, $man, $verbose, $skipChains, $skipIsoforms, $skipPeptides, $skipOrigSeq, $shortName);
$datFile='-';
$fastaFile='-';

if (!GetOptions(
		"in=s"=>\$datFile,
		"out=s" => \$fastaFile,

		"noderived"=>\$noDerivedForm,
		'skippeptides' => \$skipPeptides,
		'skipchains' => \$skipChains,
		'skipisoforms' => \$skipIsoforms,
		'skiporigseq' => \$skipOrigSeq,
		'shortname' => \$shortName,

                "help" => \$help,
                "man" => \$man,
                "verbose" => \$verbose,
               )
    || $help || $man){


  pod2usage(-verbose=>2, -exitval=>2) if(defined $man);
  pod2usage(-verbose=>1, -exitval=>2);
}


my ($pg, $size, $nextpgupdate, $readsize);
$readsize=0;
$nextpgupdate=0;

eval{
  if (($datFile ne '-')&& -t STDIN && -t STDOUT){
    require Term::ProgressBar;
    $size=(stat $datFile)[7];
    $pg=Term::ProgressBar->new ({name=> "parsing ".basename($datFile), count=>$size});
  }
};
if ($@){
  warn "could not use Term::ProgressBar (consider installing the module for more interactive use";
}


open (FDIN, "<$datFile") or die "could not open for reading [$datFile]:$!";

my $saver;
if ($fastaFile ne '-'){
  open (FDOUT, ">$fastaFile") or die "could not open for writing [$fastaFile]:$!";
  $saver=new SelectSaver(\*FDOUT);
}


$/="\n//\n";
while (<FDIN>){
  $readsize+=length $_;
  $nextpgupdate=$pg->update($readsize) if $pg && $readsize>$nextpgupdate;
  next unless /\S/;
  my $dbu=InSilicoSpectro::Databanks::DBEntryUniprot->new;
  $dbu->readDat($_);
#  if ($dbu->AC){
    unless($noDerivedForm){
      my %params;
      $params{skipChains} = 1 if (defined($skipChains));
      $params{skipPeptides} = 1 if (defined($skipPeptides));
      $params{skipIsoforms} = 1 if (defined($skipIsoforms));
      $params{shortName} = 1 if (defined($shortName));
      my @tmp=$dbu->generateDerivedForms(%params);
      my $origSeq;
      unless (defined($skipOrigSeq) && @tmp){
	$dbu->printFasta;
	$origSeq = $dbu->sequence;
      }
      foreach (@tmp){
	if ($_->sequence ne $origSeq){
	  $_->printFasta;
	}
      }
    }else{
      $dbu->printFasta;
    }
#  }
}
$pg->update($size) if $pg;
exit(0);
