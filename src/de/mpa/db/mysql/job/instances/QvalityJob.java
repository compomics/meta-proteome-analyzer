package de.mpa.db.mysql.job.instances;

import java.io.File;

import de.mpa.db.mysql.job.Job;
import de.mpa.util.PropertyLoader;

/**
 * This class respresents the qvality job for the FDR calculation. It takes the
 * target and the null model aka. the decoy as input. The instance uses QVality:
 * Lukas Käll, John D. Storey and William Stafford Noble "qvality:
 * Nonparametric estimation of q values and posterior error probabilities"
 * Bioinformatics, 25(7):964-966, February 2009
 * 
 * @author Thilo Muth
 *
 */
public class QvalityJob extends Job {
	private final File targetFile;
	private final File decoyFile;
	private final String qvaluedOutput;
	private final boolean reverseScoring;

	/**
	 * Constructor for the QValityJob
	 * 
	 * @param targetFilename
	 * @param decoyFilename
	 * @param reverseScoring
	 */
	public QvalityJob(String targetFilename, String decoyFilename, boolean reverseScoring) {
        targetFile = new File(targetFilename);
        decoyFile = new File(decoyFilename);
		this.reverseScoring = reverseScoring;
        this.qvaluedOutput = this.targetFile.getAbsolutePath().substring(0, this.targetFile.getAbsolutePath().lastIndexOf("_target"))
				+ "_qvalued.out";
        this.initJob();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// set the description
        this.setDescription("QVALITY JOB");

		String pathQvality = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_QVALITY);
		String appQvality = pathQvality + PropertyLoader.getProperty(PropertyLoader.APP_QVALITY);

		// full path to executable
        this.procCommands.add(appQvality);

		// Reverse scoring mechanism: Low score are better than high scores
		if (this.reverseScoring) {
            this.procCommands.add("-r");
		}

		// Link to the input files
        this.procCommands.add(this.targetFile.getPath());
        this.procCommands.add(this.decoyFile.getPath());

		// Link to output file
        this.procCommands.add("-o");
        this.procCommands.add(this.qvaluedOutput);
        this.procCommands.trimToSize();

        Job.log.info("qvality commands: " + this.procCommands);
        this.procBuilder = new ProcessBuilder(this.procCommands);

		// set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);
	}

	public String getQValuedOutput() {
		return this.qvaluedOutput;
	}

}
