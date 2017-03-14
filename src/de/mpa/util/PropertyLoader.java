package de.mpa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * contains all properties and constants
 * 
 * @author rzoun, K. Schallert
 *
 */
public class PropertyLoader {

	private static Properties props;

	//
	public static final String OS = System.getProperty("os.name").toLowerCase();

	// central place to change property names
	public static final String BASE_PATH = "base_path";

	// fasta settings
	public static final String FILES_FASTA = "file.fastalist";
	public static final String PATH_FASTA = "path.fasta";
	public static final String BLAST_DB = "path.blastdb";

	// connection-settings
	public static final String DB_ADRESS = "dbAddress";
	public static final String DB_NAME = "dbName";
	public static final String DB_USERNAME = "dbUsername";
	public static final String DB_PASSWORD = "dbPass";
	public static final String SERVER_ADRESS = "srvAddress";

	// server-settings
	// Port of the server webservice
	public static final String APP_PORT = "app.port";
	// Transfer path for the spectra
	public static final String PATH_TRANSFER = "path.transfer";
	// Path to the FASTA database folder

	// X!Tandem
	public static final String PATH_XTANDEM = "path.xtandem";
	public static final String PATH_XTANDEM_OUTPUT = "path.xtandem.output";
	public static final String APP_XTANDEM = "app.xtandem";
	// OMSSA
	public static final String PATH_OMSSA = "path.omssa";
	public static final String PATH_OMSSA_OUTPUT = "path.omssa.output";
	public static final String APP_OMSSA = "app.omssa";
	// Crux
	public static final String PATH_CRUX = "path.crux";
	public static final String PATH_CRUX_OUTPUT = "path.crux.output";
	public static final String APP_CRUX = "app.crux";
	// Inspect
	public static final String PATH_INSPECT = "path.inspect";
	public static final String APP_INSPECT = "app.inspect";
	public static final String FILE_INPUT_INSPECT = "file.input.inspect";
	public static final String PATH_INSPECT_OUTPUT_RAW = "path.inspect.output.raw";
	public static final String PATH_INSPECT_OUTPUT_PVALUED = "path.inspect.output.pvalued";
	// # QVality
	public static final String PATH_QVALITY = "path.qvality";
	public static final String APP_QVALITY = "app.qvality";
	// mysql
	public static final String PATH_MYSQL = "win_mysql";
	public static final String PATH_MYSQLDUMP = "win_mysqldump";

	static {
		// load the property file
		File propFile = new File("./config.properties");


		if(System.getProperty("os.name").toLowerCase().indexOf("win")!=-1)
			propFile = new File("./config.properties");
		//System.out.println(propFile.getAbsolutePath());
		// error and exit if not found
		if (!propFile.exists()) {
			System.err
					.println("config.properties nicht gefunden\nbase_path ist "
							+ new File("./").getAbsolutePath());
			System.exit(1);
		}

		Properties properties = new Properties();
		try {
			// load the properties
			properties.load(new FileInputStream(propFile));
            PropertyLoader.props = properties;
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * get specific property
	 * 
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return PropertyLoader.props.getProperty(key);
	}

	public static void main(String[] args) {
		String pathFasta = getProperty(BASE_PATH)
				+ getProperty(PATH_FASTA);
		System.out.println(pathFasta);
	}

}
