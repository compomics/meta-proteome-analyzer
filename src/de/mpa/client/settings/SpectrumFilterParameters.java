package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

/**
 * Parameter settings for spectrum filtering in File Panel.
 * 
 * @author T.Muth
 */
public class SpectrumFilterParameters extends ParameterMap {
	
	@Override
	public void initDefaults() {
		this.put("minpeaks", new Parameter("Min. Significant Peaks", new Integer[] { 10, 1, Integer.MAX_VALUE, 1 }, "Spectrum Filtering", "The minimum number of significant peaks in the spectrum."));
		this.put("mintic", new Parameter("Min. Total Ion Current", new Integer[] { 10000, 1, Integer.MAX_VALUE, 1}, "Spectrum Filtering", "The minimum total ion current of the spectrum."));
		this.put("minsnr", new Parameter("Min. Signal/Noise Ratio", 1.0, "Spectrum Filtering", "The minimum signal-to-noise ratio."));
		this.put("noiselvl", new Parameter("Noise level", 2.5, "Spectrum Filtering", "The spectrum noise level."));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}
}