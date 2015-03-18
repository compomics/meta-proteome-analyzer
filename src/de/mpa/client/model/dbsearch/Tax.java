package de.mpa.client.model.dbsearch;

import java.io.Serializable;

public class Tax implements Serializable {
	
	/**
	 * Serialization UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This variable represents the contents for the 'taxonomyid' column.
	 */
	protected long taxonomyid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'parentid' column.
	 */
	protected long parentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'description' column.
	 */
	protected String description = null;


	/**
	 * This variable represents the contents for the 'rank' column.
	 */
	protected String rank = null;


	public Tax(long taxonomyid, long parentid, String description, String rank) {
		this.taxonomyid = taxonomyid;
		this.parentid = parentid;
		this.description = description;
		this.rank = rank;
	}


	public long getTaxonomyid() {
		return taxonomyid;
	}


	public long getParentid() {
		return parentid;
	}


	public String getDescription() {
		return description;
	}


	public String getRank() {
		return rank;
	}

}
