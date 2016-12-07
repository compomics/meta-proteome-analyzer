
package de.mpa.client;

import de.mpa.task.SearchType;


public class DbSearchSettings {
	private boolean iterativeSearch;
	private boolean msgf;
	private boolean xtandem;
	private boolean comet;
	private double fragIonTol;
	private double precIonTol;
	private int nMissedCleavages;
	private boolean isPrecIonTolPpm;
	private String fastaFilePath;
	private long experimentid;
	private SearchType searchType;
	private String xtandemParams;
	private String cometParams;
	private String msgfParams;
	private String iterativeSearchSettings;
	
	/**
	 * Returns the value of the iterative search property.
	 * @return flag whether iterative search is used or not.
	 */
    public boolean useIterativeSearch() {
		return iterativeSearch;
	}
    
    /**
     * Sets the boolean flag whether iterative search is used or not. 
     * @param iterativeSearch Iterative search boolean flag
     */
	public void setIterativeSearch(boolean iterativeSearch) {
		this.iterativeSearch = iterativeSearch;
	}
	
	/**
	 * Returns the iterative search settings.
	 * @return Iterative search settings string.
	 */
    public String getIterativeSearchSettings() {
		return iterativeSearchSettings;
	}
    
    /**
     * Sets the iterative search settings string
     * @param iterativeSearchSettings Iterative search settings. 
     */
	public void setIterativeSearchSettings(String iterativeSearchSettings) {
		this.iterativeSearchSettings = iterativeSearchSettings;
	}

	/**
     * Gets the value of the MS-GF+ property.
     * @return boolean flag whether MS-GF+ is used or not.
     */
    public boolean useMSGF() {
        return msgf;
    }

    /**
     * Sets the flag whether MS-GF+ is used or not. 
     * @param msgf MS-GF+ boolean flag
     */
    public void setMSGF(boolean msgf) {
        this.msgf = msgf;
    }
    
    /**
     * Gets the value of the X!Tandem property.
     * @return flag whether X!Tandem is used or not.
     */
    public boolean useXTandem() {
        return xtandem;
    }

    /**
     * Sets the value of the X!Tandem property.
     */
    public void setXTandem(boolean xtandem) {
        this.xtandem = xtandem;
    }
    
	/**
     * Gets the value of the Comet property.
     * @return flag whether Comet is used or not.
     */
    public boolean useComet() {
        return comet;
    }

    /**
     * Sets the value of the Comet property.
     */
    public void setComet(boolean comet) {
        this.comet = comet;
    }  

    /**
     * Gets the value of the experimentid property.
     */
    public long getExperimentid() {
        return experimentid;
    }

    /**
     * Sets the value of the experimentid property.
     */
    public void setExperimentid(long value) {
        this.experimentid = value;
    }

	public double getFragIonTol() {
		return fragIonTol;
	}

	public void setFragIonTol(double fragIonTol) {
		this.fragIonTol = fragIonTol;
	}

	public double getPrecIonTol() {
		return precIonTol;
	}

	public void setPrecIonTol(double precIonTol) {
		this.precIonTol = precIonTol;
	}

	public int getMissedCleavages() {
		return nMissedCleavages;
	}

	public void setMissedCleavages(int nMissedCleavages) {
		this.nMissedCleavages = nMissedCleavages;
	}

	public boolean isPrecIonTolPpm() {
		return isPrecIonTolPpm;
	}

	public void setPrecIonTolPpm(boolean isPrecIonTolPpm) {
		this.isPrecIonTolPpm = isPrecIonTolPpm;
	}

	public String getFastaFilePath() {
		return fastaFilePath;
	}

	public void setFastaFilePath(String fastaFilePath) {
		this.fastaFilePath = fastaFilePath;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public String getXTandemParams() {
		return xtandemParams;
	}

	public void setXTandemParams(String xtandemParams) {
		this.xtandemParams = xtandemParams;
	}

	public String getCometParams() {
		return cometParams;
	}

	public void setCometParams(String cometParams) {
		this.cometParams = cometParams;
	}

	public String getMsgfParams() {
		return msgfParams;
	}

	public void setMsgfParams(String msgfParams) {
		this.msgfParams = msgfParams;
	}
}
