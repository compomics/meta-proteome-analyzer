package de.mpa.io;

public class Peak {

	double mz, intensity;
	
	public Peak(double mz, double intensity) {
		this.mz = mz;
		this.intensity = intensity;
	}

	public double getMz() {
		return mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

}