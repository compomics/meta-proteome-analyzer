package de.mpa.graphdb.setup;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;
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
	
	/**
	 * The graph database instance access point.
	 */
	private EmbeddedGraphDatabase graphDatabase;

	/**
	 * Launches the database instance stored in the specified location.
	 * @param dbPath the database location path
	 */
	public GraphDatabase(String dbPath) {
		this(dbPath, false);
	}

	/**
	 * Launches the database instance stored in the specified location.
	 * Optionally removes any pre-existing instances and creates a new one.
	 * @param dbPath the database location path
	 * @param cleanStart <code>true</code> if pre-existing instances shall be
	 *  deleted and a new one to be created instead, <code>false</code> otherwise
	 */
	public GraphDatabase(String dbPath, boolean cleanStart) {
		if (cleanStart) {
			// clear pre-existing database instance
			this.clearDatabase(dbPath);
		}

		// launch new database instance
		this.startDatabase(dbPath);
	}

	/**
	 * Start the database.
	 * 
	 * @param dbPath Database path
	 */
	public void startDatabase(String dbPath) {
		graphDatabase = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		this.registerShutdownHook(graphDatabase);
	}

	/**
	 * Returns the graph database service.
	 * @return graphDb
	 */
	public GraphDatabaseService getService() {
		return graphDatabase;
	}

	/**
	 * Registers a shutdown hook for the Neo4j instance so that it shuts down
	 * nicely when the VM exits.
	 * @param graphDb GraphDatabaseService
	 */
	private void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/**
	 * Clears the database.
	 * @param dbPath Database path.
	 */
	private void clearDatabase(String dbPath) {
		try {
			File file = new File(dbPath);
			if (file.exists()) {
				FileUtils.deleteRecursively(file);
			}
			
		} catch (IOException e) {
			// try again
			this.clearDatabase(dbPath);
			// TODO: it's possible this causes endless recursion, add safety precautions
		}
	}

	/**
	 * Shuts down the graph database.
	 */
	public void shutDown() {
		System.out.print("Shutting down database... ");
		
		// shut down the graph database service
		graphDatabase.shutdown();
		// clear file-based database contents
		this.clearDatabase(graphDatabase.getStoreDir());
		// clear service instance
		graphDatabase = null;
		
		System.out.println("done.");
	}

	/**
	 * Entry point for testing purposes.
	 * @param args
	 */
	public static void main(String[] args) {
		// Starts the graph database.
		GraphDatabase graphDatabase = new GraphDatabase("target/graphdb", true);

		// Shutdown the database.
		graphDatabase.shutDown();
	}
	
}
