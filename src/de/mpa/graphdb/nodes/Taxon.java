package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Taxon extends VertexFrame {

	@Property("IDENTIFIER")
    String getIdentifier();
	
	@Property("TAXID")
    String getTaxID();
	
	@Property("RANK")
    String getRank();
	
	@Adjacency(label="BELONGS_TO")
    Taxon getParentTaxon();
	
	@Adjacency(label="IS_ANCESTOR_OF", direction=Direction.IN)
    Iterable<Taxon> getChildTaxa();
	
	@Adjacency(label="BELONGS_TO_TAXONOMY", direction=Direction.IN)
    Iterable<Protein> getProteins();
	
	@Property("IDENTIFIER")
    String toString();
}
