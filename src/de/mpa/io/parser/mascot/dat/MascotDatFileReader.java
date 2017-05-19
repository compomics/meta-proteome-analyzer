package de.mpa.io.parser.mascot.dat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.io.InputFileReader;
import de.mpa.io.MascotGenericFile;

/**
 * This class reads Mascot DAT-files and stores them into the database
 * @author F. Kohrs and R. Heyer
 *
 */
public class MascotDatFileReader extends InputFileReader {
    
    /**
     * This file stream will be used to read from the specified mergefile.
     */
    protected RandomAccessFile raf;
    
    /**
     * The list of positions inside the query sections.
     */
	private List<Long> queryPositions;

	/**
	 * RandomAccessFile based Mascot DAT-File reader.
	 * @param file Mascot DAT file.
	 * @throws IOException
	 */
	public MascotDatFileReader(File file) throws IOException {
		super(file);
        raf = new RandomAccessFile(file, "r");
	}

	/**
	 * Loads the spectrum at a specified index.
	 */
	@Override
	public MascotGenericFile loadSpectrum(int index) throws IOException {
		
		// Determine start and end positions
    	long summaryPos = this.spectrumPositions.get(index);
    	long queryPos = this.queryPositions.get(index);
    	return this.loadSpectrum(index, summaryPos, queryPos);
	}

	@Override
	public MascotGenericFile loadSpectrum(int index, long summaryPos, long queryPos) throws IOException {
		String line;
		String[] split;
		
		// Skip to specified summary line
        this.raf.seek(summaryPos);
    	
    	// Read first summary line of query, e.g. qexp1=336.205400,2+
    	
    	line = this.raf.readLine();
    	// Proteome discoverer needs a further read line to get the right row, but ProteinScape not... 
    	// maybe some differences in the linebreak
    	if (line == null || line.length() == 0) {
    		line = this.raf.readLine();
		}
    	split = line.split("=");
    	split = split[1].split(",");
    	double precursorMz = Double.parseDouble(split[0]);
    	int precursorCharge = Integer.parseInt(split[1].substring(0, 1));
    	
    	// Read second summary line of query, e.g. qintensity1=201964.0000
    	line = this.raf.readLine();
    	split = line.split("=");
    	double precursorInt = Double.parseDouble(split[1]);
    	
    	// Skip to specified query line
        this.raf.seek(queryPos);
    	
    	// Read first query line, e.g. title=Cmpd%20839%2c%20%2bMSn%28336%2e2054%29%2c%2017%2e0%20min
    	line = this.raf.readLine();
    	if (line == null || line.length() == 0) {
    		line = this.raf.readLine();
		}
    	split = line.split("=");
    	String title = URLDecoder.decode(split[1], "UTF-8");
    	
    	// Skip the next lines until one starts with "Ions1"
    	while (!(line = this.raf.readLine()).startsWith("Ions1")) {
    		// do nothing
    	}
    	
    	// Read last query line, e.g. Ions1=146.808300:1463,278.965800:5580,370.074100:2342, ...
    	line = line.split("=")[1];
    	
    	HashMap<Double, Double> ions = new HashMap<Double, Double>();
    	
    	split = line.split(",");
    	for (String ion : split) {
			String[] ionSplit = ion.split(":");
			ions.put(Double.parseDouble(ionSplit[0]), Double.parseDouble(ionSplit[1]));
		}
		
		return new MascotGenericFile(this.getFilename(), title, ions, precursorMz, precursorInt, precursorCharge);
	}

	@Override
	public void survey() throws IOException {
        this.spectrumPositions = new ArrayList<Long>();
        this.queryPositions = new ArrayList<Long>();

		// Instantiate forward-only buffered reader for lightning-fast parsing to determine spectrum positions
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        
        String line = null;
        long oldPos = 0L;
        long newPos = 0L;
		long queryPos = -1L;
        boolean summary = true;
        
        boolean first = false;
        // Read file line by line
        while ((line = br.readLine()) != null) {
        	oldPos = newPos;
        	
    		newPos += line.getBytes().length + this.newlineCharCount;
    		
    		if (summary) {
            	if (line.startsWith("qmass")) {
            		if(!first) {
                		first = true;
                	}
                    this.spectrumPositions.add(newPos);
            	}
            	
            	if (line.startsWith("num_hits")) {
            		summary = false;
            		
            	}
    		} else {
    			if (line.startsWith("title")) {
    				queryPos = oldPos;
    			}
    			if (line.startsWith("Ions1")) {
                    this.queryPositions.add(queryPos);
    			}
    			if (line.startsWith("parameters")) {
    				break;
    			}
    		}
        }
        br.close();
	}

	@Override
	public void load() throws IOException {
		// Empty
	}

	@Override
	public void close() throws IOException {
        this.raf.close();
	}
	
	@Override
	public List<Long> getSpectrumPositions(boolean doClose) {
		if (doClose) {
			try {
                close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return queryPositions;
	}
	
}
