package de.mpa.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MS2Formatter {
	
	private final String ms2file;
	private final String outputfile;
	
	public MS2Formatter(final String ms2file, final String outputfile) {
		this.ms2file = ms2file;
		this.outputfile = outputfile;
		format();
	}
	
	private void format() {
			BufferedReader reader = null;
			BufferedWriter writer = null;
			int scan = 1;
			String formatted = "";
			try {
				reader = new BufferedReader(new FileReader(ms2file));
				writer = new BufferedWriter(new FileWriter(outputfile));
				String nextLine;			
				String[] splits;
				
				// Iterate over all the lines of the file.
				while ((nextLine = reader.readLine()) != null) {					
					if(nextLine.charAt(0) == 'S'){
						splits = nextLine.split("\\s+");
						formatted = splits[0] + "\t" + scan + "\t" + scan + "\t" + splits[3];
						writer.write(formatted  + "\n");
						scan++;
					} else {
						writer.write(nextLine  + "\n");
					}					
				}			
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
	}
}
