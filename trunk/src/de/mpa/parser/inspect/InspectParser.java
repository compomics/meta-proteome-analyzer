package de.mpa.parser.inspect;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 07.09.2010
 * Time: 15:42:44
 * To change this template use File | Settings | File Templates.
 */
public class InspectParser {

    /**
     * Default constructor.
     */
    public InspectParser() {
    }

   
	
	/**
	 * Reads the inspect pValued file.
	 * @param file
	 * @return inspectFile InspectFile
	 */
	public InspectFile read(String file) {
		InspectFile inspectFile = new InspectFile(file);
		BufferedReader reader = null;
		List<InspectHit> identificationList = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String nextLine;
            identificationList = new ArrayList<InspectHit>();
			// Iterate over all the lines of the file.
			while ((nextLine = reader.readLine()) != null) {

		     if (!nextLine.startsWith("#")) {
					StringTokenizer tokenizer = new StringTokenizer(nextLine, "\t");
					List<String> tokenList = new ArrayList<String>();
					InspectHit hit = new InspectHit();

					// Iterate over all the tokens
					while (tokenizer.hasMoreTokens()) {
						tokenList.add(tokenizer.nextToken());
					}

                    // Check tokenList for null
					if (tokenList.size() > 0){
						hit.setScanNumber(Long.valueOf(tokenList.get(1)));
						// Get annotation.
						String peptide = tokenList.get(2);
						String formattedPeptide = peptide.substring(peptide.indexOf('.') + 1, peptide.lastIndexOf('.'));
						hit.setAnnotation(formattedPeptide);
                        hit.setProtein(tokenList.get(3));
                        hit.setCharge(Integer.valueOf(tokenList.get(4)));
                        hit.setMqScore(Double.valueOf(tokenList.get(5)));
                        hit.setLength((Double.valueOf(tokenList.get(6)).intValue()));
                        hit.setTotalPRMScore(Double.valueOf(tokenList.get(7)));
                        hit.setMedianPRMScore(Double.valueOf(tokenList.get(8)));
                        hit.setFractionY(Double.valueOf(tokenList.get(9)));
                        hit.setFractionB(Double.valueOf(tokenList.get(10)));
                        hit.setIntensity(Double.valueOf(tokenList.get(11)));
                        hit.setNtt(Double.valueOf(tokenList.get(12)));
                        hit.setpValue(Double.valueOf(tokenList.get(13)));
                        hit.setfScore(Double.valueOf(tokenList.get(14)));
                        hit.setDeltaScore(Double.valueOf(tokenList.get(15)));
                        hit.setDeltaScoreOther(Double.valueOf(tokenList.get(16)));
                        hit.setRecordNumber(Double.valueOf(tokenList.get(17)));
                        hit.setDbFilePos(Long.valueOf(tokenList.get(18)));
                        hit.setSpecFilePos(Long.valueOf(tokenList.get(19)));
                        hit.setPrecursorMZ(Double.valueOf(tokenList.get(20)));
                        hit.setPrecursorMZError(Double.valueOf(tokenList.get(21)));
						identificationList.add(hit);
					}
				}
			}
		reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}       
        inspectFile.setIdentifications(identificationList);
		return inspectFile;
	}
}
