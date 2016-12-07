package de.mpa.io.parser.comet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;

public class CometParser extends GenericContainer {
	
	 /**
     * Variable holding an Comet file.
     */
    private File cometFile;
    
    /**
     * TargetDecoyAnalysis. 
     */
    private TargetDecoyAnalysis targetDecoyAnalysis;
	
    /**
     * Constructor for storing results from a target-decoy search with Comet.
     * @param file Original Comet output file
     */
	public CometParser(final File file, TargetDecoyAnalysis targetDecoyAnalysis) {
		this.file = file;
		this.targetDecoyAnalysis = targetDecoyAnalysis;
		this.searchEngineType = SearchEngineType.COMET;
		load();
	}

    /**
     * This method loads the MS-GF+ results file into memory.
     */
    public void load() {
    	cometFile = new File(file.getAbsolutePath());
    }
    
    /**
     * This methods parses the Comet output file.
     */
	public void parse() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(cometFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("CometVersion") && !line.startsWith("scan")) {
					String[] split = line.split("\t");
					Long spectrumId = Long.valueOf(split[0]);

					// Only store if the search spectrum id is referenced.
					if (SpectrumTitle2IdMap.containsValue(spectrumId)) {
						String spectrumTitle = SpectrumId2TitleMap.get(spectrumId);
						String spectrumFilename = SpectrumTitle2FilenameMap.get(spectrumTitle);
				        
						double qValue = targetDecoyAnalysis.getQValue((float)(-Math.log(Double.valueOf(split[5]))));
						if (qValue < 0.05) {
							CometHit hit = new CometHit();
							hit.setSpectrumId(spectrumId);
							hit.setSpectrumFilename(spectrumFilename);
							hit.setSpectrumTitle(spectrumTitle);
							hit.setCharge(Integer.valueOf(split[2]));
							hit.setExpNeutralMass(Double.valueOf(split[3]));
							hit.setCalcNeutralMass(Double.valueOf(split[4]));
							hit.setEValue(Double.valueOf(split[5]));
							hit.setXCorr(Double.valueOf(split[6]));
							hit.setDeltaCn(Double.valueOf(split[7]));
							hit.setSpScore(Double.valueOf(split[8]));
							hit.setIonsMatches(Integer.valueOf(split[9]));
							hit.setTotalIons(Integer.valueOf(split[10]));
							hit.setPeptideSequence(split[11]);
							Header header = Header.parseFromFASTA(split[15]);
	                        String accession = header.getAccession();
							hit.setAccession(accession);
							hit.setQValue(qValue);
							hit.setType(searchEngineType);

							// Store peptide-spectrum association
							Protein protein;
							protein = FastaLoader.getProteinFromFasta(accession);
							String description = protein.getHeader().getDescription();
							hit.setProteinSequence(protein.getSequence().getSequence());
							hit.setProteinDescription(description);

							// Add protein for UniProt storing.
							UniprotQueryProteins.put(accession, null);
							nHits++;
							SearchHits.add(hit);
						}
					}
				}
			}
			br.close();
			log.debug("No. of Comet hits saved: " + nHits);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
