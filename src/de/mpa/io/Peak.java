package de.mpa.io;

public class Peak {

	double mz, intensity;
	int charge;
	
	public Peak(double mz, double intensity, int charge) {
		this.mz = mz;
		this.intensity = intensity;
		this.charge = charge;
	}
	
	public Peak(double mz, double intensity) {
		this.mz = mz;
		this.intensity = intensity;
	}

	public Peak(Peak peak) {
        mz = peak.getMz();
        intensity = peak.getIntensity();
	}


	public double getMz() {
		return this.mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public double getIntensity() {
		return this.intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public int getCharge() {
		return this.charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

}