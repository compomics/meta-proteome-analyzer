package de.mpa.graphdb.io;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

/**
 * This class serves of I/O of Cypher queries. 
 * @author Thilo Muth 
 *
 */
public class QueryHandler {
	
	/**
	 * Exports the user-defined Cypher queries by using XStream (writing to userqueries.xml).
	 * @param userQueries User-defined cypher queries.
	 * @param queryFile Exported query file.
	 * @throws IOException Exception thrown if exporting does not work.
	 */
	public static void exportUserQueries(UserQueries userQueries, File queryFile) throws IOException {
		XStream xstream = new XStream();
		xstream.toXML(userQueries, new BufferedOutputStream(new FileOutputStream(queryFile)));
	}
	
	/**
	 * Imports the user-defined Cypher queries by using XStream.
	 * @param queryFile Exported query file.
	 * @throws IOException Exception thrown if importing does not work.
	 * @throws ClassNotFoundException Exception thrwon if the class is not found.
	 */
	public static UserQueries importUserQueries(File queryFile) {
		XStream xstream = new XStream();
		UserQueries userQueries = (UserQueries) xstream.fromXML(queryFile);
		return userQueries;
	}

}
