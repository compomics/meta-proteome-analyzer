package de.mpa.graphdb.edges;

public enum DirectionType {
	IN("<-","-"),
	OUT("-","->"),
	BOTH("-","-");
	
	private String left;
	private String right;

	private DirectionType(String left, String right) {
		this.left = left;
		this.right = right;
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getRight() {
		return right;
	}
}
