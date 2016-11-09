
package de.mpa.client;

import de.mpa.job.SearchType;


public class DbSearchSettings {
	
	private boolean omssa;
	private boolean xtandem;
	private boolean msgf;
	private double fragIonTol;
	private double precIonTol;
	private int nMissedCleavages;
	private boolean isPrecIonTolPpm;
	private String fastaFile;
	private long experimentid;
	private SearchType searchType;
	private String xtandemParams;
	private String omssaParams;
	private String msgfParams;
	
    /**
     * Gets the value of the omssa property.
     * 
     */
    public boolean isOmssa() {
        return omssa;
    }

    /**
     * Sets the value of the omssa property.
     * 
     */
    public void setOmssa(boolean omssa) {
        this.omssa = omssa;
    }  

    /**
     * Gets the value of the xTandem property.
     * 
     */
    public boolean isXTandem() {
        return xtandem;
    }

    /**
     * Sets the value of the xTandem property.
     * 
     */
    public void setXTandem(boolean xtandem) {
        this.xtandem = xtandem;
    }

    /**
     * Gets the value of the MS-GF+ property.
     * 
     */
    public boolean isMSGF() {
        return msgf;
    }

    /**
     * Sets the value of the MS-GF+ property.
     * 
     */
    public void setMSGF(boolean msgf) {
        this.msgf = msgf;
    }

    /**
     * Gets the value of the experimentid property.
     * 
     */
    public long getExperimentid() {
        return experimentid;
    }

    /**
     * Sets the value of the experimentid property.
     * 
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

	public String getFastaFile() {
		return fastaFile;
	}

	public void setFastaFile(String fastaFile) {
		this.fastaFile = fastaFile;
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

	public String getOmssaParams() {
		return omssaParams;
	}

	public void setOmssaParams(String omssaParams) {
		this.omssaParams = omssaParams;
	}

	public String getMsgfParams() {
		return msgfParams;
	}

	public void setMsgfParams(String msgfParams) {
		this.msgfParams = msgfParams;
	}
}
