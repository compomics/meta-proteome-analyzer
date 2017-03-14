package de.mpa.io.parser.pepnovo;

import java.util.List;

/**
 * This class represents the PepnovoFile with all information regarding the PepNovo-Output-File
 * @author Thilo Muth
 *
 */
public class PepnovoFile {

	/**
	 * The name of the PepNovo output file.
	 */
	private String filename;
	
	/**
	 * The list of the output spectra. 
	 */
	private List<PepnovoEntry> entryList;
	
	/**
	 * The number of processed spectra.
	 */
	private int numberSpectra;
	
	/**
	 * Constructor for the pepnovo file.
	 * @param filename
	 */
	public PepnovoFile(String filename) {
        this.filename = filename;        
	}
	
	/**
	 * Returns the name of the PepNovo output file.
	 * @return filename String
	 */
	public String getFilename() {
		return this.filename;
	}
	
	/**
	 * Sets the name of the PepNovo output file.
	 * @param filename String
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the entryList.
	 * @return entryList List<PepnovoEntry>
	 */
	public List<PepnovoEntry> getEntryList() {
		return this.entryList;
	}
	
	/**
	 * Sets the entry list.
	 * @param entryList
	 */
	public void setEntryList(List<PepnovoEntry> entryList) {
		this.entryList = entryList;
	}
	
	/**
	 * Returns the number of processed spectra.
	 * @return numberSpectra
	 */
	public int getNumberSpectra() {
		return this.numberSpectra;
	}
	
	/**
	 * Sets the number of processed spectra.
	 * @param numberSpectra
	 */
	public void setNumberSpectra(int numberSpectra) {
		this.numberSpectra = numberSpectra;
	}
}
