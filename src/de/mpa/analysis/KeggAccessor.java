package de.mpa.analysis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;
import de.mpa.client.Constants;
import de.mpa.io.parser.ec.ECReader;

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
	private static KeggAccessor instance;

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
            this.serv = locator.getKEGGPort();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the KEGG accessor instance.
	 * @return the KEGG accessor instance.
	 */
	public static KeggAccessor getInstance() {
		if (KeggAccessor.instance == null) {
            KeggAccessor.instance = new KeggAccessor();
		}
		return KeggAccessor.instance;
	}
	
	/**
	 * Returns the KEGG port type.
	 * @return the KEGG port type.
	 */
	public KEGGPortType getKeggPort() {
		return this.serv;
	}

	/**
	 * Convenience method to return the value of a single KO-to-pathways mapping
	 * where the K number is in String representation.
	 * @param ec The K number in String representation.
	 * @return A list of pathways mapped to the specified K number.
	 */
	public List<Short> getPathwaysByKO(String ko) {
		if (ko.startsWith("K")) {
			ko = ko.substring(1);
		} else {
			return null;
		}
		return this.getPathwaysByKO(Short.parseShort(ko));
	}

	/**
	 * Returns the value of a single KO-to-pathways mapping.
	 * @param ko The K number.
	 * @return A list of pathways mapped to the specified K number.
	 */
	public List<Short> getPathwaysByKO(Short ko) {
		if (this.ko2pathway == null) {
            this.readDumpedKeggPathways();
		}
		return this.ko2pathway.get(ko);
	}
	
	/**
	 * Returns the value of a single pathway-to-KOs mapping.
	 * @param pw The pathway ID.
	 * @return A list of K numbers mapped to the specified pathway ID.
	 */
	public List<Short> getKOsByPathway(Short pw) {
		if (this.pathway2ko == null) {
            this.readDumpedKeggPathways();
		}
		return this.pathway2ko.get(pw);
	}

	/**
	 * Convenience method to return the value of a single EC-to-pathways mapping
	 * where the EC number is in String representation.
	 * @param ec The EC number in String representation.
	 * @return A list of pathways mapped to the specified EC number.
	 */
	public List<Short> getPathwaysByEC(String ec) {
		return this.getPathwaysByEC(ECReader.toArray(ec));
	}

	/**
	 * Returns the value of a single EC-to-pathways mapping.
	 * @param ec The EC number.
	 * @return A list of pathways mapped to the specified EC number.
	 */
	public List<Short> getPathwaysByEC(short[] ec) {
		if (this.ec2pathway == null) {
            this.readDumpedKeggPathways();
		}
		return this.ec2pathway.get(ec);
	}
	
	/**
	 * Returns the value of a single pathway-to-ECs mapping.
	 * @param pw The pathway ID.
	 * @return A list of EC numbers mapped to the specified pathway ID.
	 */
	public List<short[]> getECsByPathway(Short pw) {
		if (this.pathway2ec == null) {
            this.readDumpedKeggPathways();
		}
		return this.pathway2ec.get(pw);
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
			InputStream is = this.getClass().getResourceAsStream("/de/mpa/resources/conf/keggKO2PW.map");
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(is)));

            this.ko2pathway = (HashMap<Short, List<Short>>) ois.readObject();
            this.pathway2ko = (HashMap<Short, List<Short>>) ois.readObject();
			
//			ec2pathway = (HashMap<short[], List<Short>>) ois.readObject();
            this.ec2pathway = new TreeMap<short[], List<Short>>(
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
            this.ec2pathway.putAll((Map<? extends short[], ? extends List<Short>>) ois.readObject());

            this.pathway2ec = (HashMap<Short, List<short[]>>) ois.readObject();
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

        this.ko2pathway = new HashMap<Short, List<Short>>();
        this.pathway2ko = new HashMap<Short, List<Short>>();
        this.ec2pathway = new HashMap<short[], List<Short>>();
        this.pathway2ec = new HashMap<Short, List<short[]>>();
		int i = 1;
		// iterate list of pathway IDs
		for (Short pathwayID : pathwayIDs) {
			System.out.println("" + (i++) + "/" + pathwayIDs.size());
			String pathway = "path:map" + String.format("%05d", pathwayID);
			
			String[] koStrings = this.serv.get_kos_by_pathway(pathway);
			List<Short> koList = new ArrayList<Short>();
			for (String koString : koStrings) {
				Short ko = Short.parseShort(koString.substring(4));
				List<Short> pathwayList = this.ko2pathway.get(ko);
				if (pathwayList == null) {
					pathwayList = new ArrayList<Short>();
				}
				if (!pathwayList.contains(pathwayID)) {
					pathwayList.add(pathwayID);
				}
                this.ko2pathway.put(ko, pathwayList);
				
				koList.add(ko);
			}
			if (!koList.isEmpty()) {
                this.pathway2ko.put(pathwayID, koList);
			}

			String[] ecStrings = this.serv.get_enzymes_by_pathway(pathway);
			List<short[]> ecList = new ArrayList<short[]>();
			for (String ecString : ecStrings) {
				short[] ec = ECReader.toArray(ecString.substring(3));
				List<Short> pathwayList = this.ec2pathway.get(ec);
				if (pathwayList == null) {
					pathwayList = new ArrayList<Short>();
				}
				if (!pathwayList.contains(pathwayID)) {
					pathwayList.add(pathwayID);
				}
                this.ec2pathway.put(ec, pathwayList);
				
				ecList.add(ec);
			}
            this.pathway2ec.put(pathwayID, ecList);
			
		}
		
		// dump objects to file
		File output = new File("conf/keggKO2PW.map");
		FileOutputStream fos = new FileOutputStream(output);
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(fos)));

		oos.writeObject(this.ko2pathway);
		oos.writeObject(this.pathway2ko);
		oos.writeObject(this.ec2pathway);
		oos.writeObject(this.pathway2ec);
		oos.flush();
		oos.close();
	}

}
