package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Taxon extends VertexFrame {

	@Property("IDENTIFIER")
	public String getIdentifier();
	
	@Property("TAXID")
	public String getTaxID();
	
	@Property("RANK")
	public String getRank();
	
	@Adjacency(label="BELONGS_TO")
	public Taxon getParentTaxon();
	
	@Adjacency(label="IS_ANCESTOR_OF", direction=Direction.IN)
	public Iterable<Taxon> getChildTaxa();
	
	@Adjacency(label="BELONGS_TO_TAXONOMY", direction=Direction.IN)
	public Iterable<Protein> getProteins();
	
	@Property("IDENTIFIER")
	public String toString();
}
