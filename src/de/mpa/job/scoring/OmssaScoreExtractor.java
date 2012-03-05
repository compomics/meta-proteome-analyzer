package de.mpa.job.scoring;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSSpectrum;

public class OmssaScoreExtractor extends ScoreExtractor {
	
	private OmssaOmxFile omxFileTarget; 
	private OmssaOmxFile omxFileDecoy;
	/**
	 * Constructor the omssa score extractor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public OmssaScoreExtractor(File targetFile, File decoyFile) {
		super(targetFile, decoyFile);		
	}
	
	/**
	 * Loads the Omssa output OMX files, both target and decoy.
	 */
	protected void load() {
		//omxFileTarget = new OmssaOmxFile(targetFile.getAbsolutePath());
		//omxFileDecoy = new OmssaOmxFile(decoyFile.getAbsolutePath());		
	}
	
	/**
	 * This methods extracts the scores from the target and decoy hits.
	 */
	protected void extract() {
		// Initialize the score lists
		targetScores = new ArrayList<Double>();
		decoyScores = new ArrayList<Double>();		
		omxFileTarget = new OmssaOmxFile(targetFile.getAbsolutePath());
		
		 // Initialize the spectrum iterators
        HashMap<MSSpectrum, MSHitSet> targetResults = omxFileTarget.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> targetIter = targetResults.keySet().iterator();
    	
    	// Target hits
    	while (targetIter.hasNext()) {
    	    MSSpectrum msSpectrum = targetIter.next();   
    	    MSHitSet msHitSet = targetResults.get(msSpectrum);
    	    List<MSHits> targetHits = msHitSet.MSHitSet_hits.MSHits;
    	    for (MSHits msHit : targetHits) {
    	    	targetScores.add(msHit.MSHits_evalue);
    	    }
    	}
    	// Help the GC:
    	omxFileTarget = null;
    	targetResults = null;
    	targetIter = null;
    	
    	// Decoy
    	omxFileDecoy = new OmssaOmxFile(decoyFile.getAbsolutePath());	
    	HashMap<MSSpectrum, MSHitSet> decoyResults = omxFileDecoy.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> decoyIter = decoyResults.keySet().iterator();  	
    	
    	// Decoy hits
    	while (decoyIter.hasNext()) {
    	    MSSpectrum msSpectrum = decoyIter.next();   
    	    MSHitSet msHitSet = decoyResults.get(msSpectrum);
    	    List<MSHits> decoyHits = msHitSet.MSHitSet_hits.MSHits;
    	    for (MSHits msHit : decoyHits) {
    	    	decoyScores.add(msHit.MSHits_evalue);
    	    }
    	}
   	}	
}

