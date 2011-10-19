package de.mpa.algorithms.graph;

import java.util.List;


/**
 * General (yet undirected) graph class consisting of vertices and edges.
 * @author Thilo Muth
 *
 */
public class Graph {
	
	// The vertices/nodes
	private List<Vertex> vertices;
	
	// The egdes/weights
	private List<Edge> edges;
	
	/**
	 * Constructing the graph with nodes and egdes.
	 * @param vertices
	 * @param edges
	 */
	public Graph(List<Vertex> vertices, List<Edge> edges) {
		super();
		this.vertices = vertices;
		this.edges = edges;
	}

	/**
	 * Returns the vertices in the graph.
	 * @return
	 */
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	/**
	 * Returns the egdes in the graph.
	 * @return
	 */
	public List<Edge> getEdges(){
		return edges;
	}
	
	/**
	 * Adds a vertex to the graph.
	 * @param v
	 */
	public void addVertex(Vertex v){
		vertices.add(v);
	}	
	
	/**
	 * Adds an edge to the graph.
	 * @param e
	 */
	public void addEdge(Edge e){
		edges.add(e);
	}
}
