package de.mpa.graphdb.edges;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.Range;

import de.mpa.graphdb.vertices.Peptide;
import de.mpa.graphdb.vertices.Protein;

public interface HasPeptide {
	    @Domain
	    public Protein getDomain();

	    @Range
	    public Peptide getRange();
}
