use strict;

package InSilicoSpectro::Databanks::DBEntry;
require Exporter;
use Carp;

=head1 NAME

InSilico::Databanks::DBEntry - fasta databank entry + header's annotated info

=head1 SYNOPSIS


=head1 DESCRIPTION

describes an databnk entry (header + sequence)

=head1 FUNCTIONS


=head1 METHODS


=head3 my $dbe=InSilico::Databanks::DBEntry->new([\%h]);

=head2 Accessors/Setters

=head3 $dbe->AC([val])

get/set accession code

=head3 $dbe->ID([val])

get/set accession ID

=head3 $dbe->dbName([val])

[GS]et databank name

=head3 $dbe->taxoID([val])

[GS]et taxonomy if (typically NCBI taxid

=head3 $dbe->description([val])

=head3 $dbe->sequence([val])

Get/Set AC, ID, ...

=head3 $dbe->seqType(["AA"|"DNA"])

Get or set the sequence type

=head3 $dbe->annotatedModRes(string|[[pos1, mod1], [pos2, mod2]]);

get or set annotated PTM from a string (e.g. (1|ACET_nterm)(2|ACET_nterm)(185|PHOS)) or an array. All previously set TPM are removed

=head3 $dbe->add_annotatedModRes(pos, modkey)

add an annotated PTM

=head3 $dbe->clear_annotatedModRes()

remove all annotated modres

=head3 $dbe->variants(string|[[pos1, seq1a, seq1b], [pos2, seq2a, seq2b]]);

get or set variant a string (e.g. ("(9|F|Y)(30|D|N)(41|A|G)(43|Q|R)") or an array. All previously set TPM are removed

=head3 $dbe->add_variant(pos, seqa, seqb)

add a VARIANT, replacing seqa by seqb (often just one aminoacid) at pos

=head3 $dbe->clear_variants()

remove all Variants

=head2 I/O

=head3 $dbe->readFasta($fastacontent);

read info from fasta contents (fitrs line with '>' and info + remaining is sequence.

=head3 $dbe->printFasta();

print the entry under fasta format (use SelectSaver or whatever select method to redirect towards a file descriptor);

=head3 $dbe->printHtml();

Print the entry under a html format.

=head1 EXAMPLES


=head1 SEE ALSO

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

use File::Basename;
{
  use Object::InsideOut;

  my @AC :Field(Accessor => 'AC' );
  my @ID :Field(Accessor => 'ID' );
  my @ACorig :Field(Accessor => 'ACorig' );
  my @dbName :Field(Accessor => 'dbName' );
  my @taxoID :Field(Accessor => 'taxoID' );
  my @__sequence :Field(Accessor => '__sequence',  permission=>'Private');
  my @description :Field(Accessor => 'description' );
  my @ncbiTaxid :Field(Accessor => 'ncbiTaxid' );
  my @annotatedModRes :Field(Accessor => '__annotatedModRes', Type=>'List', permission=>'Private');
  my @variants :Field(Accessor => '__variants', Type=>'List', permission=>'Private');
  my @PI :Field(Accessor => 'PI' );
  my @mass :Field(Accessor => 'mass' );

  my %init_args :InitArgs = (
			     AC=>'AC',
			     COPY=>qr/^copy$/i,
			    );
  sub _init :Init{
    my ($self, $h) = @_;
    $self->__annotatedModRes([]);
    $self->__variants([]);
    if($h->{COPY}){
      my $src=$h->{COPY};
      foreach(qw(AC ID ACorig __sequence description ncbiTaxid __annotatedModRes __variants)){
	$self->$_($src->$_());
      }
    }else{
      foreach (keys %$h) {
	$self->$_($h->{$_});
      }
    }
    return $self;
  };


  sub sequence{
    my ($self, $val)=@_;
    if (defined $val) {
      $val=~s/\s+//g;
      $self->__sequence($val);
      return $val;
    }
    return $self->__sequence;
  }

  sub seqType{
    my ($self, $val)=@_;
    if (defined $val) {
      croak "DBEntry seqtype must be of (AA|DNA)" unless $val=~/^(AA|DNA)$/;
      $self->{seqType}=$val;
      return $val;
    }
    return $self->{seqType};
  }

  sub annotatedModRes{
    my $self=shift;
    my $set=exists $_[0];
    if ($set) {

      if ((ref $_[0]) eq 'ARRAY') {
	my @tmp=@{$_[0]};
	$self->__annotatedModRes(\@tmp);
      } else {
	my @tmp;
	my $s=$_[0];
	while ($s=/\((\d+)\|([^\)]+)\)/g) {
	  push @tmp, [$1, $2];
	}
	$self->__annotatedModRes(\@tmp);
      }
      return;
    }
    return undef unless defined  $self->__annotatedModRes;
    my @tmp=@{$self->__annotatedModRes};
    if (wantarray) {
      return @tmp;
    } else {
      my $ret;
      foreach (@tmp) {
	$ret.="($_->[0]|$_->[1])";
      }
      return $ret;
    }
  }

  sub add_annotatedModRes{
    my ($self, $p, $m)=@_;

    push @{$self->__annotatedModRes}, [$p, $m];
  }


  sub clear_annotatedModRes{
    my $self=shift;
    $self->__annotatedModRes([]);
  }

  sub variants{
    my $self=shift;
    my $set=exists $_[0];
    if ($set) {

      if ((ref $_[0]) eq 'ARRAY') {
	my @tmp=@{$_[0]};
	$self->__variants(\@tmp);
      } else {
	my @tmp;
	my $s=$_[0];
	while ($s=/\((\d+)\|(\w+)\|(\w+)\)/g) {
	  push @tmp, [$1, $2, $3];
	}
	$self->__variants(\@tmp);
      }
      return;
    }
    return undef unless defined  $self->__variants;
    my @tmp=@{$self->__variants};
    if (wantarray) {
      return @tmp;
    } else {
      my $ret;
      foreach (@tmp) {
	$ret.="($_->[0]|$_->[1]|$_->[2])";
      }
      return $ret;
    }
  }

  sub add_variant{
    my ($self, $p, $s1, $s2)=@_;

    push @{$self->__variants}, [$p, $s1, $s2];
  }


  sub clear_variants{
    my $self=shift;
    $self->__variants([]);
  }

  # I/O
  sub readFasta{
    my $self=shift;
    my ($header, $seq)=split /\n/, shift, 2;
    $header=~s/^>//;
    $header=~s/^(\S+)\s*// or croak "fasta header [$header] does not start with an no empty string for AC";
    my $ac=$1;
    $self->AC($ac);
    foreach (split /\\(?=\w+=)/, $header) {
      my ($key, $val)=split /=/, $_, 2;
      $val=~s/\s+$// if $val;
      next unless $key;
      if ($key eq 'AC') {
	$self->AC($val);
      } elsif ($key eq 'ACOR') {
	$self->ACorig($val);
      } elsif ($key eq 'ID') {
	$self->ID($val);
      } elsif ($key eq 'DE') {
	$self->description($val);
      } elsif ($key eq 'NCBITAXID') {
	$self->ncbiTaxid($val);
      } elsif ($key eq 'MODRES') {
	$self->annotatedModRes($val);
      } elsif ($key eq 'VARIANT') {
	$self->variants($val);
      } elsif ($key eq 'LENGTH') {
      } else {
	carp "DBEntry::readFasta no function handler for fasta head [$key]";
      }
    }
    $self->description($header) unless $self->description;
    $seq=~s/\s+//g;
    $self->sequence($seq);
  }

  sub printFasta{
    my $self=shift;
    print ">".$self->AC()." \\ID=".($self->ID|| '')." \\MODRES=".($self->annotatedModRes() ||'');
    print " \\VARIANT=".$self->variants if $self->variants;
    print " \\ACOR=".$self->ACorig if $self->ACorig;
    print " \\NCBITAXID=".$self->ncbiTaxid if $self->ncbiTaxid;
    print " \\DE=".$self->description."\n";
    my $seq=$self->sequence();
    $seq=~s/(.{60})(?=.)/$1\n/g;
    #$seq=~s/(.{10})(?=.)/$1 /g;

    print $seq."\n";
  }

  sub printHtml{
    my $self=shift;
    print "<h3>".$self->AC."</h3>\n";
    print "
    <table border=1 cellspacing=0>
      <tr><td><b>AC</b></td><td>".$self->AC."</td></tr>
      <tr><td><b>ID</b></td><td>".$self->ID."</td></tr>
";
    print "      <tr><td><b>original AC</b></td><td>".$self->ACorig."</td></tr>\n" if $self->ACorig;
    print "      <tr><td><b>description</b></td><td>".$self->description."</td></tr>
    </table>
";
    my @seq=split //, $self->sequence();
    my @annot;
    if ($self->annotatedModRes) {
      my @tmp=$self->annotatedModRes;
      foreach (@tmp) {
	my ($p, $mr)=($_->[0]-1, $_->[1]);
	$annot[$p].="</br>\n" if $annot[$p];
	$annot[$p].="MOD_RES $mr";
      }
    }
    if ($self->variants) {
      my @tmp=$self->variants;
      foreach (@tmp) {
	my ($p, $s, $u)=($_->[0]-1, $_->[1], $_->[2]);
	$annot[$p].="</br>" if $annot[$p];
	$annot[$p].="VARIANT $s -> $u";
      }
    }
    print "<div class='sequence'>\n";
    foreach (0..$#seq) {
      print " " unless $_%10;
      print "</br>\n" unless $_%60;
      if ($annot[$_]) {
	print "<b><a onmouseover=\"return escape('$annot[$_]')\">$seq[$_]</a></b>";
      } else {
	print $seq[$_];
      }
    }
    print "</div>\n";
  }
}

return 1;
