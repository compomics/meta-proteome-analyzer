package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Pathway extends VertexFrame {
	
	@Property("IDENTIFIER")
	public String getIdentifier();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Property("PATHWAYID")
	public String getPathwayID();
	
	@Adjacency(label="BELONGS_TO_PATHWAY", direction=Direction.IN)
	public Iterable<Protein> getProteins();
	
	@Property("IDENTIFIER")
	public String toString();

}