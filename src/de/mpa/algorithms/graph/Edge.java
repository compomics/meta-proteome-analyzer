package de.mpa.algorithms.graph;


/**
 *  The Edge class represents a weighted edge in an undirected graph.
 *  @author Thilo Muth
 */
public class Edge implements Comparable<Edge> { 
	
	// The first node
    private GraphVertex v;
    
    // The second node (other end for the edge)
    private GraphVertex w;
    
    // The weight for the edge
    private double weight;

   /**
     * Create an edge between v and w with given weight of the m/z difference.
     */
    public Edge(GraphVertex v, GraphVertex w) {
        this.v = v;
        this.w = w;
        this.weight = Math.abs(v.getMass() - w.getMass());
    }

   /**
     * Returns the weight of this edge.
     */
    public double getWeight() {
        return weight;
    }

   /**
     * Returns the end of this edge that is different from the given vertex.
     */
    public GraphVertex getOther(GraphVertex vertex) {
    	if      (vertex == v) return w;
        else if (vertex == w) return v;
        else return null;
    }

   /**
     * Compares edges by weight.
     */
    public int compareTo(Edge edge) {
        if (this.getWeight() < edge.getWeight()) return -1;
        else if (this.getWeight() > edge.getWeight()) return +1;
        else return 0;
    }
}
