package de.mpa.analysis;

import java.util.List;

import org.expasy.mzjava.stats.statscores.FitDecoyScoreStatistics;
import org.expasy.mzjava.stats.statscores.ScoreStatistics.SortAction;

/**
 * Helper class to perform an FDR estimation based on target-decoy analysis using q-values.
 * 
 * @author T. Muth
 * 
 */
public class TargetDecoyAnalysis {
	/**
	 * Array of target scores (float type).
	 */
	private double[] targetScores;
	
	/**
	 * Array of decoy scores (float type).
	 */
	private double[] decoyScores;
	
	/**
	 * FitDecoyScoreStatistics instance using MzJava library.
	 */
	private FitDecoyScoreStatistics stats;
	
	public TargetDecoyAnalysis(List<Double> target, List<Double> decoy) {
		this.targetScores = convertToScoreArray(target);
		this.decoyScores = convertToScoreArray(decoy);
		stats = new FitDecoyScoreStatistics(targetScores, decoyScores, SortAction.SORT_IF_UNSORTED);
	}
	 
	/**
	 * Converts a given list of PSMs to a float array of PSM scores.
	 * @param scores List of PSMs.
	 * @return Float array of PSM scores.
	 */
	private static double[] convertToScoreArray(List<Double> scoresVector) {
		double[] scores = new double[scoresVector.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = scoresVector.get(i).floatValue();
		}
		return scores;
	}
	
	/**
	 * Returns the q-value (minimum FDR) for a given PSM score.
	 * @param score PSM score.
	 * @return q-value (minimum FDR value) for a given PSM score.
	 */
	public float getQValue(float score) {
		return stats.getQValue(score);
	}
	
	/**
	 * Returns a specific PSM score threshold for a given q-value.
	 * @param qValue User-defined q-value.
	 * @return PSM score threshold
	 */
	public float getThresholdScore(float qValue) {
		return stats.getScoreForQValue(qValue);
	}
	
	/**
	 * Returns float array of target scores.
	 * @return Float array of target scores.
	 */
	public double[] getTargetScores() {
		return targetScores;
	}
	
	/**
	 * Returns float array of decoy scores.
	 * @return Float array of decoy scores.
	 */
	public double[] getDecoyScores() {
		return decoyScores;
	}
}

