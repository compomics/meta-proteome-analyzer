package de.mpa.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
			Client.getInstance().firePropertyChange("new message", null, "DATABASE DUMP FINISHED");
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

		// The file of the restored database
		File targetFile = new File(filePath);

		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		if (winOS) {
			String expectedPath = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "\\mysql\\bin\\mysql.exe";
			File exe = new File(expectedPath);
			if (!exe.isFile()) {
				System.out.println("MYSQL.EXE COULD NOT BE FOUND");
			} else {
			}
		} else {
		}

		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("new message", null, "RESTORING DATABASE FROM " + targetFile.getName());
			Client.getInstance().firePropertyChange("indeterminate", false,	true);	
		}

		//***********************************************************/
		// Execute Shell Command
		//***********************************************************/

		// initialize 2 files, 1. sh-script which runs 2. mysql-script 
		BufferedWriter writer_sh = new BufferedWriter(new FileWriter(new File(Constants.DB_DUMPER_SH_PATH)));
		BufferedWriter writer_sql = new BufferedWriter(new FileWriter(new File(Constants.DB_DUMPER_SQL_PATH)));
		// write the files to do the correct things 
		writer_sh.write("mysql --user=" + dbUser + " --password=" + dbPass +" mysql< " + Constants.DB_DUMPER_SQL_PATH);
		writer_sql.write("DROP DATABASE IF EXISTS " + dbName + ";\n");
		writer_sql.write("CREATE DATABASE " + dbName + ";\n");
		writer_sql.write("USE " + dbName + ";\n");
		writer_sql.write("source " + filePath + "\n");
		// close file instances
		writer_sh.close();
		writer_sql.close();
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
		// Show progress
		if (Client.getInstance() != null) {
			Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABASE RESTORE FINISHED");
		}
	}
}
