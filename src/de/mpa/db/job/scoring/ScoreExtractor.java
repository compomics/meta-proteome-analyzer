package de.mpa.db.job.scoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ScoreExtractor {

	protected File targetFile;
	protected File decoyFile;
	protected String targetOutput;
	protected String decoyOutput;	
	protected ArrayList<Double> targetScores;
	protected ArrayList<Double> decoyScores;
	
	/**
	 * The constructor for the score extractor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public ScoreExtractor(File targetFile, File decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
		load();
		extract();
		this.targetOutput = targetFile.getAbsolutePath().substring(0, targetFile.getAbsolutePath().lastIndexOf("_target")) + "_target.out";
		this.decoyOutput = decoyFile.getAbsolutePath().substring(0, decoyFile.getAbsolutePath().lastIndexOf("_decoy")) + "_decoy.out";
		write(targetOutput, decoyOutput);
	}	
		
	abstract void load();
	
	abstract void extract();
	
	/**
	 * This method does the writing of the files.
	 * 
	 * @param targetOutput
	 * @param decoyOutput
	 */
	public void write(String targetOutput, String decoyOutput) {
		FileWriter targetWriter, decoyWriter;
		try {
			targetWriter = new FileWriter(targetOutput);
			decoyWriter = new FileWriter(decoyOutput);
			
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

	public String getTargetOutput() {
		return targetOutput;
	}

	public String getDecoyOutput() {
		return decoyOutput;
	}
	
	
}
