package de.mpa.io.parser.xtandem;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;
import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.ProteinMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

public class XTandemParser extends GenericContainer {
	
    /**
     * Variable holding an xTandemFile.
     */
    private XTandemFile xTandemFile;
	
	/**
	 * TargetDecoyAnalysis instance.
	 */
	private TargetDecoyAnalysis targetDecoyAnalysis;
	
    /**
     * Constructor for storing results from a target-decoy search with X!Tandem.
     * @param file X!Tandem file
     * @param targetDecoyAnalysis TargetDecoyAnalysis instance
     */
	public XTandemParser(final File file, TargetDecoyAnalysis targetDecoyAnalysis) {
		this.file = file;
		this.targetDecoyAnalysis = targetDecoyAnalysis;
		this.searchEngineType = SearchEngineType.XTANDEM;
		load();
	}

    /**
     * Parses and loads the X!Tandem results file(s).
     */
    public void load() {
        try {
            xTandemFile = new XTandemFile(file.getAbsolutePath());
        } catch (SAXException ex) {
            log.error("Error while parsing X!Tandem file: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Parse X!Tandem results file contents and adds them to a searchhit list.
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public void parse() {
        // Iterate over all the spectra
        @SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();
        
        // Prepare everything for the peptides.
        PeptideMap pepMap = xTandemFile.getPeptideMap();
        
        // ProteinMap protMap 
        ProteinMap protMap = xTandemFile.getProteinMap();
        
        nHits = 0;
        while (iter.hasNext()) {
            // Get the next spectrum.
            Spectrum spectrum = iter.next();
            int spectrumNumber = spectrum.getSpectrumNumber();
            long spectrumId = (long) spectrum.getSpectrumId();
            // Get all identifications from the spectrum
            ArrayList<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
            
            // Iterate over all peptide identifications aka. domains
            for (Peptide peptide : pepList) {
            	
            	List<Domain> domains = peptide.getDomains();
            	for (Domain domain : domains) {
                   	String peptideSequence = domain.getDomainSequence();
                   	
            	    // Only store if the search spectrum id is referenced.
					if (SpectrumId2TitleMap.containsKey(spectrumId)) {
						
            	    	String spectrumTitle = SpectrumId2TitleMap.get(spectrumId);
            	    	spectrumTitle = formatSpectrumTitle(spectrumTitle);
            	    	String spectrumFilename = SpectrumTitle2FilenameMap.get(spectrumTitle);
            	    	
            	    	double qValue = targetDecoyAnalysis.getQValue((float) domain.getDomainHyperScore());
        	            Double pep = 1.0;
        	    	    	
        				if (qValue < 0.1) {
                            // Parse the FASTA header.
        					Header header = Header.parseFromFASTA(protMap.getProtein(domain.getProteinKey()).getLabel());
        					String accession = header.getAccession();
                            
        					XTandemHit hit = new XTandemHit();
    						hit.setSpectrumId(spectrumId);
                 	    	hit.setSpectrumFilename(spectrumFilename);  
                 	    	hit.setSpectrumTitle(spectrumTitle);
                            hit.setAccession(accession);
                            hit.setScore(domain.getDomainHyperScore());                
                            hit.setPep(pep);
                            hit.setQValue(qValue);
                            hit.setType(searchEngineType);
   						
                            // Get and store the peptide.
                            hit.setPeptideSequence(peptideSequence);
                            hit.setCharge(xTandemFile.getSupportData(spectrumNumber).getFragIonCharge());
    						
    						// Add peptide-to-protein relations.
                            Set<String> accessions = new HashSet<>();
                            if (accession.length() > 0){
                            	accessions.add(accession);
                            }
                            Protein protein;
//                            Map<String, Set<String>> peptideIndex = GenericContainer.PeptideIndex;
//                            if (peptideIndex.get(peptideSequence) != null) {
//								accessions.addAll(peptideIndex.get(peptideSequence));
//							}
							
							for (String acc : accessions) {
								try {
									protein = FastaLoader.getProteinFromFasta(acc);
									String description = protein.getHeader().getDescription();
									XTandemHit newHit = new XTandemHit(hit);
									newHit.setAccession(acc);
									newHit.setProteinSequence(protein.getSequence().getSequence());
									newHit.setProteinDescription(description);
	                                
	                        		// Add protein for UniProt storing.
	                        		UniprotQueryProteins.put(acc, null);
	                                nHits++;
	                                SearchHits.add(newHit);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
        				}
            	    }
				}
            }      
        }
        log.debug("No. of X!Tandem hits saved: " + nHits);
    }
}
