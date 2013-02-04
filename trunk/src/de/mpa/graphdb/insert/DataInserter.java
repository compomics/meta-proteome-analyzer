package de.mpa.graphdb.insert;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.io.GraphMLHandler;
import de.mpa.graphdb.properties.EdgeProperty;
import de.mpa.graphdb.properties.EnzymeProperty;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.PathwayProperty;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.properties.SpeciesProperty;

/**
 * DataInserter provides the minimum of needed fields and methods for graph database insertions.
 * @author Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 *
 */
public class DataInserter extends AbstractDataInserter implements Inserter {
	
	private Index<Vertex> proteinIndex;
	private Index<Vertex> peptideIndex;
	private Index<Vertex> psmIndex;
	private Index<Vertex> speciesIndex;
	private Index<Vertex> enzymeIndex;
	private Index<Vertex> pathwayIndex;
	private Index<Vertex> ontologyIndex;
	private Index<Edge> edgeIndex;
	
	public DataInserter(GraphDatabaseService graphDb) {
		super(graphDb);
		setupIndices();
		
	}
	
	/**
	 * TODO: Move this function to the data accessor class!
	 */
	public void exportGraph(File outputFile) {
		GraphMLHandler.exportGraphML(graph, outputFile);
	}
	
	/**
	 * Sets the data object.
	 */
	@Override
	public void setData(Object data) {
		this.data = data;
	}
	
	
	@Override
	public void insert() {
		if(data instanceof DbSearchResult) {
			DbSearchResult dbSearchResult = (DbSearchResult) data;
			List<ProteinHit> proteinHits = dbSearchResult.getProteinHitList();
			for (ProteinHit protHit : proteinHits) {
				addProtein(protHit);
			}
		}
		
		// Stop the transaction
		stop();
	}
	
	/**
	 * Adds a protein to the graph as vertex.
	 * @param protHit Protein hit.
	 */
	private void addProtein(ProteinHit protHit) {
		Vertex proteinVertex =  graph.addVertex(null);
		
		String accession = protHit.getAccession();
		
		proteinVertex.setProperty(ProteinProperty.ACCESSION.name(), accession);
		proteinVertex.setProperty(ProteinProperty.DESCRIPTION.name(), protHit.getDescription());
		proteinVertex.setProperty(ProteinProperty.PROTEINSEQUENCE.name(), protHit.getSequence());
		proteinVertex.setProperty(ProteinProperty.LENGTH.name(), protHit.getSequence().length());
		proteinVertex.setProperty(ProteinProperty.COVERAGE.name(), protHit.getCoverage());
		
		// Index the proteins by their accession.
		proteinIndex.put(ProteinProperty.ACCESSION.name(), accession, proteinVertex);
		
		// Add peptides.
		addPeptides(protHit.getPeptideHitList(), proteinVertex);
		
		// Add species.
		addSpecies(protHit.getSpecies(), proteinVertex);
		
		// Add enzyme numbers.
		UniProtEntry uniprotEntry = protHit.getUniprotEntry();
		// TODO: Null check valid here ?
		if(uniprotEntry != null) {
			
			List<String> ecNumbers = uniprotEntry.getProteinDescription().getEcNumbers();
			addEnzymeNumbers(ecNumbers, proteinVertex);
			
			// Add pathways. 
			List<DatabaseCrossReference> xRefs = uniprotEntry.getDatabaseCrossReferences(DatabaseType.KO);
			addPathways(xRefs, proteinVertex);
			
			// Add ontologies.
			addOntologies(protHit, proteinVertex);
		}
	}
	
	/**
	 * Adds a species to the graph as vertex.
	 * @param species Species to be added.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addSpecies(String species, Vertex proteinVertex) {
		Vertex speciesVertex = null;
		// Check if peptide is already contained in the graph.
		// TODO: Add more information on the species.
		Iterator<Vertex> speciesIterator = speciesIndex.get(SpeciesProperty.NAME.name(), species).iterator();
		if(speciesIterator.hasNext()) {
			speciesVertex = speciesIterator.next();
		} else {
			// Create new vertex.
			speciesVertex = graph.addVertex(null);
			speciesVertex.setProperty(SpeciesProperty.NAME.name(), species);
			// Index the species by the species name.
			speciesIndex.put(SpeciesProperty.NAME.name(), species, speciesVertex);
		}
		// Add edge between protein and species.
		addEdge(proteinVertex, speciesVertex, RelationType.BELONGS_TO);
	}
	
	/**
	 * Adds an enzyme number to the graph as vertex.
	 * @param ecNumber Enzyme number to be added.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addEnzymeNumbers(List<String> ecNumbers, Vertex proteinVertex) {
		Vertex enzymeVertex = null;
		for (String ecNumber : ecNumbers) {
			Iterator<Vertex> vertexIterator = enzymeIndex.get(EnzymeProperty.ECNUMBER.name(), ecNumber).iterator();
			if(vertexIterator.hasNext()) {
				enzymeVertex = vertexIterator.next();
			} else {
				// Create new vertex.
				enzymeVertex = graph.addVertex(null);
				enzymeVertex.setProperty(EnzymeProperty.ECNUMBER.name(), ecNumber);
				// Index the enzyme by the EC Number.
				enzymeIndex.put(EnzymeProperty.ECNUMBER.name(), ecNumber, enzymeVertex);
			}
			// Add edge between protein and enzyme number.
			addEdge(proteinVertex, enzymeVertex, RelationType.BELONGS_TO_ENZYME);
		}
	}
	
	/**
	 * Adds KEGG pathways to the database.
	 * @param xRefs List of database cross references.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addPathways(List<DatabaseCrossReference> xRefs, Vertex proteinVertex) {
		Vertex pathwayVertex = null;
		for (DatabaseCrossReference xRef : xRefs) {
			String koNumber = ((KO) xRef).getKOIdentifier().getValue();
			Iterator<Vertex> pathwayIterator = pathwayIndex.get(PathwayProperty.KONUMBER.name(), koNumber).iterator();
			if(pathwayIterator.hasNext()) {
				pathwayVertex = pathwayIterator.next();
			} else {
				// Create new vertex.
				pathwayVertex = graph.addVertex(null);
				pathwayVertex.setProperty(PathwayProperty.KONUMBER.name(), koNumber);
				//TODO: Add KEGG description + KEGG Number!

				// Index the pathway by the ID.
				pathwayIndex.put(PathwayProperty.KONUMBER.name(), koNumber, pathwayVertex);
			}
			// Add edge between protein and pathway.
			addEdge(proteinVertex, pathwayVertex, RelationType.BELONGS_TO_PATHWAY);
		}
	}
	
	/**
	 * Adds ontologies to the graph database.
	 * @param protHit
	 * @param proteinVertex
	 */
	private void addOntologies(ProteinHit protHit, Vertex proteinVertex) {
		Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
		UniProtEntry entry = protHit.getUniprotEntry();
		Vertex ontologyVertex = null;
		
		// Entry must be provided
		if (entry != null) {
			List<Keyword> keywords = entry.getKeywords();
			for (Keyword kw : keywords) {
				String keyword = kw.getValue();
				if (ontologyMap.containsKey(keyword)) {
					KeywordOntology type = ontologyMap.get(keyword);
					
					// Check if peptide is already contained in the graph.
					Iterator<Vertex> ontologyIterator = ontologyIndex.get(OntologyProperty.KEYWORD.name(), keyword).iterator();
					if(ontologyIterator.hasNext()) {
						ontologyVertex = ontologyIterator.next();
					} else {
						// Create new vertex.
						ontologyVertex = graph.addVertex(null);
						ontologyVertex.setProperty(OntologyProperty.KEYWORD.name(), keyword);
						ontologyVertex.setProperty(OntologyProperty.TYPE.name(), type.name());
						
						// Index the proteins by their accession.
						ontologyIndex.put(OntologyProperty.KEYWORD.name(), keyword, ontologyVertex);
					}			
					
					// Add edge between protein and ontology.
					switch (type) {
					case BIOLOGICAL_PROCESS:
						addEdge(proteinVertex, ontologyVertex, RelationType.INVOLVED_IN_BIOPROCESS);
						break;
					case CELLULAR_COMPONENT:
						addEdge(proteinVertex, ontologyVertex, RelationType.BELONGS_TO_COMPONENT);
						break;
					case MOLECULAR_FUNCTION:
						addEdge(proteinVertex, ontologyVertex, RelationType.HAS_MOLECULAR_FUNCTION);
						break;
					}
					
				}
			}
		}
	}
	
	/**
	 * Adds peptides to the graph.
	 * @param peptideHitList The peptide list
	 */
	private void addPeptides(List<PeptideHit> peptideHitList, Vertex proteinVertex) {
		for (PeptideHit peptideHit : peptideHitList) {
			String sequence = peptideHit.getSequence();
			Vertex peptideVertex = null;
			// Check if peptide is already contained in the graph.
			Iterator<Vertex> peptideIterator = peptideIndex.get(PeptideProperty.SEQUENCE.name(), sequence).iterator();
			if(peptideIterator.hasNext()) {
				peptideVertex = peptideIterator.next();
			} else {
				// Create new vertex.
				peptideVertex = graph.addVertex(null);
				peptideVertex.setProperty(PeptideProperty.SEQUENCE.name(), sequence);
				peptideVertex.setProperty(PeptideProperty.LENGTH.name(), sequence.length());
				
				// Index the proteins by their accession.
				peptideIndex.put(PeptideProperty.SEQUENCE.name(), sequence, peptideVertex);
			}			
			
			// Add edge between peptide and protein.
			addEdge(proteinVertex, peptideVertex, RelationType.HAS_PEPTIDE);
		
			// Add PSMs.
			addPeptideSpectrumMatches(peptideHit.getSpectrumMatches(), peptideVertex);
		}
	}
	
	/**
	 * Adds PSMs to the graph.
	 * @param peptideHitList The peptide list
	 */
	private void addPeptideSpectrumMatches(List<SpectrumMatch> spectrumMatches, Vertex peptideVertex) {
		for (SpectrumMatch sm : spectrumMatches) {
			Vertex psmVertex =  null;
			PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
			long spectrumID = psm.getSearchSpectrumID();
			
			// Check if PSM is already contained in the graph.
			Iterator<Vertex> psmIterator = psmIndex.get(PsmProperty.SPECTRUMID.name(), spectrumID).iterator();
			if(psmIterator.hasNext()) {
				psmVertex = psmIterator.next();
			} else {
				// Create new vertex.
				psmVertex = graph.addVertex(null);
				//TODO: Use spectrum title as property too!
				psmVertex.setProperty(PsmProperty.SPECTRUMID.name(), spectrumID);
				psmVertex.setProperty(PsmProperty.VOTES.name(), psm.getVotes());
				
				// Index the proteins by their accession.
				psmIndex.put(PsmProperty.SPECTRUMID.name(), spectrumID, psmVertex);
			}	
			addEdge(psmVertex, peptideVertex, RelationType.IS_MATCH_IN);
		}
	}
	
	/**
	 * Adds an edge to the graph and creates a unique ID from both involved vertices.
	 * Also checks whether edge is already existing in the graph.
	 * @param outVertex The outgoing vertex.
	 * @param inVertex The incoming vertex.
	 * @param label The edge label.
	 */
	private void addEdge(final Vertex outVertex, final Vertex inVertex, final RelationType relType) {
		
		String id = outVertex.getId()+ "_" + inVertex.getId();
		// Check if edge is not already contained in the graph.
		if(!edgeIndex.get(EdgeProperty.ID.name(), id).iterator().hasNext()) {
			Edge edge = graph.addEdge(id, outVertex, inVertex, relType.name());
			edge.setProperty(EdgeProperty.LABEL.name(), relType.name());
			edgeIndex.put(EdgeProperty.ID.name(), id, edge);
		} 
	}
	
	@Override
	public void setupIndices() {
		// Protein index
		proteinIndex = indexGraph.getIndex("proteins", Vertex.class);
		
		// If not existent, create a new index.
		if (proteinIndex == null) {
			proteinIndex = indexGraph.createIndex("proteins", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Peptide Index
		peptideIndex = indexGraph.getIndex("peptides", Vertex.class);
		// If not existent, create a new index.
		if (peptideIndex == null) {
			peptideIndex = indexGraph.createIndex("peptides", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// PSM index
		psmIndex = indexGraph.getIndex("psms", Vertex.class);
		// If not existent, create a new index.
		if (psmIndex == null) {
			psmIndex = indexGraph.createIndex("psms", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Species index
		speciesIndex = indexGraph.getIndex("species", Vertex.class);
		// If not existent, create a new index.
		if (speciesIndex == null) {
			speciesIndex = indexGraph.createIndex("species", Vertex.class);			
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Enzyme index
		enzymeIndex = indexGraph.getIndex("enzymes", Vertex.class);
		// If not existent, create a new index.
		if (enzymeIndex == null) {
			enzymeIndex = indexGraph.createIndex("enzymes", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Pathway index
		pathwayIndex = indexGraph.getIndex("pathways", Vertex.class);
		// If not existent, create a new index.
		if (pathwayIndex == null) {
			pathwayIndex = indexGraph.createIndex("pathways", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Ontology index
		ontologyIndex = indexGraph.getIndex("ontologies", Vertex.class);
		// If not existent, create a new index.
		if (ontologyIndex == null) {
			ontologyIndex = indexGraph.createIndex("ontologies", Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Edge index
		edgeIndex = indexGraph.getIndex("edges", Edge.class);
		// If not existent, create a new index.
		if (edgeIndex == null) {
			edgeIndex = indexGraph.createIndex("edges", Edge.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
	}

	@Override
	public int[] getDefaultIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultDelimiter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultIndexes(int[] indexes) {
		
		// TODO Auto-generated method stub
	}

	@Override
	public void setDefaultDelimiter(String delimiter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		graph.stopTransaction(Conclusion.SUCCESS);
		((TransactionalGraph)indexGraph).stopTransaction(Conclusion.SUCCESS);
		
	}
	
	
}
