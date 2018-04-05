package de.mpa.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.compomics.util.interfaces.SpectrumFile;

import de.mpa.db.mysql.accessor.Spectrum;

/**
 * This class maps a Mascot Generic File to memory. It allows for search and
 * retrieval as well as comparing functionality.
 *
 * @author Thilo Muth
 */
@SuppressWarnings("serial")
public class MascotGenericFile implements SpectrumFile, Serializable {

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
	protected HashMap<Double, Double> iPeaks = new HashMap<Double, Double>();

	/**
	 * The spectrum's database ID. Used to mark spectrum as already stored.
	 */
	private Long spectrumID;

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
	 * This HashMap will hold the charges for those ions for which a charge is
	 * known.
	 */
	private HashMap<Double, Integer> iCharges = new HashMap<Double, Integer>();

	/**
	 * This constant defines the key in the spectrum header for the title.
	 */
	private static final String TITLE = "TITLE";

	/**
	 * This constant defines the key in the spectrum header for the precursor M/Z
	 * and intensity.
	 */
	private static final String PEPMASS = "PEPMASS";

	/**
	 * This constant defines the key in the spectrum header for the precursor
	 * charge. Note that this field can be omitted from a MascotGenericFile.
	 */
	private static final String CHARGE = "CHARGE";

	/**
	 * This constant defines the start of a comment line.
	 */
	// private static final String COMMENT_START = "###";

	/**
	 * This constant defines the start tag for the ions.
	 */
	private static final String IONS_START = "BEGIN IONS";

	/**
	 * This constant defines the end tag for the ions.
	 */
	private static final String IONS_END = "END IONS";

	/**
	 * This Properties instance contains all the Embedded properties that are listed
	 * in a Mascot Generic File.
	 */
	private Properties iExtraEmbeddedParameters;

	/**
	 * This constructor takes the MGF File as a String as read from file or DB. The
	 * filename is specified separately here.
	 *
	 * @param aFilename
	 *            String with the filename for the MGF File.
	 * @param aContents
	 *            String with the contents of the MGF File.
	 */
	public MascotGenericFile(String aFilename, String aContents) {
		iFilename = aFilename;
		parseFromString(aContents);
	}

	/**
	 * This constructor takes the filename of the MGF File as argument and loads it
	 * form the hard drive.
	 *
	 * @param aFilename
	 *            File with the pointer to the MGF File.
	 * @throws IOException
	 *             when the file could not be read.
	 */
	public MascotGenericFile(File aFilename) throws IOException {
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
	 * This constructor takes filename and title strings, a peak map as well as a
	 * double and an integer denoting precursor m/z and charge.
	 * 
	 * @param aFileName
	 *            The filename string.
	 * @param aTitle
	 *            The spectrum title string.
	 * @param aPeaks
	 *            The peak map.
	 * @param aPrecursorMz
	 *            The precursor m/z.
	 * @param aCharge
	 *            The precursor charge.
	 */
	public MascotGenericFile(String aFileName, String aTitle, HashMap<Double, Double> aPeaks, double aPrecursorMz,
			double aIntensity, int aCharge) {
		iFilename = aFileName;
		iTitle = aTitle;
		iPeaks = aPeaks;
		iPrecursorMz = aPrecursorMz;
		iIntensity = aIntensity;
		iCharge = aCharge;
	}

	/**
	 * The constructor takes directly the spectrum DAO object.
	 * 
	 * @param spectrum
	 *            The spectrum accessor DAO object.
	 */
	public MascotGenericFile(Spectrum spectrum) {
		iTitle = spectrum.getTitle();
		iPrecursorMz = spectrum.getPrecursor_mz().doubleValue();
		iIntensity = spectrum.getPrecursor_int().doubleValue();
		iCharge = (int) spectrum.getPrecursor_charge();
		iPeaks = SixtyFourBitStringSupport.buildPeakMap(
				SixtyFourBitStringSupport.decodeBase64StringToDoubles(spectrum.getMzarray()),
				SixtyFourBitStringSupport.decodeBase64StringToDoubles(spectrum.getIntarray()));
	}

	/**
	 * This constructor takes a result set from an SQL query.
	 * 
	 * @param rs
	 *            The result set.
	 * @throws SQLException
	 */
	public MascotGenericFile(ResultSet aResultSet) throws SQLException {
		iTitle = aResultSet.getString("title");
		iPrecursorMz = aResultSet.getDouble("precursor_mz");
		iIntensity = aResultSet.getDouble("precursor_int");
		iCharge = aResultSet.getInt("precursor_charge");
		iPeaks = SixtyFourBitStringSupport.buildPeakMap(
				SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("mzarray")),
				SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("intarray")));
		iCharges = SixtyFourBitStringSupport.buildChargeMap(
				SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("mzarray")),
				SixtyFourBitStringSupport.decodeBase64StringToInts(aResultSet.getString("chargearray")));
	}

	/**
	 * This method allows to write the spectrum file to the specified OutputStream.
	 *
	 * @param aOut
	 *            OutputStream to write the file to. This Stream will <b>NOT</b> be
	 *            closed by this method.
	 * @throws IOException
	 *             when the write operation fails.
	 */
	public void writeToStream(OutputStream aOut) throws IOException {
		writeToStream(aOut, false);
	}

	/**
	 * This method allows to write the MascotGenericFile to the specified
	 * OutputStream.
	 *
	 * @param aOut
	 *            OutputStream to write the file to. This Stream will <b>NOT</b> be
	 *            closed by this method.
	 * @param aSubstituteFilename
	 *            if this boolean is true, the filename is set to be the title in
	 *            the output header. If it is false, the title is set as the title.
	 * @throws IOException
	 *             when the write operation fails.
	 */
	public void writeToStream(OutputStream aOut, boolean aSubstituteFilename) throws IOException {
		writeToWriter(new OutputStreamWriter(aOut), aSubstituteFilename);
	}

	/**
	 * This method allows the caller to write the spectrum file to the specified
	 * folder using its current filename.
	 *
	 * @param aParentDir
	 *            File with the parent directory to put the file in.
	 * @throws IOException
	 *             whenever the write process failed.
	 */
	public void writeToFile(File aParentDir) throws IOException {
		if (!aParentDir.exists() && !aParentDir.isDirectory()) {
			throw new IOException(
					"Parent '" + aParentDir.getCanonicalPath() + "' does not exist or is not a directory!");
		}
		File output = new File(aParentDir, iFilename);
		FileOutputStream fos = new FileOutputStream(output);
		writeToStream(fos);
		fos.flush();
		fos.close();
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
	 * @param aComments
	 *            String with the comments for this MAscotGenericFile.
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
	 * @param aTitle
	 *            String with the title for the MascotGenericFile.
	 */
	public void setTitle(String aTitle) {
		this.iTitle = aTitle;
	}

	/**
	 * This method returns the Value of the corresponding embedded parameter Key.
	 *
	 * @param aKey
	 *            String with the Key of the embedded parameter.
	 * @return String Value of the embedded parameter Key.
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
	 * This private method can be called during the parsing of the aFileContents
	 * String to save embedded parameters in the Properties instance.
	 *
	 * @param aKey
	 *            Embedded Property Key.
	 * @param aValue
	 *            Embedded Property Value.
	 */
	private void addExtraEmbeddedParameter(String aKey, String aValue) {
		if (this.iExtraEmbeddedParameters == null) {
			this.iExtraEmbeddedParameters = new Properties();
		}
		this.iExtraEmbeddedParameters.put(aKey, aValue);
	}

	/**
	 * This method compares two MascotGenericFiles and allows them to be sorted
	 * relative to each other. Sorting is done on the basis of precursor M/Z (we
	 * cannot always calculate mass due to the possible absence of charge
	 * information).
	 *
	 * @param anObject
	 *            MascotGenericFile to compare this instance to.
	 * @return int with the code for sorting (negative, positive or 0).
	 */
	public int compareTo(Object anObject) {
		int result = 0;

		// Comparison is done based on precursor M/Z. We cannot reliably calculate
		// the mass, since (many) MGF spectra do not include charge information.
		MascotGenericFile other = (MascotGenericFile) anObject;

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
	 * @param anObject
	 *            Object to test equality with.
	 * @return boolean indicating whether the presented objects are equal ('true')
	 *         or not ('false').
	 */
	public boolean equals(Object anObject) {
		boolean result = false;

		if (anObject != null && anObject instanceof MascotGenericFile) {
			MascotGenericFile other = (MascotGenericFile) anObject;
			if (iFilename.equals(other.iFilename) && iCharge == other.iCharge && iTitle.equals(other.iTitle)
					&& iPeaks.equals(other.iPeaks) && iCharges.equals(other.iCharges)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * This method returns a String representation of this MGF file.
	 *
	 * @return String with the String representation of this object.
	 */
	public String toString() {
		String result = null;
		StringWriter sw = new StringWriter();
		try {
			writeToWriter(sw, false);
			result = sw.toString();
			sw.close();
		} catch (IOException ioe) {
			// No exceptions are expected here.
			ioe.printStackTrace();
		}
		return result;
	}

	/**
	 * This method returns a String representation of this MGF file.
	 *
	 * @param aSubstituteFilename
	 *            if this boolean is true, the filename is set to be the title in
	 *            the output header. If it is false, the title is set as the title.
	 * @return String with the String representation of this object.
	 */
	public String toString(boolean aSubstituteFilename) {
		String result = null;
		StringWriter sw = new StringWriter();
		try {
			writeToWriter(sw, aSubstituteFilename);
			result = sw.toString();
			sw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}

	/**
	 * This method formats an integer to a charge String as used in a
	 * MascotGenericFile (eg., 1 to 1+).
	 *
	 * @param aCharge
	 *            int with the charge to format.
	 * @return String with the formatted charge (eg., 1+).
	 */
	private String processCharge(int aCharge) {
		// The charge notation is '1+', or conversely, '1-'.
		// Therefore we do some extra processing.
		String affix = "+";
		if (aCharge < 0) {
			affix = "-";
		}
		return Math.abs(aCharge) + affix;
	}

	/**
	 * This method extracts an integer from Mascot Generic File charge notation,
	 * eg., 1+. Remark that the charge can also be annotated as "+2,+3", in those
	 * rather cases the charge is also "not known." So we save a zero value.
	 *
	 * @param aCharge
	 *            String with the Mascot Generic File charge notation (eg., 1+).
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
			if (trimmedCharge.length() == 1) {
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
	 * This method will parse the input String and read all the information present
	 * into a MascotGenericFile object.
	 *
	 * @param aFileContent
	 *            String with the contents of the file.
	 */
	private void parseFromString(String aFileContent) {
		try {
			BufferedReader br = new BufferedReader(new StringReader(aFileContent));
			String line = null;
			// Cycle the file.
			int lineCount = 0;
			boolean inSpectrum = false;
			boolean titleFound = false;
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
				if (lineCount == 1 && line.startsWith(MascotGenericFile.CHARGE)) {
					continue;
				}
				// Read all starting comments.
				if (line.startsWith("#")) {
					if (line.startsWith("sid", 1)) {
						try {
							spectrumID = Long.parseLong(line.substring(5));
						} catch (Exception e) {
							// do nothing, just catch failed parse attempt
						}
					}
					comments.append(line + "\n");
				}
				// BEGIN IONS marks the start of the real file.
				else if (line.equals(MascotGenericFile.IONS_START)) {
					inSpectrum = true;
				}
				// END IONS marks the end.
				else if (line.equals(MascotGenericFile.IONS_END)) {
					inSpectrum = false;
				}
				// Read embedded parameters. The most important parameters (such as TITLE,
				// PEPMASS and optional CHARGE fields)
				// will be saved as instance variables as well as in the iEmbeddedParameter
				// Properties instance.
				else if (inSpectrum && (line.indexOf("=") >= 0)) {
					// Find the starting location of the value (which is one beyond the location
					// of the '=').
					int equalSignIndex = line.indexOf("=");

					// See which header line is encountered.
					if (line.startsWith(MascotGenericFile.TITLE)) {
						// TITLE line found.
						// the final title format is "File: #### Spectrum: #### scans: ####"
						// sometimes the title contains an "id" which is removed here
						String title;
						title = line.substring(equalSignIndex + 1);
						title = title.split("( \\(id)")[0];
						if (title.length() > 255) {
							title = title.substring(0, 255);
						}
						
						if (title.toUpperCase().contains("CHARGE=")) {

							if (title.toUpperCase().contains("CHARGE=|")) {
								inSpectrum = false;
							}
							if (inSpectrum) {
								String titleChargeString = title.toUpperCase()
								.substring(title.toUpperCase().indexOf("CHARGE=")).substring(0, 9).replace("|", "").trim().split("=")[1];
								
								//System.out.println(titleChargeString);
								setCharge(extractCharge(titleChargeString));
							}

						}
						setTitle(title);
						titleFound = true;
					} else if (line.startsWith(MascotGenericFile.PEPMASS)) {
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
					} else if (line.startsWith(MascotGenericFile.CHARGE)) {
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
					// 234.56 789 1+
					if (!titleFound) { // if no title was found, substitute with filename
						setTitle(getFilename());
						titleFound = true;
					}
					StringTokenizer st = new StringTokenizer(line, " \t");
					int count = st.countTokens();
					if (count == 2 || count == 3) {
						String temp = st.nextToken().trim();
						Double mass = new Double(temp);
						temp = st.nextToken().trim();
						Double intensity = new Double(temp);

						iPeaks.put(mass, intensity);
						if (st.hasMoreTokens()) {
							int charge = this.extractCharge(st.nextToken());
							this.iCharges.put(mass, new Integer(charge));
						}
					} else {
						System.err.println("\n\nUnrecognized line at line number " + lineCount + ": '" + line + "'!\n");
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
	 * This method writes the MGF object to the specified Writer.
	 *
	 * @param aWriter
	 *            Writer to write a String representation of this class to.
	 * @param aSubstituteFilename
	 *            if this boolean is true, the filename is set to be the title in
	 *            the output header. If it is false, the title is set as the title.
	 * @throws IOException
	 *             when the writing failed.
	 */
	private void writeToWriter(Writer aWriter, boolean aSubstituteFilename) throws IOException {
		BufferedWriter bw = new BufferedWriter(aWriter);

		// Comments go first.
		if (this.iComments != null) {
			bw.write(this.iComments);
		}
		// Next the ion start tag.
		bw.write(MascotGenericFile.IONS_START + "\n");
		// Now the title, or the filename if the substitution flag is 'true'.
		if (aSubstituteFilename) {
			// Substituting the title with the filename.
			bw.write(MascotGenericFile.TITLE + "=" + getFilename() + "\n");
		} else {
			if (getTitle() != null) {
				// Keeping the title.
				bw.write(MascotGenericFile.TITLE + "=" + getTitle() + "\n");
			}
		}
		// Precursor M/Z and intensity (separated by a space).
		if ((getPrecursorMZ() >= 0.0)) {
			bw.write(MascotGenericFile.PEPMASS + "=" + getPrecursorMZ() + " " + getIntensity() + "\n");
		}
		// For charge: see if it is present first (charge != 0).
		// If it is not present, omit this line altogether.
		if (getCharge() != 0) {
			bw.write(MascotGenericFile.CHARGE + "=" + processCharge(getCharge()) + "\n");
		}
		// If there are any extra embedded parameters in the mascot generic file,
		// also write them in this header section.
		if (iExtraEmbeddedParameters != null) {
			if (!this.iExtraEmbeddedParameters.isEmpty()) {
				Iterator<Object> iter = this.iExtraEmbeddedParameters.keySet().iterator();
				while (iter.hasNext()) {
					String aKey = (String) iter.next();
					String aValue = (String) this.iExtraEmbeddedParameters.get(aKey);
					bw.write(aKey + "=" + aValue + "\n");
				}
			}
		}
		// After the header, it is customary to leave an empty line.
		bw.write("\n");
		// Next up the ions themselves.
		SortedSet<Double> ss = new TreeSet<Double>(getPeaks().keySet());
		Iterator<Double> it = ss.iterator();
		while (it.hasNext()) {
			Double tempKey = it.next();
			BigDecimal lDouble = new BigDecimal(tempKey.doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP);
			// We need to check whether a charge is known for this peak.
			String charge = "";
			if (this.iCharges.containsKey(tempKey)) {
				int chargeState = this.iCharges.get(tempKey).intValue();
				charge = "\t" + processCharge(chargeState);
			}
			bw.write(lDouble + " "
					+ new BigDecimal(iPeaks.get(tempKey).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP) + charge
					+ "\n");
		}

		bw.write(MascotGenericFile.IONS_END);
		bw.write("\n\n");

		bw.flush();
	}

	/**
	 * Returns the total intensity of all peaks.
	 *
	 * @return Intensity total rounded.
	 */
	public double getTotalIntensity() {
		Iterator<Double> iter = iPeaks.values().iterator();
		double totalIntensity = 0.0;
		while (iter.hasNext()) {
			totalIntensity += iter.next();
		}
		return this.round(totalIntensity);
	}

	/**
	 * Returns the highest intensity.
	 * 
	 * @return Highest intensity rounded
	 */
	public double getHighestIntensity() {
		Iterator<Double> iter = iPeaks.values().iterator();
		double highestIntensity = -1.0;
		while (iter.hasNext()) {
			double temp = iter.next();
			if (temp > highestIntensity) {
				highestIntensity = temp;
			}
		}
		return this.round(highestIntensity);
	}

	/**
	 * Helper rounding function.
	 * 
	 * @param aTotalIntensity
	 * @return Rounded value
	 */
	private double round(double aTotalIntensity) {
		BigDecimal bd = new BigDecimal(aTotalIntensity).setScale(2, RoundingMode.UP);
		return bd.doubleValue();
	}

	/**
	 * Calculates and returns the signal-to-noise ratio of the spectrum.
	 * 
	 * @param noiseLvl
	 *            The intensity below which peaks are assumed to be noise.
	 * @return The signal-to-noise ratio.
	 */
	public double getSNR(double noiseLvl) {
		if (noiseLvl <= 0.0) {
			return this.getTotalIntensity();
		}

		double signal = 0.0;
		double noise = 0.0;
		for (double intensity : this.iPeaks.values()) {
			if (intensity > noiseLvl) {
				signal += intensity;
			} else {
				noise += intensity;
			}
		}
		if (noise > 0.0) {
			return signal / noise;
		} else {
			return signal; // same as total ion current
		}
	}

	/**
	 * This method reports on the charge of the precursor ion. Note that when the
	 * charge could not be determined, this method will return '0'.
	 *
	 * @return int with the charge of the precursor, or '0' if no charge state is
	 *         known.
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
	 * This method reports on the peaks in the spectrum, with the Doubles for the
	 * masses as keys in the HashMap, and the intensities for each peak as Double
	 * value for that mass key.
	 *
	 * @return HashMap with Doubles as keys (the masses) and Doubles as values (the
	 *         intensities).
	 */
	public HashMap<Double, Double> getPeaks() {
		return this.iPeaks;
	}

	/**
	 * Returns the k highest peaks of the spectrum.
	 * 
	 * @param k
	 *            The amount of peaks to be picked.
	 * @return Map containing mass-intensity pairs.
	 */
	public HashMap<Double, Double> getHighestPeaks(int k) {
		if (k == 0) {
			return this.iPeaks;
		} else {
			HashMap<Double, Double> res = new HashMap<Double, Double>(this.iPeaks);
			ArrayList<Double> sortedList = new ArrayList<Double>(res.values());
			Collections.sort(sortedList);
			Iterator<Double> iter = sortedList.listIterator();
			while (res.size() > k) {
				res.values().remove(iter.next());
				iter.remove();
			}
			return res;
		}
	}

	/**
	 * Returns the charges map.
	 * 
	 * @return Map containing fragment m/z-to-charge pairs.
	 */
	public HashMap<Double, Integer> getCharges() {
		return this.iCharges;
	}

	/**
	 * Returns the precursor ion m/z.
	 */
	public double getPrecursorMZ() {
		return this.iPrecursorMz;
	}

	/**
	 * Sets the precursor ion charge.
	 * 
	 * @param aCharge
	 *            the charge to set
	 */
	public void setCharge(int aCharge) {
		iCharge = aCharge;
	}

	/**
	 * Sets the precursor ion intensity.
	 * 
	 * @param aIntensity
	 *            the intensity to set
	 */
	public void setIntensity(double aIntensity) {
		iIntensity = aIntensity;
	}

	/**
	 * Sets the precursor m/z.
	 * 
	 * @param the
	 *            m/z to set
	 */
	public void setPrecursorMZ(double aPrecursorMz) {
		iPrecursorMz = aPrecursorMz;
	}

	/**
	 * Returns the spectrum's database ID.
	 * 
	 * @return the spectrum ID or <code>null</code> if undefined.
	 */
	public Long getSpectrumID() {
		if (this.spectrumID == null) {
			if (this.iComments != null) {
				int indexOfID = this.iComments.indexOf("#sid ");
				if (indexOfID >= 0) {
					int indexOfBr = this.iComments.indexOf("\n", indexOfID);
					if (indexOfBr > 0) {
						this.spectrumID = Long.parseLong(this.iComments.substring(indexOfID + 5, indexOfBr));
					} else {
						this.spectrumID = Long.parseLong(this.iComments.substring(indexOfID + 5));
					}
				}
			}
		}
		return this.spectrumID;
	}

	/**
	 * 
	 * @param spectrumID
	 */
	public void setSpectrumID(Long spectrumID) {
		// TODO: automatically adapt #sid comment
		this.spectrumID = spectrumID;
	}

	// inherited from SpectrumFile
	@Override
	public void setFilename(String aFilename) {
		iFilename = aFilename;
	}

	@Override
	public void setPeaks(HashMap aPeaks) {
		iPeaks = aPeaks;
	}

}