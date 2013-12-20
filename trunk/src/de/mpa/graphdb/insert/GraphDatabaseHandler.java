package de.mpa.graphdb.insert;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.KeywordOntology;
import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.ClientFrame;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.io.GraphMLHandler;
import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.EdgeProperty;
import de.mpa.graphdb.properties.EnzymeProperty;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.PathwayProperty;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.properties.TaxonProperty;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.taxonomy.TaxonomyNode;

/**
 * GraphDatabaseHandler provides possibilities to insert data into the graph database.
 * 
 * @author Thilo Muth
 * @date 2013-04-17
 * @version 0.7.0
 *
 */
public class GraphDatabaseHandler {
	
	/**
	 *  Data to be inserted in the graph.
	 */
	private Object data;
	
	/**
	 * By default, the first operation on a TransactionalGraph will start a transaction automatically.
	 */
	private TransactionalGraph graph;
	
	/**
	 *  An IndexableGraph is a graph that supports the manual indexing of its elements.
	 */
	private IndexableGraph indexGraph;
	
	/**
	 * GraphDatabase object.
	 */
	private GraphDatabase graphDb;
	
	private Index<Vertex> proteinIndex;
	private Index<Vertex> peptideIndex;
	private Index<Vertex> psmIndex;
	private Index<Vertex> taxonomyIndex;
	private Index<Vertex> enzymeIndex;
	private Index<Vertex> pathwayIndex;
	private Index<Vertex> ontologyIndex;
	private Index<Edge> edgeIndex;

	private ExecutionEngine engine;
	
	
	public GraphDatabaseHandler(GraphDatabase graphDb) {
		this.graphDb = graphDb;
		indexGraph = new Neo4jGraph(graphDb.getService());
		graph = new Neo4jGraph(graphDb.getService());		
		setupIndices();
		
	}
	
	/**
	 * Exports the graph to an GraphML output file.
	 */
	public void exportGraph(File outputFile) {
		Client client = Client.getInstance();
		String status = "FINISHED";
		client.firePropertyChange("new message", null, "EXPORTING GRAPHML FILE");
		client.firePropertyChange("indeterminate", false, true);
		try {
			GraphMLHandler.exportGraphML(graph, outputFile);
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		} 
		client.firePropertyChange("indeterminate", true, false);
		client.firePropertyChange("new message", null, "EXPORTING GRAPHML FILE " + status);
	}
	
	/**
	 * Sets the data object.
	 */
	public void setData(Object data) {
		this.data = data;
		insert();
		setupExecutionEngine();
	}
	
	/**
	 * Initializes the execution engine instance.
	 */
	private void setupExecutionEngine() {
		engine = new ExecutionEngine(graphDb.getService());
		
	}

	/**
	 * This method inserts the data into the graph database and stops the transaction afterwards.
	 */
	private void insert() {
		if (data instanceof DbSearchResult) {
			DbSearchResult dbSearchResult = (DbSearchResult) data;
			List<ProteinHit> proteinHits = dbSearchResult.getProteinHitList();
			
			Client client = Client.getInstance();
			client.firePropertyChange("new message", null, "BUILDING GRAPH DATABASE");
			client.firePropertyChange("resetall", -1L, Long.valueOf(proteinHits.size()));
			
			// Add proteinhits
			for (ProteinHit proteinHit : proteinHits) {
				addProtein(proteinHit);
				client.firePropertyChange("progressmade", -1L, 0L);
			}
			
			// Add Meta-Proteins.
			for (ProteinHit proteinHit : dbSearchResult.getMetaProteins()) {
				MetaProteinHit metaProtein = (MetaProteinHit) proteinHit;
				addMetaprotein(metaProtein);
				client.firePropertyChange("progressmade", -1L, 0L);
			}
			
			client.firePropertyChange("new message", null, "BUILDING GRAPH DATABASE FINISHED");
		}
		
		// Stop the transaction
		stopTransaction();
	}
	
	/**
	 * Adds a protein to the graph as vertex.
	 * @param protHit Protein hit.
	 */
	private void addProtein(ProteinHit protHit) {
		Vertex proteinVertex =  graph.addVertex(null);
		
		String accession = protHit.getAccession();
		proteinVertex.setProperty(ProteinProperty.IDENTIFIER.toString(), accession);
		proteinVertex.setProperty(ProteinProperty.DESCRIPTION.toString(), protHit.getDescription());
		proteinVertex.setProperty(ProteinProperty.TAXONOMY.toString(), protHit.getTaxonomyNode().getName());
		proteinVertex.setProperty(ProteinProperty.COVERAGE.toString(), protHit.getCoverage());
		proteinVertex.setProperty(ProteinProperty.SPECTRALCOUNT.toString(), protHit.getSpectralCount());		
		
		// Index the proteins by their accession.
		proteinIndex.put(ProteinProperty.IDENTIFIER.toString(), accession, proteinVertex);
		
		// Add taxonomy.
		addTaxonomy(protHit, proteinVertex);
		
		// Add peptides.
		addPeptides(protHit.getPeptideHitList(), proteinVertex);
		
		// Add enzyme numbers.
		ReducedUniProtEntry uniprotEntry = protHit.getUniProtEntry();
		// Null check is needed, as if no (reduced) UniProt Entry is provided, enzymes, ontologies, pathways and taxonomies can be skipped.
		if (uniprotEntry != null) {
			
			List<String> ecNumbers = uniprotEntry.getEcNumbers();
			addEnzymeNumbers(ecNumbers, proteinVertex);
			
			// Add pathways.			
			List<String> koNumbers = uniprotEntry.getKNumbers();
			addPathways(koNumbers, proteinVertex);
			
			// Add ontologies.
			addOntologies(protHit, proteinVertex);
		}
	}
	
	/**
	 * Adds a meta-protein to the graph as vertex.
	 * @param metaProteinHit
	 */
	private void addMetaprotein(MetaProteinHit metaProteinHit) {
		Vertex metaProteinVertex =  graph.addVertex(null);
		String accession = metaProteinHit.getAccession();
		metaProteinVertex.setProperty(ProteinProperty.IDENTIFIER.toString(), accession);
		metaProteinVertex.setProperty(ProteinProperty.DESCRIPTION.toString(), metaProteinHit.getDescription());
		metaProteinVertex.setProperty(ProteinProperty.TAXONOMY.toString(), metaProteinHit.getTaxonomyNode().getName());
		metaProteinVertex.setProperty(ProteinProperty.COVERAGE.toString(), metaProteinHit.getCoverage());
		metaProteinVertex.setProperty(ProteinProperty.SPECTRALCOUNT.toString(), metaProteinHit.getSpectralCount());
		
		// Index the proteins by their accession.
		proteinIndex.put(ProteinProperty.IDENTIFIER.toString(), accession, metaProteinVertex);
		
		Vertex proteinVertex = null;
		ProteinHitList proteinHits = metaProteinHit.getProteinHits();
		
		for (ProteinHit proteinHit : proteinHits) {
			Iterator<Vertex> proteinIterator = proteinIndex.get(ProteinProperty.IDENTIFIER.toString(), proteinHit.getAccession()).iterator();
			if (proteinIterator.hasNext()) {
				proteinVertex = proteinIterator.next();
			}
			// Add edge between peptide and protein.
			addEdge(metaProteinVertex, proteinVertex, RelationType.IS_METAPROTEIN_OF);
		}
	}
	
	/**
	 * Adds the taxonomic information derived from the protein hit to the graph.
	 * @param proteinHit ProteinHit object with taxonomic information.
	 * @param vertex Involved vertex (outgoing edge).
	 */
	private void addTaxonomy(ProteinHit proteinHit, Vertex vertex) {
		TaxonomyNode childNode = proteinHit.getTaxonomyNode();
		String species = childNode.toString();
		Vertex childVertex = null;
		Iterator<Vertex> taxonomyIterator = taxonomyIndex.get(TaxonProperty.IDENTIFIER.toString(), species).iterator();
		if (taxonomyIterator.hasNext()) {
			childVertex = taxonomyIterator.next();
		} else {
			// Create new vertex.
			childVertex = graph.addVertex(null);
			childVertex.setProperty(TaxonProperty.IDENTIFIER.toString(), species);
			childVertex.setProperty(TaxonProperty.TAXID.toString(), childNode.getId());
			childVertex.setProperty(TaxonProperty.RANK.toString(), childNode.getRank().toString());
			// Index the species by the species name.
			taxonomyIndex.put(TaxonProperty.IDENTIFIER.toString(), species, childVertex);
		}		
		// Add edge between protein and species.
		addEdge(vertex, childVertex, RelationType.BELONGS_TO);
		
		Vertex parentVertex = null;	
		// Add complete taxonomy path.
		TaxonomyNode[] path = childNode.getPath();
		for (TaxonomyNode pathNode : path) {
			String taxon = pathNode.toString();
			Iterator<Vertex> iterator = taxonomyIndex.get(TaxonProperty.IDENTIFIER.toString(), taxon).iterator();
			if (iterator.hasNext()) {
				parentVertex = iterator.next();
			} else {
				// Create new vertex.
				parentVertex = graph.addVertex(null);
				parentVertex.setProperty(TaxonProperty.IDENTIFIER.toString(), taxon);
				parentVertex.setProperty(TaxonProperty.TAXID.toString(), pathNode.getId());
				parentVertex.setProperty(TaxonProperty.RANK.toString(), pathNode.getRank().toString());
				// Index the species by the species name.
				taxonomyIndex.put(TaxonProperty.IDENTIFIER.toString(), taxon, parentVertex);
			}		
			// Add edge between parent and child vertex
			addEdge(parentVertex, childVertex, RelationType.IS_ANCESTOR_OF);
			childVertex = parentVertex;
		}
	}
	
	/**
	 * Adds an enzyme number to the graph as vertex.
	 * @param ecNumber Enzyme number to be added.
	 * @param proteinVertex Involved protein vertex (outgoing edge).
	 */
	private void addEnzymeNumbers(List<String> ecNumbers, Vertex proteinVertex) {
		Vertex enzymeVertex = null;
		for (String ecNumber : ecNumbers) {
			Iterator<Vertex> vertexIterator =
					enzymeIndex.get(EnzymeProperty.IDENTIFIER.toString(), ecNumber).iterator();
			if (vertexIterator.hasNext()) {
				enzymeVertex = vertexIterator.next();
			} else {
				// Split E.C. number string of the format '1.2.3.4'
				String[] ecTokens = ecNumber.split("\\.");
				for (int i = 0; i < ecTokens.length; i++) {
					// Check tokens, abort prematurely if '-' is found
					String ecToken = ecTokens[i];
					if ("-".equals(ecToken)) {
						break;
					}
					// Build partial E.C. number, e.g. '1.2.-.-'
					String ecFragment = ecTokens[0];
					for (int j = 1; j < ecTokens.length; j++) {
						ecFragment += "." + ((j <= i) ? ecTokens[j] : "-");
					}
					// Get existing vertex for (partial) E.C. number...
					Vertex temp = graph.getVertex(ecFragment);
					if (temp == null) {
						// ... or create new vertex.
						temp = graph.addVertex(ecFragment);
						temp.setProperty(EnzymeProperty.IDENTIFIER.toString(), ecFragment);
						// TODO set description property, fetch from intenz.xml (ftp://ftp.ebi.ac.uk/pub/databases/intenz/xml/)
					}
					// Index vertex by E.C. Number.
					enzymeIndex.put(EnzymeProperty.IDENTIFIER.toString(), ecFragment, temp);
					
					// Link enzyme vertices
					if (i > 0) {
						addEdge(enzymeVertex, temp, RelationType.IS_SUPERGROUP_OF);
					}
					
					enzymeVertex = temp;
				}
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
	private void addPathways(List<String> koNumbers, Vertex proteinVertex) {
		Vertex pathwayVertex = null;
		for (String koNumber : koNumbers) {
			Iterator<Vertex> pathwayIterator = pathwayIndex.get(PathwayProperty.IDENTIFIER.toString(), koNumber).iterator();
			if (pathwayIterator.hasNext()) {
				pathwayVertex = pathwayIterator.next();
			} else {
				// Create new vertex.
				pathwayVertex = graph.addVertex(null);
				pathwayVertex.setProperty(PathwayProperty.IDENTIFIER.toString(), koNumber);
				//TODO: Add KEGG description + KEGG Number!
				// Index the pathway by the ID.
				pathwayIndex.put(PathwayProperty.IDENTIFIER.toString(), koNumber, pathwayVertex);
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
		Map<String, KeywordOntology> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		ReducedUniProtEntry entry = protHit.getUniProtEntry();
		Vertex ontologyVertex = null;
		
		// Entry must be provided
		if (entry != null) {
			List<String> keywords = entry.getKeywords();			
			for (String keyword : keywords) {
				if (ontologyMap.containsKey(keyword)) {
					KeywordOntology type = ontologyMap.get(keyword);
					
					// Check if peptide is already contained in the graph.
					Iterator<Vertex> ontologyIterator =
							ontologyIndex.get(OntologyProperty.IDENTIFIER.toString(), keyword).iterator();
					if (ontologyIterator.hasNext()) {
						ontologyVertex = ontologyIterator.next();
					} else {
						// Create new vertex.
						ontologyVertex = graph.addVertex(null);
						ontologyVertex.setProperty(OntologyProperty.IDENTIFIER.toString(), keyword);
						ontologyVertex.setProperty(OntologyProperty.TYPE.toString(), type.toString());
						
						// Index the proteins by their accession.
						ontologyIndex.put(OntologyProperty.IDENTIFIER.toString(), keyword, ontologyVertex);
					}			
					
					// Add edge between protein and ontology.
					switch (type) {
					case BIOLOGICAL_PROCESS:
						addEdge(proteinVertex, ontologyVertex, RelationType.INVOLVED_IN_BIOPROCESS);
						break;
					case CELLULAR_COMPONENT:
						addEdge(proteinVertex, ontologyVertex, RelationType.BELONGS_TO_CELL_COMP);
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
			Iterator<Vertex> peptideIterator = peptideIndex.get(PeptideProperty.IDENTIFIER.toString(), sequence).iterator();
			if (peptideIterator.hasNext()) {
				peptideVertex = peptideIterator.next();
			} else {
				// Create new vertex.
				peptideVertex = graph.addVertex(null);
				peptideVertex.setProperty(PeptideProperty.IDENTIFIER.toString(), sequence);
				peptideVertex.setProperty(PeptideProperty.SPECTRALCOUNT.toString(), peptideHit.getSpectralCount());
				peptideVertex.setProperty(PeptideProperty.SPECIES.toString(), peptideHit.getTaxonomyNode().getName());
				peptideVertex.setProperty(PeptideProperty.PROTEINCOUNT.toString(), peptideHit.getProteinCount());
				
				// Index the proteins by their accession.
				peptideIndex.put(PeptideProperty.IDENTIFIER.toString(), sequence, peptideVertex);
			}			
			
			// Add edge between peptide and protein.
			addEdge(proteinVertex, peptideVertex, RelationType.HAS_PEPTIDE);
		    
			// Connect peptide taxon node name to peptide vertex.
//			addSpecies(peptideHit.getTaxonomyNode().getName(), peptideVertex);
			
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
			Iterator<Vertex> psmIterator = psmIndex.get(PsmProperty.SPECTRUMID.toString(), spectrumID).iterator();
			if (psmIterator.hasNext()) {
				psmVertex = psmIterator.next();
			} else {
				// Create new vertex.
				psmVertex = graph.addVertex(null);
				psmVertex.setProperty(PsmProperty.SPECTRUMID.toString(), spectrumID);
				psmVertex.setProperty(PsmProperty.VOTES.toString(), psm.getVotes());
				
				// Index the proteins by their accession.
				psmIndex.put(PsmProperty.SPECTRUMID.toString(), spectrumID, psmVertex);
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
		if (!edgeIndex.get(EdgeProperty.ID.toString(), id).iterator().hasNext()) {
			Edge edge = graph.addEdge(id, outVertex, inVertex, relType.name());
			edge.setProperty(EdgeProperty.LABEL.name(), relType.name());
			edgeIndex.put(EdgeProperty.ID.toString(), id, edge);
		} 
	}
	
	/**
	 * Method sets up the indices graph.
	 */
	public void setupIndices() {
		// Protein index
		proteinIndex = indexGraph.getIndex(NodeType.PROTEINS.toString(), Vertex.class);
		
		// If not existent, create a new index.
		if (proteinIndex == null) {
			proteinIndex = indexGraph.createIndex(NodeType.PROTEINS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Peptide Index
		peptideIndex = indexGraph.getIndex(NodeType.PEPTIDES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (peptideIndex == null) {
			peptideIndex = indexGraph.createIndex(NodeType.PEPTIDES.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// PSM index
		psmIndex = indexGraph.getIndex(NodeType.PSMS.toString(), Vertex.class);
		// If not existent, create a new index.
		if (psmIndex == null) {
			psmIndex = indexGraph.createIndex(NodeType.PSMS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Species index
		taxonomyIndex = indexGraph.getIndex(NodeType.TAXA.toString(), Vertex.class);
		// If not existent, create a new index.
		if (taxonomyIndex == null) {
			taxonomyIndex = indexGraph.createIndex(NodeType.TAXA.toString(), Vertex.class);			
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Enzyme index
		enzymeIndex = indexGraph.getIndex(NodeType.ENZYMES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (enzymeIndex == null) {
			enzymeIndex = indexGraph.createIndex(NodeType.ENZYMES.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Pathway index
		pathwayIndex = indexGraph.getIndex(NodeType.PATHWAYS.toString(), Vertex.class);
		// If not existent, create a new index.
		if (pathwayIndex == null) {
			pathwayIndex = indexGraph.createIndex(NodeType.PATHWAYS.toString(), Vertex.class);
			((TransactionalGraph) indexGraph).stopTransaction(Conclusion.SUCCESS);
		}
		
		// Ontology index
		ontologyIndex = indexGraph.getIndex(NodeType.ONTOLOGIES.toString(), Vertex.class);
		// If not existent, create a new index.
		if (ontologyIndex == null) {
			ontologyIndex = indexGraph.createIndex(NodeType.ONTOLOGIES.toString(), Vertex.class);
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

	/**
	 * Returns the graph database service.
	 * @return {@link GraphDatabaseService}
	 */
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDb.getService();
	}
	
	/**
	 * Executes a cypher query {@link CypherQuery}.
	 * @param cypherQuery {@link CypherQuery}
	 * @return {@link ExecutionResult}
	 * @throws Exception 
	 */
	public ExecutionResult executeCypherQuery(CypherQuery cypherQuery) throws Exception {
		// Validate query
		if (cypherQuery.isValid()) {
			return engine.execute(cypherQuery.toString());
		} else {
			throw new Exception("Invalid Cypher Query");
		}
	}

	/**
	 * Stops the transaction of a graph database.
	 */
	public void stopTransaction() {
		graph.stopTransaction(Conclusion.SUCCESS);
		((TransactionalGraph)indexGraph).stopTransaction(Conclusion.SUCCESS);
		
	}
	
	/**
	 * Shuts down the graph database.
	 */
	public void shutDown() {
		graphDb.shutDown();
	}
	
}
