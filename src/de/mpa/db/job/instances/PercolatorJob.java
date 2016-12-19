package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;
import de.mpa.util.PropertyLoader;

public class PercolatorJob extends Job {

	private final File cruxFile;
	private final String filename;

	/**
	 * Constructor for the XTandemJob retrieving the MGF file as the only
	 * parameter.
	 * 
	 * @param mgfFile
	 */
	public PercolatorJob(File mgfFile) {
		String pathCrux = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX);
		String pathCruxOutput = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX_OUTPUT);
		this.cruxFile = new File(pathCrux);
		filename = pathCruxOutput + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
		initJob();
	}

	/**
	 * In this step Percolator is used to re-rank the obtained results + assign
	 * q-values to them.
	 */
	private void initJob() {
		setDescription("PERCOLATOR JOB");

		String pathCrux = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX);
		String pathCruxOutput = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX_OUTPUT);
		String appCrux = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.APP_CRUX);
		
		procCommands = new ArrayList<String>();
		// Link to the output file.
		procCommands.add(pathCrux + appCrux);
		procCommands.add("percolator");

		// Link to crux sourcefolder path.
		procCommands.add(pathCruxOutput + "search.target.txt");

		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(pathCruxOutput);

		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");

		// FDR < 5%
		procCommands.add("--train-fdr");
		procCommands.add("0.05");

		procCommands.add("--test-fdr");
		procCommands.add("0.05");

		procCommands.trimToSize();

		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(cruxFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}

	/**
	 * Returns the path to the crux percolated output file.
	 */
	public String getFilename() {
		return filename;
	}
}
