package de.mpa.graphdb.insert;

/**
 * Inserter interface specifies mandatory methods graph database inserts.
 * @author Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 */
public interface Inserter {
	
	public void setData(Object data);

	public void insert();
	
	public void setupIndices();
	
	public int[] getDefaultIndexes();

	public String getDefaultDelimiter();
	
	public void setDefaultIndexes(int[] indexes);

	public void setDefaultDelimiter(String delimiter);
	
	public void stop();
}
