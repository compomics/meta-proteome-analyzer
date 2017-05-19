package de.mpa.db.neo4j.edges;

public enum DirectionType {
	IN("<-","-"),
	OUT("-","->"),
	BOTH("-","-");
	
	private final String left;
	private final String right;

	DirectionType(String left, String right) {
		this.left = left;
		this.right = right;
	}
	
	public String getLeft() {
		return this.left;
	}
	
	public String getRight() {
		return this.right;
	}
}
