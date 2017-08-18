package de.mpa.task.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.compomics.util.protein.Protein;

import de.mpa.client.DbSearchSettings;
import de.mpa.io.GenericContainer;
import de.mpa.io.fasta.FastaUtilities;
import de.mpa.task.Task;

public class CreateDatabaseTask extends Task {
	
	/**
	 * Filename of the original FASTA database (absolute path).
	 */
	private String fastaFileName;
	
	/**
	 * Iterative search settings: either protein- or taxonomy based sequence reduction.
	 */
	private String iterativeSearchSettings;
	
	/**
	 * Created a reduced protein sequence database for a given set of identifications from first search round.
	 * @param searchSettings Database search settings
	 */
	public CreateDatabaseTask(DbSearchSettings searchSettings) {
		this.fastaFileName = searchSettings.getFastaFilePath();
		this.filename = fastaFileName.substring(0, fastaFileName.indexOf(".fasta")) + "_reduced.fasta";
		this.iterativeSearchSettings = searchSettings.getIterativeSearchSettings();
		setDescription("REDUCING SEQUENCE DATABASE");
	}
	
	@Override
	public void run() {
		try {
			File fastaFile = new File(filename);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(fastaFile));
			Iterator<String> iter = GenericContainer.ProteinAccs.iterator();
			Set<String> speciesSet = new HashSet<>();
			
			// Iterate over the provided protein accessions.
			while (iter.hasNext()) {
				String accession = iter.next();
				if (iterativeSearchSettings.contains("0")) {
					Protein fastaProtein = GenericContainer.FastaLoader.getProteinFromFasta(accession);
					bWriter.append(fastaProtein.getHeader().toString());
					bWriter.newLine();
					bWriter.append(fastaProtein.getSequence().getSequence());
					bWriter.newLine();
				} else if (iterativeSearchSettings.contains("1")) {
					Protein fastaProtein = GenericContainer.FastaLoader.getProteinFromFasta(accession);
					String taxonomy = fastaProtein.getHeader().getTaxonomy();
					// Add to set of already identified species and parse information only once.
					if (!speciesSet.contains(taxonomy)) {
						speciesSet.add(taxonomy);
						Set<String> speciesAccessions = new HashSet<>();
						SortedSet<Object[]> subSet = GenericContainer.SpeciesIndex.subSet(new Object[]{taxonomy}, new Object[]{taxonomy, null});
						for (Object[] objects : subSet) {
							speciesAccessions.add(objects[1].toString());
						}
						
						// Lookup all proteins for a specific taxonomy.
						for (String speciesAccession : speciesAccessions) {
							Protein speciesProtein = GenericContainer.FastaLoader.getProteinFromFasta(speciesAccession);
							bWriter.append(speciesProtein.getHeader().toString());
							bWriter.newLine();
							bWriter.append(speciesProtein.getSequence().getSequence());
							bWriter.newLine();
						}
					}
				}
			}
			bWriter.flush();
			bWriter.close();
			
			// Create decoy database from the reduced database (for X!Tandem only).
    		FastaUtilities.createDecoyDatabase(fastaFile);
    		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
