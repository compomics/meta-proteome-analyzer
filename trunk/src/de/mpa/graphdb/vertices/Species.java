package de.mpa.graphdb.vertices;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Species extends VertexFrame {

	@Property("NAME")
	public String getName();
	
	@Property("TAXON")
	public String getTaxon();
	
	@Property("RANK")
	public String getRank();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Adjacency(label="BELONGS_TO")
	public Species getSuperspecies();
	
	@Adjacency(label="IS_PARENT_OF", direction=Direction.IN)
	public Iterable<Species> getSubspecies();
	
	@Adjacency(label="BELONGS_TO", direction=Direction.IN)
	public Iterable<Protein> getProteins();
	
	@Property("NAME")
	public String toString();
}
