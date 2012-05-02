package de.mpa.algorithms.denovo;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import de.mpa.analysis.Masses;

public class DiPeptideMap extends HashMap<Double, String> {
	
	/**
	 * Instance of the DiPeptideMap.
	 */
	private static DiPeptideMap instance;
	
	/**
     * Returns the instance of a DiPeptideMap object.
     * @return
     */
	public static DiPeptideMap getInstance() {
		if (instance == null)
			instance = new DiPeptideMap();
		return instance;
	}

	private HashMap<String, Double> reducedMap;
	
    /**
     * Private constructor for the singleton convention.
     */
    private DiPeptideMap(){
    	fillReducedMap();
    	calculateDiPeptides();
    }
	
    /**
     * Calculates the di-peptides.
     */
	private void calculateDiPeptides(){
		Set<Entry<String, Double>> entrySet = reducedMap.entrySet();
		
		double mass;
		String seq;
		for (Entry<String, Double> e1 : entrySet) {
			for (Entry<String, Double> e2 : entrySet) {
				mass = e1.getValue() + e2.getValue();
				seq = e1.getKey() + e2.getKey();
				if(!this.containsKey(mass)){
					this.put(mass, seq);
				}
				
			}
		}
	}
	
	/**
	 * This map contains only the 20 relevant amino acids.
	 * @return The reduced map
	 */
	private void fillReducedMap(){
		reducedMap = new HashMap<String, Double>();
		reducedMap.put("A", Masses.A);
		reducedMap.put("C", Masses.C);
		reducedMap.put("D", Masses.D);
		reducedMap.put("E", Masses.E);
		reducedMap.put("F", Masses.F);
		reducedMap.put("G", Masses.G);
		reducedMap.put("H", Masses.H);
		reducedMap.put("I", Masses.I);
		reducedMap.put("K", Masses.K);
		reducedMap.put("L", Masses.L);
		reducedMap.put("M", Masses.M);
		reducedMap.put("N", Masses.N);
		reducedMap.put("P", Masses.P);
		reducedMap.put("Q", Masses.Q);
		reducedMap.put("R", Masses.R);
		reducedMap.put("S", Masses.S);
		reducedMap.put("T", Masses.T);
		reducedMap.put("V", Masses.V);
		reducedMap.put("W", Masses.W);
		reducedMap.put("Y", Masses.Y);
	}

}
