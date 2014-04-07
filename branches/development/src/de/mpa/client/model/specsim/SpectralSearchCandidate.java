package de.mpa.client.model.specsim;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import de.mpa.io.SixtyFourBitStringSupport;

public class SpectralSearchCandidate {
	
	private long libspectrumID;
	private String spectrumTitle;
	private double precursorMz;
	private int precursorCharge;
	private Map<Double, Double> peaks;
	private long peptideID;
	private String sequence;
	
	/**
	 * This constructor allows the creation of a SpectralSearchCandidate object based on a ResultSet
	 * obtained by a complex SELECT query. Refer to SpectrumExtractor class for further details.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpectralSearchCandidate(ResultSet aResultSet) throws SQLException {
		this.libspectrumID = aResultSet.getLong("libspectrumid");
		this.spectrumTitle = aResultSet.getString("title");
		this.precursorMz = aResultSet.getDouble("precursor_mz");
		this.precursorCharge = aResultSet.getInt("precursor_charge");
		this.peaks = SixtyFourBitStringSupport.buildPeakMap(SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("mzarray")),
															SixtyFourBitStringSupport.decodeBase64StringToDoubles(aResultSet.getString("intarray")));
		this.peptideID = aResultSet.getLong("peptideid");
		this.sequence = aResultSet.getString("sequence");
	}

	/**
	 * @return the spectrumID
	 */
	public long getLibpectrumID() {
		return libspectrumID;
	}

	/**
	 * @return the spectrumTitle
	 */
	public String getSpectrumTitle() {
		return spectrumTitle;
	}

	/**
	 * @return the precursorMz
	 */
	public double getPrecursorMz() {
		return precursorMz;
	}

	/**
	 * @return the precursorCharge
	 */
	public int getPrecursorCharge() {
		return precursorCharge;
	}

	/**
	 * @return the peaks
	 */
	public Map<Double, Double> getPeaks() {
		return peaks;
	}
	
	/**
	 * @return the peaks
	 */
	public Map<Double, Double> getHighestPeaks(int k) {
		if (k == 0) {
    		return peaks;
    	} else {
    		HashMap<Double, Double> res = new HashMap<Double, Double>(peaks);
    		TreeSet sortedSet = null;
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
		return peptideID;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

}
