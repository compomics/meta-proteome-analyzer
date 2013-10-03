package de.mpa.graphdb.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

/**
 * Utility class that handles importing and exporting to and from GraphML format
 * @author Miro Lehteva, Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 *
 */
public class GraphMLHandler {

	/**
	 * Imports the graph from a GraphML file into given graph
	 * @param destinationGraph TransactionalGraph where data will be imported
	 * @param graphMLFile File where GraphML for this graph is stored
	 */
	public static void importGraphML(TransactionalGraph destinationGraph, File graphMLFile){
		try {
			GraphMLReader.inputGraph(destinationGraph, new FileInputStream(graphMLFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		destinationGraph.stopTransaction(Conclusion.SUCCESS);
	}
	
	/**
	 * Writes graph into a GraphML file 
	 * @param outputGraph the Graph that will be written to GraphML file
	 * @param outputFile File to write the GraphML
	 */
	public static void exportGraphML(Graph outputGraph, File outputFile){
		try {
			GraphMLWriter writer = new GraphMLWriter(outputGraph);
			writer.setNormalize(true);			
			writer.outputGraph(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes graph into a GraphML file 
	 * @param outputGraph the Graph that will be written to GraphML file
	 * @param outputFile File to write the GraphML
	 * @param normalize boolean if you have a large graph normalizing output can cause out of memory exceptions
	 */
	public static void exportGraphML(Graph outputGraph, File outputFile, boolean normalize){
		try {
			GraphMLWriter writer = new GraphMLWriter(outputGraph);
			writer.setNormalize(normalize);			
			writer.outputGraph(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
