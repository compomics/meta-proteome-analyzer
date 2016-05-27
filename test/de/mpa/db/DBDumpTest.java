package de.mpa.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


public class DBDumpTest extends TestCase {

	/**
	 * DB Connection.
	 */
	@SuppressWarnings("unused")
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}

	@Test
	public void testDBDump() throws Exception {
		
		// check operating system
		boolean winOS = System.getProperty("os.name").startsWith("Windows");
		
		// get all parameters for the dump
//		String script = "dump_database";
		String user = "root";
		String pass = "1337";
		
		String command = null;
		if (winOS) {
			String expectedPath = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
			File exe = new File(expectedPath);
			if (!exe.isFile()) {
				System.out.println("MYSQLDUMP.EXE COULD NOT BE FOUND");
			} else {
				command = expectedPath;
			}
		} else {
			command = "mysqldump";
		}
		
		String output = "\\metaprot\\dump\\dumptest.sql";
		
		// run MYSQLDUMP
		Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command + " -u" + user + " -p" + pass + " " + " --add-drop-database metaprot");
        // get streams
        InputStream in = pr.getInputStream();
        FileOutputStream out = new FileOutputStream(new File(output));
        // write the output to file
        byte[] buf=new byte[1024];
        int bytes_read;
        while ((bytes_read = in.read(buf)) != -1) {
            out.write(buf, 0, bytes_read);
        }
        out.close();
        int exitVal = pr.waitFor();
        System.out.println("Exited with error code "+exitVal);
        assertEquals(0, exitVal);
	}

}
