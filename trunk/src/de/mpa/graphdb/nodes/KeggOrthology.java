package de.mpa.graphdb.nodes;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface KeggOrthology extends VertexFrame {
	
	@Property("IDENTIFIER")
	public String getIdentifier();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Property("IDENTIFIER")
	public String toString();

}
