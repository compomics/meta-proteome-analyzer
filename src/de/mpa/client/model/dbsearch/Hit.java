package de.mpa.client.model.dbsearch;

public interface Hit {
	public int getCount(Object x, Object y);
	public Object getYForX(Object x);
}
