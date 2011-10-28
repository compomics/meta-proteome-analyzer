package de.mpa.parser.pepnovo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * This com.compomics.proteocloud.parser reads the file contents of an output file of the PepNovo algorithm.
 * @author Thilo Muth
 *
 */
public class PepnovoParser {
	
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * Default contructor for the PepNovoParser.
	 */
	public PepnovoParser() {
		
	}

	/**
	 * Reads the PepNovo output file and returns a PepNovoFile object.
	 * 
	 * @param file
	 * @return pepNovoFile PepNovoFile object
	 */
	public PepnovoFile read(String file) {
		
		PepnovoFile pepNovoFile = new PepnovoFile(file);		
		BufferedReader reader = null;
		PepnovoEntry entry = null;		
		boolean flag = false;
		List<PepnovoEntry> entryList = new ArrayList<PepnovoEntry>();
		List<Prediction> predictionList = null;
		int specNumber = 1;
		try {
			reader = new BufferedReader(new FileReader(file));
			String nextLine;
			
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {
				
				// Sequence Coordinates section
				if (nextLine.startsWith(">>")) {
					String[] temp = nextLine.split("\\s+");
					String spectrumName = "";
					for (int i = 3; i < temp.length; i++) {
						if(temp[i].startsWith("(SQS")) break;
						spectrumName += (temp[i] + " ");
					}
                    if(entry != null) entryList.add(entry);
                    if(predictionList != null) entry.setPredictionList(predictionList);
                    entry = new PepnovoEntry();
					spectrumName = spectrumName.replace('\\', '/');
					 // Remove leading whitespace
		            spectrumName = spectrumName.replaceAll("^\\s+", "");
		            // Remove trailing whitespace
		            spectrumName = spectrumName.replaceAll("\\s+$", "");
		            
		            if(spectrumName.contains("#Problem")){
		            	spectrumName = spectrumName.substring(0, spectrumName.indexOf("#"));
		            }
		            
		            entry.setSpectrumName(spectrumName);
		            entry.setSpectrumNumber(specNumber);
					predictionList = new ArrayList<Prediction>();
					flag = true;
					
					specNumber++;
				} else if (!nextLine.startsWith("#") && flag) {
					
					String[] tokens = nextLine.split("\\s+");
					
					Prediction prediction = new Prediction();
					log.info("next line: " + nextLine);
					
					if (tokens.length > 5 && !nextLine.equals("")){
						log.info("index: " + prediction.getIndex());
						log.info("rankscore " + tokens[1]);						
						log.info("token: " + tokens[2]);										
						prediction.setIndex(Integer.valueOf(tokens[0]));
						prediction.setRankScore(Double.valueOf(tokens[1]));
						prediction.setPepNovoScore(Double.valueOf(tokens[2]));
						prediction.setnTermGap(Double.valueOf(tokens[3]));
						prediction.setcTermGap(Double.valueOf(tokens[4]));
						prediction.setPrecursorMh(Double.valueOf(tokens[5]));
						prediction.setCharge(Integer.valueOf(tokens[6]));
						if (tokens.length > 7) {
							prediction.setSequence(tokens[7].trim());							
						}
						predictionList.add(prediction);
					}
				}
			}
            entryList.add(entry);
            if(predictionList != null) {
            	entry.setPredictionList(predictionList);
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pepNovoFile.setEntryList(entryList);
		pepNovoFile.setNumberSpectra(specNumber);
		return pepNovoFile;
	}
}

