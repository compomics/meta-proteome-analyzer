package de.mpa.client;

public class ProcessSettings {
	
	// fields
	private double tolMz;
	private double threshMz;
	private int k;
	private double threshSc;
	private boolean annotatedOnly;
	
	// XXX
	private long expID = 0L;

	public long getExpID() {
		return expID;
	}
	public void setExpID(long expID) {
		this.expID = expID;
	}
	// /TBD

	// constructors
	public ProcessSettings(double tolMz, double threshMz, int k, double threshSc, boolean annotatedOnly) {
		this.tolMz = tolMz;
		this.threshMz = threshMz;
		this.k = k;
		this.threshSc = threshSc;
		this.setAnnotatedOnly(annotatedOnly);
	}

	// getter/setter methods
	public double getTolMz() {
		return tolMz;
	}
	public void setTolMz(double tolMz) {
		this.tolMz = tolMz;
	}

	public double getThreshMz() {
		return threshMz;
	}
	public void setThreshMz(double threshMz) {
		this.threshMz = threshMz;
	}

	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}

	public double getThreshSc() {
		return threshSc;
	}
	public void setThreshSc(double threshSc) {
		this.threshSc = threshSc;
	}

	public boolean getAnnotatedOnly() {
		return annotatedOnly;
	}
	public void setAnnotatedOnly(boolean annotatedOnly) {
		this.annotatedOnly = annotatedOnly;
	}
	
}
