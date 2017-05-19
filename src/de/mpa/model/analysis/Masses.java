package de.mpa.model.analysis;

import java.util.HashMap;

/**
 * This class holds all the masses used for the calculation of theoretical masses.
 *
 * @author Thilo Muth
 */
@SuppressWarnings("serial")
public class Masses extends HashMap<String, Double> {

	/**
     * Singleton Masses object.
     */
    private static Masses instance;
    
    // The masses for all the amino acids including empty values for non-amino acid letters
    public static final double A = 71.037110;
    public static final double C = 103.009185;
    public static final double D = 115.026943;
    public static final double E = 129.042593;
    public static final double F = 147.068414;
    public static final double G = 57.021464;
    public static final double H = 137.058912;
    public static final double I = 113.084064;    
    public static final double K = 128.094963;
    public static final double L = 113.084064;
    public static final double M = 131.040485;
    public static final double N = 114.042927;
    // Pyrrolysine
    public static final double O = 237.147727;
    public static final double P = 97.052764;
    public static final double Q = 128.058578;
    public static final double R = 156.101111;
    public static final double S = 87.032028;
    public static final double T = 101.047679;
    // Selenocysteine
    public static final double U = 150.953636;
    public static final double V = 99.068414;
    public static final double W = 186.079313;
    public static final double Y = 163.06332;
    public static final double X = 0.0;

    /**
     * The mass of Hydrogen
     */
    public static final double Hydrogen = 1.007825;
    /**
     * The mass of Carbon
     */
    public static final double Carbon = 12.000000;
    /**
     * The mass of Nitrogen
     */
    public static final double Nitrogen = 14.003070;
    /**
     * The mass of Oxygen
     */
    public static final double Oxygen = 15.994910;
    /**
     * The mass of an electron
     */
    public static final double Electron = 0.005490;
    /**
     * The mass of the C Terminus = Oxygen + Hydrogen
     */
    public static final double C_term = 17.002735;
    /**
     * The mass of the N Terminus = Hydrogen
     */
    public static final double N_term = 1.007825;
    
    /**
     * Private constructor for the singleton convention.
     */
    private Masses(){
        this.fillMap();
    }
    
    /**
     * Fill the masses map with key and values.
     */
    private void fillMap() {
        put("A", A);
        put("C", C);
        put("D", D);
        put("E", E);
        put("F", F);
        put("G", G);
        put("H", H);
        put("I", I);
        put("K", K);
        put("L", L);
        put("M", M);
        put("N", N);
        put("O", O);
        put("P", P);
        put("Q", Q);
        put("R", R);
        put("S", S);
        put("T", T);
        put("U", U);
        put("V", V);
        put("W", W);
        put("Y", Y);
        put("Hydrogen", Hydrogen);
        put("Carbon", Carbon);
        put("Nitrogen", Nitrogen);
        put("Oxygen", Oxygen);
        put("Electron", Electron);
        put("C_term", C_term);
        put("N_term", N_term);
	}

	/**
     * Returns the instance of a masses object.
     * @return
     */
	public static Masses getInstance() {
		if (Masses.instance == null)
            Masses.instance = new Masses();
		return Masses.instance;
	}
}
