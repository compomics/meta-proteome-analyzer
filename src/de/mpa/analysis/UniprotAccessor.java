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
		
		// Biological processeses
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
		return map;
	}
}
