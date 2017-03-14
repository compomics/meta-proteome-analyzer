package de.mpa.io;

public class ExportHeader {
	
	/**
	 * Header ID.
	 */
	private final int id;
	
	/**
	 * Header name.
	 */
	private final String name;
	
	/**
	 * Header type.
	 */
	private ResultExporter.ExportHeaderType type;

	/**
	 * Constructs the ExportHeader
	 * @param id Header ID.
	 * @param name Header name.
	 * @param type Header type.
	 */
	public ExportHeader(int id, String name, ResultExporter.ExportHeaderType type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	/**
	 * Returns the ExportHeader ID.
	 * @return ExportHeader ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the ExportHeader name.
	 * @return ExportHeader name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ExportHeader type.
	 * @return ExportHeader type
	 */
	public ResultExporter.ExportHeaderType getType() {
		return this.type;
	}
	
	/**
	 * Equals method for another header comparison.
	 * @param header ExportHeader
	 * @return true if headers are the same.
	 */
	public boolean equals(ExportHeader header) {
		if (this == header)
			return true;
		else return this.id == header.getId()
                && this.name.equalsIgnoreCase(header.getName())
                && this.type == header.getType();
	}

	@Override
	public String toString() {
		return this.id + "_" + this.name + "_" + this.type.name();
	}
}
