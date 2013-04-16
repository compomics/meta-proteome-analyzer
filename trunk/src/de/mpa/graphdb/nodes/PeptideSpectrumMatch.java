package de.mpa.graphdb.nodes;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface PeptideSpectrumMatch extends VertexFrame {

	@Property("IDENTIFIER")
	public String getIdentifier();
	
	@Property("SPECTRUMID")
	public Long getSpectrumID();
	
	@Property("SCORES")
	public Integer getScores();
	
	@Adjacency(label="IS_MATCH_IN")
	public Peptide getMatchedPeptide();

	@Property("IDENTIFIER")
	public String toString();
}
