package de.mpa.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Class to access the UniProt EBI WebService.
 * @author T.Muth
 * @date 25-06-2012
 *
 */
public class UniprotAccessor {
	
	/**
	 * UniProt Query Service. 
	 */
	private static UniProtQueryService uniProtQueryService;
	
	/**
	 * KeywordOntology enumeration.
	 */
	public enum KeywordOntology {
		BIOLOGICAL_PROCESS, CELLULAR_COMPONENT, MOLECULAR_FUNCTION
	}
	
	/**
	 * Retrieve a list of protein entries which hold protein hits and UniProt entries.
	 * @param dbSearchResult The database search result.
	 * @return List of ProteinEntry objects.
	 */
	public static List<UniProtEntry> retrieveUniprotEntries(DbSearchResult dbSearchResult) throws RemoteDataAccessException {
		
		// Check whether UniProt query service has been established yet.
		if(uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		}
		
		// Get the protein hits from the search result.
		Map<String, ProteinHit> proteinHits = dbSearchResult.getProteinHits();
		List<String> accList = new ArrayList<String>(proteinHits.keySet());
		
		Query query = UniProtQueryBuilder.buildIDListQuery(accList);
		
		List<UniProtEntry> entries = new ArrayList<UniProtEntry>();
		
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		
		// Iterate the entries and add them to the list. 
		for (UniProtEntry e : entryIterator) {
			String accession = e.getPrimaryUniProtAccession().getValue();
			proteinHits.get(accession).setUniprotEntry(e);
			entries.add(e);
		}
		return entries;
	}
	
	/**
	 * Returns the UniProt keyword map.
	 * @return
	 */
	public static Map<String, KeywordOntology> getOntologyMap() {
		Map<String, KeywordOntology> map = new HashMap<String, KeywordOntology>();
		
		// TODO: Add cellular component + biological process mapping
		// Molecular functions
		map.put("Actin capping", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Activator", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Alpha-amylase inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Amphibian defense peptide", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Antifreeze protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Antimicrobial", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Antioxidant", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Antiviral protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Bence-Jones protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Blood group antigen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Capsid protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Chaperone", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Chromatin regulator", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Cyclin", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Cytokine", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Developmental protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("DNA invertase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("DNA replication inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Elongation factor", KeywordOntology.MOLECULAR_FUNCTION);		
		map.put("Endorphin", KeywordOntology.MOLECULAR_FUNCTION);		
		map.put("Excision nuclease", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Eye lens protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Growth factor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("GTPase activation", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Guanine-nucleotide releasing factor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Hemagglutinin", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Hormone", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Hydrolase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Hypotensive agent", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Ice nucleation", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Initiation factor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Integrin", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Ionic channel", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Leader peptide", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Ligase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Light-harvesting polypeptide", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Lyase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Metalloenzyme inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Milk protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Mitogen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Mobility protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Monoclonal antibody", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Morphogen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Motor protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Muscle protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Mutator protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Neuropeptide", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Neurotransmitter", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Opioid peptide", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Oxidoreductase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Pathogenesis-related protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Phage lysis protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Pheromone", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Phospholipase A2 inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Photoprotein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Porin", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Prion", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Protease inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Protein kinase inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Protein phosphatase inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Protein synthesis inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Prothrombin activator", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Pyrogen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Receptor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Repressor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Ribonucleoprotein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Serine protease homolog", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Sigma factor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Signal transduction inhibitor", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Silk protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Storage protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Superantigen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Suppressor of RNA silencing", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Taste-modifying protein", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Toxin", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Transducer", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Transferase", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Translational shunt", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Tumor antigen", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Vasoactive", KeywordOntology.MOLECULAR_FUNCTION);
		map.put("Viral movement protein", KeywordOntology.MOLECULAR_FUNCTION);
		return map;
	}
	
}
