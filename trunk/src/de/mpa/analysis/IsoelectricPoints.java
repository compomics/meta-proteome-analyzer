package de.mpa.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the pIs of the amino acids.<br>
 * Source: <a href ="http://www.mhhe.com/physsci/chemistry/carey5e/Ch27/ch27-1-4-2.html">
 * http://www.mhhe.com/physsci/chemistry/carey5e/Ch27/ch27-1-4-2.html</a>
 * 
 * @author heyer, kohrs
 */
public class IsoelectricPoints {

	// The pIs of all aminoacids
	public static final double G = 5.97;	// Glycine
	public static final double A = 6.00;	// Alanine
	public static final double V = 5.96;	// Valine
	public static final double L = 5.98;	// Leucine
	public static final double I = 6.02;	// Isoleucine
	public static final double M = 5.74;	// Methionine
	public static final double P = 6.30;	// Proline
	public static final double F = 5.48;	// Phenylalanine
	public static final double W = 5.89;	// Tryptophane
	public static final double N = 5.41;	// Asparagine
	public static final double Q = 5.65;	// Gluatamine
	public static final double S = 5.68;	// Serine
	public static final double T = 5.60;	// Threonine
	public static final double Y = 5.66;	// Tyrosine
	public static final double C = 5.07;	// Cysteine
	public static final double D = 2.77;	// Aspartic acid
	public static final double E = 3.22;	// Glutamic acid
	public static final double K = 9.74;	// Lysine
	public static final double R = 10.76;	// Arginine
	public static final double H = 7.59;	// Histidine
	
	public static final Map<Character, Double> pIMap;
	static {
		Map<Character, Double> map = new HashMap<Character, Double>(20);
		map.put('A', A);	map.put('C', C);	map.put('D', D);	map.put('E', E);
		map.put('F', F);	map.put('G', G);	map.put('H', H);	map.put('I', I);
		map.put('K', K);	map.put('L', L);	map.put('M', M);	map.put('N', N);
		map.put('P', P);	map.put('Q', Q);	map.put('R', R);	map.put('S', S);
		map.put('T', T);	map.put('V', V);	map.put('W', W);	map.put('Y', Y);
		pIMap = Collections.unmodifiableMap(map);
	}

}
