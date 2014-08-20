#!/usr/bin/env perl
use strict;
use Carp;
use Pod::Usage;

=head1 NAME

cgiUniprotEntry.pl - cgi scripts to grep a uniprot AC and print a fasta with derivated sequence


=head1 DESCRIPTION

get a uniprot Entry in .dat format and display the derived sequence

=head1 ARGUMENTS

=head3 ac=string

A uniprot accession code (to be retieved from http://www.expasy.org/uniprot/yourac.txt

=head3 outputformat=(dat|fasta|html)

The output format in which the entry is printed

=head1 COPYRIGHT

Copyright (C) 2004-2005  Geneva Bioinformatics www.genebio.com

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


$|=1;		        #  flush immediately;

BEGIN{
  eval{
   require DefEnv;
   DefEnv::read();
  };
}

END{
}

my $isCGI;
use CGI qw(:standard);
if($isCGI){
  use CGI::Carp qw(fatalsToBrowser warningsToBrowser);
  warningsToBrowser(1);
}

BEGIN{
  $isCGI=$ENV{GATEWAY_INTERFACE}=~/CGI/;
  sub carp_error{
    my $msg=shift;
    if ($isCGI){
      my $q=new CGI;
      error($q, $msg);
    }else{
      print STDERR $msg;
    }
  }
  CGI::Carp::set_message(\&carp_error) if $isCGI;

  sub error(){
    my($q, $msg)=@_;
    #  $q->header;
    print $q->start_html(-title=>"$0",
			 -author=>'alexandre.masselot@genebio.com',
			 -BGCOLOR=>'white');
    print "<center><h1>$0</h1></center>\n";
    print  "<pre>$msg</pre>\n";
    $q->end_html;
    exit;
  }
}

use InSilicoSpectro::Databanks::DBEntryUniprot;
use File::Basename;
use CGI qw(:standard);

my $query = new CGI;

if($query->param('doc')){
  print $query->header;
  while(<DATA>){
    print $_;
  }
  exit(0);
}

my $script=basename($0);

my $help=$query->param('help');
if(defined $help){
  print $query->header(-type=>'text/plain');
  pod2usage(-verbose=>2, -exitval=>2, -output=>\*STDOUT);
  exit(0);
}

unless($query->param('ac')){
  my %cookies=$query->cookie($script);
  my $outputformat=$cookies{outputformat};
  my $ac=$cookies{ac};

  print $query->header;
 # print $query->start_html(-title=>"$script",
#			   -author=>'alexandre.masselot@genebio.com',
#			   -script=>[
#				     {
#				      -src      => 'elementControl.js',
#				      -language=> 'JavaScript',
#				     }
#				    ]

#			  );


  print <<EOT;
<head>	
		<title>$script</title>


</head>

<body>
  <center>
    <h1>$script</h1>
    <h3>Uniprot entry viewer (<a href="$script?help=1">?</a>)</h3>
  </center>
  <form name='mainform' method='post' >
  <table border=1 cellspacing=0>
    <tr>
      <td>AC</td>
      <td><input type='text' name='ac'></td>
    </tr>

      <td>Output format</td>
      <td><select name='outputformat'>
EOT
  foreach (qw /fasta html dat/){
    print "         <option value='$_'".(($_ eq $outputformat)?' selected="selected"':'').">".$_."</option>\n";
  }
  print <<EOT;
        </select>
      </td>
    </tr>
  </table>
  <input type='submit'>
  </form>


EOT
  print $query->end_html;
  exit(0);
}

my $query = new CGI;

my $ac=$query->param('ac')||die "must provide an ac";
my $outputFormat=$query->param('outputformat')||die "must provide output format";


my %cookies;
$cookies{ac}=$ac;
$cookies{outputformat}=$outputFormat;
my $cookie=cookie(-name=>$script,
		  -value=>\%cookies,
		  -expires=>'+100d'
		 );
if($outputFormat=~/^(dat|fasta)$/i){
  print $query->header(-type=>'text/plain',
		       -cookie=>$cookie,
		      );
}else{
  print $query->header(
		       -cookie=>$cookie,
		      );
}

use File::Basename;
use LWP::Simple;

my $url="http://www.expasy.org/uniprot/$ac.txt";
my $contents=LWP::Simple::get($url) or die "cannot get url $url: $!";

if($outputFormat=~/^(dat)$/i){
  print $contents;
  exit(0);
}else{
  my $dbu=new InSilicoSpectro::Databanks::DBEntryUniprot;
  $dbu->readDat($contents);
  my @derived=$dbu->generateDerivedForms();

  if($outputFormat=~/^(fasta)$/i){
    $dbu->printFasta;
    foreach (@derived){
      $_->printFasta;
    }
    exit(0);
  }else{
    my $ac=$dbu->AC;
    print <<EOT;
<html>
  <header>
  <title>$ac</title>
  <style>
.sequence{
  font-family: monospace;
}
  </style>
  </header>
  <body>
EOT
    print "
    <center><h1>$ac</h1></center>
    <table border=1 cellspacing=0>
      <tr><td><b>ID</b></td><td>".$dbu->ID."</td></tr>
      <tr><td><b>description</b></td><td>".$dbu->description."</td></tr>
    </table>
";
    $dbu-> printHtml;
    foreach (@derived){
      $_->printHtml;
    }
    print <<EOT;
    <script language="JavaScript" type="text/javascript" src="/js/wz_tooltip.js"></script>
    <hr width="66%"/>visit <a href="http://insilicospectro.vital-it.ch">http://insilicospectro.vital-it.ch</a> or download the soft from <a href="http://search.cpan.org/search?mode=module;query=InSilicoSpectro::Databanks">CPAN</a>
  </body>
</html>
EOT
  }
}


