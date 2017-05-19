package de.mpa.db.neo4j.nodes;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface PeptideSpectrumMatch extends VertexFrame {

	@Property("IDENTIFIER")
    String getIdentifier();
	
//	@Property("SPECTRUMID")
//	public Long getSpectrumID();
	
	@Property("TITLE")
    String getTitle();
	
	@Property("SCORES")
    Integer getScores();
	
	@Adjacency(label="IS_MATCH_IN")
    Peptide getMatchedPeptide();

	@Property("IDENTIFIER")
    String toString();
}
