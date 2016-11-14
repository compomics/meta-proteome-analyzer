package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.mpa.client.Client;
import de.mpa.client.settings.ParameterMap;
import de.mpa.db.accessor.ProjectAccessor;

/**
 * Class to dump and restore sql databases
 * @author R. Heyer
 *
 */
public class DBDumper {
	
	/**
	 * Method to dump the database
	 * @param filePath. The file for the dump
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
		
		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		String command = null;
		
		if (winOS) {
			String expectedPath = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
			File exe = new File(expectedPath);
			if (!exe.isFile()) {
				System.out.println("MYSQLDUMP.EXE COULD NOT BE FOUND");
				command = "mysqldump";
			} else {
				command = expectedPath;
			}
		} else {
			command = "mysqldump";
		}
		
		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "DUMPING DATABASE TO " + targetFile.getName());
			Client.getInstance().firePropertyChange("indeterminate", false,	true);	
		}
		//***********************************************************/
		// Execute Shell Command
		//***********************************************************/
		String executeCmd = "";
		executeCmd = command +" -u "+dbUser+" -p"+dbPass+" "+ dbName + " -r " + targetFile.getAbsolutePath();
		Process runtimeProcess =Runtime.getRuntime().exec(executeCmd);
		try {
			runtimeProcess.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Show progress
		if (Client.getInstance() != null) {
	        Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABSE DUMP FINISHED");
		}
	}
	
	/**
	 * DROPP the old database and restore the selected one from the back
	 * @param filePath. The path of the sql backup
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void restoreDatabase(String filePath) throws SQLException, IOException {
		
		// get connection settings + database parameters
		ParameterMap para = Client.getInstance().getConnectionParameters();
		String dbUser = (String) para.get("dbUsername").getValue();
		String dbPass = (String) para.get("dbPass").getValue();
		String dbName = (String) para.get("dbName").getValue();
		File targetFile = new File(filePath);
		
		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		String command = null;
		
		if (winOS) {
			String expectedPath = "C:\\xampp\\mysql\\bin\\mysql.exe";
			File exe = new File(expectedPath);
			if (!exe.isFile()) {
				System.out.println("MYSQL.EXE COULD NOT BE FOUND");
				command = "mysql";
			} else {
				command = expectedPath;
			}
		} else {
			command = "mysql";
		}
		
		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "RESTORING DATABASE FROM " + targetFile.getName());
			Client.getInstance().firePropertyChange("indeterminate", false,	true);	
		}
		
		//***********************************************************/
		// Execute Shell Command
		//***********************************************************/
		// Delete database
		Connection conn = DBManager.getInstance().getConnection();
		 Statement statement = conn.createStatement();
         // create the DB .. 
         statement.executeUpdate("DROP DATABASE IF EXISTS " + dbName) ;
         // create table ...
         statement.executeUpdate("CREATE DATABASE " + dbName);
		
		String[] executeCmd = new String[]{"/bin/sh", "-c", "mysql -u" + dbUser+ " -p"+dbPass+" " + dbName+ " < " +  targetFile.getAbsolutePath() };
		Process runtimeProcess =Runtime.getRuntime().exec(executeCmd);
		try {
			runtimeProcess.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Show progress
		if (Client.getInstance() != null) {
	        Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABASE RESTORE FINISHED");
		}
	}
}
