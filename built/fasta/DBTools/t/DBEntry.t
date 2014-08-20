#!/usr/bin/env perl
use strict;
use Test::More tests => 3;
use File::Basename;
my $dir=dirname $0;

chdir $dir;

use InSilicoSpectro::Databanks::DBEntry;

my $dbe=InSilicoSpectro::Databanks::DBEntry->new;
ok($dbe, "InSilicoSpectro::Databanks::DBEntry object instanciated");

my $str=<<'EOT';
>P68250_CHAIN0 \ID=1433B_BOVIN \ACOR=P68250 \DE=14-3-3 protein beta/alpha (Protein kinase C inhibitor protein 1) (KCIP-1) \NCBITAXID=9913  \MODRES=(1|ACET_nterm)(2|ACET_nterm)(185|PHOS)   \VARIANT= \LENGTH=245
TMDKSELVQK AKLAEQAERY DDMAAAMKAV TEQGHELSNE ERNLLSVAYK NVVGARRSSW
RVISSIEQKT ERNEKKQQMG KEYREKIEAE LQDICNDVLQ LLDKYLIPNA TQPESKVFYL
KMKGDYFRYL SEVASGDNKQ TTVSNSQQAY QEAFEISKKE MQPTHPIRLG LALNFSVFYY
EILNSPEKAC SLAKTAFDEA IAELDTLNEE SYKDSTLIMQ LLRDNLTLWT SENQGDEGDA
GEGEN
EOT

$dbe->AC('POUET');

$dbe->readFasta($str);

$dbe->printFasta();

is($dbe->AC, "P68250_CHAIN0", "chechking AC");
is($dbe->ID, "1433B_BOVIN", "chechking ID");
