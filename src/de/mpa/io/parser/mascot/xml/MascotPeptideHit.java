package de.mpa.io.parser.mascot.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a Mascot peptide hit.
 * 
 * @author R. Heyer, A. Behne
 */
public class MascotPeptideHit {

	/**
	 * The parent protein hit this peptide hit is associated with.
	 */
	private MascotProteinHit parentProteinHit;

	/**
	 * The attributes of this hit.
	 */
	private Map<String, String> attributes;
	
	/**
	 * The precursor mass of this peptide.
	 */
	private double mz;
	
	/**
	 * The precursor charge of this peptide.
	 */
	private int charge;
	
	/**
	 * The amino acid sequence of this peptide.
	 */
	private String sequence;
	
	/**
	 * The modifications of this peptide. Maps amino acid positions to modification instances.
	 */
	private Map<Integer, MascotModification> modifications;
	
	/**
	 * The scan title of the mass spectrum associated with this peptide hit.
	 */
	private String scanTitle;

	/**
	 * Private default constructor initializing various fields.
	 */
	private MascotPeptideHit() {
        mz = 0.0;
        charge = 0;
        sequence = "";
        modifications = new HashMap<Integer, MascotModification>();
        scanTitle = "";
	}
	
	/**
	 * Creates a peptide hit linked to the provided protein hit.
	 * @param parentProteinHit the parent protein hit
	 */
	public MascotPeptideHit(MascotProteinHit parentProteinHit) {
		this();
		this.parentProteinHit = parentProteinHit;
	}
	
	/**
	 * Returns whether this peptide contains any post-translational amino acid modifications.
	 * @return <code>true</code> if modifications are present, <code>false</code> otherwise
	 */
	public boolean hasModifications() {
		return !this.modifications.isEmpty();
	}
	
	/**
	 * Generates an amino acid sequence string containing mass shifts introduced
	 * by post-translational modifications of this peptide
	 * @return the amino acid sequence containing modifications
	 */
	public String getModifiedSequence() {
		String sequence = this.getSequence();
		// get list of indexes
		List<Integer> indexes = new ArrayList<Integer>(this.modifications.keySet());
		// sort indexes in descending order
		Collections.sort(indexes);
		Collections.reverse(indexes);
		// iterate sequence back-to-front and insert +/- delta strings, e.g. M becomes M+16
		for (Integer index : indexes) {
			MascotModification mod = this.modifications.get(index);
			sequence = sequence.substring(0, index+1) + ((mod.getDelta() > 0) ? "+" : "-") +
                    Math.round(mod.getDelta()) + sequence.substring(index+1);
		}
		return sequence;
	}
	
	/**
	 * Returns the parent protein hit.
	 * @return the parent protein hit
	 */
	public MascotProteinHit getParentProteinHit() {
		return this.parentProteinHit;
	}

	/**
	 * Sets the parent protein hit.
	 * @param parentProteinHit the protein hit to set
	 */
	public void setParentProtein(MascotProteinHit parentProteinHit) {
		this.parentProteinHit = parentProteinHit;
	}

	/**
	 * Returns the list of peptide attributes.
	 * @return the list of attributes
	 */
	public Map<String, String> getAttributes() {
		return this.attributes;
	}
	
	/**
	 * Sets the list of peptide attributes.
	 * @param attributes the list of attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Returns the precursor mass.
	 * @return the precursor mass
	 */
	public double getMz() {
		return this.mz;
	}
	
	/**
	 * Sets the precursor mass.
	 * @param mz the precursor mass to set
	 */
	public void setMz(double mz) {
		this.mz = mz;
	}
	
	/**
	 * Returns the precursor charge.
	 * @return the precursor charge
	 */
	public int getCharge() {
		return this.charge;
	}
	
	/**
	 * Sets the precursor charge.
	 * @param charge the precursor charge
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}

	/**
	 * Returns the amino acid sequence.
	 * @return the amino acid sequence
	 */
	public String getSequence() {
		return this.sequence;
	}

	/**
	 * Sets the amino acid sequence.
	 * @param sequence the amino acid sequence
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * Returns the modifications.
	 * @return the modifications
	 */
	public Map<Integer, MascotModification> getModifications() {
		return this.modifications;
	}
	
	/**
	 * Sets the modifications.
	 * @param modifications the modifications to set
	 */
	public void setModifications(Map<Integer, MascotModification> modifications) {
		this.modifications = modifications;
	}
	
	/**
	 * Returns the scan title.
	 * @return the scan title
	 */
	public String getScanTitle() {
		return this.scanTitle;
	}

	/**
	 * Sets the scan title.
	 * @param title the scan title to set
	 */
	public void setScanTitle(String title) {
        scanTitle = title;
	}

	@Override
	public String toString() {
		return this.sequence;
	}
	
}
