package de.mpa.graphdb.vertices;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Protein extends VertexFrame {

	@Property("ACCESSION")
	public String getAccession();
	
	@Property("PROTEINSEQUENCE")
	public String getSequence();
	
	@Property("LENGTH")
	public int getLength();
	
	@Property("DESCRIPTION")
	public String getDescription();
	
	@Property("DB_TYPE")
	public String getDatabaseType();
	
	@Adjacency(label="BELONGS_TO", direction=Direction.OUT)
	public Iterable<Species> getSpecies();
	
	@Adjacency(label="BELONGS_TO", direction=Direction.OUT)
	public Iterable<Enzyme> getEnzymes();
	
	@Adjacency(label="BELONGS_IN_FUNCTION_GROUP")
	public Iterable<Ontology> getFunctionGroup();
	
	@Adjacency(label="HASPEPTIDE", direction=Direction.OUT)
	public Iterable<Peptide> getPeptides();
	
	@Adjacency(label = "HASPEPTIDE")
    public void setPeptides(final Iterable<Peptide> peptides);
	
	
	@Property("ACCESSION")
	public String toString();
}
