package de.mpa.algorithms;

import java.util.List;

import de.mpa.io.MascotGenericFile;

public class RankedLibrarySpectrum extends LibrarySpectrum {
	
	private double score;
	
	public RankedLibrarySpectrum(MascotGenericFile spectrumFile, double precursorMz, String sequence, List<Protein> annotations, double score) {
		super(spectrumFile, precursorMz, sequence, annotations);
		this.score = score;
	}
	
	public RankedLibrarySpectrum(MascotGenericFile spectrumFile, double precursorMz, String sequence, String accession, String description, double score) {
		super(spectrumFile, precursorMz, sequence, description, accession);
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
