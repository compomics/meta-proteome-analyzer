package de.mpa.task.scoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CometScoreExtractor extends ScoreExtractor {
	
	/**
	 * Accessing the super constructor.
	 * @param targetFile
	 */
	public CometScoreExtractor(File targetFile) {
		super(targetFile, null);		
	}
	
	/**
	 * Accessing the super constructor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public CometScoreExtractor(File targetFile, File decoyFile) {
		super(targetFile, decoyFile);		
	}
	
	
	@Override
	void extract() {
		// Initialize the score lists
		targetScores = new ArrayList<Double>();
		decoyScores = new ArrayList<Double>();	
	    
		BufferedReader br = null, br2 = null;
		try {
			br = new BufferedReader(new FileReader(targetFile));
			br2 = new BufferedReader(new FileReader(decoyFile));
			String line = null;
			
			// Target scores
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("CometVersion") && !line.startsWith("scan")) {
					String[] split = line.split("\t");
					double targetScore = -Math.log(Double.valueOf(split[5]));
					targetScores.add(targetScore);
				}
			}
			
			while ((line = br2.readLine()) != null) {
				if (!line.startsWith("CometVersion") && !line.startsWith("scan")) {
					String[] split = line.split("\t");
					double decoyScore = -Math.log(Double.valueOf(split[5]));
					decoyScores.add(decoyScore);
				}
			}
			br.close();
			br2.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	@Override
	void load() {
	}

	@Override
	void extractTargetOnly() {
	}
}
