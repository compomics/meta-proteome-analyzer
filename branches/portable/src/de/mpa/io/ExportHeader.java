package de.mpa.io;

import de.mpa.io.ResultExporter.ExportHeaderType;

public class ExportHeader {
	
	/**
	 * Header ID.
	 */
	private int id;
	
	/**
	 * Header name.
	 */
	private String name;
	
	/**
	 * Header type.
	 */
	private ExportHeaderType type;
	
	/**
	 * Constructs the ExportHeader
	 * @param id Header ID.
	 * @param name Header name.
	 * @param type Header type.
	 */
	public ExportHeader(int id, String name, ExportHeaderType type) {
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
	public ExportHeaderType getType() {
		return type;
	}
	
	/**
	 * Equals method for another header comparison.
	 * @param header ExportHeader
	 * @return true if headers are the same.
	 */
	public boolean equals(ExportHeader header) {
		if (this == header)
			return true;
		else if (id == header.getId()
				&& name.equalsIgnoreCase(header.getName())
				&& type == header.getType()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return id + "_" + name + "_" + type.name();
	}
}
