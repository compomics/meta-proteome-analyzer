package de.mpa.client.ui.sharedelements.chart;

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
	private final List<MascotGenericFile> spectra;
	
	/**
	 * The MGF filename.
	 */
	private final String filename;
	
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
		return this.spectra;
	}
	
	/**
	 * Returns the filename.
	 * @return The filename. 
	 */
	public String getFilename() {
		return this.filename;
	}
}
