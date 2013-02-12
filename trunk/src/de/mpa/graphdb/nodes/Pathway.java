package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Pathway extends VertexFrame {
	@Property("KONUMBER")
	public String getKONumber();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Adjacency(label="BELONGS_TO_PATHWAY", direction=Direction.IN)
	public Iterable<Protein> getProteins();
	
	@Property("KONUMBER")
	public String toString();

}