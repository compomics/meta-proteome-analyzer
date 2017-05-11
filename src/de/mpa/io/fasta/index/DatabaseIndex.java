package de.mpa.io.fasta.index;

public interface DatabaseIndex {
	void generateIndex() throws Exception;
	int getNumberOfIndexedProteins();
//	Set<PeptideDigest> searchByPrecursorMass(double precursorMass, Tolerance tol);
}
