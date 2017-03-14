package de.mpa.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class holds all amino acid + other chemical relevant masses.
 * @author Thilo Muth
 *
 */
public class Masses {	
	
	/**
	 * The amino acid hash map.
	 */
	public static Map<Character, Double> aaMap;	
	/**
	 * The amino acid letters.
	 */
	public static Set<Character> letters = new TreeSet<Character>();	
	/**
	 * The tryptic amino acid letters.
	 */
	public static Set<Character> trypticLetters = new TreeSet<Character>();	
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
	 * The mass of the C Terminus = Oxygen + 3* Hydrogen
	 */
	public static final double C_term = 19.017837;
	/**
	 * The mass of the N Terminus = Hydrogen
	 */
	public static final double N_term = 1.007825;
	
	/**
	 * Initializes the amino acid hash map which holds the 20 amino acids 
	 * with their characters as keys and their masses as values.
	 */
	public static void init(){
		// The 20 amino acids in the map.
        Masses.aaMap = new HashMap<Character, Double>(20);
		// Fill the map
		// Alanine
        Masses.aaMap.put('A', 71.037110);
		// Cysteine
        Masses.aaMap.put('C', 160.030649);
		// Aspartic Acid
        Masses.aaMap.put('D', 115.026940);
		// Glutatmatic Acid
        Masses.aaMap.put('E', 129.042590);
		// Phenylalanine
        Masses.aaMap.put('F', 147.068410);
		// Glycin
        Masses.aaMap.put('G', 57.021460);
		// Histidine
        Masses.aaMap.put('H', 137.0589116);
		// Isoleucine
        Masses.aaMap.put('I', 113.084060);
		// Leucine
        Masses.aaMap.put('L', 113.084060);
		// Lysine
        Masses.aaMap.put('K', 128.094963);
		// Methionine
        Masses.aaMap.put('M', 131.040490);
		// Asparagine
        Masses.aaMap.put('N', 114.042930);
		// Proline
        Masses.aaMap.put('P', 97.052760);
		// Glutamine
        Masses.aaMap.put('Q', 128.058580);
		// Arginine
        Masses.aaMap.put('R', 156.101110);
		// Serine
        Masses.aaMap.put('S', 87.032030);
		// Threonine
        Masses.aaMap.put('T', 101.047680);
		// Valine
        Masses.aaMap.put('V', 99.068410);
		// Tryptophan
        Masses.aaMap.put('W', 186.079310);
		// Tyrosine
        Masses.aaMap.put('Y', 163.063330);
		
		// Add the keys to the letters.
        Masses.letters.addAll(Masses.aaMap.keySet());
		
		// Trypsin main cleaves Lysine or Arginine
        Masses.trypticLetters.add('K');
        Masses.trypticLetters.add('R');
	}
}



