package de.mpa.db.neo4j.insert;

/**
 * Inserter interface specifies mandatory methods graph database inserts.
 * @author Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 */
public interface Inserter {
	
	void setData(Object data);

	void insert();
	
	void setupIndices();
	
	int[] getDefaultIndexes();

	String getDefaultDelimiter();
	
	void setDefaultIndexes(int[] indexes);

	void setDefaultDelimiter(String delimiter);
	
	void stop();
}
