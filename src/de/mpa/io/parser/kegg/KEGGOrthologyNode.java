package de.mpa.io.parser.kegg;

import java.util.ArrayList;
import java.util.List;

/**
 * KEGG node implementation for KEGG Orthology htext file lines.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class KEGGOrthologyNode extends KEGGNode {

	/**
	 * The description.
	 */
	private String description;
	
	/**
	 * The list of E.C. numbers.
	 */
	private List<String> ecNumbers;

	/**
	 * 
	 * @param name
	 */
	public KEGGOrthologyNode(String name) {
		this(name, null);
	}

	/**
	 * 
	 * @param name
	 * @param description
	 */
	public KEGGOrthologyNode(String name, String description) {
		this(name, description, null);
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param ecNumbers
	 */
	public KEGGOrthologyNode(String name, String description, List<String> ecNumbers) {
		super(name);
		this.description = description;
		this.ecNumbers = ecNumbers;
	}

	@Override
	public KEGGOrthologyNode createNode(String line) {
		KEGGOrthologyNode child = null;
		// extract depth-denoting character
		char depthChar = line.charAt(0);
		// prune depth-denoting character and leading whitespace
		line = line.substring(1).trim();
		// ignore empty lines
		if (line.isEmpty()) {
			return null;
		}
		// process line specific to depth
		switch (depthChar) {
			case 'A':	// category node, e.g. "<b>Metabolism</b>"
			case 'B':	// sub-category node, e.g. "<b>Overview</b>"
				// prune HTML markup
				line = line.replaceAll("\\<.*?>","");
				child = new KEGGOrthologyNode(line);
				break;
			case 'C':	// pathway node, e.g. "01200 Carbon metabolism [PATH:ko01200]"
				// tokenize line
				String[] splitC = line.split(" ", 2);
				// dissect line tokens
				String nameC = splitC[0];
				String descC = splitC[1].substring(0, splitC[1].lastIndexOf(" "));
				child = new KEGGOrthologyNode(nameC, descC);
				break;
			case 'D':	// K number node, e.g. "K00844  HK; hexokinase [EC:2.7.1.1]"
				// tokenize line into name and description pairs
				String[] split = line.split("  ");
				// dissect line tokens
				String nameD = split[0];
				String descD = split[1];
				// further tokenize description
				String[] splitD = descD.split(" \\[EC:");
				// dissect description tokens
				descD = splitD[0];
				if (splitD.length > 1) {
					// one or more E.C. numbers are suffixed to the line
					String ec = splitD[1].substring(0, splitD[1].length() - 1);
					// further tokenize E.C. number string section
					splitD = ec.split(" ");
					// dissect E.C. number tokens
					List<String> ecNumbers = new ArrayList<>();
					for (String ecNumber : splitD) {
						ecNumbers.add(ecNumber);
					}
					child = new KEGGOrthologyNode(nameD, descD, ecNumbers);
				} else {
					// no E.C. numbers suffix
					child = new KEGGOrthologyNode(nameD, descD);
				}
				break;
			default:
				// we should not get here, throw warning
				System.err.println("WARNING: unrecognized character: " + depthChar);
				return null;
		}
		return child;
	}

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return (String) userObject;
	}

	/**
	 * Returns the description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the list of E.C. numbers
	 * @return the list of E.C. numbers
	 */
	public List<String> getECNumbers() {
		return ecNumbers;
	}

}
