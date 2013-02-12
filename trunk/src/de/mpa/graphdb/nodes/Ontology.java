package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Ontology extends VertexFrame {

	@Property("KEYWORD")
	public String getKeyword();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Adjacency(label="INVOLVED_IN_BIOPROCESS", direction=Direction.IN)
	public Iterable<Protein> getBiologicalProcessProteins();
	
	@Adjacency(label="CONTAINED_IN_COMPONENT", direction=Direction.IN)
	public Iterable<Protein> getCellularComponentProteins();
	
	@Adjacency(label="HAS_MOLECULAR_FUNCTION", direction=Direction.IN)
	public Iterable<Protein> getMolecularFunctionProteins();
	
	@Property("KEYWORD")
	public String toString();
}
