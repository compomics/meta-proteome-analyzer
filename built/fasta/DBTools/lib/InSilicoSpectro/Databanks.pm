package InSilicoSpectro::Databanks;

use warnings;
use strict;

=head1 NAME

InSilicoSpectro::Databanks - parsing protein/nucleotides sequence databanks (fasta, uniprot...)

 

=head1 SYNOPSIS

$/="//\n";
while (<FDIN>){
  my $dbu=InSilicoSpectro::Databanks::DBEntryUniprot->new;
  $dbu->readDat($_);
  my @tmp=$dbu->generateDerivedForms();
  unless ( && @tmp){
    $dbu->printFasta;
    $origSeq = $dbu->sequence;
  }
  foreach (@tmp){
    if ($_->sequence ne $origSeq){
      $_->printFasta;
    }
  }
}

=head1 EXPORT

A list of functions that can be exported.  You can delete this section
if you don't export anything, such as for a purely object-oriented module.

=head1 FUNCTIONS


=head1 AUTHOR

Alexandre Masselot, C<< <alexandre.masselot@genebio.com> >>

=head1 BUGS

Please report any bugs or feature requests to
C<bug-insilicospectro-databanks@rt.cpan.org>, or through the web interface at
L<http://rt.cpan.org/NoAuth/ReportBug.html?Queue=InSilicoSpectro-Databanks>.
I will be notified, and then you'll automatically be notified of progress on
your bug as I make changes.

=head1 ACKNOWLEDGEMENTS

=head1 COPYRIGHT & LICENSE

Copyright 2006 Alexandre Masselot, all rights reserved.

This program is free software; you can redistribute it and/or modify it
under the same terms as Perl itself.

=cut

our $VERSION = '0.0.43';

1; # End of InSilicoSpectro::Databanks
