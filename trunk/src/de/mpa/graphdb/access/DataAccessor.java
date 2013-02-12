package de.mpa.graphdb.access;
import org.neo4j.graphdb.GraphDatabaseService;

import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.frames.FramedGraph;

import de.mpa.graphdb.nodes.Enzyme;
import de.mpa.graphdb.nodes.Ontology;
import de.mpa.graphdb.nodes.Pathway;
import de.mpa.graphdb.nodes.Peptide;
import de.mpa.graphdb.nodes.PeptideSpectrumMatch;
import de.mpa.graphdb.nodes.Protein;
import de.mpa.graphdb.nodes.Species;
import de.mpa.graphdb.properties.EnzymeProperty;
import de.mpa.graphdb.properties.FunctionProperty;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.PathwayProperty;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.properties.SpeciesProperty;

/**
 * A DataAccessor class provides access to vertices of the graph by querying any property that 
 * the vertices has. Class is meant to access single or group of vertices that are having certain properties.
 * @author Miro Lehtev�, Thilo Muth
 *
 */
public class DataAccessor {
	
	/**
	 * FramedGraph instance that wraps Neo4j graph and is able to return Frame instances
	 */
	private FramedGraph<Neo4jGraph> graph;
	
	/**
	 * For ending transactions
	 */
	private TransactionalGraph baseGraph;
	
	/**
	 * Protein index.
	 */
	private Index<Vertex> proteinIndex;
	
	/**
	 * Peptide index.
	 */
	private Index<Vertex> peptideIndex;
	
	/**
	 * Species index.
	 */
	private Index<Vertex> speciesIndex;
	
	/**
	 * PSM index.
	 */
	private Index<Vertex> psmIndex;
	
	/**
	 * Enzyme index.
	 */
	private Index<Vertex> enzymeIndex;
	
	/**
	 * Pathway index.
	 */
	private Index<Vertex> pathwayIndex;
	
	/**
	 * Ontology index.
	 */
	private Index<Vertex> ontologyIndex;
	
	/**
	 * Edge index.
	 */
	//private Index<Edge> edgeIndex;
	
	/**
	 * Constructor for DataAccessObject 
	 * @param graphDb Neo4jGraph instance to be wrapped in a FramedGraph
	 */
	public DataAccessor(GraphDatabaseService graphDb) {
		Neo4jGraph neograph = new Neo4jGraph(graphDb);
		baseGraph = neograph;
		this.graph = new FramedGraph<Neo4jGraph>(neograph);
		proteinIndex = neograph.getIndex("proteins", Vertex.class);
		peptideIndex = neograph.getIndex("peptides", Vertex.class);
		speciesIndex = neograph.getIndex("species", Vertex.class);
		psmIndex = neograph.getIndex("psms", Vertex.class);
		enzymeIndex = neograph.getIndex("enzymes", Vertex.class);
		pathwayIndex = neograph.getIndex("pathways", Vertex.class);
		ontologyIndex = neograph.getIndex("ontologies", Vertex.class);
		//edgeIndex = neograph.getIndex("edges", Edge.class);
		this.stopTransaction();
	}
	
	/**
	 * Constructor for DataAccessObject
	 * @param baseGraph a IndexableGraph for indexing, it has to implement TransactionalGraph
	 */
	public DataAccessor(IndexableGraph baseGraph) {
		this.baseGraph = (TransactionalGraph) baseGraph;
		proteinIndex = baseGraph.getIndex("proteins", Vertex.class);
		peptideIndex = baseGraph.getIndex("peptides", Vertex.class);
		speciesIndex = baseGraph.getIndex("species", Vertex.class);
		psmIndex = baseGraph.getIndex("psms", Vertex.class);
		enzymeIndex = baseGraph.getIndex("enzymes", Vertex.class);
		pathwayIndex = baseGraph.getIndex("pathways", Vertex.class);
		ontologyIndex = baseGraph.getIndex("ontologies", Vertex.class);
		//edgeIndex = baseGraph.getIndex("edges", Edge.class);
		this.stopTransaction();
	}
	
	/**
	 * Constructor for DataAccessObject
	 * @param baseGraph a Graph instance that must implement also IndexableGraph
	 */
	public DataAccessor(Neo4jGraph baseGraph) {
		this((IndexableGraph)baseGraph);
		graph = new FramedGraph<Neo4jGraph>(baseGraph);
		this.stopTransaction();
	}
	
	/**
	 * Stops transactions invoked by reading
	 */
	protected void stopTransaction() {
		baseGraph.stopTransaction(Conclusion.SUCCESS);
	}

	/**
	 * Getter for Species Frames
	 * @param property SpeciesProperty property you want to query
	 * @param value Object value of the property
	 * @return a Iterable of Species found
	 */
	public Iterable<Species> getSpecies(SpeciesProperty property, Object value) {
		Iterable<Species> itty = graph.getVertices(property.name(), value, Species.class);
		this.stopTransaction();
		return itty;
	}
	
	/**
	 * A getter for Species Frame
	 * @param property SpeciesProperty property you want to query
	 * @param value Object value of the property
	 * @return a Species from index
	 */
	public Species getSingleSpecies(SpeciesProperty property, Object value) {
		Species s = null;
		if(speciesIndex != null) {
			Vertex v  = speciesIndex.get(property.name(), value).iterator().next(); 
			s =  graph.frame(v, Species.class);	 
		} else {
			s = graph.getVertices(property.name(), value, Species.class).iterator().next();
		}
		this.stopTransaction();
		return s;
	}
	
	/**
	 * A getter for PSM Frame.
	 * @param property PsmProperty property you want to query
	 * @param value Object value of the property
	 * @return a Species from index
	 */
	public PeptideSpectrumMatch getSinglePSM(PsmProperty property, Object value) {
		// PeptideSpectrumMatch PSM.
		PeptideSpectrumMatch psm = null;
		if (psmIndex != null) {
			Vertex v = psmIndex.get(property.name(), value).iterator().next();
			psm = graph.frame(v, PeptideSpectrumMatch.class);
		} else {
			psm = graph.getVertices(property.name(), value,	PeptideSpectrumMatch.class).iterator().next();
		}
		this.stopTransaction();
		return psm;
	}
	
	/**
	 * This method retrieves a single peptide.
	 * @param property PeptideProperty property you want to query
	 * @param value Object value of the property
	 * @return a Peptide from index
	 */
	public Peptide getSinglePeptide(PeptideProperty property, Object value) {
		Peptide p = null;
		if(peptideIndex != null) {
			Vertex v  = peptideIndex.get(property.name(), value).iterator().next(); 
			p =  graph.frame(v, Peptide.class);	 
		} else {
			p = graph.getVertices(property.name(), value, Peptide.class).iterator().next();
		}
		this.stopTransaction();
		return p;
	}
	
	/**
	 * A getter for Protein Frames
	 * @param property ProteinProperty property you want to query
	 * @param value Object value of the property
	 * @return a Iterable of Proteins found
	 */
	public Iterable<Protein> getProteins(ProteinProperty property, Object value) {
		Iterable<Protein> itty =  graph.getVertices(property.name(), value, Protein.class);
		this.stopTransaction();
		return itty;
	}
	
	/**
	 * This method retrieves a single protein.
	 * @param property ProteinProperty property you want to query
	 * @param value Object value of the property
	 * @return a Protein from index
	 */
	public Protein getSingleProtein(ProteinProperty property, Object value){
		Protein p;
		if(proteinIndex != null){
			Vertex v  = proteinIndex.get(property.name(), value).iterator().next();
			p = graph.frame(v, Protein.class);	
		} else {
			p = graph.getVertices(property.name(), value, Protein.class).iterator().next();
		} 
		this.stopTransaction();
		return p;
	}
	
	/**
	 * This method retrieves a single enzyme.
	 * @param property ProteinProperty property you want to query
	 * @param value Object value of the property
	 * @return a Protein from index
	 */
	public Enzyme getSingleEnzyme(EnzymeProperty property, Object value){
		Enzyme e;
		if(enzymeIndex != null){
			Vertex v  = enzymeIndex.get(property.name(), value).iterator().next();
			e = graph.frame(v, Enzyme.class);	
		} else {
			e = graph.getVertices(property.name(), value, Enzyme.class).iterator().next();
		} 
		this.stopTransaction();
		return e;
	}
	
	/**
	 * This method retrieves a single pathway.
	 * @param property PathwayProperty property you want to query
	 * @param value Object value of the property
	 * @return a Pathway from index
	 */
	public Pathway getSinglePathway(PathwayProperty property, Object value){
		Pathway p;
		if(pathwayIndex != null){
			Vertex v  = pathwayIndex.get(property.name(), value).iterator().next();
			p = graph.frame(v, Pathway.class);	
		} else {
			p = graph.getVertices(property.name(), value, Pathway.class).iterator().next();
		} 
		this.stopTransaction();
		return p;
	}
	
	/**
	 * This method retrieves a single ontology.
	 * @param property OntologyProperty property you want to query
	 * @param value Object value of the property
	 * @return a Ontology from index
	 */
	public Ontology getSingleOntology(OntologyProperty property, Object value){
		Ontology o;
		if(ontologyIndex!= null){
			Vertex v  = ontologyIndex.get(property.name(), value).iterator().next();
			o = graph.frame(v, Ontology.class);	
		} else {
			o = graph.getVertices(property.name(), value, Ontology.class).iterator().next();
		} 
		this.stopTransaction();
		return o;
	}
	
	/**
	 * A getter for Peptide Frames
	 * @param property PeptideProperty property you want to query
	 * @param value Object value of the property
	 * @return a Iterable of Peptides found
	 */
	public Iterable<Peptide> getPeptides(PeptideProperty property, Object value) {
		Iterable<Peptide> itty = graph.getVertices(property.name(), value, Peptide.class);
		this.stopTransaction();
		return itty;
	}
	
	/**
	 * Retrieves PSMs for specified property. 
	 * @param property PSMProperty you want to query
	 * @param value Object value of the property
	 * @return Iterable of found Peptide spectrum matches
	 */
	public Iterable<PeptideSpectrumMatch> getPSMs(PsmProperty property, Object value) {
		Iterable<PeptideSpectrumMatch> itty =  graph.getVertices(property.name(), value, PeptideSpectrumMatch.class);
		this.stopTransaction();
		return itty;
	}
	
	/**
	 * Retrieves functions for specified property.
	 * @param property FunctionProperty you want to query
	 * @param value Object value of the property
	 * @return a Iterable of Functions found
	 */
	public Iterable<Ontology> getFunctionGroups(FunctionProperty property, Object value) {
		Iterable<Ontology> itty =  graph.getVertices(property.name(), value, Ontology.class);
		this.stopTransaction();
		return itty;
	}
	/**
	 * Returns all Species frame objects from the graph
	 * @return Iterable of Species frames 
	 */
	public Iterable<Species> getAllSpecies(){
		Iterable<Species> itty = graph.getVertices(SpeciesProperty.TAXON.toString(), "*", Species.class);
		this.stopTransaction();
		return itty;
	}
	
	/**
	 * Getter for Vertex objects
	 * @param property String property you want to query
	 * @param value Object value of the property
	 * @return a Iterable of Vertices found
	 */
	public Iterable<Vertex> getVertices(String property, Object value) {
		Iterable<Vertex> itty =  graph.getVertices(property, value);
		this.stopTransaction();
		return itty;
	}
	

	@SuppressWarnings("unchecked")
	public <T> T frameObject(Vertex object, Class<?> clazz) {
		T framed = null;
		try {
			framed = (T) graph.frame(object, clazz);
		}catch(ClassCastException e) {
			e.printStackTrace();
		}
		return framed;
	}
}
