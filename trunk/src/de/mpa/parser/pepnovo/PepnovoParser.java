package de.mpa.parser.pepnovo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This com.compomics.proteocloud.parser reads the file contents of an output file of the PepNovo algorithm.
 * @author Thilo Muth
 *
 */
public class PepnovoParser {
	
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
										
					StringTokenizer tokenizer = new StringTokenizer(nextLine);
					List<String> tokenList = new ArrayList<String>();
					Prediction prediction = new Prediction();
					
					// Iterate over all the tokens
					while (tokenizer.hasMoreTokens()) {
						tokenList.add(tokenizer.nextToken());
					}
					if (tokenList.size() > 0){
						prediction.setIndex(Integer.valueOf(tokenList.get(0)));
						prediction.setRankScore(Double.valueOf(tokenList.get(1)));
						prediction.setPepNovoScore(Double.valueOf(tokenList.get(2)));
						prediction.setnTermGap(Double.valueOf(tokenList.get(3)));
						prediction.setcTermGap(Double.valueOf(tokenList.get(4)));
						prediction.setPrecursorMh(Double.valueOf(tokenList.get(5)));
						prediction.setCharge(Integer.valueOf(tokenList.get(6)));
						if (tokenList.size() > 7) {
							prediction.setSequence(tokenList.get(7).trim());							
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

