package de.mpa.analysis;

import keggapi.KEGGLocator;
import de.mpa.client.Constants;
import de.mpa.io.parser.ec.ECReader;
import keggapi.KEGGPortType;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.rpc.ServiceException;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class providing access to KEGG remote service and local dumps of database contents.
 * 
 * @author A. Behne
 */
@Deprecated
public class KeggAccessor {
	
	/**
	 * The accessor instance.
	 */
	private static KeggAccessor instance = null;

	/**
	 * The KEGG port type instance.
	 */
	private KEGGPortType serv;

	/**
	 * Map containing KO-to-pathways pairs.
	 */
	private Map<Short,List<Short>> ko2pathway;

	/**
	 * Map containing pathway-to-KOs pairs.
	 */
	private Map<Short,List<Short>> pathway2ko;

	/**
	 * Map containing E.C.-to-pathways pairs.
	 */
	private Map<short[],List<Short>> ec2pathway;

	/**
	 * Map containing pathway-to-E.C. pairs
	 */
	private Map<Short,List<short[]>> pathway2ec;

	/**
	 * Private singleton constructor.
	 */
	private KeggAccessor() {
        try {
    		KEGGLocator  locator = new KEGGLocator();
			serv = locator.getKEGGPort();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the KEGG accessor instance.
	 * @return the KEGG accessor instance.
	 */
	public static KeggAccessor getInstance() {
		if (instance == null) {
			instance = new KeggAccessor();
		}
		return instance;
	}
	
	/**
	 * Returns the KEGG port type.
	 * @return the KEGG port type.
	 */
	public KEGGPortType getKeggPort() {
		return serv;
	}

	/**
	 * Convenience method to return the value of a single KO-to-pathways mapping
	 * where the K number is in String representation.
	 * @return A list of pathways mapped to the specified K number.
	 */
	public List<Short> getPathwaysByKO(String ko) {
		if (ko.startsWith("K")) {
			ko = ko.substring(1);
		} else {
			return null;
		}
		return getPathwaysByKO(Short.parseShort(ko));
	}

	/**
	 * Returns the value of a single KO-to-pathways mapping.
	 * @param ko The K number.
	 * @return A list of pathways mapped to the specified K number.
	 */
	public List<Short> getPathwaysByKO(Short ko) {
		if (ko2pathway == null) {
			readDumpedKeggPathways();
		}
		return ko2pathway.get(ko);
	}
	
	/**
	 * Returns the value of a single pathway-to-KOs mapping.
	 * @param pw The pathway ID.
	 * @return A list of K numbers mapped to the specified pathway ID.
	 */
	public List<Short> getKOsByPathway(Short pw) {
		if (pathway2ko == null) {
			readDumpedKeggPathways();
		}
		return pathway2ko.get(pw);
	}

	/**
	 * Convenience method to return the value of a single EC-to-pathways mapping
	 * where the EC number is in String representation.
	 * @param ec The EC number in String representation.
	 * @return A list of pathways mapped to the specified EC number.
	 */
	public List<Short> getPathwaysByEC(String ec) {
		return getPathwaysByEC(ECReader.toArray(ec));
	}

	/**
	 * Returns the value of a single EC-to-pathways mapping.
	 * @param ec The EC number.
	 * @return A list of pathways mapped to the specified EC number.
	 */
	public List<Short> getPathwaysByEC(short[] ec) {
		if (ec2pathway == null) {
			readDumpedKeggPathways();
		}
		return ec2pathway.get(ec);
	}
	
	/**
	 * Returns the value of a single pathway-to-ECs mapping.
	 * @param pw The pathway ID.
	 * @return A list of EC numbers mapped to the specified pathway ID.
	 */
	public List<short[]> getECsByPathway(Short pw) {
		if (pathway2ec == null) {
			readDumpedKeggPathways();
		}
		return pathway2ec.get(pw);
	}
	
	/**
	 * Reads dumped pathway-to-KOs and KO-to-pathways maps from a local file.
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found.
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void readDumpedKeggPathways() {
		try {
			// read dumped file contents
//			File input = new File("conf/keggKO2PW.map");
			InputStream is = getClass().getResourceAsStream("/de/mpa/resources/conf/keggKO2PW.map");
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(is)));

			ko2pathway = (HashMap<Short, List<Short>>) ois.readObject();
			pathway2ko = (HashMap<Short, List<Short>>) ois.readObject();
			
//			ec2pathway = (HashMap<short[], List<Short>>) ois.readObject();
			ec2pathway = new TreeMap<short[], List<Short>>(
					new Comparator<short[]>() {
						public int compare(short[] o1, short[] o2) {
							int delta = 0;
							for (int i = 0; i < o1.length; i++) {
								delta = o1[i] - o2[i];
								if (delta != 0) {
									break;
								}
							}
							return delta;
						}
			});
			ec2pathway.putAll((Map<? extends short[], ? extends List<Short>>) ois.readObject());
			
			pathway2ec = (HashMap<Short, List<short[]>>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dumps pathway-to-KOs and KO-to-pathways mappings to a local file.
	 * 
	 * @throws IOException if remote data retrieval or local writing fails.
	 */
	@SuppressWarnings("unchecked")
	public void dumpRemoteKeggPathways() throws IOException {
		List<Short> pathwayIDs = new ArrayList<Short>();
		
		// get pathway tree structure
		TreeNode pathwayRoot = Constants.KEGG_ORTHOLOGY_MAP.getRoot();
		Enumeration<TreeNode> dfEnum = ((DefaultMutableTreeNode) pathwayRoot).depthFirstEnumeration();
		// iterate nodes of pathway
		while (dfEnum.hasMoreElements()) {
			TreeNode treeNode = dfEnum.nextElement();
			// extract data from leaf nodes only
			if (treeNode.isLeaf()) {
				String leafName = (String) ((DefaultMutableTreeNode) treeNode).getUserObject();
				// extract numeric identifier and store in list
				pathwayIDs.add(Short.parseShort(leafName.substring(0, 5)));
			}
		}

		ko2pathway = new HashMap<Short, List<Short>>();
		pathway2ko = new HashMap<Short, List<Short>>();
		ec2pathway = new HashMap<short[], List<Short>>();
		pathway2ec = new HashMap<Short, List<short[]>>();
		int i = 1;
		// iterate list of pathway IDs
		for (Short pathwayID : pathwayIDs) {
			System.out.println("" + (i++) + "/" + pathwayIDs.size());
			String pathway = "path:map" + String.format("%05d", pathwayID);
			
			String[] koStrings = serv.get_kos_by_pathway(pathway);
			List<Short> koList = new ArrayList<Short>();
			for (String koString : koStrings) {
				Short ko = Short.parseShort(koString.substring(4));
				List<Short> pathwayList = ko2pathway.get(ko);
				if (pathwayList == null) {
					pathwayList = new ArrayList<Short>();
				}
				if (!pathwayList.contains(pathwayID)) {
					pathwayList.add(pathwayID);
				}
				ko2pathway.put(ko, pathwayList);
				
				koList.add(ko);
			}
			if (!koList.isEmpty()) {
				pathway2ko.put(pathwayID, koList);
			}

			String[] ecStrings = serv.get_enzymes_by_pathway(pathway);
			List<short[]> ecList = new ArrayList<short[]>();
			for (String ecString : ecStrings) {
				short[] ec = ECReader.toArray(ecString.substring(3));
				List<Short> pathwayList = ec2pathway.get(ec);
				if (pathwayList == null) {
					pathwayList = new ArrayList<Short>();
				}
				if (!pathwayList.contains(pathwayID)) {
					pathwayList.add(pathwayID);
				}
				ec2pathway.put(ec, pathwayList);
				
				ecList.add(ec);
			}
			pathway2ec.put(pathwayID, ecList);
			
		}
		
		// dump objects to file
		File output = new File("conf/keggKO2PW.map");
		FileOutputStream fos = new FileOutputStream(output);
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(fos)));

		oos.writeObject(ko2pathway);
		oos.writeObject(pathway2ko);
		oos.writeObject(ec2pathway);
		oos.writeObject(pathway2ec);
		oos.flush();
		oos.close();
	}

}
