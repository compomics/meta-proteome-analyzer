package de.mpa.job.scoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class ScoreExtractor {

	protected File targetFile;
	protected File decoyFile;
	protected String targetOutput;
	protected String decoyOutput;	
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
			this.targetOutput = targetFile.getAbsolutePath().substring(0, targetFile.getAbsolutePath().lastIndexOf("_target")) + "_target.out";
			this.decoyOutput = decoyFile.getAbsolutePath().substring(0, decoyFile.getAbsolutePath().lastIndexOf("_decoy")) + "_decoy.out";
			write(targetOutput, decoyOutput);
		} else {
			extractTargetOnly();
			this.targetOutput = targetFile.getAbsolutePath().substring(0, targetFile.getAbsolutePath().lastIndexOf("_target")) + "_target.out";
			writeTargetOnly(targetOutput);
		}
	}	
		
	abstract void load();
	
	abstract void extract();
	
	abstract void extractTargetOnly();
	
	/**
	 * This method writes the target and decoy score files.
	 * 
	 * @param targetOutputPath Target output file path
	 * @param decoyOutputPath Decoy output file path
	 */
	public void write(String targetOutputPath, String decoyOutputPath) {
		FileWriter targetWriter, decoyWriter;
		try {
			targetWriter = new FileWriter(targetOutputPath);
			decoyWriter = new FileWriter(decoyOutputPath);
			
			for (double target : targetScores) {
				targetWriter.write(Double.toString(target));
				targetWriter.write("\n");
			}
			for (double decoy : decoyScores) {
				decoyWriter.write(Double.toString(decoy));
				decoyWriter.write("\n");
			}			
			targetWriter.close();
			decoyWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method does the writing of the files.
	 * 
	 * @param targetOutput
	 */
	public void writeTargetOnly(String targetOutput) {
		FileWriter targetWriter;
		try {
			targetWriter = new FileWriter(targetOutput);
			for (double target : targetScores) {
				targetWriter.write(Double.toString(target));
				targetWriter.write("\n");
			}
			targetWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTargetOutput() {
		return targetOutput;
	}
	
	public String getDecoyOutput() {
		return decoyOutput;
	}
	
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
