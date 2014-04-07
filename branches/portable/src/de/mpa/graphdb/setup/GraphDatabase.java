package de.mpa.graphdb.setup;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

/**
 * Within this class the graph database system is fired up.
 * 
 * @author Thilo Muth
 * @version 0.6.1
 * @date 2013-01-09
 * 
 */
public class GraphDatabase {

	private GraphDatabaseService graphDbService;

	/**
	 * Starts an existing database.
	 * 
	 * @param dbPath Database path
	 */
	public GraphDatabase(String dbPath) {
		this(dbPath, false);
	}

	/**
	 * Constructor for starting a new graph database.
	 * 
	 * @param dbPath Database path.
	 * @param cleanStart Condition whether to clear the database previously.
	 */
	public GraphDatabase(String dbPath, boolean cleanStart) {
		// Clear the database.
		if (cleanStart)
			clearDatabase(dbPath);

		// Start the database.
		startDatabase(dbPath);
	}

	/**
	 * Start the database.
	 * 
	 * @param dbPath Database path
	 */
	public void startDatabase(String dbPath) {
		graphDbService = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		// graphDb = new RestGraphDatabase("http://localhost:7474/db/data");
		registerShutdownHook(graphDbService);
	}

	/**
	 * Returns the graph database service.
	 * 
	 * @return graphDb
	 */
	public GraphDatabaseService getService() {
		return graphDbService;
	}

	/**
	 * Registers a shutdown hook for the Neo4j instance so that it shuts down
	 * nicely when the VM exits.
	 * 
	 * @param graphDb GraphDatabaseService
	 */
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		//
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/**
	 * Clears the database.
	 * 
	 * @param dbPath Database path.
	 */
	private void clearDatabase(String dbPath) {
		try {
			File file = new File(dbPath);
			if (file.exists()) {
				FileUtils.deleteRecursively(new File(dbPath));
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Shuts down the graph database.
	 */
	public void shutDown() {
		System.out.print("Shutting down database... ");
		
		// Shut down the graph database service
		graphDbService.shutdown();
		
		System.out.print("done.");
	}

	public static void main(String[] args) {
		// Starts the graph database.
		GraphDatabase graphDatabase = new GraphDatabase("target/graphdb", true);

		// Shutdown the database.
		graphDatabase.shutDown();
	}
}
