package de.mpa.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.taxonomy.NcbiTaxonomy;
import de.mpa.taxonomy.TaxonomyNode;

/**
 * Class to access the EBI UniProt WebService.
 * @author T.Muth, A. Behne, R. Heyer
 * @date 25-06-2012
 *
 */
public class UniprotAccessor {

	/**
	 * The shared UniProt query service instance. 
	 */
	private static UniProtQueryService uniProtQueryService;

	/**
	 * Enumeration holding ontology keywords.
	 */
	public enum KeywordOntology {
		BIOLOGICAL_PROCESS, CELLULAR_COMPONENT, MOLECULAR_FUNCTION
	}

	/**
	 * Enumeration holding taxonomic ranks.
	 */
	public enum TaxonomyRank {
		KINGDOM, PHYLUM, CLASS,	ORDER, FAMILY, GENUS, SPECIES, NO_RANK 
	}

	/**
	 * Queries UniProt entries from protein accessions stored in the specified
	 * result object and appends them to the respective protein hits.
	 * @param dbSearchResult The result object.
	 * @throws Exception 
	 */
	public static void retrieveUniProtEntries(DbSearchResult dbSearchResult) throws Exception {
		// Check whether UniProt query service has been established yet.
		if (uniProtQueryService == null) {
			uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
		}

		// Get protein hits from the search result instance
		Map<String, ProteinHit> resultHits = dbSearchResult.getProteinHits();
		
		Map<String, ProteinHit> proteinHits = new HashMap<String, ProteinHit>();
		List<String> idList = new ArrayList<String>();
		for (Entry<String, ProteinHit> entry : dbSearchResult.getProteinHits().entrySet()) {
			String acc = entry.getKey();
			if (acc.matches("^\\d*$")) {
				// accession contains only numbers, most likely a non-UniProt identifier
				idList.add(acc);
			} else {
				proteinHits.put(acc, entry.getValue());
			}
		}
		
		// Check whether any identifiers are in need of re-mapping
		if (!idList.isEmpty()) {
			// TODO: add mapping support for other protein databases (when demanded)
			// Retrieve GI number-to-UniProt mapping
			int increment = 512;
			for (int fromIndex = 0; fromIndex < idList.size(); fromIndex += increment) {
				int toIndex = fromIndex + increment;
				if (toIndex > idList.size()) {
					toIndex = idList.size();
				}
				List<String> subList = idList.subList(fromIndex, toIndex);
				Map<String, String> mapping = UniProtGiMapper.getMapping(subList);
				if ((mapping != null) && !mapping.isEmpty()) {
					for (String gi : idList) {
						String acc = mapping.get(gi);
						// store re-mapped protein hit 
						if (acc != null) {
							proteinHits.put(acc, resultHits.get(gi));
						}
					}
				}
			}
		}
		
		// Gather accessions
		List<String> accList = new ArrayList<String>(proteinHits.keySet());
		// maxClauseCount is set to 1024
		int maxClauseCount = 1024;
		if (accList.size() > maxClauseCount) {
			List<String> shortList = new ArrayList<String>();
			// Break list of accessions into batches of 1024
			for (String acc : accList) {
				shortList.add(acc);
				if (shortList.size() % maxClauseCount == 0) {
					// Query UniProt web service for meta data entries and
					// append them to the respective proteins
					addUniProtEntries(shortList, proteinHits);
					shortList.clear();
				}
			}
			accList = shortList;
		}
		addUniProtEntries(accList, proteinHits);
	}

	/**
	 * Convenience method to query UniProt entries from a list of accession
	 * strings and link them to their respective protein hits.
	 * @param accList The list of accession strings.
	 * @param proteinHits The map of protein hits identified by their accession.
	 * @throws Exception 
	 */
	private static void addUniProtEntries(List<String> accList, Map<String, ProteinHit> proteinHits) throws Exception {
		Query query = UniProtQueryBuilder.buildIDListQuery(accList);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);

		// Iterate the entries and add them to the list. 
		for (UniProtEntry entry : entryIterator) {
			String accession = entry.getPrimaryUniProtAccession().getValue();
			ProteinHit proteinHit = proteinHits.get(accession);
			if (proteinHit != null) {
				proteinHit.setUniprotEntry(entry);
				addTaxonomy(entry, proteinHit);
			} else {
				// primary accession could not be found, try secondary accessions
				List<SecondaryUniProtAccession> secAccs = entry.getSecondaryUniProtAccessions();
				for (SecondaryUniProtAccession secAcc : secAccs) {
					accession = secAcc.getValue();
					proteinHit = proteinHits.get(accession);
					if (proteinHit != null) {
						proteinHit.setUniprotEntry(entry);
						addTaxonomy(entry, proteinHit);
						break;
					}
				}
				// none of the secondary accessions could be found, throw error
				System.err.println("Unable to link UniProt entry " + accession + " to protein hit!");
			}
		}
	}

	/**
	 * Inserts taxonomy node information stored inside the specified UniProt
	 * entry into the specified protein hit.
	 * @param entry the UniProt entry
	 * @param proteinHit the protein hit
	 * @throws Exception if something goes wrong
	 */
	private static void addTaxonomy(UniProtEntry entry, ProteinHit proteinHit)
			throws Exception {
		Integer taxID = Integer.valueOf(entry.getNcbiTaxonomyIds().get(0).getValue());
		String rank = NcbiTaxonomy.getInstance().getRank(taxID);
		String taxName = NcbiTaxonomy.getInstance().getTaxonName(taxID);
		proteinHit.setTaxonomyNode(new TaxonomyNode(taxID, rank, taxName));
	}

	/**
	 * The UniProt keyword taxonomy map.
	 */
	public static final Map<String, TaxonomyRank> TAXONOMY_MAP;
	static {
		Map<String, TaxonomyRank> map = new LinkedHashMap<String, TaxonomyRank>();
		
		map.put("kingdom", TaxonomyRank.KINGDOM);
		map.put("phylum", TaxonomyRank.PHYLUM);
		map.put("class", TaxonomyRank.CLASS);
		map.put("order", TaxonomyRank.ORDER);
		map.put("family", TaxonomyRank.FAMILY);
		map.put("genus", TaxonomyRank.GENUS);
		map.put("species", TaxonomyRank.SPECIES);
//		map.put("no rank", TaxonomyRank.NO_RANK);

//		// (Super-)Kingdoms
//		map.put("Viruses", TaxonomyRank.KINGDOM);
//		map.put("Viroids", TaxonomyRank.KINGDOM);
//		map.put("Archaea", TaxonomyRank.KINGDOM);
//		map.put("Bacteria", TaxonomyRank.KINGDOM);
//		map.put("Eukaryota", TaxonomyRank.KINGDOM);
//
//		// Classes
//		map.put("Thermoprotei", TaxonomyRank.CLASS);
//		map.put("Archaeoglobi", TaxonomyRank.CLASS);
//		map.put("Halobacteria", TaxonomyRank.CLASS);
//		map.put("Methanobacteria", TaxonomyRank.CLASS);
//		map.put("Methanococci", TaxonomyRank.CLASS);
//		map.put("Methanomicrobia", TaxonomyRank.CLASS);
//		map.put("Methanopyri", TaxonomyRank.CLASS);
//		map.put("Nanohaloarchaea", TaxonomyRank.CLASS);
//		map.put("Thermococci", TaxonomyRank.CLASS);
//		map.put("Thermoplasmata", TaxonomyRank.CLASS);
//		map.put("unclassified Euryarchaeota", TaxonomyRank.CLASS);
//		map.put("Actinobacteria (high G+C Gram-positive bacteria)", TaxonomyRank.CLASS);
//		map.put("Aquificae", TaxonomyRank.CLASS);
//		map.put("Armatimonadia", TaxonomyRank.CLASS);
//		map.put("Chthonomonadetes", TaxonomyRank.CLASS);
//		map.put("Bacteroidia", TaxonomyRank.CLASS);
//		map.put("Cytophagia", TaxonomyRank.CLASS);
//		map.put("Flavobacteriia", TaxonomyRank.CLASS);
//		map.put("Sphingobacteriia", TaxonomyRank.CLASS);
//		map.put("Chlorobia", TaxonomyRank.CLASS);
//		map.put("Caldisericia", TaxonomyRank.CLASS);
//		map.put("Chlamydiia", TaxonomyRank.CLASS);
//		map.put("Opitutae", TaxonomyRank.CLASS);
//		map.put("Anaerolineae", TaxonomyRank.CLASS);
//		map.put("Caldilineae", TaxonomyRank.CLASS);
//		map.put("Chloroflexi", TaxonomyRank.CLASS);
//		map.put("Dehalococcoidetes", TaxonomyRank.CLASS);
//		map.put("Ktedonobacteria", TaxonomyRank.CLASS);
//		map.put("Thermomicrobia", TaxonomyRank.CLASS);
//		map.put("Chrysiogenetes", TaxonomyRank.CLASS);
//		map.put("Deferribacteres", TaxonomyRank.CLASS);
//		map.put("Deinococci", TaxonomyRank.CLASS);
//		map.put("Dictyoglomia", TaxonomyRank.CLASS);
//		map.put("Elusimicrobia", TaxonomyRank.CLASS);
//		map.put("Acidobacteriia", TaxonomyRank.CLASS);
//		map.put("Holophagae", TaxonomyRank.CLASS);
//		map.put("Solibacteres", TaxonomyRank.CLASS);
//		map.put("Fibrobacteria", TaxonomyRank.CLASS);
//		map.put("Bacilli", TaxonomyRank.CLASS);
//		map.put("Clostridia", TaxonomyRank.CLASS);
//		map.put("Erysipelotrichi", TaxonomyRank.CLASS);
//		map.put("Negativicutes", TaxonomyRank.CLASS);
//		map.put("Thermolithobacteria", TaxonomyRank.CLASS);
//		map.put("Unclassified Firmicutes", TaxonomyRank.CLASS);
//		map.put("Fusobacteriia", TaxonomyRank.CLASS);
//		map.put("Gemmatimonadetes", TaxonomyRank.CLASS);
//		map.put("Nitrospira", TaxonomyRank.CLASS);
//		map.put("Phycisphaerae", TaxonomyRank.CLASS);
//		map.put("Planctomycetia", TaxonomyRank.CLASS);
//		map.put("Alphaproteobacteria", TaxonomyRank.CLASS);
//		map.put("Betaproteobacteria", TaxonomyRank.CLASS);
//		map.put("Deltaproteobacteria", TaxonomyRank.CLASS);		
//		map.put("Gammaproteobacteria", TaxonomyRank.CLASS);
//		map.put("unclassified Proteobacteria", TaxonomyRank.CLASS);
//		map.put("Zetaproteobacteria", TaxonomyRank.CLASS);
//		map.put("Spirochaetia", TaxonomyRank.CLASS);
//		map.put("unclassified Spirochaetes", TaxonomyRank.CLASS);
//		map.put("Synergistia", TaxonomyRank.CLASS);
//		map.put("Mollicutes", TaxonomyRank.CLASS);
//		map.put("unclassified Tenericutes", TaxonomyRank.CLASS);
//		map.put("Thermodesulfobacteria", TaxonomyRank.CLASS);
//		map.put("Thermotogae", TaxonomyRank.CLASS);
//		map.put("Asteroidea (starfishes)", TaxonomyRank.CLASS);
//		map.put("Echinoidea (sea urchins)", TaxonomyRank.CLASS);
//		map.put("Cryptophyta", TaxonomyRank.CLASS);
//		map.put("Heterolobosea", TaxonomyRank.CLASS);
//		map.put("Glaucocystophyceae", TaxonomyRank.CLASS);
//		map.put("Katablepharidophyta", TaxonomyRank.CLASS);
//		map.put("Bangiophyceae", TaxonomyRank.CLASS);
//		map.put("Florideophyceae", TaxonomyRank.CLASS);
//		map.put("Chrysophyceae (golden algae)", TaxonomyRank.CLASS);
//		map.put("Dinophyceae", TaxonomyRank.CLASS);
//		map.put("Prasinophyceae", TaxonomyRank.CLASS);
//		map.put("Chlorophyceae", TaxonomyRank.CLASS);
//		map.put("Coccidia", TaxonomyRank.CLASS);
//		map.put("Foraminifera (foraminifers)", TaxonomyRank.CLASS);
//		map.put("Ulvophyceae", TaxonomyRank.CLASS);
//		map.put("Coscinodiscophyceae (centric diatoms)", TaxonomyRank.CLASS);
//		map.put("Bacillariophyceae (Raphid, pennate diatoms)", TaxonomyRank.CLASS);
//		map.put("Fragilariophyceae (Araphid, pennate diatoms)", TaxonomyRank.CLASS);
//		map.put("Synurophyceae", TaxonomyRank.CLASS);
//		map.put("Gregarinia", TaxonomyRank.CLASS);
//		map.put("Pedinophyceae", TaxonomyRank.CLASS);
//		map.put("Pelagophyceae", TaxonomyRank.CLASS);
//		map.put("Raphidophyceae", TaxonomyRank.CLASS);
//		map.put("Dictyochophyceae", TaxonomyRank.CLASS);
//		map.put("Hyphochytriomycetes", TaxonomyRank.CLASS);
//		map.put("Chrysomerophyceae", TaxonomyRank.CLASS);
//		map.put("Acantharea", TaxonomyRank.CLASS);
//		map.put("Polycystinea", TaxonomyRank.CLASS);
//		map.put("Trebouxiophyceae", TaxonomyRank.CLASS);
//		map.put("Phaeothamniophyceae", TaxonomyRank.CLASS);
//		map.put("Mesostigmatophyceae", TaxonomyRank.CLASS);
//		map.put("Zygnemophyceae", TaxonomyRank.CLASS);
//		map.put("Chlorokybophyceae", TaxonomyRank.CLASS);
//		map.put("Klebsormidiophyceae", TaxonomyRank.CLASS);
//		map.put("Placididea", TaxonomyRank.CLASS);
//		map.put("Actinophryidae", TaxonomyRank.CLASS);
//		map.put("Hypermastigia", TaxonomyRank.CLASS);
//		map.put("Aconoidasida", TaxonomyRank.CLASS);
//		map.put("Stylonematophyceae", TaxonomyRank.CLASS);
//		map.put("Synchromophyceae", TaxonomyRank.CLASS);
//		map.put("Mediophyceae", TaxonomyRank.CLASS);
//		map.put("Rhodellophyceae", TaxonomyRank.CLASS);
//		map.put("Compsopogonophyceae", TaxonomyRank.CLASS);
//		map.put("Mamiellophyceae", TaxonomyRank.CLASS);
//		map.put("Ustilaginomycetes", TaxonomyRank.CLASS);
//		map.put("Colpodea", TaxonomyRank.CLASS);
//		map.put("Litostomatea", TaxonomyRank.CLASS);
//		map.put("Prostomatea (prostomes)", TaxonomyRank.CLASS);
//		map.put("Nassophorea", TaxonomyRank.CLASS);
//		map.put("Oligohymenophorea", TaxonomyRank.CLASS);
//		map.put("Demospongiae", TaxonomyRank.CLASS);
//		map.put("Hydrozoa (hydrozoans)", TaxonomyRank.CLASS);
//		map.put("Anthozoa (anthozoans)", TaxonomyRank.CLASS);
//		map.put("Cubozoa (sea wasps)", TaxonomyRank.CLASS);
//		map.put("Scyphozoa (jellyfishes)", TaxonomyRank.CLASS);
//		map.put("Calcarea", TaxonomyRank.CLASS);
//		map.put("Karyorelictea", TaxonomyRank.CLASS);
//		map.put("Spirotrichea", TaxonomyRank.CLASS);
//		map.put("Phyllopharyngea", TaxonomyRank.CLASS);
//		map.put("Hexactinellida", TaxonomyRank.CLASS);
//		map.put("Ichthyosporea", TaxonomyRank.CLASS);
//		map.put("Pneumocystidomycetes", TaxonomyRank.CLASS);
//		map.put("Schizosaccharomycetes", TaxonomyRank.CLASS);
//		map.put("Taphrinomycetes", TaxonomyRank.CLASS);
//		map.put("Tremellomycetes", TaxonomyRank.CLASS);
//		map.put("Homobasidiomycetes", TaxonomyRank.CLASS);
//		map.put("Agaricostilbomycetes", TaxonomyRank.CLASS);
//		map.put("Marchantiopsida", TaxonomyRank.CLASS);
//		map.put("Jungermanniopsida", TaxonomyRank.CLASS);
//		map.put("Heterotrichea", TaxonomyRank.CLASS);
//		map.put("Micrognathozoa (micrognathozoans)", TaxonomyRank.CLASS);
//		map.put("Glomeromycetes", TaxonomyRank.CLASS);
//		map.put("Coleochaetophyceae", TaxonomyRank.CLASS);
//		map.put("Charophyceae", TaxonomyRank.CLASS);
//		map.put("Leiosporocerotopsida", TaxonomyRank.CLASS);
//		map.put("Anthocerotopsida", TaxonomyRank.CLASS);
//		map.put("Haplomitriopsida", TaxonomyRank.CLASS);
//		map.put("Atractiellomycetes", TaxonomyRank.CLASS);
//		map.put("Chytridiomycetes", TaxonomyRank.CLASS);
//		map.put("Monoblepharidomycetes", TaxonomyRank.CLASS);
//		map.put("Neocallimastigomycetes", TaxonomyRank.CLASS);
//		map.put("Blastocladiomycetes", TaxonomyRank.CLASS);
//		map.put("Exobasidiomycetes", TaxonomyRank.CLASS);
//		map.put("Dacrymycetes", TaxonomyRank.CLASS);
//		map.put("Armophorea", TaxonomyRank.CLASS);
//		map.put("Tritirachiomycetes", TaxonomyRank.CLASS);
//		map.put("Paraglomeromycetes", TaxonomyRank.CLASS);
//		map.put("Archaeosporomycetes", TaxonomyRank.CLASS);
//		map.put("Bryopsida", TaxonomyRank.CLASS);
//		map.put("Isoetopsida", TaxonomyRank.CLASS);
//		map.put("Lycopodiopsida (clubmosses)", TaxonomyRank.CLASS);
//		map.put("Equisetopsida", TaxonomyRank.CLASS);
//		map.put("Polypodiopsida", TaxonomyRank.CLASS);
//		map.put("Saccharomycetes", TaxonomyRank.CLASS);
//		map.put("Trematoda", TaxonomyRank.CLASS);
//		map.put("Cestoda", TaxonomyRank.CLASS);
//		map.put("Anopla", TaxonomyRank.CLASS);
//		map.put("Enopla", TaxonomyRank.CLASS);
//		map.put("Gastropoda", TaxonomyRank.CLASS);
//		map.put("Bivalvia", TaxonomyRank.CLASS);
//		map.put("Cephalopoda (cephalopods)", TaxonomyRank.CLASS);
//		map.put("Polyplacophora (chitons)", TaxonomyRank.CLASS);
//		map.put("Monogononta", TaxonomyRank.CLASS);
//		map.put("Gymnolaemata", TaxonomyRank.CLASS);
//		map.put("Enteropneusta (acorn worms)", TaxonomyRank.CLASS);
//		map.put("Archiacanthocephala", TaxonomyRank.CLASS);
//		map.put("Ophioglossopsida", TaxonomyRank.CLASS);
//		map.put("Scaphopoda (tusk shells)", TaxonomyRank.CLASS);
//		map.put("Pterobranchia", TaxonomyRank.CLASS);
//		map.put("Phylactolaemata", TaxonomyRank.CLASS);
//		map.put("Monogenea", TaxonomyRank.CLASS);
//		map.put("Gordioida", TaxonomyRank.CLASS);
//		map.put("Bdelloidea", TaxonomyRank.CLASS);
//		map.put("Palaeacanthocephala", TaxonomyRank.CLASS);
//		map.put("Eoacanthocephala", TaxonomyRank.CLASS);
//		map.put("Aplacophora (solenogasters)", TaxonomyRank.CLASS);
//		map.put("Entorrhizomycetes", TaxonomyRank.CLASS);
//		map.put("Marattiopsida", TaxonomyRank.CLASS);
//		map.put("Seisonidea", TaxonomyRank.CLASS);
//		map.put("Stenolaemata", TaxonomyRank.CLASS);
//		map.put("Andreaeopsida", TaxonomyRank.CLASS);
//		map.put("Sphagnopsida", TaxonomyRank.CLASS);
//		map.put("Polytrichopsida", TaxonomyRank.CLASS);
//		map.put("Takakiopsida", TaxonomyRank.CLASS);
//		map.put("Andreaeobryopsida", TaxonomyRank.CLASS);
//		map.put("Enoplea", TaxonomyRank.CLASS);
//		map.put("Chromadorea", TaxonomyRank.CLASS);
//		map.put("Eurotiomycetes", TaxonomyRank.CLASS);
//		map.put("Lecanoromycetes", TaxonomyRank.CLASS);
//		map.put("Pezizomycetes", TaxonomyRank.CLASS);
//		map.put("Turbellaria", TaxonomyRank.CLASS);
//		map.put("Polyacanthocephala", TaxonomyRank.CLASS);
//		map.put("Orbiliomycetes", TaxonomyRank.CLASS);
//		map.put("Nectonematoida", TaxonomyRank.CLASS);
//		map.put("Sipunculidea", TaxonomyRank.CLASS);
//		map.put("Phascolosomatidea", TaxonomyRank.CLASS);
//		map.put("Lichinomycetes", TaxonomyRank.CLASS);
//		map.put("Monoplacophora", TaxonomyRank.CLASS);
//		map.put("Oedipodiopsida", TaxonomyRank.CLASS);
//		map.put("Tetraphidopsida", TaxonomyRank.CLASS);
//		map.put("Wallemiomycetes", TaxonomyRank.CLASS);
//		map.put("Geoglossomycetes", TaxonomyRank.CLASS);
//		map.put("Gnetopsida", TaxonomyRank.CLASS);
//		map.put("Liliopsida", TaxonomyRank.CLASS);
//		map.put("Polychaeta (polychaetes)", TaxonomyRank.CLASS);
//		map.put("Branchiopoda", TaxonomyRank.CLASS);
//		map.put("Ostracoda (mussel shrimps)", TaxonomyRank.CLASS);
//		map.put("Malacostraca", TaxonomyRank.CLASS);
//		map.put("Merostomata (horseshoe crabs)", TaxonomyRank.CLASS);
//		map.put("Arachnida", TaxonomyRank.CLASS);
//		map.put("Chilopoda (centipedes)", TaxonomyRank.CLASS);
//		map.put("Diplopoda (millipedes)", TaxonomyRank.CLASS);
//		map.put("Ophiuroidea (brittle stars)", TaxonomyRank.CLASS);
//		map.put("Holothuroidea (sea cucumbers)", TaxonomyRank.CLASS);
//		map.put("Ascidiacea", TaxonomyRank.CLASS);
//		map.put("Chondrichthyes", TaxonomyRank.CLASS);
//		map.put("Diplura (diplurans)", TaxonomyRank.CLASS);
//		map.put("Appendicularia (appendicularians)", TaxonomyRank.CLASS);
//		map.put("Thaliacea", TaxonomyRank.CLASS);
//		map.put("Crinoidea (crinoids)", TaxonomyRank.CLASS);
//		map.put("Eutardigrada", TaxonomyRank.CLASS);
//		map.put("Insecta", TaxonomyRank.CLASS);
//		map.put("Hirudinida (leeches)", TaxonomyRank.CLASS);
//		map.put("Pycnogonida (sea spiders)", TaxonomyRank.CLASS);
//		map.put("Coniferopsida", TaxonomyRank.CLASS);
//		map.put("Maxillopoda", TaxonomyRank.CLASS);
//		map.put("Cephalocarida (horseshoe shrimps)", TaxonomyRank.CLASS);
//		map.put("Remipedia", TaxonomyRank.CLASS);
//		map.put("Heterotardigrada", TaxonomyRank.CLASS);
//		map.put("Lingulata", TaxonomyRank.CLASS);
//		map.put("Craniata", TaxonomyRank.CLASS);
//		map.put("Rhynchonellata", TaxonomyRank.CLASS);
//		map.put("Arthoniomycetes", TaxonomyRank.CLASS);
//		map.put("Dothideomycetes", TaxonomyRank.CLASS);
//		map.put("Leotiomycetes", TaxonomyRank.CLASS);
//		map.put("Sordariomycetes", TaxonomyRank.CLASS);
//		map.put("Branchiobdellae", TaxonomyRank.CLASS);
//		map.put("Laboulbeniomycetes", TaxonomyRank.CLASS);
//		map.put("Actinopterygii", TaxonomyRank.CLASS);
//		map.put("Amphibia", TaxonomyRank.CLASS);
//		map.put("Mammalia", TaxonomyRank.CLASS);
//		map.put("Aves", TaxonomyRank.CLASS);
//		map.put("Neolectomycetes", TaxonomyRank.CLASS);
//		map.put("Archaeorhizomycetes", TaxonomyRank.CLASS);
//		map.put("Cycadopsida (cycads)", TaxonomyRank.CLASS);
//		map.put("Ginkgoopsida", TaxonomyRank.CLASS);
//		map.put("Ellipura", TaxonomyRank.CLASS);
//
//		// Phyla
//		map.put("Actinobacteria", TaxonomyRank.PHYLUM);
//		map.put("Aquificae", TaxonomyRank.PHYLUM);
//		map.put("Armatimonadetes", TaxonomyRank.PHYLUM);
//		map.put("Bacteroidetes", TaxonomyRank.PHYLUM);
//		map.put("Chlorobi", TaxonomyRank.PHYLUM);
//		map.put("Ignavibacteria", TaxonomyRank.PHYLUM);
//		map.put("Caldiserica", TaxonomyRank.PHYLUM);
//		map.put("Chlamydiae", TaxonomyRank.PHYLUM);
//		map.put("Lentisphaerae", TaxonomyRank.PHYLUM);
//		map.put("Verrucomicrobia", TaxonomyRank.PHYLUM);
//		map.put("Chloroflexi", TaxonomyRank.PHYLUM);
//		map.put("Chrysiogenetes", TaxonomyRank.PHYLUM);
//		map.put("Cyanobacteria", TaxonomyRank.PHYLUM);
//		map.put("Deferribacteres", TaxonomyRank.PHYLUM);
//		map.put("Deinococcus-Thermus", TaxonomyRank.PHYLUM);
//		map.put("Dictyoglomi", TaxonomyRank.PHYLUM);
//		map.put("Elusimicrobia", TaxonomyRank.PHYLUM);
//		map.put("Acidobacteria", TaxonomyRank.PHYLUM);
//		map.put("Fibrobacteres", TaxonomyRank.PHYLUM);
//		map.put("Firmicutes", TaxonomyRank.PHYLUM);
//		map.put("Fusobacteria", TaxonomyRank.PHYLUM);
//		map.put("Gemmatimonadetes", TaxonomyRank.PHYLUM);
//		map.put("Nitrospirae", TaxonomyRank.PHYLUM);
//		map.put("Planctomycetes", TaxonomyRank.PHYLUM);
//		map.put("Proteobacteria", TaxonomyRank.PHYLUM);
//		map.put("Spirochaetes", TaxonomyRank.PHYLUM);
//		map.put("Synergistetes", TaxonomyRank.PHYLUM);
//		map.put("Tenericutes", TaxonomyRank.PHYLUM);
//		map.put("Thermodesulfobacteria", TaxonomyRank.PHYLUM);
//		map.put("Thermotogae", TaxonomyRank.PHYLUM);
//		map.put("unclassified Bacteria", TaxonomyRank.PHYLUM);
//		map.put("Crenarchaeota", TaxonomyRank.PHYLUM);
//		map.put("Euryarchaeota", TaxonomyRank.PHYLUM);
//		map.put("Korarchaeota", TaxonomyRank.PHYLUM);
//		map.put("Nanoarchaeota", TaxonomyRank.PHYLUM);
//		map.put("Thaumarchaeota", TaxonomyRank.PHYLUM);
//		map.put("unclassified Archaea", TaxonomyRank.PHYLUM);
//		map.put("Amoebozoa", TaxonomyRank.PHYLUM);
//		map.put("Alveolata", TaxonomyRank.PHYLUM);
//		map.put("Apusozoa", TaxonomyRank.PHYLUM);
//		map.put("Centroheliozoa", TaxonomyRank.PHYLUM);
//		map.put("Euglenozoa", TaxonomyRank.PHYLUM);
//		map.put("Fornicata", TaxonomyRank.PHYLUM);
//		map.put("Haptophyceae", TaxonomyRank.PHYLUM);
//		map.put("Jakobida", TaxonomyRank.PHYLUM);
//		map.put("Blastocladiomycota", TaxonomyRank.PHYLUM);
//		map.put("Chytridiomycota", TaxonomyRank.PHYLUM);
//		map.put("Cryptomycota", TaxonomyRank.PHYLUM);
//		map.put("Ascomycota", TaxonomyRank.PHYLUM);
//		map.put("Basidiomycota", TaxonomyRank.PHYLUM);
//		map.put("Fungi incertae sedis", TaxonomyRank.PHYLUM);
//		map.put("Glomeromycota", TaxonomyRank.PHYLUM);
//		map.put("Microsporidia", TaxonomyRank.PHYLUM);
//		map.put("Neocallimastigomycota", TaxonomyRank.PHYLUM);
//		map.put("Platyhelminthes", TaxonomyRank.PHYLUM);
//		map.put("Gnathostomulida", TaxonomyRank.PHYLUM);
//		map.put("Chaetognatha (arrow worms)", TaxonomyRank.PHYLUM);
//		map.put("Chordata", TaxonomyRank.PHYLUM);
//		map.put("Echinodermata", TaxonomyRank.PHYLUM);
//		map.put("Hemichordata (hemichordates)", TaxonomyRank.PHYLUM);
//		map.put("Xenoturbellida", TaxonomyRank.PHYLUM);
//		map.put("Annelida", TaxonomyRank.PHYLUM);
//		map.put("Echiura (spoonworms)", TaxonomyRank.PHYLUM);
//		map.put("Brachiopoda (lampshells)", TaxonomyRank.PHYLUM);
//		map.put("Entoprocta (goblet worms)", TaxonomyRank.PHYLUM);
//		map.put("Mollusca", TaxonomyRank.PHYLUM);
//		map.put("Myzostomida", TaxonomyRank.PHYLUM);
//		map.put("Nemertea (ribbon worms)", TaxonomyRank.PHYLUM);
//		map.put("Arthropoda", TaxonomyRank.PHYLUM);
//		map.put("Onychophora (velvet worms)", TaxonomyRank.PHYLUM);
//		map.put("Tardigrada (water bears)", TaxonomyRank.PHYLUM);
//		map.put("Priapulida (priapulids)", TaxonomyRank.PHYLUM);
//		map.put("Sipuncula (peanut worms) ", TaxonomyRank.PHYLUM);
//		map.put("Acanthocephala (thorny-headed worms)", TaxonomyRank.PHYLUM);
//		map.put("Cycliophora", TaxonomyRank.PHYLUM);
//		map.put("Gastrotricha (gastrotrichs)", TaxonomyRank.PHYLUM);
//		map.put("Kinorhyncha (mud dragons)", TaxonomyRank.PHYLUM);
//		map.put("Loricifera (loriciferans)", TaxonomyRank.PHYLUM);
//		map.put("Micrognathozoa (micrognathozoans)", TaxonomyRank.PHYLUM);
//		map.put("Nematoda (roundworms)", TaxonomyRank.PHYLUM);
//		map.put("Nematomorpha (horsehair worms)", TaxonomyRank.PHYLUM);
//		map.put("Rotifera (rotifers)", TaxonomyRank.PHYLUM);
//		map.put("Cnidaria", TaxonomyRank.PHYLUM);
//		map.put("Ctenophora (ctenophores)", TaxonomyRank.PHYLUM);
//		map.put("Orthonectida", TaxonomyRank.PHYLUM);
//		map.put("Rhombozoa", TaxonomyRank.PHYLUM);
//		map.put("Placozoa (placozoans)", TaxonomyRank.PHYLUM);
//		map.put("Porifera (sponges)", TaxonomyRank.PHYLUM);
//		map.put("Parabasalia (parabasalids)", TaxonomyRank.PHYLUM);
//		map.put("Rhizaria", TaxonomyRank.PHYLUM);
//		map.put("Rhodophyta", TaxonomyRank.PHYLUM);
//		map.put("stramenopiles", TaxonomyRank.PHYLUM);
//		map.put("unclassified eukaryotes", TaxonomyRank.PHYLUM);
//		map.put("Chlorophyta", TaxonomyRank.PHYLUM);
//		map.put("Streptophyta", TaxonomyRank.PHYLUM);

		TAXONOMY_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * The UniProt keyword ontology map.
	 */
	public static final Map<String, KeywordOntology> ONTOLOGY_MAP;
	static {
		Map<String, KeywordOntology> map = new HashMap<String, KeywordOntology>();

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

		// Biological processes
		map.put("Abscisic acid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Abscisic acid signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Acetoin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Acetoin catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Acute phase", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Alginate biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Alkaloid metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Alkylphosphonate uptake", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Amino-acid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Angiogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Antibiotic biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Antibiotic resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Antiviral defense", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Apoptosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Arginine metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Aromatic hydrocarbons catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Arsenical resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ascorbate biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("ATP synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Autoinducer synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Autophagy", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Auxin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Auxin signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("B-cell activation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Bacterial flagellum biogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Bacteriocin immunity", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Behavior", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Biological rhythms", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Biomineralization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Biotin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Branched-chain amino acid catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Brassinosteroid signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cadmium resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Calvin cycle", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("cAMP biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cap snatching", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Capsid assembly", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Capsid maturation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Capsule biogenesis/degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Carbohydrate metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Carbon dioxide fixation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Carnitine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Carotenoid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Catecholamine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Catecholamine metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cell adhesion", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cell cycle", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cell shape", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cell wall biogenesis/degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cellulose biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("cGMP biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chemotaxis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chlorophyll biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chlorophyll catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chondrogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chromate resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Chromosome partition", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cilium biogenesis/degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Citrate utilization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cobalamin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Coenzyme A biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Coenzyme M biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Collagen degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Competence", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Conjugation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytadherence", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytochrome c-type biogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytokinin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytokinin signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytolysis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Cytosine metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Deoxyribonucleotide synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Dermonecrosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Detoxification", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Diaminopimelate biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Differentiation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Digestion", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA condensation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA damage", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA excision", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA integration", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA packaging", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA recombination", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA replication", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("DNA synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Endocytosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Enterobactin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Erythrocyte maturation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ethylene biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ethylene signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Exocytosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Exopolysaccharide synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Fertilization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Fimbrium biogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Flagellar rotation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Flavonoid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Flight", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Flowering", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Folate biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Fruit ripening", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Galactitol metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Gaseous exchange", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Gastrulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Germination", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Gibberellin signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Gluconate utilization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Gluconeogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glutathione biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glycerol metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glycogen biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glycolate pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glycolysis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Glyoxylate bypass", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("GPI-anchor biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Growth regulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hearing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Heme biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hemolymph clotting", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hemostasis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Herbicide resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hibernation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Histidine metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Host-virus interaction", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hydrogen peroxide", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hypersensitive response elicitation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Hypusine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Immunity", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Inflammatory response", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Virus entry into host cell", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Inositol biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Insecticide resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Interferon antiviral system evasion", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Intron homing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Iron storage", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Isoprene biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Jasmonic acid signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Karyogamy", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Keratinization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lactation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lactose biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lactose metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Leukotriene biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lignin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lignin degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lipid degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lipid metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lipid synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Lipopolysaccharide biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Luminescence", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Maltose metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Mandelate pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Mast cell degranulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Meiosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Melanin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Melatonin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Menaquinone biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Mercuric resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Methanogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Methanol utilization", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Methotrexate resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Mineral balance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Molybdenum cofactor biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("mRNA processing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Myogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Neurogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Neurotransmitter biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Neurotransmitter degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nickel insertion", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nitrate assimilation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nitrogen fixation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nodulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nonsense-mediated mRNA decay", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Notch signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nucleotide biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nucleotide metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Nylon degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("One-carbon metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Osteogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pantothenate biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pentose shunt", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Peptidoglycan synthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Peroxisome biogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("PHA biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phage maturation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phagocytosis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("PHB biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phenylalanine catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phenylpropanoid metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pheromone response", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phospholipid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phospholipid degradation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phosphotransferase system", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Photorespiration", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Photosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Phytochrome signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Plant defense", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Plasmid copy control", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Plasmid partition", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Plasminogen activation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Polyamine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Porphyrin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("PQQ biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pregnancy", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Proline metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Protein biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Purine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Purine metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Purine salvage", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Putrescine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pyridine nucleotide biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pyridoxine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Pyrimidine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Queuosine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Quinate metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Quorum sensing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Restriction system", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Rhamnose metabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Riboflavin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ribosome biogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("RNA-mediated gene silencing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("RNA repair", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Viral RNA replication", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("rRNA processing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Self-incompatibility", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Sensory transduction", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Serotonin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Spermidine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Sporulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Starch biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Steroidogenesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Stress response", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Sulfate respiration", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Syncytium formation induced by viral infection", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Taxol biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Teichoic acid biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tellurium resistance", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Terminal addition", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tetrahydrobiopterin biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Thiamine biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Thiamine catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Thyroid hormones biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tissue remodeling", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Transcription", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Translation regulation", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Transport", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Transposition", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tricarboxylic acid cycle", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("tRNA processing", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tryptophan catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Two-component regulatory system", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Tyrosine catabolism", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ubiquinone biosynthesis", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Ubl conjugation pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Unfolded protein response", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Urea cycle", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Viral DNA replication", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Viral primary envelope fusion with host outer nuclear membrane", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Viral transcription", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Virulence", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Virus exit from host cell", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Wnt signaling pathway", KeywordOntology.BIOLOGICAL_PROCESS);
		map.put("Xylan degradation", KeywordOntology.BIOLOGICAL_PROCESS);

		// Cellular components
		map.put("Amyloid", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Antenna complex", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Apoplast", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Archaeal flagellum", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Bacterial flagellum", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Cell junction", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Cell projection", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Centromere", KeywordOntology.CELLULAR_COMPONENT);
		map.put("CF(0)", KeywordOntology.CELLULAR_COMPONENT);
		map.put("CF(1)", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Chlorosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Chromosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Chylomicron", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Copulatory plug", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Cuticle", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Cytoplasm", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Cytoplasmic vesicle", KeywordOntology.CELLULAR_COMPONENT);
		map.put("DNA-directed RNA polymerase", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Dynein", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Endoplasmic reticulum", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Endosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Exosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Fimbrium", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Glycosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Glyoxysome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Golgi apparatus", KeywordOntology.CELLULAR_COMPONENT);
		map.put("HDL", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host cell junction", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host cell projection", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host cytoplasm", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host cytoplasmic vesicle", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host endoplasmic reticulum", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host endosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host Golgi apparatus", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host lipid droplet", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host lysosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host mitochondrion", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host nucleus", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host periplasm", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Host thylakoid", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Hydrogenosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Intermediate filament", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Keratin", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Kinetochore", KeywordOntology.CELLULAR_COMPONENT);
		map.put("LDL", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Lipid droplet", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Lysosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Membrane", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Membrane attack complex", KeywordOntology.CELLULAR_COMPONENT);
		map.put("MHC I", KeywordOntology.CELLULAR_COMPONENT);
		map.put("MHC II", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Microtubule", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Mitochondrion", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Mitosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Nematocyst", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Nucleomorph", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Nucleus", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Periplasm", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Peroxisome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Photosystem I", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Photosystem II", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Phycobilisome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Plastid", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Primosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Proteasome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Reaction center", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Sarcoplasmic reticulum", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Secreted", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Signal recognition particle", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Signalosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Spliceosome", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Thick filament", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Thylakoid", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Vacuole", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Viral occlusion body", KeywordOntology.CELLULAR_COMPONENT);
		map.put("Virion", KeywordOntology.CELLULAR_COMPONENT);
		map.put("VLDL", KeywordOntology.CELLULAR_COMPONENT);

		ONTOLOGY_MAP = Collections.unmodifiableMap(map);
	}
}
