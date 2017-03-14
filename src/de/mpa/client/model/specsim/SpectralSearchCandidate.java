package de.mpa.client.model.specsim;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import de.mpa.io.SixtyFourBitStringSupport;

public class SpectralSearchCandidate {
	
	private final long libspectrumID;
	private final String spectrumTitle;
	private final double precursorMz;
	private final int precursorCharge;
	private final Map<Double, Double> peaks;
	private final long peptideID;
	private final String sequence;
	
	/**
	 * This constructor allows the creation of a SpectralSearchCandidate object based on a ResultSet
	 * obtained by a complex SELECT query. Refer to SpectrumExtractor class for further details.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpectralSearchCandidate(ResultSet aResultSet) throws SQLException {
        libspectrumID = aResultSet.getLong("libspectrumid");
        spectrumTitle = aResultSet.getString("title");
        precursorMz = aResultSet.getDouble("precursor_mz");
        precursorCharge = aResultSet.getInt("precursor_charge");
        peaks = SixtyFourBitStringSupport.buildPeakMap(SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("mzarray")),
															SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("intarray")));
        peptideID = aResultSet.getLong("peptideid");
        sequence = aResultSet.getString("sequence");
	}

	/**
	 * @return the spectrumID
	 */
	public long getLibpectrumID() {
		return this.libspectrumID;
	}

	/**
	 * @return the spectrumTitle
	 */
	public String getSpectrumTitle() {
		return this.spectrumTitle;
	}

	/**
	 * @return the precursorMz
	 */
	public double getPrecursorMz() {
		return this.precursorMz;
	}

	/**
	 * @return the precursorCharge
	 */
	public int getPrecursorCharge() {
		return this.precursorCharge;
	}

	/**
	 * @return the peaks
	 */
	public Map<Double, Double> getPeaks() {
		return this.peaks;
	}
	
	/**
	 * @return the peaks
	 */
	public Map<Double, Double> getHighestPeaks(int k) {
		if (k == 0) {
    		return this.peaks;
    	} else {
    		HashMap<Double, Double> res = new HashMap<Double, Double>(this.peaks);
    		TreeSet<Double> sortedSet = null;
    		try {
    			sortedSet = new TreeSet<Double>(res.values());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		while (sortedSet.size() > k) {
    			res.values().remove(sortedSet.first());
    			sortedSet.remove(sortedSet.first());
    		}
    		return res;
    	}
	}

	/**
	 * @return the peptideID
	 */
	public long getPeptideID() {
		return this.peptideID;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return this.sequence;
	}

}
