package de.mpa.algorithms.denovo;

public class GappedPeptideElement {
    public double iStartMass = 0.0;
    public double iEndMass = 0.0;
    public double iMassDifference = 0.0;
    public String iElement = null;
    
    public GappedPeptideElement(double lStartMass, double lEndMass, double lMassDifference, String lElement) {
        this.iStartMass = lStartMass;
        this.iEndMass = lEndMass;
        this.iMassDifference = lMassDifference;
        this.iElement = lElement;
    }

    public double getStartMass() {
        return iStartMass;
    }

    public double getEndMass() {
        return iEndMass;
    }

    public double getMassDifference() {
        return iMassDifference;
    }

    public String getElement() {
        return iElement;
    }
}
