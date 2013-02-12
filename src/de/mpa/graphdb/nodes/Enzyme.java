package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Enzyme extends VertexFrame {
	@Property("ECNUMBER")
	public String getECNumber();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Adjacency(label="BELONGS_TO_ENZYME", direction=Direction.IN)
	public Iterable<Protein> getProteins();
	
	@Property("ECNUMBER")
	public String toString();

}
