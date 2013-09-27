package de.mpa.graphdb.io;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpa.client.ui.dialogs.GraphQueryDialog;
import de.mpa.graphdb.cypher.CypherQuery;

/**
 * This class wraps a user-defined list of <code>CypherQuery</code> objects.
 * 
 * @author Thilo Muth
 * @see CypherQuery
 *
 */
public class UserQueries {
	/**
	 * List of user-defined list of <code>CypherQuery</code> objects.
	 */
	private List<CypherQuery> queries;
	
	/**
	 * Last modified <code>Date</code>.
	 */
	private Date lastModified;
	
	/**
	 * Default constructor.
	 */
	public UserQueries() {
		this(new ArrayList<CypherQuery>());
	}
	
	/**
	 * Constructor for wrapping a list of <code>CypherQuery</code> objects.
	 * @param queries the user-defined <code>CypherQuery</code> objects.
	 */
	public UserQueries(List<CypherQuery> queries) {
		this.queries = queries;
		this.lastModified = new Date();
	}
	
	/**
	 * Returns the user-defined list of <code>CypherQuery</code> objects.
	 * @return User-defined list of <code>CypherQuery</code> objects.
	 */
	public List<CypherQuery> getQueries() {
		return queries;
	}
	
	/**
	 * Last-modified <code>Date</code>.
	 * @return Last-modified <code>Date</code>
	 */
	public Date getLastModified() {
		return lastModified;
	}
	
	/**
	 * Adds a user query.
	 * @param query User query
	 */
	public void addQuery(CypherQuery query) {
		this.queries.add(query);
	}
	
	/**
	 * Returns a user query with a specified index.
	 * @param index Index of user query. 
	 */
	public CypherQuery getQuery(int index) {
		return this.queries.get(index);
	}
	
	/**
	 * Returns all query titles by passing an object array, used for the GraphQueryDialogs JXList. 
	 * @return query titles as object array
	 * @see GraphQueryDialog
	 */
	public Object[] getTitleObjects() {
		Object[] data = new Object[this.queries.size()];
		for (int i = 0; i < this.queries.size(); i++) {
			data[i] = getQuery(i).getTitle();
		}
		return data;
	}
}
