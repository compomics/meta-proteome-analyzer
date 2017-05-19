package de.mpa.db.neo4j.nodes;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Protein extends VertexFrame {

	@Property("IDENTIFIER")
    String getIdentifier();
	
	@Property("SPECIES")
    String getSpecies();
	
	@Property("SPECTRALCOUNT")
    int getSpectralCount();
	
	@Property("DESCRIPTION")
    String getDescription();
	
	@Adjacency(label="BELONGS_TO_TAXONOMY", direction=Direction.OUT)
    Iterable<Taxon> getTaxa();
	
	@Adjacency(label="BELONGS_TO_ENZYME", direction=Direction.OUT)
    Iterable<Enzyme> getEnzymes();
	
	@Adjacency(label="BELONGS_TO_PATHWAY", direction=Direction.OUT)
    Iterable<Pathway> getPathways();
	
	@Adjacency(label="BELONGS_IN_FUNCTION_GROUP")
    Iterable<Ontology> getFunctionGroup();
	
	@Adjacency(label="HAS_PEPTIDE", direction=Direction.OUT)
    Iterable<Peptide> getPeptides();
	
	@Adjacency(label = "HAS_PEPTIDE")
    void setPeptides(Iterable<Peptide> peptides);
	
	@Property("IDENTIFIER")
    String toString();
}
