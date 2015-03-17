package de.mpa.job.instances;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpa.io.GeneralParser;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.job.Job;

public class SpectraJob extends Job {
	
	private MascotGenericFileReader reader;
	private List<File> files;
	
    /**
     * Constructor with experiment id as additional parameter:
     * Used for storing the search spectra.
     * 
     * @param files The spectrum search files.
     */
    public SpectraJob(List<File> files) {
    	this.files = files;
    }

	@Override
	public void run() {
		long spectrumCounter = 1;
		log = Logger.getLogger(getClass());
    	setDescription("INITIALIZE SPECTRA");
    	
		// Iterate the MGF files.
		for (File file : files) {
			try {
				reader = new MascotGenericFileReader(file);
				// Get all spectra from the reader.
				List<MascotGenericFile> spectra = reader.getSpectrumFiles();

				// Iterate over all spectra.
				for (MascotGenericFile mgf : spectra) {
					// The filename, remove leading and trailing whitespace.
					String title = mgf.getTitle().trim();
					
					// Fill the cache maps
					GeneralParser.SpectrumTitle2IdMap.put(title, spectrumCounter);
					GeneralParser.FileName2IdMap.put(mgf.getFilename(), spectrumCounter++);
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
