package de.mpa.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the pIs of the amino acids.<br>
 * Source: <a href ="http://www.mhhe.com/physsci/chemistry/carey5e/Ch27/ch27-1-4-2.html">
 * http://www.mhhe.com/physsci/chemistry/carey5e/Ch27/ch27-1-4-2.html</a>
 * source of here titled expasy list: http://old.nabble.com/Expasy-pI-calculation-algorythm-td31300241.html
 * 
 * @author heyer, kohrs
 */
public class IsoelectricPoints {

	
// Expasy	
	public static final Map<Character, Double> pKaCtermMap;
	static {
		Map<Character, Double> map = new HashMap<Character, Double>(20);
		map.put('A', 3.55);
		map.put('C', 3.55);
		map.put('D', 4.55);
		map.put('E', 4.75);
		map.put('F', 3.55);
		map.put('G', 3.55);
		map.put('H', 3.55);
		map.put('I', 3.55);
		map.put('K', 3.55);
		map.put('L', 3.55);
		map.put('M', 3.55);
		map.put('N', 3.55);
		map.put('P', 3.55);
		map.put('Q', 3.55);
		map.put('R', 3.55);
		map.put('S', 3.55);
		map.put('T', 3.55);
		map.put('V', 3.55);
		map.put('W', 3.55);
		map.put('Y', 3.55);
		pKaCtermMap = Collections.unmodifiableMap(map);
	}
	
	public static final Map<Character, Double> pKaNtermMap;
	static {
		Map<Character, Double> map = new HashMap<Character, Double>(20);
		map.put('A', 7.59);
		map.put('C', 7.50);
		map.put('D', 7.50);
		map.put('E', 7.70);
		map.put('F', 7.50);
		map.put('G', 7.50);
		map.put('H', 7.50);
		map.put('I', 7.50);
		map.put('K', 7.50);
		map.put('L', 7.50);
		map.put('M', 7.00);
		map.put('N', 7.50);
		map.put('P', 8.36);
		map.put('Q', 7.50);
		map.put('R', 7.50);
		map.put('S', 6.93);
		map.put('T', 6.82);
		map.put('V', 7.44);
		map.put('W', 7.50);
		map.put('Y', 7.50);
		pKaNtermMap = Collections.unmodifiableMap(map);
	}
	
	public static final Map<Character, Double> pKaSideChainMap;
	static {
		Map<Character, Double> map = new HashMap<Character, Double>(20);
		map.put('C', 9.0);
		map.put('D', 4.05);
		map.put('E', 4.45);
		map.put('H', 5.98);
		map.put('K',10.00);
		map.put('R',12.00);
		map.put('Y',10.00);
		pKaSideChainMap = Collections.unmodifiableMap(map);
	}	
}
