package de.mpa.db.job.scoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSPepHit;
import de.proteinms.omxparser.util.MSSpectrum;

public class OmssaScoreWriter {
	
	private OmssaOmxFile omxFileTarget; 
	private OmssaOmxFile omxFileDecoy;
	private final File targetFile;
	private final File decoyFile;
	
	/**
	 * Constructor the omssa score extractor.
	 * @param targetFile
	 * @param decoyFile
	 */
	public OmssaScoreWriter(File targetFile, File decoyFile) {
		this.targetFile = targetFile;
		this.decoyFile = decoyFile;
	}

	/**
	 * This methods extracts the scores from the target and decoy hits.
	 * @throws IOException 
	 */
	protected void writeTabfile(File tabfile) throws IOException {
		
		FileWriter writer = new FileWriter(tabfile);

        this.omxFileTarget = new OmssaOmxFile(this.targetFile.getAbsolutePath());
			
		 // Initialize the spectrum iterators
        HashMap<MSSpectrum, MSHitSet> targetResults = this.omxFileTarget.getSpectrumToHitSetMap();
    	Iterator<MSSpectrum> targetIter = targetResults.keySet().iterator();
    	
    	
    	// Header
    	writer.write("SpecId");
    	writer.write("\t");
    	writer.write("Label");
    	writer.write("\t");
       	writer.write("PepLen");
    	writer.write("\t");
    	writer.write("MassDiff");
    	writer.write("\t");
    	writer.write("EValue");
    	writer.write("\t");
    	writer.write("PValue");
    	writer.write("\t");
    	writer.write("Charge");
    	writer.write("\t");
    	writer.write("Mass");
    	writer.write("\t");
    	writer.write("TheoMass");
    	writer.write("\t");
    	writer.write("ProtLen");
    	writer.write("\t");
    	writer.write("numPep");
    	writer.write("\t");
    	writer.write("Peptide");
    	writer.write("\t");
    	writer.write("Protein");
    	writer.write("\t");
    	writer.write("pepStart");
    	writer.write("\t");
    	writer.write("pepStop");
    	writer.write("\t");
    	writer.write("eins");
    	writer.write("\t");
    	writer.write("zwei");
    	writer.write("\t");
    	writer.write("drei");
    	writer.write("\t");
    	writer.write("vier");
    	writer.write("\n");
    	
    	// Target hits
    	while (targetIter.hasNext()) {
    	    MSSpectrum msSpectrum = targetIter.next();   
    	    MSHitSet msHitSet = targetResults.get(msSpectrum);
    	    List<MSHits> targetHits = msHitSet.MSHitSet_hits.MSHits;
    	    for (MSHits msHit : targetHits) {
    	    	writer.write(msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString());
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(1));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_pepstring.length()));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(Math.abs(msHit.MSHits_mass - msHit.MSHits_theomass)));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_evalue));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_pvalue));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_charge));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_mass));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_theomass));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_protlength));
    	    	writer.write("\t");
    	    	writer.write(msHit.MSHits_pepstring);    	    	
    	       	// Get the MSPepHit (for the accession)
    	    	List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
    	    	writer.write(Integer.toString(pepHits.size()));
    	    	writer.write("\t");
                Iterator<MSPepHit> pepHitIterator = pepHits.iterator();                
                MSPepHit pepHit = pepHitIterator.next();  
                writer.write("\t");
    	    	writer.write(pepHit.MSPepHit_accession);
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_start));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\n");
    	    }
    	}

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
    	    for (MSHits msHit : decoyHits) {
    	    	writer.write(msSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0).toString());
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(-1));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_pepstring.length()));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_evalue));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_pvalue));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_charge));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_mass));
    	    	writer.write("\t");
    	    	writer.write(Double.toString(msHit.MSHits_theomass));    	    	
       	    	writer.write("\t");
    	    	writer.write(Integer.toString(msHit.MSHits_protlength));
    	    	writer.write("\t");
    	    	writer.write(msHit.MSHits_pepstring);    	    	
    	       	// Get the MSPepHit (for the accession)
    	    	List<MSPepHit> pepHits = msHit.MSHits_pephits.MSPepHit;
    	    	writer.write(Integer.toString(pepHits.size()));
    	    	writer.write("\t");
                Iterator<MSPepHit> pepHitIterator = pepHits.iterator();                
                MSPepHit pepHit = pepHitIterator.next();  
                writer.write("\t");
    	    	writer.write(pepHit.MSPepHit_accession);
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_start));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\t");
    	    	writer.write(Integer.toString(pepHit.MSPepHit_stop));
    	    	writer.write("\n");
    	    }
    	}
    	writer.flush();
    	writer.close();

        this.omxFileDecoy = null;
    	decoyResults = null;
    	decoyIter = null;
   	}	
	
	public static void main(String[] args) {
		OmssaScoreWriter writer = new OmssaScoreWriter(new File("/home/muth/target.omx"), new File("/home/muth/decoy.omx"));
		try {
			writer.writeTabfile(new File("tabfile.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


