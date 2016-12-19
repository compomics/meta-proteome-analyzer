package de.mpa.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import de.mpa.webservice.WSPublisher;

public class ServerClientStart {

	public static void main(String[] args) {

//		File home = new File("./");
//		String basePath = home.getAbsolutePath();
//
//		try {
//
//			FileInputStream in = new FileInputStream("config.properties");
//
//			Properties props = new Properties();
//			props.load(in);
//			in.close();
//
//			FileOutputStream out = new FileOutputStream("config.properties");
//			props.setProperty("base_path", basePath);
//			props.store(out, null);
//			out.close();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		WSPublisher.main(args);

		Starter.main(args);

	}

}
