package de.mpa.algorithms.fragmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.analysis.Masses;

/**
 * This class is used to do a calculation for the theoretical masses of
 * a, b, c, x, y and z ions (plus double charged ones) and do the matching
 * of experimental and theoretical masses.
 *
 * @author Thilo Muth
 */
public class Fragmentizer {

    
    private final String iSequence;
    /**
     * The masses map for knowledge of amino acid masses etc.
     */
    private final Map<String, Double> iMasses;
    /**
     * The a ions.
     */
    private final FragmentIon[] iAIons;
    /**
     * The a* ions.
     */
    private final FragmentIon[] iANH3Ions;
    /**
     * The a° ions.
     */
    private final FragmentIon[] iAH2OIons;
    /**
     * The b ions.
     */
    private final FragmentIon[] iBIons;
    /**
     * The b* ions.
     */
    private final FragmentIon[] iBNH3Ions;
    /**
     * The b° ions.
     */
    private final FragmentIon[] iBH2OIons;
    /**
     * The c ions.
     */
    private final FragmentIon[] iCIons;
    /**
     * The x ions.
     */
    private final FragmentIon[] iXIons;
    /**
     * The y ions.
     */
    private final FragmentIon[] iYIons;
    /**
     * The y* ions.
     */
    private final FragmentIon[] iYNH3Ions;
    /**
     * The y° ions.
     */
    private final FragmentIon[] iYH2OIons;
    /**
     * The z ions.
     */
    private final FragmentIon[] iZIons;
    /**
     * The MH ion.
     */
    private final FragmentIon[] iMH;
    /**
     * The MH-NH3 ion.
     */
    private final FragmentIon[] iMHNH3;
    /**
     * The MH-H2O ion.
     */
    private final FragmentIon[] iMHH2O;
    /**
     * The fragment mass error tolerance.
     */
    //private final double iFragmentMassError;
    /**
     * The peptide charge
     */
    private final int iPeptideCharge;

	private double mh;
	
	private Map<Ion.IonType, FragmentIon[]> fragIonsMap = new HashMap<Ion.IonType, FragmentIon[]>();




    /**
     * Constructor get a peptide object, the modification map, the input parameters and the masses map.
     *
     * @param sequence A peptide sequence String which should be "in silico" digested.
     * @param masses  Masses map to know which amino acid has which mass.
     * @param charge The charge of the given peptide.
     */
    public Fragmentizer(String sequence, Map<String, Double> masses, int charge) {
        this.iSequence = sequence;
        this.iMasses = masses;
        this.iPeptideCharge = charge;
        int length = this.iSequence.length() * this.iPeptideCharge;
        this.iAIons = new FragmentIon[length];
        this.iAH2OIons = new FragmentIon[length];
        this.iANH3Ions = new FragmentIon[length];
        this.iBIons = new FragmentIon[length];
        this.iBH2OIons = new FragmentIon[length];
        this.iBNH3Ions = new FragmentIon[length];
        this.iCIons = new FragmentIon[length];
        this.iXIons = new FragmentIon[length];
        this.iYIons = new FragmentIon[length];
        this.iYNH3Ions = new FragmentIon[length];
        this.iYH2OIons = new FragmentIon[length];
        this.iZIons = new FragmentIon[length];
        this.iMH = new FragmentIon[this.iPeptideCharge];
        this.iMHNH3 = new FragmentIon[this.iPeptideCharge];
        this.iMHH2O = new FragmentIon[this.iPeptideCharge];
        this.calculateIons();
        this.fillFragmentIonMap();
    }

    /**
     * This method calculates the masses of the peptide, including the masses of the aminoacids plus
     * the masses of the modifications (fixed/variable and N- resp. C-term included)
     *
     * @return double[] Contains the mass of the part of the sequence. The amino acid position is the index.
     */
    double[] calculatePeptideMasses() {
        double mass;
        this.mh = 0.0;
        this.mh += this.iMasses.get("C_term");
        this.mh += this.iMasses.get("N_term") + Masses.Hydrogen;

        double[] peptideMasses = new double[this.iSequence.length()];
        for (int i = 0; i < this.iSequence.length(); i++) {
            mass = 0.0;

            // For each amino acid add the specific mass
            String aa = String.valueOf(this.iSequence.charAt(i));
            if(this.iMasses.containsKey(aa)){
            	mass += this.iMasses.get(aa);
                this.mh += mass;
                // Add each specific mass to the array
                peptideMasses[i] = mass;
            }
        }
        return peptideMasses;
    }

    /**
     * This method calculates the theoretical masses of the ions of the peptide.
     * The fragment ion are stored as objects, for example yIons[0] is the y1 ion.
     */
    private void calculateIons() {
        double[] peptideMasses = this.calculatePeptideMasses();
        double hydrogenMass = Masses.Hydrogen;
        double oxygenMass = Masses.Oxygen;
        double nitrogenMass = Masses.Nitrogen;
        double carbonMass = Masses.Carbon;
        double c_termMass = this.iMasses.get("C_term");

        // Calculate ions masses for each charge
        int length = this.iSequence.length();
        int cptb = 0;
        int cpty = 0;
        for (int charge = 1; charge <= this.iPeptideCharge; charge++) {
            iMH[charge - 1] = new FragmentIon((mh + (charge - 1) * hydrogenMass) / charge, Ion.IonType.MH_ION, 0, charge);
            iMHH2O[charge - 1] = new FragmentIon((mh - oxygenMass - 2 * hydrogenMass + (charge - 1) * hydrogenMass) / charge, Ion.IonType.MHH2O_ION, 0, charge);
            iMHNH3[charge - 1] = new FragmentIon((mh - nitrogenMass - 3 * hydrogenMass + (charge - 1) * hydrogenMass) / charge, Ion.IonType.MHNH3_ION, 0, charge);

            for (int i = 0; i < length; i++) {
                double bMass = 0.0;
                double yMass = 0.0;

                // Each peptide mass is added to the b ion mass
                for (int j = 0; j <= i; j++) {
                    bMass += peptideMasses[j];
                }
                // Create an instance for each fragment ion
                if (charge <= this.iPeptideCharge) {
                    iAIons[cptb] = new FragmentIon((bMass - oxygenMass - carbonMass + charge * hydrogenMass) / charge, Ion.IonType.A_ION, i + 1, charge);
                    iANH3Ions[cptb] = new FragmentIon((bMass - oxygenMass - carbonMass - nitrogenMass - 3 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.ANH3_ION, i + 1, charge);
                    iAH2OIons[cptb] = new FragmentIon((bMass - 2 * oxygenMass - carbonMass - 2 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.AH2O_ION, i + 1, charge);
                    iBIons[cptb] = new FragmentIon((bMass + charge * hydrogenMass) / charge, Ion.IonType.B_ION, i + 1, charge);
                    iBNH3Ions[cptb] = new FragmentIon((bMass - nitrogenMass - 3 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.BNH3_ION, i + 1, charge);
                    iBH2OIons[cptb] = new FragmentIon((bMass - oxygenMass - 2 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.BH2O_ION, i + 1, charge);
                    iCIons[cptb] = new FragmentIon((bMass + nitrogenMass + 3 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.C_ION, i + 1, charge);
                    cptb++;
                }

                // Each peptide mass is added to the y ion mass, taking the reverse direction (from the C terminal end)
                for (int j = 0; j <= i; j++) {
                    yMass += peptideMasses[(length - 1) - j];
                }
                // Add two extra hydrogen on the N terminal end and one hydroxyl at the C terminal end
                yMass = yMass + c_termMass + hydrogenMass;

                // Create an instance of the fragment y ion
                iXIons[cpty] = new FragmentIon((yMass + carbonMass + oxygenMass - 2 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.X_ION, i + 1, charge);
                iYIons[cpty] = new FragmentIon((yMass + charge * hydrogenMass) / charge, Ion.IonType.Y_ION, i + 1, charge);
                iYNH3Ions[cpty] = new FragmentIon((yMass - nitrogenMass - 3 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.YNH3_ION, i + 1, charge);
                iYH2OIons[cpty] = new FragmentIon((yMass - 2 * hydrogenMass - oxygenMass + charge * hydrogenMass) / charge, Ion.IonType.YH2O_ION, i + 1, charge);
                iZIons[cpty] = new FragmentIon((yMass - nitrogenMass - 2 * hydrogenMass + charge * hydrogenMass) / charge, Ion.IonType.Z_ION, i + 1, charge);
                cpty++;
            }
        }
    }

    /**
     * This method tries to match the theoretical masses of the ions with the
     * masses of the experimental peaks.
     *
     * @param ionType The ion type.
     * @param aPeaks  The experimental peaks.
     * @return matchedIons A Vector containing all the matched fragment ions.
     */
    public List<FragmentIon> getMatchedIons(List<SpectrumPeak> aPeaks, double fragMassTol, double threshold) {
    	List<FragmentIon> matchedIons = new ArrayList<FragmentIon>();
    	for (Ion.IonType type : Ion.IonType.values()) {
    		FragmentIon[] theoreticalIons = getTheoreticalIons(type);
            for (FragmentIon fragIon : theoreticalIons) {
                if (fragIon != null) {
                    if (fragIon.isMatch(aPeaks, fragMassTol, threshold)) {
                        matchedIons.add(fragIon);
                    }
                }
            }
		}
        return matchedIons;
    }

    /**
     * Fill the fragment ion map.
     */
    private void fillFragmentIonMap() {
    	fragIonsMap.put(Ion.IonType.A_ION, iAIons);
    	fragIonsMap.put(Ion.IonType.AH2O_ION, iAH2OIons);
    	fragIonsMap.put(Ion.IonType.ANH3_ION, iANH3Ions);
    	fragIonsMap.put(Ion.IonType.B_ION, iBIons);
    	fragIonsMap.put(Ion.IonType.BH2O_ION, iBH2OIons);
    	fragIonsMap.put(Ion.IonType.BNH3_ION, iBNH3Ions);
    	fragIonsMap.put(Ion.IonType.C_ION, iCIons);
    	fragIonsMap.put(Ion.IonType.X_ION, iXIons);
    	fragIonsMap.put(Ion.IonType.Y_ION, iYIons);
    	fragIonsMap.put(Ion.IonType.YH2O_ION, iYH2OIons);
    	fragIonsMap.put(Ion.IonType.YNH3_ION, iYNH3Ions);
    	fragIonsMap.put(Ion.IonType.Z_ION, iZIons);
    	fragIonsMap.put(Ion.IonType.MH_ION, iMH);
    	fragIonsMap.put(Ion.IonType.MHH2O_ION, iMHH2O);
    	fragIonsMap.put(Ion.IonType.MHNH3_ION, iMHNH3);
    }

    /**
     * Returns the fragment ions map.
     * @return the fragment ions map
     */
	public Map<Ion.IonType, FragmentIon[]> getFragmentIons() {
    	return this.fragIonsMap;

    }

    /**
     * Returns the corresponding array of theoretic ions.
     */
    public FragmentIon[] getTheoreticalIons(Ion.IonType type) {
    	return (type != null) ? this.fragIonsMap.get(type) : null;
    }
}

