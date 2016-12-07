package de.mpa.io.parser.xtandem;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.compomics.util.protein.Header;

import de.mpa.io.GenericContainer;
import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.ProteinMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

public class XTandemProteinParser extends GenericContainer {
	/**
	 * X!Tandem output file.
	 */
    private XTandemFile xTandemFile;

	/**
     * Constructor for storing results from a target-only search with X!Tandem.
     * @param file X!Tandem file
     * @param targetScoreFile File containing the original PSM scores.
     */
	public XTandemProteinParser(final File file) {
		this.file = file;
		load();
		parse();
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
    
    @Override
	public void parse() {
		 // Iterate over all the spectra
        @SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();

        // Prepare everything for the peptides.
        PeptideMap pepMap = xTandemFile.getPeptideMap();
        
        // ProteinMap protMap 
        ProteinMap protMap = xTandemFile.getProteinMap();
        
        while (iter.hasNext()) {
            // Get the next spectrum.
            Spectrum spectrum = iter.next();
            int spectrumNumber = spectrum.getSpectrumNumber();
            
            // Get all identifications from the spectrum
            List<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
            
            // Iterate over all peptide identifications aka. domains
            for (Peptide peptide : pepList) {
            	
            	List<Domain> domains = peptide.getDomains();
            	
            	for (Domain domain : domains) {
                   	// parse the FASTA header
            		
                    Header header = Header.parseFromFASTA(protMap.getProtein(domain.getProteinKey()).getLabel());
                    String accession = header.getAccession();
                    ProteinAccs.add(accession);
				}
            }      
        }
        nHits = ProteinAccs.size();
	}
}
