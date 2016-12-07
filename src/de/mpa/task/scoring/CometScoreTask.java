package de.mpa.task.scoring;

import java.io.File;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.io.GenericContainer;
import de.mpa.task.Task;

/**
 * This job does the scoring for Comet target and decoy searches.
 *  
 * @author T. Muth
 *
 */
public class CometScoreTask extends Task {
	
	// Comet target result file.
	private String targetFile;
	
	// Comet decoy result file.
	private String decoyFile;
	
	private TargetDecoyAnalysis targetDecoyAnalysis;

	/**
	 * Constructs the Omssa score job.
	 * @param targetFile Omssa target search result file.
	 * @param decoyFile Omssa decoy search result file.
	 */
	public CometScoreTask(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		setDescription("Comet FDR/q-Value calculation");
	}
	
	/**
	 * Initialize the Comet score job.
	 */
	public void run() {
		// Extract the scores for Comet target and decoy search.
		CometScoreExtractor scoreExtractor = new CometScoreExtractor(new File(targetFile), new File(decoyFile));
		targetDecoyAnalysis = new TargetDecoyAnalysis(scoreExtractor.getTargetScores(), scoreExtractor.getDecoyScores());
		GenericContainer.currentTDA = targetDecoyAnalysis;
	}
	
	/**
	 * Returns the target-decoy analysis.
	 * @return TargetDecoyAnalysis instance.
	 */
	public TargetDecoyAnalysis getTargetDecoyAnalysis() {
		return targetDecoyAnalysis;
	}
}