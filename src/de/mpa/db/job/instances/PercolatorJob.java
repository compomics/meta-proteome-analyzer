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
        cruxFile = new File(pathCrux);
        this.filename = pathCruxOutput + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
        this.initJob();
	}

	/**
	 * In this step Percolator is used to re-rank the obtained results + assign
	 * q-values to them.
	 */
	private void initJob() {
        this.setDescription("PERCOLATOR JOB");

		String pathCrux = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX);
		String pathCruxOutput = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_CRUX_OUTPUT);
		String appCrux = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.APP_CRUX);

        this.procCommands = new ArrayList<String>();
		// Link to the output file.
        this.procCommands.add(pathCrux + appCrux);
        this.procCommands.add("percolator");

		// Link to crux sourcefolder path.
        this.procCommands.add(pathCruxOutput + "search.target.txt");

		// Link to outputfolder path.
        this.procCommands.add("--output-dir");
        this.procCommands.add(pathCruxOutput);

		// Overwrite existing files (if any searches before)
        this.procCommands.add("--overwrite");
        this.procCommands.add("T");

		// FDR < 5%
        this.procCommands.add("--train-fdr");
        this.procCommands.add("0.05");

        this.procCommands.add("--test-fdr");
        this.procCommands.add("0.05");

        this.procCommands.trimToSize();

        this.procBuilder = new ProcessBuilder(this.procCommands);
        this.procBuilder.directory(this.cruxFile);
		// set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);
	}

	/**
	 * Returns the path to the crux percolated output file.
	 */
	public String getFilename() {
		return this.filename;
	}
}
