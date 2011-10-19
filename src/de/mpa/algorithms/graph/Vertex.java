package de.mpa.algorithms.graph;

/**
 *  The Vertex class represents a node with the properties m/z, intensity and index.
 *  The vertices lie in an undirected graph.
 *  @author Thilo Muth
 */
public class Vertex {
	
	// The m/z
	private double mass;
	
	// The abundance
	private double intensity;
	
	// The vertex index
	private int index;
	
	/**
	 * Constructing a vertex by mass, intensity and index.
	 * @param mass
	 * @param intensity
	 * @param index
	 */
	public Vertex(double mass, double intensity, int index) {
		super();
		this.mass = mass;
		this.intensity = intensity;
		this.index = index;
	}
	
	/**
	 * Check whether this vertex equals to another vertex.
	 * @param v
	 * @return
	 */
	public boolean equals(Vertex v) {
		if(this.mass == v.mass && this.intensity == v.intensity) return true;
		else return false;
	}
	
	/**
	 * Returns the m/z.
	 * @return
	 */
	public double getMass() {
		return mass;
	}
	
	/**
	 * Returns the abundance.
	 * @return
	 */
	public double getIntensity() {
		return intensity;
	}
	
	/**
	 * Returns the index.
	 * @return
	 */
	public int getIndex() {
		return index;
	}
}
