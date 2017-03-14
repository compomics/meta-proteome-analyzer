package de.mpa.algorithms.similarity;

import java.util.List;

import com.compomics.util.protein.Protein;

import de.mpa.io.MascotGenericFile;

public class RankedLibrarySpectrum extends LibrarySpectrum {
	
	private final double score;
	
	public RankedLibrarySpectrum(MascotGenericFile spectrumFile, long spectrumID, String sequence, List<Protein> annotations, double score) {
		super(spectrumFile, spectrumID, sequence, annotations);
		this.score = score;
	}
	
	public RankedLibrarySpectrum(MascotGenericFile spectrumFile, long spectrumID, String sequence, String accession, String description, double score) {
		super(spectrumFile, spectrumID, sequence, description, accession);
		this.score = score;
	}	

	public RankedLibrarySpectrum(LibrarySpectrum libSpec, double score) {
		super(libSpec);
		this.score = score;
	}

	public double getScore() {
		return this.score;
	}

}
