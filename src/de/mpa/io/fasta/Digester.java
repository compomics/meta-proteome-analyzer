package de.mpa.io.fasta;

import java.util.ArrayList;
import java.util.List;

import com.compomics.util.experiment.biology.Enzyme;

/**
 * Digest protein to peptide based on regex and Comp class.
 * @author R.Heyer and F. Kohrsi
 */
public class Digester {

	/**
	 * Methode to cleave by regular expression "(?<=[RK])(?=[^P])", only for no misscleavages.
	 * @param seq
	 * @return peptideList
	 */
	public List<String> digestRegEx(String seq){
		ArrayList<String> peptideList = new ArrayList<String>();
		String [] peptideArray = seq.split("(?<=[RK])(?=[^P])");
		for (int i = 0; i < peptideArray.length; i++) {
			if (peptideArray[i].length()>4) {
				peptideList.add(peptideArray[i]);
			}
		}
		return peptideList;
	}

	/**
	 * This method allows you to specify all the information for this
	 * enzyme plus the number of missed cleavages that this instance will allow.
	 * Title and restrict can be 'null'.
	 * https://code.google.com/p/compomics-utilities/source/search?q=enzyme&origq=enzyme&btnG=Search+Trunkcleavage.
	 * @param name (String)
	 * @param aminoAcidBefore (String)
	 * @param restrictionBefore (String)
	 * @param aminoAcidAfter (String)
	 * @param restrictionAfter (String)
	 * @param sequence (String)
	 * @param nMissedCleavages (int)
	 * @return peptideList
	 */
	public List<String> digestCompomics(String name, String aminoAcidBefore,
			String restrictionBefore, String aminoAcidAfter,
			String restrictionAfter, String sequence, int nMissedCleavages) {

		// Create Enzyme
		Enzyme enzyme = new Enzyme(0, name, aminoAcidBefore, restrictionBefore, aminoAcidAfter, restrictionAfter);
		// Digest		
		ArrayList<String> peptideList = enzyme.digest(sequence, nMissedCleavages, 5, Integer.MAX_VALUE);
		return peptideList;
	}
}
