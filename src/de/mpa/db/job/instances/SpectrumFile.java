package de.mpa.db.job.instances;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import de.mpa.io.Peak;


/**
 * This class maps a Mascot Generic File to memory. It allows for search and retrieval as well as comparing
 * functionality.
 *
 * @author Thilo Muth
 */
public class SpectrumFile{

    /**
     * This variable holds the filename for the spectrum file.
     */
    protected String iFilename;
    
    /**
     * This variable holds the comments for this MascotGenericFile.
     */
    private String iComments;

    /**
     * The title of the MascotGenericFile.
     */
    private String iTitle;

    /**
     * This HashMap holds all the peaks in the spectrum file.
     */
    protected List<Peak> iPeaks = new ArrayList<Peak>();

    /**
     * This variable holds the precursor M/Z
     */
    protected double iPrecursorMz = -1.0;

    /**
     * This variable holds the charge state.
     */
    protected int iCharge;

    /**
     * The precursor intensity.
     */
    protected double iIntensity = -1.0;

    /**
     * This HashMap will hold the charges for those ions for which a charge is known.
     */
    private final HashMap<Double, Integer> iCharges = new HashMap<Double, Integer>();

    /**
     * This constant defines the key in the spectrum header for the title.
     */
    private static final String TITLE = "TITLE";

    /**
     * This constant defines the key in the spectrum header for the precursor M/Z and intensity.
     */
    private static final String PEPMASS = "PEPMASS";

    /**
     * This constant defines the key in the spectrum header for the precursor charge. Note that this field can be
     * omitted from a MascotGenericFile.
     */
    private static final String CHARGE = "CHARGE";

    /**
     * This constant defines the start of a comment line.
     */
    //private static final String COMMENT_START = "###";

    /**
     * This constant defines the start tag for the ions.
     */
    private static final String IONS_START = "BEGIN IONS";

    /**
     * This constant defines the ernd tag for the ions.
     */
    private static final String IONS_END = "END IONS";

    /**
     * This Properties instance contains all the Embedded properties that are listed in a Mascot Generic File.
     */
    private Properties iExtraEmbeddedParameters;


    /**
     * This constructor takes the MGF File as a String as read from file or DB. The filename is specified separately
     * here.
     *
     * @param aFilename String with the filename for the MGF File.
     * @param aContents String with the contents of the MGF File.
     */
    public SpectrumFile(String aFilename, String aContents) {
        parseFromString(aContents);
        iFilename = aFilename;
    }

    /**
     * This constructor takes the filename of the MGF File as argument and loads it form the hard drive.
     *
     * @param aFilename File with the pointer to the MGF File.
     * @throws IOException when the file could not be read.
     */
    public SpectrumFile(File aFilename) throws IOException {
        if (!aFilename.exists()) {
            throw new IOException("MGF File '" + aFilename.getCanonicalPath() + "' was not found!");
        } else {
            StringBuffer lsb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(aFilename));
            String line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            parseFromString(lsb.toString());
            iFilename = aFilename.getName();
        }
    }


    /**
     * This methods returns the comments for this MascotGenericFile.
     *
     * @return String with the comments for this MascotGenericFile.
     */
    public String getComments() {
        return this.iComments;
    }

    /**
     * This method sets the comments for this MascotGenericFile.
     *
     * @param aComments String with the comments for this MAscotGenericFile.
     */
    public void setComments(String aComments) {
        this.iComments = aComments;
    }

    /**
     * This method reports on the title of the MascotGenericFile.
     *
     * @return String with the title for the MascotGenericFile.
     */
    public String getTitle() {
        return this.iTitle;
    }

    /**
     * This method allows the setting of the title for the MascotGenericFile.
     *
     * @param aTitle String with the title for the MascotGenericFile.
     */
    public void setTitle(String aTitle) {
        this.iTitle = aTitle;
    }


    /**
     * This method returns the Value of the corresponding embedded parameter Key.
     *
     * @param aKey String with the Key of the embedded parameter.
     * @return String   Value of the embedded parameter Key.
     */
    public String getExtraEmbeddedProperty(String aKey) {
        String lReturn = "NoSuchKey";
        if (this.iExtraEmbeddedParameters != null) {
            if (this.iExtraEmbeddedParameters.containsKey(aKey)) {
                lReturn = (String) this.iExtraEmbeddedParameters.get(aKey);
            }
        }
        return lReturn;
    }

    /**
     * This private method can be called during the parsing of the aFileContents String to save embedded parameters in
     * the Properties instance.
     *
     * @param aKey   Embedded Property Key.
     * @param aValue Embedded Property Value.
     */
    private void addExtraEmbeddedParameter(String aKey, String aValue) {
        if (this.iExtraEmbeddedParameters == null) {
            this.iExtraEmbeddedParameters = new Properties();
        }
        this.iExtraEmbeddedParameters.put(aKey, aValue);
    }

    /**
     * This method compares two MascotGenericFiles and allows them to be sorted relative to each other. Sorting is done
     * on the basis of precursor M/Z (we cannot always calculate mass due to the possible absence of charge
     * information).
     *
     * @param anObject MascotGenericFile to compare this instance to.
     * @return int with the code for sorting (negative, positive or 0).
     */
    public int compareTo(Object anObject) {
        int result = 0;

        // Comparison is done based on precursor M/Z. We cannot reliably calculate
        // the mass, since (many) MGF spectra do not include charge information.
        SpectrumFile other = (SpectrumFile) anObject;

        double intermediate_result = (getPrecursorMZ() - other.getPrecursorMZ());

        if (intermediate_result > 0) {
            result = 1;
        } else if (intermediate_result < 0) {
            result = -1;
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * This method checks for equality between this object and the specified object.
     *
     * @param anObject Object to test equality with.
     * @return boolean indicating whether the presented objects are equal ('true') or not ('false').
     */
    public boolean equals(Object anObject) {
        boolean result = false;

        if (anObject != null && anObject instanceof SpectrumFile) {
            SpectrumFile other = (SpectrumFile) anObject;
            if (iFilename.equals(other.iFilename) && iCharge == other.iCharge &&
                    iTitle.equals(other.iTitle) && iPeaks.equals(other.iPeaks) &&
                    iCharges.equals(other.iCharges)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * This method extracts an integer from Mascot Generic File charge notation, eg., 1+. Remark that the charge can
     * also be annotated as "+2,+3", in those rather cases the charge is also "not known." So we save a zero value.
     *
     * @param aCharge String with the Mascot Generic File charge notation (eg., 1+).
     * @return int with the corresponding integer.
     */
    private int extractCharge(String aCharge) {
        int charge = 0;

        // Trim the charge String.
        String trimmedCharge = aCharge.trim();

        boolean negate = false;
        boolean multiCharge = false;

        // See if there is a '-' in the charge String.
        if (trimmedCharge.indexOf("-") >= 0) {
            negate = true;
        }

        // See if there are multiple charges assigned to this spectrum.
        if (trimmedCharge.indexOf(",") >= 0) {
            multiCharge = true;
        }

        if (!multiCharge) {
            // Charge is now: trimmedCharge without the sign character,
            // negated if necessary.        	
        	if(trimmedCharge.length() == 1){
        		charge = Integer.parseInt(trimmedCharge.substring(0, 1));        		
        	} else {
        		charge = Integer.parseInt(trimmedCharge.substring(0, trimmedCharge.length() - 1));
        	}
            
            if (negate) {
                charge = -charge;
            }
        }

        return charge;
    }

    /**
     * This method will parse the input String and read all the information present into a MascotGenericFile object.
     *
     * @param aFileContent String with the contents of the file.
     */
    private void parseFromString(String aFileContent) {
        try {
            BufferedReader br = new BufferedReader(new StringReader(aFileContent));
            String line = null;
            // Cycle the file.
            int lineCount = 0;
            boolean inSpectrum = false;
            StringBuffer comments = new StringBuffer();
            while ((line = br.readLine()) != null) {
                // Advance line count.
                lineCount++;
                // Delete leading/trailing spaces.
                line = line.trim();
                // Skip empty lines.
                if (line.equals("")) {
                    continue;
                }
                // First line can be 'CHARGE'.
                if (lineCount == 1 && line.startsWith(SpectrumFile.CHARGE)) {
                    continue;
                }
                // Read all starting comments.
                if (line.startsWith("#")) {
                    comments.append(line + "\n");
                }
                // BEGIN IONS marks the start of the real file.
                else if (line.equals(SpectrumFile.IONS_START)) {
                    inSpectrum = true;
                }
                // END IONS marks the end.
                else if (line.equals(SpectrumFile.IONS_END)) {
                    inSpectrum = false;
                }
                // Read embedded parameters. The most important parameters (such as TITLE, PEPMASS and optional CHARGE fields )
                // will be saved as instance variables as well as in the iEmbeddedParameter Properties instance.
                else if (inSpectrum && (line.indexOf("=") >= 0)) {
                    // Find the starting location of the value (which is one beyond the location
                    // of the '=').
                    int equalSignIndex = line.indexOf("=");

                    // See which header line is encountered.
                    if (line.startsWith(SpectrumFile.TITLE)) {
                        // TITLE line found.
                        setTitle(line.substring(equalSignIndex + 1));
                    } else if (line.startsWith(SpectrumFile.PEPMASS)) {
                        // PEPMASS line found.
                        String value = line.substring(equalSignIndex + 1).trim();
                        StringTokenizer st = new StringTokenizer(value, " \t");
                        setPrecursorMZ(Double.parseDouble(st.nextToken().trim()));
                        // It is possible that parent intensity is not mentioned. We then set it to '0'.
                        if (st.hasMoreTokens()) {
                            setIntensity(Double.parseDouble(st.nextToken().trim()));
                        } else {
                            setIntensity(0.0);
                        }
                    } else if (line.startsWith(SpectrumFile.CHARGE)) {
                        // CHARGE line found.
                        // Note the extra parsing to read a Mascot Generic File charge (eg., 1+).
                        setCharge(extractCharge(line.substring(equalSignIndex + 1)));
                    } else {
                        // This is an extra embedded parameter!
                        String aKey = line.substring(0, equalSignIndex);
                        String aValue = line.substring(equalSignIndex + 1);
                        // Save the extra embedded parameter in iEmbeddedParameter
                        this.addExtraEmbeddedParameter(aKey, aValue);
                    }
                }
                // Read peaks, minding the possibility of charge present!
                else if (inSpectrum) {
                    // We're inside the spectrum, with no '=' in the line, so it should be
                    // a peak line.
                    // A peak line should be either of the following two:
                    // 234.56 789
                    // 234.56 789   1+
                	
                    StringTokenizer st = new StringTokenizer(line, " \t");
                    int count = st.countTokens();
                    if (count == 2 || count == 3) {
                        String temp = st.nextToken().trim();
                        Double mass = new Double(temp);
                        temp = st.nextToken().trim();
                        Double intensity = new Double(temp);
                        Peak peak = new Peak(mass, intensity);
                        this.iPeaks.add(peak);
                        if (st.hasMoreTokens()) {
                            int charge = extractCharge(st.nextToken());
                            this.iCharges.put(mass, new Integer(charge));
                        }
                    } else {
                        System.out.println("\n\nUnrecognized line at line number " + lineCount + ": '" + line + "'!\n");
                    }
                }
            }
            // Last but not least: add the comments.
            iComments = comments.toString();
            // That's it.
            br.close();
        } catch (IOException ioe) {
            // We do not expect IOException when using a StringReader.
            ioe.printStackTrace();
        }
    }
   

    /**
     * Returns the total intensity of all peaks.
     *
     * @return Intensity total rounded.
     */
    public double getTotalIntensity() {
        List<Peak> peaks = iPeaks;
        double totalIntensity = 0.0;
        for (Peak peak : peaks) {
        	totalIntensity += peak.getIntensity();
		}        
        return this.round(totalIntensity);
    }

    
    /**
     * Helper rounding function.
     * @param aTotalIntensity
     * @return Rounded value
     */
    private double round(double aTotalIntensity) {
        BigDecimal bd = new BigDecimal(aTotalIntensity).setScale(2, RoundingMode.UP);
        return bd.doubleValue();
    }

    /**
     * This method reports on the charge of the precursor ion. Note that when the charge could not be determined, this
     * method will return '0'.
     *
     * @return int with the charge of the precursor, or '0' if no charge state is known.
     */
    public int getCharge() {
        return this.iCharge;
    }

    /**
     * This method reports on the filename for the file.
     *
     * @return String with the filename for the file.
     */
    public String getFilename() {
        return this.iFilename;
    }

    /**
     * This method reports on the intensity of the precursor ion.
     *
     * @return double with the intensity of the precursor ion.
     */
    public double getIntensity() {
        return this.iIntensity;
    }

    /**
     * This method reports on the peaks in the spectrum, with the Doubles for the masses as keys in the HashMap, and the
     * intensities for each peak as Double value for that mass key.
     *
     * @return HashMap with Doubles as keys (the masses) and Doubles as values (the intensities).
     */
    public List<Peak> getPeaks() {
        return this.iPeaks;
    }
    
    public double getPrecursorMZ() {
        return this.iPrecursorMz;
    }
    
     public void setCharge(int aCharge) {
         iCharge = aCharge;
    }

    public void setIntensity(double aIntensity) {
        iIntensity = aIntensity;
    }

    public void setPrecursorMZ(double aPrecursorMz) {
        iPrecursorMz = aPrecursorMz;
    }

}


