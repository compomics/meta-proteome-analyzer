package de.mpa.graphdb.access;

import static org.neo4j.helpers.collection.MapUtil.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import de.mpa.graphdb.properties.ElementProperty;

/**
 * Query class using the Cypher query language.
 * @author Thilo Muth
 *
 */
public class CypherQuery {

	/**
	 * Execution engine.
	 */
    private final ExecutionEngine engine;
    
    /**
     * Construct the CypherQuery, providing method to do user-specific queries against the graph database.
     * @param graphDb GraphDatabaseService object
     */
    public CypherQuery(GraphDatabaseService graphDb) {
        engine = new ExecutionEngine(graphDb);
    }
    
    /**
     * Executes a general Cypher query and returns ExecutionResult object.
     * @param query Query string (in cypher query language)
     * @return ExecutionResult
     */
    public ExecutionResult execute(String query) {
    	return engine.execute(query);
    }
    
    /**
     * Returns the first inserted node of the graph.
     * @return First node ExecutionResult 
     */
    public ExecutionResult getFirstNode() {
        return engine.execute( "start n=node(1) return n" );
	}
	
    /**
     * Returns the protein by its accession.
     * @param accession Protein accession
     * @return Protein node ExecutionResult
     */
	public ExecutionResult getProteinByAccession(String accession) {
        return engine.execute("START protein=node:proteins(ACCESSION = {accession}) return protein", map("accession", accession));
	}
	
    /**
     * Returns the protein by its sequence.
     * @param accession Protein sequence
     * @return Protein node ExecutionResult
     */
	public ExecutionResult getProteinBySequence(String sequence) {
		return engine.execute("START protein=node:proteins(PROTEINSEQUENCE = {sequence}) return protein", map("sequence", sequence));
	}
	
	/**
	 * Returns all peptides for a protein (specified by its accession).
	 * @param accession Protein accession
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForProtein(String accession) {
		return engine.execute("START protein=node:proteins(ACCESSION = {accession}) " +
                "MATCH (protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("accession", accession));
	}
	
	/**
	 * Returns all unique peptides of the dataset.
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getAllUniquePeptides() {
		return engine.execute("START peptide=node(*) " +
				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein) " + 
				"WITH peptide, count(rel) as cn " +
				"WHERE cn = 1 " + 
				"RETURN peptide");
	}
	
	/**
	 * Returns all shared peptides of the dataset.
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getAllSharedPeptides() {
		return engine.execute("START peptide=node(*) " +
				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein) " + 
				"WITH peptide, count(rel) as cn " +
				"WHERE cn > 1 " + 
				"RETURN peptide");
	}
	
	/**
	 * Returns the unique peptides for a protein (specified by its accession).
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getUniquePeptidesForProtein(String accession) {
		return engine.execute("START protein=node:proteins(ACCESSION = {accession}) " +
				"MATCH (protein)-[:HAS_PEPTIDE]->(peptide) " +
				"WITH peptide " +
				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein2) " +
				"WITH peptide, count(rel) as cn " + 
				"WHERE cn = 1 " + 
				"RETURN peptide", map("accession", accession));
	}
	
	/**
	 * Returns the shared peptides for a protein (specified by its accession).
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getSharedPeptidesForProtein(String accession) {
		return engine.execute("START protein=node:proteins(ACCESSION = {accession}) " +
				"MATCH (protein)-[:HAS_PEPTIDE]->(peptide) " +
				"WITH peptide " +
				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein2) " +
				"WITH peptide, count(rel) as cn " + 
				"WHERE cn > 1 " + 
				"RETURN peptide", map("accession", accession));
	}
	
	/**
	 * Returns all proteins for a peptide (specified by its sequence).	 * 
	 * @param sequence Peptide sequence
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForPeptide(String sequence) {
		return engine.execute("START peptide=node:peptides(SEQUENCE = {sequence}) " +
                "MATCH (peptide)<-[:HAS_PEPTIDE]-(protein) " +
                "RETURN protein", map("sequence", sequence));
	}
	
	/**
	 * Returns all proteins for species (specified by species name). 
	 * @param species Species name
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForSpecies(String species) {
		return engine.execute("START species=node:species(NAME = {species}) " +
                "MATCH (species)<-[:BELONGS_TO]-(protein) " +
                "RETURN protein", map("species", species));
	}
	
	/**
	 * Returns all proteins for an enzyme (specified by its E.C. number). 
	 * @param ecNumber Enzyme E.C. number
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForEnzyme(String ecNumber) {
		return engine.execute("START enzyme=node:enzymes(ECNUMBER = {ecnumber}) " +
                "MATCH (enzyme)<-[:BELONGS_TO_ENZYME]-(protein) " +
                "RETURN protein", map("ecnumber", ecNumber));
	}
	
	/**
	 * Returns all proteins for a pathway (specified by its KO number). 
	 * @param koNumber Pathway KO number
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForPathway(String koNumber) {
		return engine.execute("START pathway=node:pathways(KONUMBER = {konumber}) " +
                "MATCH (pathway)<-[:BELONGS_TO_PATHWAY]-(protein) " +
                "RETURN protein", map("konumber", koNumber));
	}
	
	/**
	 * Returns all proteins for biological process (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForBiologicalProcess(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:INVOLVED_IN_BIOPROCESS]-(protein) " +
                "RETURN protein", map("keyword", keyword));
	}
	
	/**
	 * Returns all proteins for molecular function (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForMolecularFunction(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:HAS_MOLECULAR_FUNCTION]-(protein) " +
                "RETURN protein", map("keyword", keyword));
	}
	
	/**
	 * Returns all proteins for cellular component (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Protein node(s) ExecutionResult
	 */
	public ExecutionResult getProteinsForCellularComponent(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:BELONGS_TO_COMPONENT]-(protein) " +
                "RETURN protein", map("keyword", keyword));
	}
	
	/**
	 * Returns all peptides for species (specified by species name). 
	 * @param species Species name
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForSpecies(String species) {
		return engine.execute("START species=node:species(NAME = {species}) " +
                "MATCH (species)<-[:BELONGS_TO]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("species", species));
	}
	
	/**
	 * Returns all peptides for an enzyme (specified by its E.C. number). 
	 * @param ecNumber Enzyme E.C. number
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForEnzyme(String ecNumber) {
		return engine.execute("START enzyme=node:enzymes(ECNUMBER = {ecnumber}) " +
                "MATCH (enzyme)<-[:BELONGS_TO_ENZYME]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("ecnumber", ecNumber));
	}
	
	/**
	 * Returns all peptides for a pathway (specified by its KO number). 
	 * @param koNumber Pathway KO number
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForPathway(String koNumber) {
		return engine.execute("START pathway=node:pathways(KONUMBER = {konumber}) " +
                "MATCH (pathway)<-[:BELONGS_TO_PATHWAY]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("konumber", koNumber));
	}
	
	/**
	 * Returns all peptides for biological process (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForBiologicalProcess(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:INVOLVED_IN_BIOPROCESS]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("keyword", keyword));
	}
	
	/**
	 * Returns all peptides for molecular function (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForMolecularFunction(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:HAS_MOLECULAR_FUNCTION]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("keyword", keyword));
	}
	
	/**
	 * Returns all peptides for cellular component (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return Peptide node(s) ExecutionResult
	 */
	public ExecutionResult getPeptidesForCellularComponent(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:BELONGS_TO_COMPONENT]-(protein)-[:HAS_PEPTIDE]->(peptide) " +
                "RETURN peptide", map("keyword", keyword));
	}
	
	/**
	 * Returns all PSMS for a peptide (specified by its sequence):
	 * peptide<-IS_MATCH_IN-psm
	 * @param sequence Peptide sequence
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForPeptide(String sequence) {
		return engine.execute("START peptide=node:peptides(SEQUENCE = {sequence}) " +
                "MATCH (peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("sequence", sequence));
	}
	
	/**
	 * Returns all PSMS for a protein (specified by its accession):
	 * Protein-HAS_PEPTIDE->Peptide<-IS_MATCH_IN-Psm
	 * @param accession Protein accession
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForProtein(String accession) {
		return engine.execute("START protein=node:proteins(ACCESSION = {accession}) " +
                "MATCH (protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("accession", accession));
	}
	
	/**
	 * Returns all PSMS for an enzyme (specified by its E.C. number):
	 * Enzyme<--Protein-->Peptide<--PSM
	 * @param ecNumber Enzyme number
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForEnzyme(String ecNumber) {
		return engine.execute("START enzyme=node:enzymes(ECNUMBER = {ecNumber}) " +
                "MATCH (enzyme)<-[:BELONGS_TO_ENZYME]-(protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("ecNumber", ecNumber));
	}
	
	/**
	 * Returns all PSMS for a pathway (specified by its KO number):
	 * Pathway<--Protein-->Peptide<--PSM
	 * @param koNumber KEGG Orthology number
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForPathway(String koNumber) {
		return engine.execute("START pathway=node:pathways(KONUMBER = {koNumber}) " +
                "MATCH (pathway)<-[:BELONGS_TO_PATHWAY]-(protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("koNumber", koNumber));
	}
	
	/**
	 * Returns all PSMs for biological process (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForBiologicalProcess(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:INVOLVED_IN_BIOPROCESS]-(protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("keyword", keyword));
	}
	
	/**
	 * Returns all PSMs for molecular function (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForMolecularFunction(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:HAS_MOLECULAR_FUNCTION]-(protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("keyword", keyword));
	}
	
	/**
	 * Returns all PSMs for cellular component (specified by keyword). 
	 * @param keyword Ontology keyword
	 * @return PSM node(s) ExecutionResult
	 */
	public ExecutionResult getPSMsForCellularComponent(String keyword) {
		return engine.execute("START ontology=node:ontologies(KEYWORD = {keyword}) " +
                "MATCH (ontology)<-[:BELONGS_TO_COMPONENT]-(protein)-[:HAS_PEPTIDE]->(peptide)<-[:IS_MATCH_IN]-(psm) " +
                "RETURN psm", map("keyword", keyword));
	}
	
    /**
     * Returns the relationships from the protein. 
     * @return ExecutionResult object
     */
    public ExecutionResult getRelationshipsFromProtein() {
        return engine.execute("START protein=node(1) " +
                              "MATCH (protein)-[rel]->() " +
                              "RETURN rel" );
	}
	
	/**
	 * Returns all nodes in the graph (default limit = 50);
	 * @return Nodes ExecutionResult
	 */
	public ExecutionResult getAllNodes() {
        return engine.execute( "start n=node(*) return n limit 50" );
	}
	
	/**
	 * Range limit method.
	 * @param from Start of range index
	 * @param to End of range index
	 * @return Resultant range collection.
	 */
    public static Collection<Long> limitRange(int from, int to) {
        final Collection<Long> result = new ArrayList<Long>(to - from + 1);
        for (int i=from;i<=to;i++) result.add((long)i);
        return result;
    }
    
    /**
	 * Retrieves a unique node set from the ExecutionResult object for a certain property.
	 * @param executionResult ExecutionResult object
	 * @param columnName Column name
	 * @param property ElementProperty object
	 * @return Unique node set
	 */
	public static Set<Node> retrieveNodeSet(ExecutionResult executionResult, String columnName, ElementProperty property) {
		Iterator<Object> columnAs = executionResult.columnAs(columnName);
		Set<Node> nodeSet = new HashSet<Node>();
		
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                if(n.hasProperty(property.toString().toUpperCase())){
                	nodeSet.add(n);
                }
            }
        }
        return nodeSet;
	}
	
    /**
     * Convenience method for ExecutionResult output.
     * @param msg Message string
     * @param result ExecutionResult object
     * @param column Column name
     */
    public static void printResult(String msg, ExecutionResult result, String column) {
        System.out.println(msg);
        Iterator<Object> columnAs = result.columnAs(column);
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                for (String key : n.getPropertyKeys()) {
                    System.out.println("{ " + key + " : " + n.getProperty(key)	+ "; id: " + n.getId() + " } ");
                }
            } else {
                System.out.println("{ " + column + " : " + value + " } ");
            }
        }
    }
    
    /**
     * Convenience method for relationship output.
     * @param msg Message string
     * @param result ExecutionResult object
     * @param column Column name
     */
    public static void printRelationship(String msg, ExecutionResult result, String column) {
        System.out.println(msg);
        Iterator<Object> columnAs = result.columnAs(column);
        while (columnAs.hasNext()) {
            Relationship n = (Relationship) columnAs.next();
            for (String key : n.getPropertyKeys()) {
                System.out.println("{ " + key + " : " + n.getProperty(key)	+ " } ");
            }
        }
    }
}
