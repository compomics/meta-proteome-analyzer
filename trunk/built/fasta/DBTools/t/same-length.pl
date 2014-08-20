#!/usr/bin/env perl
use strict;
use Carp;
use Pod::Usage;
use File::Basename;

my ($fasta1, $fasta)=@ARGV;


my ($pg, $size, $nextpgupdate, $readsize);
$readsize=0;
$nextpgupdate=0;

my $enz='.*?[KR](?=[^P])|.+$';

my $inFile=$fasta1;
my $inFD;


open (FD0, "<$ARGV[0]") or die "cannot open for reading [$ARGV[0]]: $!";
open (FD1, "<$ARGV[1]") or die "cannot open for reading [$ARGV[1]]: $!";


$/="\n>";

my $nseq;
while (my $e0=<FD0>){
  chomp $e0;
  my $e1=<FD1> or die "$ARGV[0] is longer than $ARGV[1]";
  chomp $e1;
  my ($seq0)=(split /\n/,$e0,2)[1];
  $seq0=~s/[^A-Z]+//g;
  my ($seq1)=(split /\n/,$e1,2)[1];
  $seq1=~s/[^A-Z]+//g;

  unless (length ($seq0) == length($seq1)){
    $e0=~/^(\S+)/;
    my $ac0=$1;
    $e1=~/^(\S+)/;
    my $ac1=$1;
    die "entry $nseq: different length ".length ($seq0)."/".length($seq1)."\t$ac0/$ac1 ";
  }
  $nseq++;
}
die "$ARGV[1] is longer than $ARGV[0]" if <FD1>;
warn "ok for $nseq entries";

