package de.mpa.db.job.scoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

public class XTandemScoreExtractor extends ScoreExtractor {	
	
	protected XTandemFile xTandemFileTarget;
	protected XTandemFile xTandemFileDecoy;
	/**
	 * Accessing the super constructor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public XTandemScoreExtractor(File targetFile, File decoyFile) {
		super(targetFile, decoyFile);		
	}
	
	/**
	 * Loads the X!Tandem output XML files, both target and decoy.
	 */
	protected void load() {
		try {
			xTandemFileTarget = new XTandemFile(targetFile.getAbsolutePath());
			xTandemFileDecoy = new XTandemFile(decoyFile.getAbsolutePath());
		} catch (SAXException saxException) {
			saxException.getMessage();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * This methods extracts the scores from the target and decoy hits.
	 */
	protected void extract() {
		// Initialize the score lists
		targetScores = new ArrayList<Double>();
		decoyScores = new ArrayList<Double>();
		
		// Prepare everything for the peptides.		
		PeptideMap targetPepMap = xTandemFileTarget.getPeptideMap();
		PeptideMap decoyPepMap = xTandemFileDecoy.getPeptideMap();
		ArrayList<Spectrum> targetList = xTandemFileTarget.getSpectraList();
		ArrayList<Spectrum> decoyList = xTandemFileDecoy.getSpectraList();
		ArrayList<de.proteinms.xtandemparser.xtandem.Peptide> targetPepList = new ArrayList<de.proteinms.xtandemparser.xtandem.Peptide>();
		ArrayList<de.proteinms.xtandemparser.xtandem.Peptide> decoyPepList = new ArrayList<de.proteinms.xtandemparser.xtandem.Peptide>();
		
		// Get the peptide hits for the spectra
		for (Spectrum targetSpectrum : targetList) {
			int targetSpectrumNumber = targetSpectrum.getSpectrumNumber();
			targetPepList.addAll(targetPepMap.getAllPeptides(targetSpectrumNumber));
		}
		for (Spectrum decoySpectrum : decoyList) {				
			int decoySpectrumNumber = decoySpectrum.getSpectrumNumber();
			decoyPepList.addAll(decoyPepMap.getAllPeptides(decoySpectrumNumber));
		}
		
		// Iterate over all peptide identifications aka. domains
		for (int i = 0; i < targetPepList.size(); i++) {
			for (int j = 0; j < decoyPepList.size(); j++) {
				de.proteinms.xtandemparser.xtandem.Peptide hit = targetPepList.get(i);
				de.proteinms.xtandemparser.xtandem.Peptide decoyhit = decoyPepList.get(j);
				List<Domain> targetDomains = hit.getDomains();
				List<Domain> decoyDomains = decoyhit.getDomains();
				for (Domain targetDom : targetDomains) {
					for (Domain decoyDom : decoyDomains) {
						if (targetDom.getDomainID().equals(decoyDom.getDomainID())) {
							targetScores.add(targetDom.getDomainHyperScore());					
							decoyScores.add(decoyDom.getDomainHyperScore());
						}
					}
				}
			}
		}
		
    	// Sort the target + decoy scores descending.
    	Collections.sort(targetScores, Collections.reverseOrder());
    	Collections.sort(decoyScores, Collections.reverseOrder());
	}


}

