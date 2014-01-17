package de.mpa.algorithms.similarity;

import java.util.ArrayList;
import java.util.List;

import com.compomics.util.protein.Protein;

import de.mpa.io.MascotGenericFile;

/**
 * Data structure for a library spectrum.
 * Holds the MGF spectrum file, the peptide sequence and the protein annotation.
 * 
 * @author Thilo Muth
 * @author Alexander Behne
 *
 */
public class LibrarySpectrum {
	// The MGF spectrum file
	protected MascotGenericFile spectrumFile;
	
	// The spectrum ID
	protected long spectrumID;
	
	// The assigned peptide sequence
	protected String sequence;

	// The protein accession
	protected List<Protein> annotations;

	/**
	 * Copy constructor.
	 * @param libSpec
	 */
	public LibrarySpectrum(LibrarySpectrum libSpec) {
		this(libSpec.getSpectrumFile(),
			 libSpec.getSpectrumID(),
			 libSpec.getSequence(),
			 libSpec.getAnnotations());
	}
	
	/**
	 * Constructor for a library spectrum instance with a single protein annotation.
	 * @param spectrumFile The MGF spectrum file 
	 * @param precursorMz The precursor mass
	 * @param sequence The peptide sequence
	 * @param accession The protein accession
	 * @param description The protein description
	 */
	public LibrarySpectrum(MascotGenericFile spectrumFile, long spectrumID, String sequence, String accession, String description) {
		this.spectrumFile = spectrumFile;
		this.spectrumID = spectrumID;
		this.sequence = sequence;
		this.annotations = new ArrayList<Protein>();
		this.annotations.add(new Protein(accession, description));
	}

	/**
	 * Constructor for a library spectrum instance.
	 * @param spectrumFile The MGF spectrum file 
	 * @param precursorMz The precursor mass
	 * @param sequence The peptide sequence
	 * @param annotations The list of protein annotations
	 */
	public LibrarySpectrum(MascotGenericFile spectrumFile, long spectrumID, String sequence, List<Protein> annotations) {
		this.spectrumFile = spectrumFile;
		this.spectrumID = spectrumID;
		this.sequence = sequence;
		this.annotations = annotations;
	}

	/**
	 * Constructor for a library spectrum instance.
	 * @param spectrumFile The MGF spectrum file 
	 * @param precursorMz The precursor mass
	 * @param sequence The peptide sequence
	 */
	public LibrarySpectrum(MascotGenericFile spectrumFile, long spectrumID, String sequence) {
		this(spectrumFile, spectrumID, sequence, null);
	}

	/**
	 * Returns the MGF spectrum file.
	 */
	public MascotGenericFile getSpectrumFile() {
		return this.spectrumFile;
	}
	
	/**
	 * Returns the peptide sequence.
	 */
	public String getSequence() {
		return this.sequence;
	}
	
	/**
	 * Sets the peptide sequence.
	 * @param sequence String containing the peptide sequence.
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Returns the list of protein annotations.
	 */
	public List<Protein> getAnnotations() {
		return this.annotations;
	}
	
	/**
	 * Sets the list of protein annotations.
	 * @param annotations List of Protein objects
	 */
	public void setAnnotations(List<Protein> annotations) {
		this.annotations = annotations;
	}
	
	/**
	 * Adds a protein annotation to the list of annotations.
	 */
	public void addAnnotation(Protein protein) {
		if (this.annotations == null) {	// no annotations present yet
			this.annotations = new ArrayList<Protein>();
		}
		this.annotations.add(protein);
	}

	/**
	 * @return the spectrumID
	 */
	public long getSpectrumID() {
		return spectrumID;
	}

	/**
	 * @param spectrumID the spectrumID to set
	 */
	public void setSpectrumID(long spectrumID) {
		this.spectrumID = spectrumID;
	}
}
