package de.mpa.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.rules.TemporaryFolder;

import de.mpa.client.Client;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.ui.ClientFrame;

public class DBDumper {
	
	public static void dumpDatabase(String filePath) throws SQLException, IOException {
		
		// get connection settings
		ParameterMap para = Client.getInstance().getConnectionParameters();
		String user = (String) para.get("dbUsername").getValue();
		String pass = (String) para.get("dbPass").getValue();
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
		
		Client.getInstance().firePropertyChange("new message", null, "DUMPING DATABASE TO " + targetFile.getName());
		Client.getInstance().firePropertyChange("indeterminate", false,	true);
		
		// run MYSQLDUMP
		Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command + " -u" + user + " -p" + pass + " " + " --add-drop-database metaprot");
        // get streams
        InputStream in = pr.getInputStream();
        FileOutputStream out = new FileOutputStream(targetFile);
        // write the output to file
        byte[] buf=new byte[1024];
        int bytes_read;
        while ((bytes_read = in.read(buf)) != -1) {
            out.write(buf, 0, bytes_read);
        }
        try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        out.close();
        
        Client.getInstance().firePropertyChange("indeterminate", true, false);
		Client.getInstance().firePropertyChange("new message", null, "DATABSE DUMP FINISHED");
		
	}
	
	public static void restoreDatabase(String filePath) throws SQLException, IOException {
		
		// get connection settings
		ParameterMap para = Client.getInstance().getConnectionParameters();
		String user = (String) para.get("dbUsername").getValue();
		String pass = (String) para.get("dbPass").getValue();
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
		
		Client.getInstance().firePropertyChange("new message", null, "RESTORING DATABASE FROM " + targetFile.getName());
		Client.getInstance().firePropertyChange("indeterminate", false,	true);
		Client.getInstance().closeDBConnection();
		
		// run MYSQLDUMP
        try {
		Runtime rt = Runtime.getRuntime();
		String runner = (command + " -u" + user + " -p" + pass + " metaprot < " + targetFile.getAbsolutePath());
		// batch file work around because Runtime.exec does not work
			String fileEnd = winOS ? ".bat" : ".sh";
			File f = winOS ?
					File.createTempFile("dumper", fileEnd):
					new File("/scratch/metaprot/mysqlDB/" + "dumper" + fileEnd);
		    FileOutputStream fos = new FileOutputStream(f);
		    fos.write(runner.getBytes());
		    fos.close();
        Process pr = rt.exec(f.getAbsolutePath());
			pr.waitFor();
		f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        Client.getInstance().firePropertyChange("indeterminate", true, false);
		Client.getInstance().firePropertyChange("new message", null, "DATABASE RESTORE FINISHED");
		Client.getInstance().connectToServer();
		
		// refresh the ProjectPanel to reflect new state of the database
		ClientFrame.getInstance().getProjectPanel().refreshProjectTable();
		
	}

}
