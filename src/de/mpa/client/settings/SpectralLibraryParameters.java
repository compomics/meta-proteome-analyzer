package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import de.mpa.client.ui.panels.SpectralLibrarySettingsPanel;

/**
 * Parameter map storing spectral similarity search settings.
 * @author A. Behne
 */
public class SpectralLibraryParameters extends ParameterMap {

	@Override
	public void initDefaults() {
		SpectralLibrarySettingsPanel specLibSetPnl = new SpectralLibrarySettingsPanel();
		this.put("settings", new Parameter("", specLibSetPnl, "Settings", "Spectral Library Settings"));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}

}
