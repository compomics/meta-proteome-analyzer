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

	public Database(final String filename, List<Entry> entries) {
		this.filename = filename;
		this.entries = entries;
	}

	public String getFilename() {
		return filename;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public Entry getEntry(final int index) {
		return entries.get(index);
	}
}
