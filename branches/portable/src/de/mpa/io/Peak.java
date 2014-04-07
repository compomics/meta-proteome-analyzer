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
		this.mz = peak.getMz();
		this.intensity = peak.getIntensity();
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

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

}