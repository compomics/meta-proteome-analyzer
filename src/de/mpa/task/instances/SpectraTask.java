package de.mpa.task.instances;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.task.Task;

public class SpectraTask extends Task {
	
	private MascotGenericFileReader reader;
	private List<File> files;
	
    /**
     * Constructor with experiment id as additional parameter:
     * Used for storing the search spectra.
     * 
     * @param files The spectrum search files.
     */
    public SpectraTask(List<File> files) {
    	this.files = files;
    }

	@Override
	public void run() {
		
		log = Logger.getLogger(getClass());
    	setDescription("INDEXING SPECTRA");
    	int totalSpectra = 0;
    	client.firePropertyChange("new message", null, "INDEXING SPECTRA");
    	
		// Iterate the MGF files.
		for (File file : files) {
			try {
				long spectrumCounter = 0;
				client.firePropertyChange("resetcur", -1L, file.length());
				
				reader = new MascotGenericFileReader(file);
				// Get all spectra from the reader.
				List<MascotGenericFile> spectra = reader.getSpectrumFiles(false);
				
				// Iterate over all spectra.
				for (MascotGenericFile mgf : spectra) {
					// The filename, remove leading and trailing whitespace.
					String title = mgf.getTitle().trim();
					
					// Fill the cache maps
					GenericContainer.SpectrumTitle2IdMap.put(title, ++spectrumCounter);
					GenericContainer.SpectrumTitle2FilenameMap.put(title, file.getAbsolutePath());
					totalSpectra++;
				}
				GenericContainer.MGFReaders.put(file.getAbsolutePath(), reader);
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("new message", null, "INDEXING SPECTRA FINISHED");
			} catch (IOException e) {
				client.firePropertyChange("new message", null, "INDEXING SPECTRA FAILED");
				e.printStackTrace();
			}
		}
		GenericContainer.numberTotalSpectra = totalSpectra;
		
		// Provide a bi-directional mapping.
		GenericContainer.SpectrumId2TitleMap = GenericContainer.reverse(GenericContainer.SpectrumTitle2IdMap);
	}
}
