package de.mpa.db.storager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;

import com.compomics.util.protein.Header;

import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XtandemhitTableAccessor;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.ProteinMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 03.09.2010
 * Time: 16:00:31
 * To change this template use File | Settings | File Templates.
 */
public class XTandemStorager extends BasicStorager {

    /**
     * Variable holding an xTandemFile.
     */
    private XTandemFile xTandemFile;
    
    
    /**
     * The file instance.
     */
    private final File file;
    
    /**
     * The q-value file.
     */
    private File qValueFile = null;
    
    /**
     * The Connection instance.
     */
    private final Connection conn;
    
    private Map<Double, List<Double>> scoreQValueMap;

	private Map<String, Long> domainMap;
	
    /**
     * Default Constructor.
     */
    public XTandemStorager(final Connection conn, final File file){
    	this.conn = conn;
    	this.file = file;
    }
    
    /**
     * Default Constructor.
     */
    public XTandemStorager(final Connection conn, final File file, File qValueFile){
    	this.conn = conn;
    	this.file = file;
    	this.qValueFile = qValueFile;
    }

    /**
     * Loads the XTandemFile.
     * @param file
     */
    public void load() {
        try {
            xTandemFile = new XTandemFile(file.getAbsolutePath());
        } catch (SAXException saxException) {
            saxException.getMessage();
        }
    }
    
    /**
     * Stores XTandemfile and its contents to the database.
     * @param conn
     * @throws IOException
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
               
        // Iterate over all the spectra
        @SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();

        // Prepare everything for the peptides.
        PeptideMap pepMap = xTandemFile.getPeptideMap();
        
        // ProteinMap protMap 
        ProteinMap protMap = xTandemFile.getProteinMap();
        
        // DomainID as key, xtandemID as value.
        domainMap = new HashMap<String, Long>();
        
        // List for the q-values.
        List<Double> qvalues;
        
        while (iter.hasNext()) {

            // Get the next spectrum.
            Spectrum spectrum = iter.next();
            int spectrumNumber = spectrum.getSpectrumNumber();
            String spectrumName = xTandemFile.getSupportData(spectrumNumber).getFragIonSpectrumDescription();
            
            // Get all identifications from the spectrum
            ArrayList<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
            List<String> peptides = new ArrayList<String>();
            
            // Iterate over all peptide identifications aka. domains
            for (Peptide peptide : pepList) {
            	String sequence = peptide.getDomainSequence();
            	
            	if(!peptides.contains(sequence)){
            	      HashMap<Object, Object> hitdata = new HashMap<Object, Object>(15);                
                      
                      // Get the spectrum id for the given spectrumName for the XTandemFile     
                      long spectrumid = Searchspectrum.getSpectrumIdFromSpectrumName(spectrumName, false);
                      hitdata.put(XtandemhitTableAccessor.FK_SPECTRUMID, spectrumid);  
                      
                      // Get the peptide id
                      long peptideID = PeptideAccessor.findPeptideIDfromSequence(sequence, conn);
          	    	  hitdata.put(XtandemhitTableAccessor.FK_PEPTIDEID, peptideID);
          	    	  
                      // Set the domain id  
                      String domainID = peptide.getDomainID();
                      hitdata.put(XtandemhitTableAccessor.DOMAINID, domainID);
                      
                      // parse the FASTA header
                      Header header = Header.parseFromFASTA(protMap.getProteinWithPeptideID(domainID).getLabel());
                      String accession = header.getAccession();
                      String description = header.getDescription();
                      Long proteinID;
                      
                      ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, conn);
                      if (protein == null) {	// protein not yet in database
							// Add new protein to the database
							protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, conn);
						} else {
							proteinID = protein.getProteinid();
							// check whether pep2prot link already exists, otherwise create new one
							Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
							if (pep2prot == null) {	// link doesn't exist yet
								// Link peptide to protein.
								pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
							}
						}
                      
                      hitdata.put(XtandemhitTableAccessor.PROTEIN, protMap.getProteinWithPeptideID(domainID).getLabel());
                      hitdata.put(XtandemhitTableAccessor.START, Long.valueOf(peptide.getDomainStart()));
                      hitdata.put(XtandemhitTableAccessor.END, Long.valueOf(peptide.getDomainEnd()));
                      hitdata.put(XtandemhitTableAccessor.EVALUE, peptide.getDomainExpect());
                      hitdata.put(XtandemhitTableAccessor.PRECURSOR, peptide.getDomainMh());
                      hitdata.put(XtandemhitTableAccessor.DELTA, peptide.getDomainDeltaMh());
                      hitdata.put(XtandemhitTableAccessor.HYPERSCORE, peptide.getDomainHyperScore());                
                      hitdata.put(XtandemhitTableAccessor.PRE, peptide.getUpFlankSequence());
                      hitdata.put(XtandemhitTableAccessor.POST, peptide.getDownFlankSequence());                
                      hitdata.put(XtandemhitTableAccessor.MISSCLEAVAGES, Long.valueOf(peptide.getMissedCleavages()));
                      qvalues = scoreQValueMap.get(peptide.getDomainHyperScore());
                	
                      // Check if q-value is provided.
                      if(qvalues == null){
                        	hitdata.put(XtandemhitTableAccessor.PEP, 1.0);
                            hitdata.put(XtandemhitTableAccessor.QVALUE, 1.0);                	
                        } else {
                        	hitdata.put(XtandemhitTableAccessor.PEP, qvalues.get(0));
                            hitdata.put(XtandemhitTableAccessor.QVALUE, qvalues.get(1));
                        }

                      // Create the database object.
                      XtandemhitTableAccessor xtandemhit = new XtandemhitTableAccessor(hitdata);                
                      xtandemhit.persist(conn);
                      
                      // Get the xtandemhitid
                      Long xtandemhitid = (Long) xtandemhit.getGeneratedKeys()[0];
                      domainMap.put(domainID, xtandemhitid);   
                      peptides.add(sequence);
            	}
            }      
            conn.commit();
        }
    }
    
    /**
     * Stores the q-values (obtained from the hyperscore distribution) to the database.
     */
	private void processQValues() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(qValueFile));
			scoreQValueMap = new HashMap<Double, List<Double>>();
			String nextLine;
			// Skip the first line
			reader.readLine();
			List<Double> qvalityList;
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(nextLine, "\t");
				List<String> tokenList = new ArrayList<String>();
				qvalityList = new ArrayList<Double>();
				
				// Iterate over all the tokens
				while (tokenizer.hasMoreTokens()) {
					tokenList.add(tokenizer.nextToken());
				}
				double score = Double.valueOf(tokenList.get(0));				
				qvalityList.add(Double.valueOf(tokenList.get(1)));
				qvalityList.add(Double.valueOf(tokenList.get(2)));
				scoreQValueMap.put(score, qvalityList);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} 
	}
    

    @Override
	public void run() {
		this.load();
		if(qValueFile != null) this.processQValues();		
		try {
			this.store();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}		
		log.info("XTandem results stored to the DB.");
	}
}
