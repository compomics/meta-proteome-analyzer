package de.mpa.db.neo4j.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Enzyme extends VertexFrame {
	
	@Property("IDENTIFIER")
    String getIdentifier();
	
	@Property("DESCRIPTION")
    String getDescription();
	
	@Adjacency(label="BELONGS_TO_ENZYME", direction=Direction.IN)
    Iterable<Protein> getProteins();
	
	@Property("IDENTIFIER")
    String toString();

}
