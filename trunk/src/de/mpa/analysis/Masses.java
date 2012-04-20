package de.mpa.analysis;

import java.util.HashMap;

/**
 * This class holds all the masses used for the calculation of theoretical masses.
 *
 * @author Thilo Muth
 */
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
    public static final double P = 97.052764;
    public static final double Q = 128.058578;
    public static final double R = 156.101111;
    public static final double S = 87.032028;
    public static final double T = 101.047679;
    public static final double V = 99.068414;
    public static final double W = 186.079313;
    public static final double Y = 163.06332;

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
    	fillMap();
    }
    
    /**
     * Fill the masses map with key and values.
     */
    private void fillMap() {
    	 this.put("A", Masses.A);
         this.put("C", Masses.C);
         this.put("D", Masses.D);
         this.put("E", Masses.E);
         this.put("F", Masses.F);
         this.put("G", Masses.G);
         this.put("H", Masses.H);
         this.put("I", Masses.I);
         this.put("K", Masses.K);
         this.put("L", Masses.L);
         this.put("M", Masses.M);
         this.put("N", Masses.N);
         this.put("P", Masses.P);
         this.put("Q", Masses.Q);
         this.put("R", Masses.R);
         this.put("S", Masses.S);
         this.put("T", Masses.T);
         this.put("V", Masses.V);
         this.put("W", Masses.W);
         this.put("Y", Masses.Y);
         this.put("Hydrogen", Masses.Hydrogen);
         this.put("Carbon", Masses.Carbon);
         this.put("Nitrogen", Masses.Nitrogen);
         this.put("Oxygen", Masses.Oxygen);
         this.put("Electron", Masses.Electron);
         this.put("C_term", Masses.C_term);
         this.put("N_term", Masses.N_term);
	}

	/**
     * Returns the instance of a masses object.
     * @return
     */
	public static Masses getInstance() {
		if (instance == null)
			instance = new Masses();
		return instance;
	}
}
