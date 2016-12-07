package de.mpa.task.scoring;

import java.io.File;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.io.GenericContainer;
import de.mpa.task.Task;

/**
 * This job does the scoring for the target and decoy search of X!Tandem.
 *  
 * @author T. Muth
 *
 */
public class XTandemScoreTask extends Task {
	
	private String targetFile;
	private String decoyFile;
	private TargetDecoyAnalysis targetDecoyAnalysis;
	
	/**
	 * Constructs the X!Tandem score job.
	 * @param target X!Tandem target search job.
	 * @param decoy !XTandem decoy search job.
	 */
	public XTandemScoreTask(String targetFile, String decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		setDescription("X!Tandem FDR/q-Value calculation");
	}
	
	/**
	 * Initialize the X!Tandem score job.
	 */
	public void run() {
		// Extract the scores for X!Tandem target and decoy search.
		XTandemScoreExtractor scoreExtractor = new XTandemScoreExtractor(new File(targetFile), new File(decoyFile));
		targetDecoyAnalysis = new TargetDecoyAnalysis(scoreExtractor.getTargetScores(), scoreExtractor.getDecoyScores());
		GenericContainer.currentTDA = targetDecoyAnalysis;
	}
	
	/**
	 * Returns the TargetDecoyAnalysis instance.
	 */
	public TargetDecoyAnalysis getTargetDecoyAnalysis() {
		return targetDecoyAnalysis;
	}
}
