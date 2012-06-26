package de.mpa.analysis;

import java.util.HashMap;
import java.util.Map;

import de.mpa.util.Formatter;

/**
 * The ECNumber class is a representation of a certain enzyme class. 
 * @author T.Muth
 *
 */
public class ECNumber {
	
	/**
	 * The EC number classes.
	 */
	private int[] classes;
	
	/**
	 * The level one mape.  
	 */
	private Map<Integer, String> levelOneMap;
	
	/**
	 * Undefined sign, e.g. - or * 
	 */
	private final static int UNDEFINED = 0;
	
	/**
	 * The ECNumber constructor. 
	 * @param ecNumber The EC number. 
	 */
	public ECNumber(String ecNumber) {
		fillLevelMap();
		parse(ecNumber);
	}
	
	/**
	 * This method parses the EC number. 
	 * @param ecNumber The EC number.
	 */
	private void parse(String ecNumber) {
		classes = new int[4];
		String[] temp = ecNumber.split("\\.");
		for (int i = 0; i < temp.length; i++) {
			if(Formatter.isNumeric(temp[i])) {
				classes[i] = Integer.valueOf(temp[i]);
			} else {
				classes[i] = UNDEFINED;
			}
		}
	}
	
	/**
	 * Returns the class number.
	 * @param level The class level.
	 * @return The class number.
	 */
	public int getClassNumber(int level) {
		return classes[level];
	}
	
	/**
	 * Returns the class name of the enzyme class.
	 * @param level The class level.
	 * @return The class name.
	 */
	public String getClassName(int level) {
		if(level == 0){
			return classes[0] + " - " + levelOneMap.get(classes[0]);
		} else {
			String classname = "";
			for (int i = 0; i <= level; i++){
				classname += classes[i] + ".";
			}
			return classname.substring(0, classname.length() - 1);
		}
	}
	
	/**
	 * Fills the level one map.
	 */
	private void fillLevelMap(){
		if (levelOneMap == null) {
			levelOneMap = new HashMap<Integer, String>();
			levelOneMap.put(1, "Oxidoreductase");
			levelOneMap.put(2, "Transferase");
			levelOneMap.put(3, "Hydrolase");
			levelOneMap.put(4, "Lyase");
			levelOneMap.put(5, "Isomerase");
			levelOneMap.put(6, "Ligase");
		}
		
	}
}
