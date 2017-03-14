package de.mpa.db.job.scoring;

import java.io.File;

import de.mpa.db.job.Job;
import de.mpa.db.job.instances.QvalityJob;

/**
 * This job does the scoring for the target and decoy search of X!Tandem.
 *  
 * @author Thilo Muth
 *
 */
public class XTandemScoreJob extends Job {
	
	private final String targetFile;
	private final String decoyFile;
	private final String filename;
	
	/**
	 * Constructs the X!Tandem score job.
	 * @param target X!Tandem target search job.
	 * @param decoy !XTandem decoy search job.
	 */
	public XTandemScoreJob(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		
		// Set the description
        this.filename = new File(targetFile).getAbsolutePath().substring(0, targetFile.lastIndexOf("_target")) + "_qvalued.out";
        this.setFilename(this.filename);
        this.setDescription("X!TANDEM QVALUES");
	}
	
	/**
	 * Initalize the job.
	 */
	public void run() {
		// check if xtandem wrote a file or not 
		if (new File(this.targetFile).exists()) {
			// Extract the scores for X!Tandem target and decoy search
			XTandemScoreExtractor xTandemExtractor = new XTandemScoreExtractor(new File(this.targetFile), new File(this.decoyFile));

			// Executes QVality
			QvalityJob xTandemQVality = new QvalityJob(xTandemExtractor.getTargetOutput(), xTandemExtractor.getDecoyOutput(), false);
			xTandemQVality.run();
		}
	}
	

}
