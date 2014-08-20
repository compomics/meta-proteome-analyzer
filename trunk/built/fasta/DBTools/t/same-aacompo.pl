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
  $seq0=~s/\s+//g;
  my ($seq1)=(split /\n/,$e1,2)[1];
  $seq1=~s/\s+//g;
  my %aa;
  $aa{$_}++ foreach (split //, $seq0);
  $aa{$_}-- foreach (split //, $seq1);
  foreach ('A'..'Z'){
    unless (($aa{$_}+0)==0){
      $e0=~/^(\S+)/;
      my $ac0=$1;
      $e1=~/^(\S+)/;
      my $ac1=$1;
      die "different aa composition [$_->$aa{$_}] $ac0/$ac1 ";
    }
  }
  $nseq++;
}
die "$ARGV[1] is longer than $ARGV[0]" if <FD1>;

warn "ok for $nseq entries";

