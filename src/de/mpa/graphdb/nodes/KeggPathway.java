package de.mpa.graphdb.nodes;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface KeggPathway extends VertexFrame {
	
	@Property("IDENTIFIER")
    String getIdentifier();
	
	@Property("DESCRIPTION")
    String getDescription();
	
	@Property("IDENTIFIER")
    String toString();

}
