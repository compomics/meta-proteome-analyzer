package de.mpa.job.scoring;

import java.io.File;

import de.mpa.job.Job;
import de.mpa.job.instances.QvalityJob;

/**
 * This job does the scoring for the target and decoy search of X!Tandem.
 *  
 * @author Thilo Muth
 *
 */
public class XTandemScoreJob extends Job {
	
	private String targetFile;
	private String decoyFile;
	private String filename;
	
	/**
	 * Constructs the X!Tandem score job.
	 * @param target X!Tandem target search job.
	 * @param decoy !XTandem decoy search job.
	 */
	public XTandemScoreJob(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		
		// Set the description
		filename = new File(targetFile).getAbsolutePath().substring(0, targetFile.lastIndexOf("_target")) + "_qvalued.out";
		setFilename(filename);
		setDescription("X!Tandem FDR/q-Value calculation");
	}
	
	/**
	 * Initalize the job.
	 */
	public void run() {
		// Extract the scores for X!Tandem target and decoy search
		XTandemScoreExtractor xTandemExtractor = new XTandemScoreExtractor(new File(targetFile), new File(decoyFile));
		
		// Executes QVality
		QvalityJob xTandemQVality = new QvalityJob(xTandemExtractor.getTargetOutput(), xTandemExtractor.getDecoyOutput(), false);
		xTandemQVality.run();
	}
	

}
