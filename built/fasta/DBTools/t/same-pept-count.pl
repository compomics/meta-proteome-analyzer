#!/usr/bin/env perl
use strict;
use Carp;
use Pod::Usage;
use File::Basename;

my ($fasta1, $fasta2, $nseq)=@ARGV;


my ($pg, $size, $nextpgupdate, $readsize);
$readsize=0;
$nextpgupdate=0;

my $enz='.*?[KR](?=[^P])|.+$';

my $inFile=$fasta1;
my $inFD;

my %peptdic;
my @palind;
__setInput();
my $n=0;
while((my ($head, $seq)=__nextEntry())[0]){
  while ($seq=~/($enz)/og) {
    $peptdic{$1}++;
    $palind[length $1]++ if $1 eq reverse($1);
  }
  last if $nseq && ++$n>=$nseq;
}

$inFile=$fasta2;
__setInput();
$n=0;
my @matchedPept;
while((my ($head, $seq)=__nextEntry())[0]){
  while ($seq=~/($enz)/og) {
    if(exists $peptdic{$1}){
      $matchedPept[length($1)]++;
      if (length($1)>=6){
	#warn "$1";
	my $tmp=$seq;
	my $pept=$1;
      }
      delete $peptdic{$1}; ## REMOVE this line if you wish to count several times the same repeated peptide
    }
  }
}

foreach (0..$#matchedPept){
  #next unless $matchedPept[$_];
  print "$_\t".($matchedPept[$_] || 0)."\n";
}
# foreach (0..$#palind){
#   next unless $palind[$_];
#   print "$_\t$palind[$_]\n";
# }

sub __nextEntry{
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

sub __setInput{
  eval{
    if (($inFile !~ /gz$/i) && ($inFile ne '-')&& -t STDIN && -t STDOUT){
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
  if ($@){
    warn $@;
    warn "could not use Term::ProgressBar (consider installing the module for more interactive use";
  }
  #set input
  if ($inFile eq '-'){
    $inFD=\*STDIN;
  }elsif($inFile=~/\.gz$/i){
    require PerlIO::gzip;
    open $inFD, "<:gzip", "$inFile" or die $!;
  }else{
    open ($inFD, "<$inFile") or die "cannot open for reading [$inFile]: $!";
  }
}

