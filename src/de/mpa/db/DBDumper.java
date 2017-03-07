package de.mpa.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.solr.common.util.Hash;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.settings.ParameterMap;
import de.mpa.util.PropertyLoader;


/**
 * Class to dump and restore sql databases
 * @author R. Heyer
 *
 */
public class DBDumper {

	/**
	 * Method to dump the database
	 * @param filePath The file for the dump
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void dumpDatabase(String filePath) throws SQLException, IOException {
		// get connection settings + database parameters
		ParameterMap para = Client.getInstance().getConnectionParameters();
		String dbUser = (String) para.get("dbUsername").getValue();
		String dbPass = (String) para.get("dbPass").getValue();
		String dbName = (String) para.get("dbName").getValue();
		File targetFile = new File(filePath);
		System.out.println(targetFile);
		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		String command = null;
		String target_file = "";
		if (winOS) {
			String expectedPath = "\"" + PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_MYSQLDUMP) + "\"";
			target_file = "\"" + targetFile.getAbsolutePath() + "\"";
			command = expectedPath;
		} else {
			command = "mysqldump";
			target_file = targetFile.getAbsolutePath();
		}
		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "DUMPING DATABASE TO " + target_file);
			Client.getInstance().firePropertyChange("indeterminate", false,	true);	
		}
		ArrayList<String> executeCmd = new ArrayList<String>();
		executeCmd.add(command);
		executeCmd.add("--user=" + dbUser);
		executeCmd.add("--password=" + dbPass);
		executeCmd.add("metaprot");
		executeCmd.add("--result-file=" + target_file);
		ProcessBuilder processBuilder = new ProcessBuilder(executeCmd);
		Process process = null;
		try {
			processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			process.destroy();
		}
		// Show progress NullPointer Exception:
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABASE DUMP FINISHED");
		}
	}

	/**
	 * DROPP the old database and restore the selected one from the back
	 * @param filePath The path of the sql backup
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void restoreDatabase(String filePath) throws SQLException, IOException {

		// get connection settings + database parameters
		ParameterMap para = Client.getInstance().getConnectionParameters();
		String dbUser = (String) para.get("dbUsername").getValue();
		String dbPass = (String) para.get("dbPass").getValue();
		String dbName = (String) para.get("dbName").getValue();

		// The file of the restored database
		File targetFile = new File(filePath);

		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "RESTORING DATABASE FROM " + targetFile.getName());
			Client.getInstance().firePropertyChange("indeterminate", false,	true);	
		}
		// write sql-file
		BufferedWriter writer_sql = new BufferedWriter(new FileWriter(new File(Constants.DB_DUMPER_SQL_PATH)));
		writer_sql.write("DROP DATABASE IF EXISTS " + dbName + ";\n");
		writer_sql.write("CREATE DATABASE " + dbName + ";\n");
		writer_sql.write("USE " + dbName + ";\n");
		writer_sql.write("source " + filePath + "\n");
		writer_sql.close();
		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		if (winOS) {
//			String expectedPath = "\"" + PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_MYSQL) + "\"";
			String expectedPath =PropertyLoader.getProperty(PropertyLoader.PATH_MYSQL) + "\"";
			File exe = new File(expectedPath);
			String run_command = exe + " --user=" + dbUser + " --password=" + dbPass + " mysql < " + Constants.DB_DUMPER_SQL_PATH;
			Process process = null;
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(run_command);
				processBuilder.redirectErrorStream(true);
				process = processBuilder.start();
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				process.destroy();
			}
		} else {
			// for linux initialize sh-script which runs mysql-script 
			BufferedWriter writer_sh = new BufferedWriter(new FileWriter(new File(Constants.DB_DUMPER_SH_PATH)));
			
			// for linux: write sh-file  
			writer_sh.write("mysql --user=" + dbUser + " --password=" + dbPass +" mysql < " + Constants.DB_DUMPER_SQL_PATH);

			// close file instance
			writer_sh.close();
			File sh_file = new File(Constants.DB_DUMPER_SH_PATH);
			if (!sh_file.canExecute()) {
				sh_file.setExecutable(true);
			}
			// prepare process
			ArrayList<String> sql_process = new ArrayList<String>();
			sql_process.add(Constants.DB_DUMPER_SH_PATH);
			Process process = null;
	
			// Important to close old connection, if not script get stuck
			Client.getInstance().closeDBConnection();
			// run process and catch errors
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(sql_process);
				processBuilder.redirectErrorStream(true);
				process = processBuilder.start();
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				process.destroy();
			}
		}
		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABASE RESTORE FINISHED");
		}
	}
	
	
	
	/**
	 * 
	 * Method used to transform tables from earlier versions (v1.3 or lower)
	 * into tables for v2
	 * 
	 * @throws SQLException
	 */
	public static void upgradeDatabase() throws SQLException {
		// get connection 
		Connection conn = Client.getInstance().getConnection();
		
		System.out.println("Starting");
		
		// get uniprotentryid to proteinid mapping
		PreparedStatement ps = conn.prepareStatement("SELECT u.uniprotentryid, u.fk_proteinid FROM uniprotentry u");
		ResultSet rs = ps.executeQuery();
		HashMap<Long, Long> prot2upmap = new HashMap<Long, Long>();
		int j = 0;
		while (rs.next())  {
			Long up = rs.getLong("uniprotentryid");
			Long prot = rs.getLong("fk_proteinid");
			prot2upmap.put(prot, up);
			j++;
			if ((j % 1000) == 0) {
				System.out.println("Processed: " + j);
			}
		}
		rs.close();
		ps.close();
		
		// put new up values
		int i = 0;
		for (Long proteinid : prot2upmap.keySet()) {
			PreparedStatement ps2 = conn.prepareStatement("UPDATE protein SET fk_uniprotentryid = ? WHERE proteinid = ?");
			ps2.setLong(1, prot2upmap.get(proteinid));
			ps2.setLong(2, proteinid);
			ps2.execute();
			i++;
			if ((i % 1000) == 0) {
				System.out.println("Updated: " + i);
				conn.commit();
			}
		}
		
		// set all proteins with uniprotid = 0 to uniprotid = -1
		// put new up values
		PreparedStatement ps2 = conn.prepareStatement("UPDATE protein SET fk_uniprotentryid = -1 WHERE fk_uniprotentryid = 0");
		System.out.println("execute");
		ps2.execute();
		System.out.println("commit");
		conn.commit();
		System.out.println("finished");
		
		
	}
}
