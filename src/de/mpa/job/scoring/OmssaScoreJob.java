package de.mpa.job.scoring;

import java.io.File;

import de.mpa.job.Job;
import de.mpa.job.instances.QvalityJob;

/**
 * This job does the scoring for the target and decoy search of Omssa.
 *  
 * @author Thilo Muth
 *
 */
public class OmssaScoreJob extends Job{
	
	// The Omssa target result file.
	private String targetFile;
	
	// The Omssa decoy result file.
	private String decoyFile;
	
	private String filename;

	/**
	 * Constructs the Omssa score job.
	 * @param targetFile Omssa target search result file.
	 * @param decoyFile Omssa decoy search result file.
	 */
	public OmssaScoreJob(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		
		// Set the description
		filename = new File(targetFile).getAbsolutePath().substring(0, targetFile.lastIndexOf("_target")) + "_qvalued.out";
		setFilename(filename);
		setDescription("OMSSA QVALUES");
	}
	
	/**
	 * Initalize the job.
	 */
	public void run() {
		
		// Extract the scores for X!Tandem target and decoy search
		OmssaScoreExtractor omssaExtractor = new OmssaScoreExtractor(new File(targetFile), new File(decoyFile));
		
		// Executes QVality
		QvalityJob omssaQVality = new QvalityJob(omssaExtractor.getTargetOutput(), omssaExtractor.getDecoyOutput(), true);
		omssaQVality.run();
	}
	

}