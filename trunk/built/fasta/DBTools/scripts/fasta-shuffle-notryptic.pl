#!/usr/bin/env perl
use strict;
use Carp;
use Pod::Usage;

=head1 NAME

  fasta-shuffle-notryptic.pl - shuffle each sequence, without any original tryptic peptide

=head1 DESCRIPTION

Reads input fasta file and produce a shuffle databank & avoid known cleaved peptides: shuffle sequence but avoid producing known tryptic peptides


=head1 SYNOPSIS

#shuffle each sequence
fasta-shuffle-notryptic.pl --in=/tmp/uniprot_sprot.fasta


#to limit memory usage, one can use CRC code (--crcsize will 
./fasta-shuffle-notryptic.pl --ac-prefix=DECOY_ --in=/home/alex/tmp/a.fasta  --out=/tmp/a.fasta  --crcsize=33 -v  --norandom 

=head1 ARGUMENTS


=head3 --in=infile.fasta

An input fasta file (will be uncompressed if ending with gz)

=head3 -out=outfile.fasta

A .fasta file [default is stdout]

=head1 OPTIONS

=head2 --ac-prefix=string

Set a key to be prepended before the AC in the randomized bank. By default, it will be dependent on the choosen method.


=head3 --peptminlength [default 6]

Set the size of the peptide to be reshuffled if they already exist

=head3 --crcsize=int

Building a hash of known cleaved peptide can be quite demanding for memory (uniprot_trembl => ~4GB). Therefore solution is to make an  array containing statements if or not a peptide with corresponding crc code was found.

The argument passed here is the number of bits use for the CRC coding: 33 means 2^33 bit of memory => 2^30 bytes => 1GB

=head3 --norandomseed

Random generator seed is set to 0, so 2 run on same data will produce the same result

=head2 misc

=head3 --noprogressbar

do not display terminal progress bar (if possible)

=head3 --help

=head3 --man

=head3 --verbose

Setting an environment variable DO_NOT_DELETE_TEMP=1 will keep the temporay file after the script exit

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

use Inline 'C';
use String::CRC;
use Bit::Vector;
use SelectSaver;
use File::Basename;
use List::Util qw/shuffle/;
use Log::StdLog;


use Getopt::Long;
my ($acPrefix, $noProgressBar);

my $shuffle_reshuffleCleavPept_minLength=6;
my $shuffle_reshuffleCleavPept_CRCLen;


my $inFile='-';
my $outFile='-';
my $inFD;

my $outLength;
my $noSeed;

my($help, $man, $verbose);


if (!GetOptions(
		"in=s"=>\$inFile,
		"out=s" => \$outFile,

		"ac-prefix=s" =>\$acPrefix,

		"peptminlength=i"=> \$shuffle_reshuffleCleavPept_minLength,
		"crcsize=i"=> \$shuffle_reshuffleCleavPept_CRCLen,

		"outlength=i"=>\$outLength,
		"norandomseed"=>\$noSeed,

		"noprogressbar" => \$noProgressBar,

                "help" => \$help,
                "man" => \$man,
                "verbose" => \$verbose,
               )
    || $help || $man) {


  pod2usage(-verbose=>2, -exitval=>2) if(defined $man);
  pod2usage(-verbose=>1, -exitval=>2);
}

Log::StdLog->import({level=>$verbose?'info':'warn', handle => \*STDERR});

srand(1)  if $noSeed;

my $trypsinQR=qr/(.*?[KR](?=[^P])|.+$)/;

my $nbentries=0;
my $nbpept;

my ($nbVCRC, $modVCRC, $maxBitCRC);
my @vcrc;
my %peptsDico;

if ($shuffle_reshuffleCleavPept_CRCLen>=32) {
  $nbVCRC=1<<($shuffle_reshuffleCleavPept_CRCLen-32);
  $maxBitCRC=2*(1<<31); #WARN 1<<32 => 1!!!
} else {
  $nbVCRC=1;
  $maxBitCRC=1<<$shuffle_reshuffleCleavPept_CRCLen;
}
print {*STDLOG} info=> "nb CRC vector=$nbVCRC\nmax bit 4 crc=$maxBitCRC\n";

#init parsing progress bar
my ($pg, $size, $nextpgupdate, $readsize);
$readsize=0;
$nextpgupdate=0;
my $imaxreshuffle=1000;
my $imaxreshuffleFinal=10000;

__setInput();

#set output on default
my $saver;
if ($outFile ne '-') {
  if ($outFile=~/\.gz$/i) {
    open FDOUT, ">:gzip", $outFile or die "cannot open for writing gziped [$outFile]: $!";
    $saver=new SelectSaver(\*FDOUT);
  } else {
    open (FDOUT, ">$outFile") or die "could not open for writing [$outFile]:$!";
    $saver=new SelectSaver(\*FDOUT);
  }
}

$acPrefix ||='SHFLPLUS_';
@vcrc = $shuffle_reshuffleCleavPept_CRCLen && Bit::Vector->new($maxBitCRC, $nbVCRC||1);
$nbpept=0;

my ($cptbit, $size)=(0,0);
if($shuffle_reshuffleCleavPept_CRCLen){
  foreach my $v (@vcrc) {
    $v->Empty;
    $cptbit+=$v->Norm();
  }
  $size=$vcrc[0]->Size();
  warn "cptbit=$cptbit";
  warn "size=".($maxBitCRC*$nbVCRC);
}

computeCRC();
print {*STDLOG} info=> "nbentries=$nbentries\tnbpept=$nbpept\n";

if ($shuffle_reshuffleCleavPept_CRCLen) {
  my $cptbit=0;
  my $size=0;
  foreach my $v (@vcrc) {
    $cptbit+=$v->Norm();
  }
  print {*STDLOG} info=>  "duplication rate in the CRC index=".sprintf("%2.2f", (1.0*$nbpept)/$cptbit)." ($nbpept/$cptbit) - this include 'natural' duplication when a cleaved peptide is seen more than once in the databank\n";
  print {*STDLOG} info => "crc vector fill rate=".sprintf("%3.3f", (1.0*$cptbit)/$nbVCRC/$maxBitCRC)." ($cptbit/($nbVCRC*$maxBitCRC))\n";
}else{
  print {*STDLOG} info => "nb <> pept seq=".scalar(keys %peptsDico)."\n";
}
__setInput();
my @histoReshuffled;
my $nreshuffled=0;
my $nFinalReshuffle=0;
my $donotReadNext;
my ($head, $seq);
my $seqbak;
my $nreshuffperseq;
#donotReadNext is useed to resshuffle the whole sequence without reading the next one
my $iseq=0;
while ($donotReadNext || (($head, $seq)=__nextEntry())[0]) {
  #print  "(".__LINE__.") [$head]\n[$seq]\n";
#   if($head=~/^>A0BIX5\|A0BIX5_PARTE\b/){
#     Log::StdLog->import({level=>'debug', handle => \*STDERR});
#     my $dummy=<STDIN>;
#   }else{
#     Log::StdLog->import({level=>$verbose?'info':'warn', handle => \*STDERR});
#   }
  print {*STDLOG} debug=>$head;
  print {*STDLOG} debug=>$seq;

  if ($donotReadNext) {
    print {*STDLOG} debug=>"donotReadNext: seq bak $seqbak";

    $seq=$seqbak;
    undef $donotReadNext;
  } else {
    $nreshuffperseq=0 ;
    $seqbak=$seq;
    $iseq++;
    last if $outLength && $iseq>$outLength;
  }
   #die"TEMP END"  if $iseq>100;
   print {*STDLOG} debug => __LINE__.": ($nreshuffperseq>$imaxreshuffleFinal)";
   if ($nreshuffperseq>$imaxreshuffleFinal) {

    $head=~/>(\S+)/;
    if ($pg) {
      $pg->message("reshuffling the whole sequence without control [$1]");
    } else {
      warn "reshuffling the whole sequence without control [$1]\n";
    }
    $head=~s/>/>$acPrefix/ or die "entry header does not start with '>': $head";
    my $newseq=join ('', shuffle (split //, $seq));
    print "$head\n".__prettySeq($newseq)."\n";

    $histoReshuffled[$nreshuffperseq]++;
    $nFinalReshuffle++;
    undef $donotReadNext;
    next;
  }
  $seq=join ('', shuffle (split //, $seq));
  my $newseq="";
  while ($seq && $seq=~ /$trypsinQR/) {
#    warn "$seq";
    print {*STDLOG} debug =>  __LINE__.": $seq";
    if ($nreshuffperseq && (($nreshuffperseq % $imaxreshuffle) == 0)) {
      $head=~/>(\S+)/;
      print {*STDLOG} debug =>  __LINE__.": reshuffling the whole sequence [$1] ($nreshuffperseq/$imaxreshuffleFinal)";
      if ($pg) {
	$pg->message("reshuffling the whole sequence [$1] ($nreshuffperseq/$imaxreshuffleFinal)");
      } else {
	warn "reshuffling the whole sequence [$1] ($nreshuffperseq/$imaxreshuffleFinal)\n";
      }
      $nreshuffperseq++;
      $donotReadNext=1;
      last;
    }
    my $pept=$1;
    print {*STDLOG} debug=> "$pept";

    my $reshuffle;

    if (length($pept)<$shuffle_reshuffleCleavPept_minLength) {
      $newseq.=$pept;
      $seq=substr($seq, length($pept));
      next;
    }
    if ($shuffle_reshuffleCleavPept_CRCLen) {
      my ($i, $c)=crc($pept, $shuffle_reshuffleCleavPept_CRCLen);
      if($nbVCRC>1){
	#$c%=$maxBitCRC;
      }else{
	$c=$i;
	$i=0;
      }
      $reshuffle =  $vcrc[$i]->bit_test($c);
    } else {
      $reshuffle = exists $peptsDico{$pept};
    }
    if ($reshuffle) {
      print {*STDLOG} debug=>"($nreshuffperseq<$imaxreshuffle/2) reshuf\t$pept";
      while($nreshuffperseq<$imaxreshuffle/2) {
	print {*STDLOG} debug=> "looping\t$pept";
	#warn "($nreshuffperseq<$imaxreshuffle/2)";
	$nreshuffled++;
	$nreshuffperseq++;
	my $lpept=length($pept);
	my $lseq=length($seq);
	last if $lpept== $lseq;
	my ($l1, $l2, $lmax);
	#well, the 2 numbers are not independent. et alors?
	$l2=int(rand($lseq-2-$lpept));
	#warn "lpept=$lpept lseq=$lseq l2=$l2";
	$l1=$l2%($lpept-3)+1;
	$l2+=$lpept;
	#warn "l1=$l1 l2=$l2";
	c_swap($seq, $l1, $l2);

	my $trialpept=substr($seq, 0, length($pept));
	print {*STDLOG} debug=>"trial\t$trialpept";
	if ($shuffle_reshuffleCleavPept_CRCLen) {
	  my ($i, $c)=crc($trialpept, $shuffle_reshuffleCleavPept_CRCLen);
	  $i%=$nbVCRC;
	  $c%=$maxBitCRC;
	  if(!($vcrc[$i]->bit_test($c))){
	    #warn "undef reshuffle";
	    undef $reshuffle;
	    $pept=$trialpept;
	    last;
	  }
	}else{
	  unless (exists $peptsDico{$trialpept}){
	    undef $reshuffle;
	    $pept=$trialpept;
	    last;
	  }
	}
      }
      if($reshuffle){
	$nreshuffled++;
	$nreshuffperseq++;

	if ($seq=~/(.{50})(.+)/) {
	  my ($s1, $s2)=($1, $2);
	  $seq=join ('', shuffle (split //, $s1)).$s2;
	} else {
	  $seq=join ('', shuffle (split //, $seq));
	}
	print {*STDLOG} debug=>"($nreshuffperseq<$imaxreshuffle/2) reshuffling whole seq $seq";
	next;
      }
    }
    unless ($reshuffle){
#      warn "append newseq";
      $newseq.=$pept;
      $seq=substr($seq, length($pept));
      $histoReshuffled[$nreshuffperseq]++;
      $nreshuffperseq=0;
    }
  }
  unless($donotReadNext){
    $head=~s/>/>$acPrefix/ or die "entry header does not start with '>': $head";
    print "$head\n".__prettySeq($newseq)."\n";
    $histoReshuffled[$nreshuffperseq]++;
    #print "(".__LINE__.") [$head]\n[$newseq]\n";
  }
}
if ($verbose) {
  print {*STDLOG} info=>  "reshuffled pept/nb sequences: $nreshuffled/$nbentries\n";
  print {*STDLOG} info=>  "nb final (no-check)  seq reshuffling: $nFinalReshuffle\n";
  print {*STDLOG} info=>  "#seq\t#nb reshuffled peptides\n";
  foreach (0..$#histoReshuffled) {
    next unless $histoReshuffled[$_];
    print {*STDLOG} info=>  "$_\t$histoReshuffled[$_]\n";
  }
}


sub __nextEntry{
  lock ($inFD);
  local $/="\n>";
  my $contents=<$inFD>;
  return undef unless $contents;

  $readsize+=length $contents;
  $nextpgupdate=$pg->update($readsize) if $pg && $readsize>=$nextpgupdate;

  chomp $contents;
  $contents=">$contents" unless $contents=~/^>/;
  my ($head, $seq)=split /\n/,$contents,2;
  $seq=~s/\s+//g;
  return ($head, $seq);
}

sub __prettySeq{
  $_=$_[0];
  s/(\S{60})(?=\S)/$1\n/g;
  return $_;
}

sub __setInput{
  eval{
    if ((!$noProgressBar) && ($inFile !~ /gz$/i) && ($inFile ne '-')&& -t STDIN && -t STDOUT) {
      require Term::ProgressBar;
      $size=(stat $inFile)[7];
      $pg=Term::ProgressBar->new ({name=> "parsing ".basename($inFile),
				   count=>$size,
				   ETA=>'linear',
				   remove=>1
				  });
    }
    $nextpgupdate=0;
    $readsize=0;
  };
  if ($@) {
    warn "could not use Term::ProgressBar (consider installing the module for more interactive use";
  }
  #set input
  if ($inFile eq '-') {
    $inFD=\*STDIN;
  } elsif ($inFile=~/\.gz$/i) {
    require PerlIO::gzip;
    open $inFD, "<:gzip", "$inFile" or die $!;
  } else {
    open ($inFD, "<$inFile") or die "cannot open for reading [$inFile]: $!";
  }
}

sub computeCRC{
  while ((my ($head, $seq)=__nextEntry())[0]) {
    {
      $nbentries++;
    }
    my @tmpcrcs;
    while ($seq=~/$trypsinQR/g) {
      $_=$1;
      my $l=length($_);
      if ($l>=$shuffle_reshuffleCleavPept_minLength) {
	if ($shuffle_reshuffleCleavPept_CRCLen) {
	  my ($i, $c)=crc($_, $shuffle_reshuffleCleavPept_CRCLen);
	  if($nbVCRC>1){
	    #$c%=$maxBitCRC;
	  }else{
	    $c=$i;
	    $i=0;
	  }
	  $vcrc[$i]->Bit_On($c);
	} else {
	  $peptsDico{$_}=undef;
	}
	$nbpept++;
      }
    }
  }
}

__END__ 
time ./fasta-decoy.pl --ac-prefix=DECOY_ --in=/home/alex/tmp/a.fasta --method=shuffle --shuffle-reshufflecleavedpeptides-crc=33 --shuffle-reshufflecleavedpeptides --out=/tmp/a.fasta --outlength=10000

  just la partie cksum => 50sec
  la partie cksum sur un thread avec les call fonction => 56 (ca va, c'est pas la mort)
nbentries=447332        nbpept=9526340


En dessous, on compte just la partie decoying
originale => 7.30
substr_=> 1.30

je ne comprends pourquoi, mais j'en suis à 3'35...

c_swap take care of KR/P =>2'24

loop taking care of check CRC & reshuffling

__C__
void c_swap(char* str, int i1, int i2){
  if (((str[i2]=='K') || (str[i2]=='R')) && (str[i1+1]!='P'))
    return;

  if (i1>0 && ((str[i1-1]=='K') || (str[i1-1]=='R')) && (str[i1]=='P'))
    return;
  if(str[i1]<'A' || str[i1]>'Z'){
    printf("PROBLEM i1 %d/%d: %s\n", i1, i2, str);
  }
  if(str[i2]<'A' || str[i2]>'Z'){
    printf("PROBLEM i2 %d/%d: %s\n", i1, i2, str);
  }
  char t=str[i2];
  str[i2]=str[i1];
  str[i1]=t;
}


