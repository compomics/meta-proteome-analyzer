package de.mpa.client;

public class DBSearchSettings {
	
	// Fasta file name
	private String fastaFile;
	
	// Precursor ion tolerance
	private double precursorIonTol;
	
	// Fragment ion tolerance
	private double fragmentIonTol;
	
	// Number of missed cleavage sites
	private int numMissedCleavages;
	
	// Enzyme/Protease used
	private Protease enzyme;
	
	// X!Tandem flag
	private boolean xTandem = false;
	
	// Omssa flag
	private boolean omssa = false;
	
	// Crux flag
	private boolean crux = false;
	
	// Inspect flag
	private boolean inspect = false;
	
	public String getFastaFile() {
		return fastaFile;
	}
	public void setFastaFile(String fastaFile) {
		this.fastaFile = fastaFile;
	}
	public double getPrecursorIonTol() {
		return precursorIonTol;
	}
	public void setPrecursorIonTol(double precursorIonTol) {
		this.precursorIonTol = precursorIonTol;
	}
	public double getFragmentIonTol() {
		return fragmentIonTol;
	}
	public void setFragmentIonTol(double fragmentIonTol) {
		this.fragmentIonTol = fragmentIonTol;
	}
	public int getNumMissedCleavages() {
		return numMissedCleavages;
	}
	public void setNumMissedCleavages(int numMissedCleavages) {
		this.numMissedCleavages = numMissedCleavages;
	}
	public Protease getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(Protease enzyme) {
		this.enzyme = enzyme;
	}
	public boolean isXTandem() {
		return xTandem;
	}
	public void setXTandem(boolean xTandem) {
		this.xTandem = xTandem;
	}
	public boolean isOmssa() {
		return omssa;
	}
	public void setOmssa(boolean omssa) {
		this.omssa = omssa;
	}
	public boolean isCrux() {
		return crux;
	}
	public void setCrux(boolean crux) {
		this.crux = crux;
	}
	public boolean isInspect() {
		return inspect;
	}
	public void setInspect(boolean inspect) {
		this.inspect = inspect;
	}
}

enum Protease {
	TRYPSIN, SEMI_TRYPTIC
}
