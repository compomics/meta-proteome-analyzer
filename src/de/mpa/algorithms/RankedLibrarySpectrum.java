package de.mpa.algorithms;

import de.mpa.io.MascotGenericFile;

public class RankedLibrarySpectrum extends LibrarySpectrum {
	
	private double score;
	
	public RankedLibrarySpectrum(MascotGenericFile spectrumFile, double precursorMz, String sequence, String annotation, double score) {
		super(spectrumFile, precursorMz, sequence, annotation);
		this.score = score;
	}	

	public RankedLibrarySpectrum(LibrarySpectrum libSpec, double score) {
		super(libSpec);
		this.score = score;
	}

	public double getScore() {
		return score;
	}

}
