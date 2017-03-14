package de.mpa.io.fasta;

import java.util.List;

public class Database {

	/**
	 * The filename of the FASTA-Database
	 */
	private final String filename;

	/**
	 * The list of FASTA entries.
	 */
	private final List<Entry> entries;

	public Database(String filename, List<Entry> entries) {
		this.filename = filename;
		this.entries = entries;
	}

	public String getFilename() {
		return this.filename;
	}

	public List<Entry> getEntries() {
		return this.entries;
	}

	public Entry getEntry(int index) {
		return this.entries.get(index);
	}
}
