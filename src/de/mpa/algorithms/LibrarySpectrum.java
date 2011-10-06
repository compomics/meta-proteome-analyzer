package de.mpa.algorithms;

import de.mpa.io.MascotGenericFile;

/**
 * Data structure for a library spectrum.
 * Holds the MGF spectrum file, the precursor mass, the peptide sequence and the protein annotation.
 * 
 * @author Thilo Muth
 *
 */
public class LibrarySpectrum {
	// The MGF spectrum file
	private MascotGenericFile spectrumFile;
	
	// The precursor mass 
	private double precursorMz;
	
	// The assigned peptide sequence
	private String sequence;
	
	// The protein annotation
	private String annotation;
	
	/**
	 * Constructor for a library spectrum instance.
	 * @param spectrumFile The MGF spectrum file 
	 * @param precursorMz The precursor mass
	 * @param sequence The peptide sequence
	 * @param annotation The protein annotation
	 */
	public LibrarySpectrum(MascotGenericFile spectrumFile, double precursorMz, String sequence, String annotation) {
		this.spectrumFile = spectrumFile;
		this.precursorMz = precursorMz;
		this.sequence = sequence;
		this.annotation = annotation;
	}
	
	/**
	 * Returns the MGF spectrum file.
	 * @return
	 */
	public MascotGenericFile getSpectrumFile() {
		return spectrumFile;
	}
	
	/**
	 * Returns the precursor mass.
	 * @return
	 */
	public double getPrecursorMz() {
		return precursorMz;
	}
	
	/**
	 * Returns the peptide sequence.
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}
	
	/**
	 * Returns the protein annotation.
	 * @return
	 */
	public String getAnnotation() {
		return annotation;
	}
}
