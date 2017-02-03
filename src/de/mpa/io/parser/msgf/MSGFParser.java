package de.mpa.io.parser.msgf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;

public class MSGFParser extends GenericContainer {
	
	 /**
     * Variable holding an msgfFile instance.
     */
    private File msgfFile;
	
    /**
     * Constructor for storing results from a target-decoy search with MS-GF+.
     * @param file Original MS-GF+ output file
     */
	public MSGFParser(final File file) {
		this.file = file;
		this.searchEngineType = SearchEngineType.MSGF;
		load();
	}

    /**
     * This method loads the MS-GF+ results file into memory.
     */
    public void load() {
    	msgfFile = new File(file.getAbsolutePath());
    }
    
    /**
     * This methods parses the MS-GF+ results output file.
     */
	public void parse() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(msgfFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#SpecFile")) {
					String[] split = line.split("\t");

					Long spectrumId = Long.valueOf(split[1].substring(6)) + 1L;

					// Only store if the search spectrum id is referenced.
					if (SpectrumId2TitleMap.containsKey(spectrumId)) {
						String spectrumTitle = SpectrumId2TitleMap.get(spectrumId);
						String spectrumFilename = SpectrumTitle2FilenameMap.get(spectrumTitle);
						Set<String> proteinAccessions = new HashSet<String>();

						String[] temp = split[10].split(";");
						for (String string : temp) {
							String[] split2 = string.split("\\|");
							proteinAccessions.add(split2[1]);
						}

						double qValue = Double.valueOf(split[15]);

						if (qValue < 0.1) {
							for (String accession : proteinAccessions) {
								MSGFHit hit = new MSGFHit();
								hit.setSpectrumId(spectrumId);
								hit.setSpectrumFilename(spectrumFilename);

								if (!spectrumTitle.equals(split[3])) {
									throw new IOException("Wrong spectrum title: " + spectrumTitle);
								}
								hit.setSpectrumTitle(spectrumTitle);

								// parse the FASTA header
								hit.setAccession(accession);
								hit.setScore(Double.valueOf(split[12]));
								hit.setPepQValue(Double.valueOf(split[16]));
								hit.setQValue(qValue);
								hit.setType(searchEngineType);

								// Get and store the peptide.
								hit.setPeptideSequence(split[9].replaceAll("\\+57.021", ""));
								hit.setCharge(Integer.valueOf(split[8]));

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
			}
			br.close();
			log.debug("No. of MS-GF+ hits saved: " + nHits);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
