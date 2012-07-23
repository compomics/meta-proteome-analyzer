package de.mpa.analysis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.model.blast.JobStatus;
import uk.ac.ebi.kraken.model.blast.parameters.DatabaseOptions;
import uk.ac.ebi.kraken.model.blast.parameters.ExpectedThreshold;
import uk.ac.ebi.kraken.model.blast.parameters.SimilarityMatrixOptions;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import uk.ac.ebi.kraken.uuw.services.remoting.blast.BlastData;
import uk.ac.ebi.kraken.uuw.services.remoting.blast.BlastHit;
import uk.ac.ebi.kraken.uuw.services.remoting.blast.BlastInput;
import junit.framework.TestCase;

public class UniprotBlastTest extends TestCase{
	
	private UniProtQueryService service;
	private enum params {
		S
	}
	@Override
	public void setUp() throws Exception {
		//Get the UniProt Service. This is how to access the blast service
	    service = UniProtJAPI.factory.getUniProtQueryService();
	}
	
	@Test
	public void testEntryRetrieval() {
		String seq1 = "BXXVADESHAGSSDK";
		String seq2 = "BATEEQLGA";
		String seq3 = "BTCVADESHAGXXDK";
		List<String> sequences = new ArrayList<String>();
		
		sequences.add(seq1);
		sequences.add(seq2);
		sequences.add(seq3);
		Enum params[] = new Enum[2];
		params[0] = SimilarityMatrixOptions.PAM_30;
		params[1] = ExpectedThreshold.ONETHOUSAND;
		
		for (String sequence : sequences) {
			// Create a blast input with a Database and sequence
			BlastInput input = new BlastInput(DatabaseOptions.SWISSPROT, sequence, params);

			// Submitting the input to the service will return a job id
			String jobid = service.submitBlast(input);

			// Use this jobid to check the service to see if the job is complete
			while (!(service.checkStatus(jobid) == JobStatus.FINISHED)) {
				try {
					// Sleep a bit before the next request
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// The blast data contains the job information and the hits with
				// entries
				BlastData<UniProtEntry> blastResult = service.getResults(jobid);
				List<BlastHit<UniProtEntry>> blastHits = blastResult.getBlastHits();
				for (BlastHit<UniProtEntry> blastHit : blastHits) {
					System.out.println("Description: " + blastHit.getHit().getDescription());
					System.out.println();
					System.out.println("Accession: " + blastHit.getEntry().getPrimaryUniProtAccession().getValue());
				}
			}
		}
	}
	
}	
