package de.mpa.task.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.compomics.util.protein.Protein;

import de.mpa.client.DbSearchSettings;
import de.mpa.io.GenericContainer;
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
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(filename)));
			Iterator<String> iter = GenericContainer.ProteinAccs.iterator();
			
			// Iterate over the provided protein accessions.
			while (iter.hasNext()) {
				String accession = iter.next();
				// TODO: Use two different protein sequence extraction methods properly!
				if (iterativeSearchSettings.contains("0")) {
					Protein fastaProtein = GenericContainer.FastaLoader.getProteinFromFasta(accession);
					bWriter.append(fastaProtein.getHeader().toString());
					bWriter.newLine();
					bWriter.append(fastaProtein.getSequence().getSequence());
					bWriter.newLine();
				} else {
					// Choose either on the 
				}
			}
			bWriter.flush();
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
