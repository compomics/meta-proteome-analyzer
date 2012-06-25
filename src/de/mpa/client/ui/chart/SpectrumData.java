package de.mpa.client.ui.chart;

import java.util.List;

import de.mpa.io.MascotGenericFile;

/**
 * Data object used for the TotalIonHistogram.
 * @author T.Muth
 *
 */
public class SpectrumData {
	/**
	 * List of spectra.
	 */
	private List<MascotGenericFile> spectra;
	
	/**
	 * The MGF filename.
	 */
	private String filename;
	
	/**
	 * SpectrumData constructor. 
	 * @param spectra The list of spectra.
	 */
	public SpectrumData(List<MascotGenericFile> spectra, String filename) {
		this.spectra = spectra;
		this.filename = filename;
	}
	
	/**
	 * Returns the spectra.
	 * @return The list of spectra. 
	 */
	public List<MascotGenericFile> getSpectra() {
		return spectra;
	}
	
	/**
	 * Returns the filename.
	 * @return The filename. 
	 */
	public String getFilename() {
		return filename;
	}
}
