package de.mpa.db.job.scoring;

import java.io.File;

import de.mpa.db.job.Job;
import de.mpa.db.job.instances.QvalityJob;

/**
 * This job does the scoring for the target and decoy search of Omssa.
 *  
 * @author Thilo Muth
 *
 */
public class OmssaScoreJob extends Job{
	
	// The Omssa target result file.
	private final String targetFile;
	
	// The Omssa decoy result file.
	private final String decoyFile;
	
	private final String filename;

	/**
	 * Constructs the Omssa score job.
	 * @param targetFile Omssa target search result file.
	 * @param decoyFile Omssa decoy search result file.
	 */
	public OmssaScoreJob(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		
		// Set the description
        this.filename = new File(targetFile).getAbsolutePath().substring(0, targetFile.lastIndexOf("_target")) + "_qvalued.out";
        this.setFilename(this.filename);
        this.setDescription("OMSSA QVALUES");
	}
	
	/**
	 * Initalize the job.
	 */
	public void run() {
		
		// Extract the scores for OMSSA target and decoy search
		OmssaScoreExtractor omssaExtractor = new OmssaScoreExtractor(new File(this.targetFile), new File(this.decoyFile));
		
		// Executes QVality
		QvalityJob omssaQVality = new QvalityJob(omssaExtractor.getTargetOutput(), omssaExtractor.getDecoyOutput(), true);
		omssaQVality.run();
	}
	

}