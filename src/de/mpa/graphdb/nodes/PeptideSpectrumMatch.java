package de.mpa.graphdb.nodes;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface PeptideSpectrumMatch extends VertexFrame {

	@Property("SPECTRUMID")
	public Long getSpectrumID();
	
	@Property("VOTES")
	public Integer getVotes();
	
	@Adjacency(label="IS_MATCH_IN")
	public Peptide getMatchedPeptide();

	@Property("SPECTRUMID")
	public String toString();
}
