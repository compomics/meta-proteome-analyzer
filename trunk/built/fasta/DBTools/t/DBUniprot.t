#!/usr/bin/env perl
use strict;
use Test::More tests => 1;
use File::Basename;
my $dir=dirname $0;

chdir $dir;

ok("test should be a bit more explicit here");

exit(0);


use InSilicoSpectro::Databanks::DBEntryUniprot;


my $dbu=new InSilicoSpectro::Databanks::DBEntryUniprot;
ok($dbu, "InSilicoSpectro::Databanks::DBEntryUniprot object instanciated");

eval{
    require InSilicoSpectro;
    InSilicoSpectro::init();
};
if($@){
  warn "[WARNING]: $@\n";
}

my $f='/data/databases/uniprot_sprot/src/uniprot_sprot.dat';
open (FD, "<$f") or die "cannot open [$f]: $!";
$/="//\n";

my ($pg);
eval{
  if (-t STDIN && -t STDOUT){
    require Term::ProgressBar;
    my $size=(stat $f)[7];
    $pg=Term::ProgressBar->new ({count=>$size});
  }
};

my $nextpgupdate;
my $readsize;
while (<FD>){
  $readsize+=length $_;
  $nextpgupdate=$pg->update($readsize) if $pg && $readsize>$nextpgupdate;
  my $dbu=InSilicoSpectro::Databanks::DBEntryUniprot->new;
  $dbu->readDat($_);
}
