package de.mpa.io.parser.kegg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.mpa.client.Constants;
import de.mpa.main.Starter;

/**
 * Class providing parsing capabilities for KEGG database files.
 * @author A. Behne
 */
public class KEGGReader {
	
	/**
	 * TODO: API
	 * @param path
	 * @return
	 */
	public static KEGGNode readKEGGTree() {
		KEGGNode root = new KEGGOrthologyNode("root");
		KEGGNode parent = root;
		KEGGNode child = null;
		String path = null;
		Reader in = null;
		
		path = "" + Constants.CONFIGURATION_PATH_JAR + Constants.SEP + "ko00001.keg";
		
		try {
			in = new FileReader(new File(path));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		
		
		
		try (BufferedReader br = new BufferedReader(in)) {
			char currentDepth = 'A';
			String line;
			while ((line = br.readLine()) != null) {
				// check whether line starts with upper case letter
				if (line.matches("^[A-Z].*")) {
					// create new child node
					KEGGNode newChild = root.createNode(line);
					if (newChild != null) {
						// determine depth
						char depth = line.charAt(0);
						// check whether depth changed
						if (depth < currentDepth) {
							// move up in hierarchy
							int delta = currentDepth - depth;
							for (int i = 0; i < delta; i++) {
								parent = (KEGGNode) parent.getParent();
							}
						} else if (depth > currentDepth) {
							// last created node becomes new parent
							parent = child;
						}
						currentDepth = depth;
						// insert child into tree hierarchy
						parent.add(newChild);
						child = newChild;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	public static TreeNode readKeggTree(InputStream is) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char oldID = 0;
			int dist = 1;
			TreeNode parent = null;
			TreeNode child = root;
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// check whether line starts with upper case letter
				if (currentLine.matches("^[A-Z].*")) {
					// prune HTML markup
					currentLine = currentLine.replaceAll("\\<.*?>","");
					// isolate identifier
					char newID = currentLine.charAt(0);
					// prune whitespaces and identifier from line
					currentLine = currentLine.replaceAll("^[A-Z]\\s*", "");
					// check whether line contains any data
					if (!currentLine.isEmpty()) {
						// check whether the hierarchy depth level has changed
						if (oldID != 0) {
							dist = newID - oldID;
						}
						if (dist > 0) {
							// go down in hierarchy
							parent = child;
						} else if (dist < 0) {
							// go up in hierarchy
							for (int i = 0; i > dist; i--) {
								parent = parent.getParent();
							}
						}
						// store in node
						child = new DefaultMutableTreeNode(currentLine);
						// add node to parent
						((DefaultMutableTreeNode) parent).add((MutableTreeNode) child);
						// store ID for later use
						oldID = newID;
					}
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * Parses a KEGG map text file contained in the specified input stream and
	 * stores contents which satisfy the provided regular expression condition
	 * into a map object.
	 * 
	 * @param filePath The path string pointing to the file to be parsed.
	 * @param regex The regular expression string for matching contents.
	 * @return The KEGG map.
	 */
	public static Map<String, Character> readKeggMap(InputStream is, String regex) {
		Map<String, Character> keggMap = new LinkedHashMap<String, Character>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				// check whether line matches regular expression pattern
				if (currentLine.matches(regex)) {
					// prune HTML markup
					currentLine = currentLine.replaceAll("\\<.*?>","");
					// split line, prune whitespaces
					char identifier = currentLine.charAt(0);
					currentLine = currentLine.substring(1).trim();
					if (!currentLine.isEmpty()) {
						// put value in map
						keggMap.put(currentLine, identifier);
					}
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keggMap;
	}
	
	/**
	 * Parses the file contained in the specified input stream and reads KEGG pathway data from it.
	 * <br>
	 * Source: <a href="http://www.genome.jp/kegg-bin/get_htext?htext=br08901.keg&hier=2">
	 * http://www.genome.jp/kegg-bin/get_htext?htext=br08901.keg&hier=2</a>
	 * 
	 * @param path The file input stream pointing to the file to be parsed.
	 * @return The KEGG pathway map.
	 */
	public static Map<String, Character> readKeggPathways(InputStream is) {
		return KEGGReader.readKeggMap(is, "^[ABC].*");
	}
	
	/**
	 * Parses the file contained in the specified input stream and reads KEGG organism data from it.
	 * <br>
	 * Source: <a href="http://www.genome.jp/kegg-bin/get_htext?htext=br08601.keg&hier=4">
	 * http://www.genome.jp/kegg-bin/get_htext?htext=br08601.keg&hier=4</a>
	 * 
	 * @param The file input stream pointing to the file to be parsed.
	 * @return The KEGG taxonomy map.
	 */
	public static Map<String, Character> readKeggOrganisms(InputStream is) {
		return KEGGReader.readKeggMap(is, "^[ABCDE].*");
	}
	
}
