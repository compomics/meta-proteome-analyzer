package de.mpa.graphdb.nodes;


import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Peptide extends VertexFrame {
	
	@Property("PROTEINCOUNT")
    int getProteinCount();
	
	@Adjacency(label="IS_PEPTIDE_IN")
    Iterable<Protein> getProteins();
	
	@Adjacency(label="IS_MATCH_IN", direction=Direction.IN)
    Iterable<PeptideSpectrumMatch> getPSMs();

	@Property("IDENTIFIER")
    String toString();
}
