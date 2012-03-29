package de.mpa.fragmentation;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds all the masses used for the calculation of theoretical masses.
 *
 * @author Thilo Muth
 */
public class Masses {

    // The masses for all the amino acids including empty values for non-amino acid letters
    // Update on 05/25/10: Using NEW mono isotopic reference masses due to wrong values. 

    //public static final double A = 71.037110;
    public static final double A = 71.037110;
    public static final double B = 114.534930;
    //public static final double C = 160.030649;
    public static final double C = 103.009185;
    //public static final double D = 115.026940;
    public static final double D = 115.026943;
    //public static final double E = 129.042590;
    public static final double E = 129.042593;
    //public static final double F = 147.068410;
    public static final double F = 147.068414;
    //public static final double G = 57.021460;
    public static final double G = 57.021464;
    //public static final double H = 137.058910;
    public static final double H = 137.058912;
    //public static final double I = 113.084060;
    public static final double I = 113.084064;    
    public static final double J = 0.000000;
    public static final double K = 128.094963;
    
    //public static final double L = 113.084060;
    public static final double L = 113.084064;
    //public static final double M = 131.040490;
    public static final double M = 131.040485;
    //public static final double N = 114.042930;
    public static final double N = 114.042927;
    public static final double O = 0.000000;
    //public static final double P = 97.052760;
    public static final double P = 97.052764;
    //public static final double Q = 128.058580;
    public static final double Q = 128.058578;
    //public static final double R = 156.101110;
    public static final double R = 156.101111;
    //public static final double S = 87.032030;
    public static final double S = 87.032028;
    //public static final double T = 101.047680;
    public static final double T = 101.047679;
    public static final double U = 0.000000;
    //public static final double V = 99.068410;
    public static final double V = 99.068414;
    //public static final double W = 186.079310;
    public static final double W = 186.079313;
    public static final double X = 111.000000;
    //public static final double Y = 163.063330;
    public static final double Y = 163.06332;
    public static final double Z = 128.550590;

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
     * This method returns a hash map for the masses.
     *
     * @return map The masses hashmap
     */
    public static Map<String, Double> getMap() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("A", Masses.A);
        map.put("B", Masses.B);
        map.put("C", Masses.C);
        map.put("D", Masses.D);
        map.put("E", Masses.E);
        map.put("F", Masses.F);
        map.put("G", Masses.G);
        map.put("H", Masses.H);
        map.put("I", Masses.I);
        map.put("J", Masses.J);
        map.put("K", Masses.K);
        map.put("L", Masses.L);
        map.put("M", Masses.M);
        map.put("N", Masses.N);
        map.put("O", Masses.O);
        map.put("P", Masses.P);
        map.put("Q", Masses.Q);
        map.put("R", Masses.R);
        map.put("S", Masses.S);
        map.put("T", Masses.T);
        map.put("U", Masses.U);
        map.put("V", Masses.V);
        map.put("W", Masses.W);
        map.put("X", Masses.X);
        map.put("Y", Masses.Y);
        map.put("Z", Masses.Z);
        map.put("Hydrogen", Masses.Hydrogen);
        map.put("Carbon", Masses.Carbon);
        map.put("Nitrogen", Masses.Nitrogen);
        map.put("Oxygen", Masses.Oxygen);
        map.put("Electron", Masses.Electron);
        map.put("C_term", Masses.C_term);
        map.put("N_term", Masses.N_term);
        return map;
    }
}
