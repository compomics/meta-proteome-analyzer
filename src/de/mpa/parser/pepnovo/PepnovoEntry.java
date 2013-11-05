package de.mpa.parser.pepnovo;

import java.util.List;

/**
 * This class holds information from one spectrum of the pepnovo output file.
 * 
 * @author Thilo Muth
 *
 */
public class PepnovoEntry {
	
	/**
	 * The name of the spectrum.
	 */
	private String spectrumName;
	
	/**
	 * The number of the spectrum (starting at 1)
	 */
	private int spectrumNumber;
	
	/**
	 * The list with all the predictions of the spectrum.
	 */
	private List<Prediction> predictionList;
	
	/**
	 * Returns the spectrum name.
	 * @return spectrumName String
	 */	
	public String getSpectrumName() {
		return spectrumName;
	}
	
	/**
	 * Sets the spectrum name.
	 * @param spectrumName
	 */
	public void setSpectrumName(String spectrumName) {
		this.spectrumName = spectrumName;
	}
	
	/**
	 * Returns the list of predictions.
	 * @return predictionList List<Prediction>
	 */
	public List<Prediction> getPredictionList() {
		return predictionList;
	}
	
	/**
	 * Sets the list of predictions
	 * @param predictionList
	 */
	public void setPredictionList(List<Prediction> predictionList) {
		this.predictionList = predictionList;
	}
	
	/**
	 * Returns the spectrum number.
	 * @return
	 */
	public int getSpectrumNumber() {
		return spectrumNumber;
	}
	
	/**
	 * Sets the spectrum number.
	 * @param spectrumNumber
	 */
	public void setSpectrumNumber(int spectrumNumber) {
		this.spectrumNumber = spectrumNumber;
	}
}
