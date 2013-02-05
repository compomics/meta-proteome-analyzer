package de.mpa.graphdb.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.SpeciesProperty;

/**
 * Utility class that handles importing and exporting to and from GraphML format
 * @author Miro Lehtev�, Thilo Muth
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
	 * Imports the graph from a GraphML file into given graph and creates indices
	 * for Species, Peptides and Proteins. This will index species with name and
	 * taxon number, proteins with accession and peptides with sequence.
	 * @param destinationGraph Neo4jGraph where data will be stored
	 * @param graphMLFile File where GraphML for this graph is imported from
	 */
	public static void importGraphML(Neo4jGraph destinationGraph, File graphMLFile){
		// Add data to database
		try {
			GraphMLReader.inputGraph(destinationGraph, new FileInputStream(graphMLFile));
			destinationGraph.stopTransaction(Conclusion.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			destinationGraph.stopTransaction(Conclusion.FAILURE);
		}
		// Get indices if they exists
		Index<Vertex> species = destinationGraph.getIndex("species", Vertex.class);
		Index<Vertex> proteins = destinationGraph.getIndex("proteins", Vertex.class);
		Index<Vertex> peptides = destinationGraph.getIndex("peptides", Vertex.class);
		// If not, try to create indices
		try{		 
			if(species == null){
				species = destinationGraph.createIndex("species", Vertex.class);
			}
			if(proteins == null) {
				proteins = destinationGraph.createIndex("proteins", Vertex.class);
			}
			if(peptides == null) {
				peptides = destinationGraph.createIndex("peptides", Vertex.class);
			}
			destinationGraph.stopTransaction(Conclusion.SUCCESS);
		} catch(Exception e){
			e.printStackTrace();
			destinationGraph.stopTransaction(Conclusion.FAILURE);
		}
		// Initialize property names
		String taxon = SpeciesProperty.TAXON.toString();
		String name =  SpeciesProperty.NAME.toString();
		
		String accession = ProteinProperty.ACCESSION.toString();
		
		String sequence = PeptideProperty.SEQUENCE.toString();
		// Try to index properties
		try {	
			for(Vertex v : destinationGraph.getVertices()) {
				Set<String> properties = v.getPropertyKeys();
				if(properties.contains(SpeciesProperty.TAXON.toString())) {
					species.put(taxon, v.getProperty(taxon), v);
					species.put(name, v.getProperty(name), v);
				}
				else if(properties.contains(accession)) {
					proteins.put(accession, v.getProperty(accession), v);
				}
				else if(properties.contains(sequence)){
					peptides.put(sequence, v.getProperty(sequence), v);
				}
			}
			destinationGraph.stopTransaction(Conclusion.SUCCESS);
		}catch(Exception e) {
			e.printStackTrace();
			destinationGraph.stopTransaction(Conclusion.FAILURE);
		}
		
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
