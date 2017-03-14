package de.mpa.db.job.scoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
	 * Accessing the super constructor.
	 * @param targetFile
	 */
	public OmssaScoreExtractor(File targetFile) {
		super(targetFile, null);		
	}
	
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
	}
	
	/**
	 * This methods extracts the scores from the target and decoy hits.
	 */
	protected void extract() {
		// Initialize the score lists
        this.targetScores = new ArrayList<Double>();
        this.decoyScores = new ArrayList<Double>();
        this.omxFileTarget = new OmssaOmxFile(this.targetFile.getAbsolutePath());
		 // Initialize the spectrum iterators
        HashMap<MSSpectrum, MSHitSet> targetResults = this.omxFileTarget.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> targetIter = targetResults.keySet().iterator();
    	
    	// Target hits
    	while (targetIter.hasNext()) {
    	    MSSpectrum msSpectrum = targetIter.next();   
    	    MSHitSet msHitSet = targetResults.get(msSpectrum);
    	    List<MSHits> targetHits = msHitSet.MSHitSet_hits.MSHits;
    	    double lowestEValue = Double.POSITIVE_INFINITY;
//    	    String peptideSequence = "";
    	    for (MSHits targetHit : targetHits) {
    	    	if (targetHit.MSHits_evalue < lowestEValue) {
    	    		lowestEValue = targetHit.MSHits_evalue;
//    	    		peptideSequence = targetHit.MSHits_pepstring;
    	    	}
    	    }
    	    if (lowestEValue < Double.POSITIVE_INFINITY) {
                this.targetScores.add(lowestEValue);
//    	     	String spectrumTitle = formatSpectrumTitle(msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString());
//    	    	searchHits.add(new CustomSearchHit(lowestEValue, peptideSequence, spectrumTitle));
    	    }
//    	    
    	}
    	
    	// Sort the target scores ascending.
    	Collections.sort(this.targetScores);
    	
    	// Help the GC:
        this.omxFileTarget = null;
    	targetResults = null;
    	targetIter = null;
    	
    	// Decoy
        this.omxFileDecoy = new OmssaOmxFile(this.decoyFile.getAbsolutePath());
    	HashMap<MSSpectrum, MSHitSet> decoyResults = this.omxFileDecoy.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> decoyIter = decoyResults.keySet().iterator();  	
    	
    	// Decoy hits
    	while (decoyIter.hasNext()) {
    	    MSSpectrum msSpectrum = decoyIter.next();   
    	    MSHitSet msHitSet = decoyResults.get(msSpectrum);
    	    List<MSHits> decoyHits = msHitSet.MSHitSet_hits.MSHits;
    	    double lowestEValue = Double.POSITIVE_INFINITY;
    	    for (MSHits decoyHit : decoyHits) {
    	    	if (decoyHit.MSHits_evalue < lowestEValue) {
    	    		lowestEValue = decoyHit.MSHits_evalue;
    	    	}
    	    }
    	    if (lowestEValue < Double.POSITIVE_INFINITY) {
                this.decoyScores.add(lowestEValue);
    	    }
    	}    	
   	}

//	 /**
//     * Format OMSSA spectrum title.
//     * @param spectrumTitle Unformatted spectrum title
//     * @return Formatted spectrum title.
//     */
//	private String formatSpectrumTitle(String spectrumTitle) {
//		if(spectrumTitle.contains("\\\\")){
//			spectrumTitle = spectrumTitle.replace("\\\\", "\\");
//		} 
//		if(spectrumTitle.contains("\\\"")){
//			spectrumTitle = spectrumTitle.replace("\\\"", "\"");
//		}
//		return spectrumTitle;
//	}
	
	@Override
	void extractTargetOnly() {
		// Initialize the score lists
        this.targetScores = new ArrayList<Double>();
        this.omxFileTarget = new OmssaOmxFile(this.targetFile.getAbsolutePath());
		
		 // Initialize the spectrum iterators
        HashMap<MSSpectrum, MSHitSet> targetResults = this.omxFileTarget.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> targetIter = targetResults.keySet().iterator();
    	
    	// Target hits
    	while (targetIter.hasNext()) {
    	    MSSpectrum msSpectrum = targetIter.next();   
    	    MSHitSet msHitSet = targetResults.get(msSpectrum);
    	    List<MSHits> targetHits = msHitSet.MSHitSet_hits.MSHits;
    	    double lowestEValue = Double.POSITIVE_INFINITY;
    	    for (MSHits targetHit : targetHits) {
    	    	if (targetHit.MSHits_evalue < lowestEValue) {
    	    		lowestEValue = targetHit.MSHits_evalue;
    	    	}
    	    }
    	    if (lowestEValue < Double.POSITIVE_INFINITY) {
                this.targetScores.add(lowestEValue);
    	    }
    	}

	}	
}

