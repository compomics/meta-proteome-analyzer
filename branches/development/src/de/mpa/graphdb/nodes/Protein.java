package de.mpa.graphdb.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Protein extends VertexFrame {

	@Property("IDENTIFIER")
	public String getIdentifier();
	
	@Property("SPECIES")
	public String getSpecies();
	
	@Property("SPECTRALCOUNT")
	public int getSpectralCount();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Adjacency(label="BELONGS_TO", direction=Direction.OUT)
	public Iterable<Taxon> getTaxa();
	
	@Adjacency(label="BELONGS_TO_ENZYME", direction=Direction.OUT)
	public Iterable<Enzyme> getEnzymes();
	
	@Adjacency(label="BELONGS_TO_PATHWAY", direction=Direction.OUT)
	public Iterable<Pathway> getPathways();
	
	@Adjacency(label="BELONGS_IN_FUNCTION_GROUP")
	public Iterable<Ontology> getFunctionGroup();
	
	@Adjacency(label="HAS_PEPTIDE", direction=Direction.OUT)
	public Iterable<Peptide> getPeptides();
	
	@Adjacency(label = "HAS_PEPTIDE")
    public void setPeptides(final Iterable<Peptide> peptides);
	
	@Property("IDENTIFIER")
	public String toString();
}
