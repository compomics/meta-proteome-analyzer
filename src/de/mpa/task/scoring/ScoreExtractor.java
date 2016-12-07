package de.mpa.task.scoring;

import java.io.File;
import java.util.List;

public abstract class ScoreExtractor {

	protected File targetFile;
	protected File decoyFile;
	protected List<Double> targetScores;
	protected List<Double> decoyScores;
	
	/**
	 * The constructor for the score extractor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public ScoreExtractor(File targetFile, File decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		load();
		if (decoyFile != null) {
			extract();
		} else {
			extractTargetOnly();
		}
	}	
		
	abstract void load();
	
	abstract void extract();
	
	abstract void extractTargetOnly();
	
	/**
	 * Returns the target scores.
	 * @return List of target score values.
	 */
	public List<Double> getTargetScores() {
		return targetScores;
	}
	
	/**
	 * Returns the decoy scores.
	 * @return List of decoy score values.
	 */
	public List<Double> getDecoyScores() {
		return decoyScores;
	}
}
