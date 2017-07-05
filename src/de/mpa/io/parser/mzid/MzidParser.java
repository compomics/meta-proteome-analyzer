package de.mpa.io.parser.mzid;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.DBSequence;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentification;

public class MzidParser extends GenericContainer {
	
	 /**
     * Variable holding a MzIdentML file.
     */
    private File mzidFile;
    
    /**
     * Constructor for parsing results from a MzIdentML file.
     * @param file Original MzIdentML (.mzid) file
     */
	public MzidParser(final File file) {
		this.file = file;
		this.searchEngineType = SearchEngineType.MZID;
		load();
	}

    /**
     * This method loads the MS-GF+ results file into memory.
     */
    public void load() {
    	mzidFile = new File(file.getAbsolutePath());
    }
    
    /**
     * This methods parses the Comet output file.
     */
	public void parse() {
		// Open an inputFile mzIdentml File using memory
		MzIdentMLControllerImpl mzIdentMlController = new MzIdentMLControllerImpl(mzidFile, true);

		// Retrieve all protein Identifications
		for (Comparable proteinId : mzIdentMlController.getProteinIds()) {
			Collection<Comparable> peptideIds = mzIdentMlController.getPeptideIds(proteinId);
			for (Comparable peptideId : peptideIds) {
				Peptide peptide = mzIdentMlController.getPeptideByIndex(proteinId, peptideId);
				Protein protein = mzIdentMlController.getProteinById(proteinId);
				MzidHit hit = new MzidHit();
				DBSequence proteinSequence = protein.getDbSequence();
				String accession = proteinSequence.getAccession();
				List<CvParam> cvParams = proteinSequence.getCvParams();
				for (CvParam cvParam : cvParams) {
					if (cvParam.getName().equals("protein description")) {
						hit.setProteinDescription(cvParam.getValue());
					}
				}
				SpectrumIdentification spectrumIdentification = peptide.getSpectrumIdentification();
				String psmIdentifier = spectrumIdentification.getId().toString();
				int spectrumID = Integer.valueOf(psmIdentifier.substring(psmIdentifier.indexOf("_") + 1, psmIdentifier.lastIndexOf("_")));
				hit.setSpectrumId(spectrumID);
				hit.setSpectrumTitle(spectrumIdentification.getId().toString());
				hit.setPeptideSequence(peptide.getSequence());
				hit.setCharge(spectrumIdentification.getChargeState());
				hit.setType(SearchEngineType.MZID);
				String[] split = accession.split(Pattern.quote("|"));
				accession = split[split.length - 1];
				hit.setAccession(accession);
				hit.setProteinSequence(proteinSequence.getSequence());
				List<CvParam> cvParams2 = spectrumIdentification.getCvParams();
				for (CvParam cvParam : cvParams2) {
					if (cvParam.getName().contains("score")) {
						hit.setScore(Double.valueOf(cvParam.getValue()));
					}
					if (cvParam.getName().contains("q-value")) {
						hit.setQValue(Double.valueOf(cvParam.getValue()));
					}
				}
				
				// Add protein for UniProt storing.
				UniprotQueryProteins.put(accession, null);
				nHits++;
				SearchHits.add(hit);
			}
		}
		log.debug("No. of MzIdentML hits saved: " + nHits);
	}
}
