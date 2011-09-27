package de.mpa.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import junit.framework.TestCase;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class Tests extends TestCase {
	public static void main(String[] args) {
		
		ArrayList<ArrayList<String>> fileNames = new ArrayList<ArrayList<String>>();
		ArrayList<String> folderNames = new ArrayList<String>();
		String[] files = new String[] {
//				"ESI;Bande1,Spot1",
				"MALDI;1A1",//1A2,1A3,1A4,1B1,1B2",
//				"QSTAR;Test"
		};
		
		for (String str : files) {
			String[] strParts = str.split(";");
			folderNames.add(strParts[0]);
			strParts = strParts[1].split(",");
			ArrayList<String> strList = new ArrayList<String>();
			for (String file : strParts) {
				strList.add(file);
			}
			fileNames.add(strList);
		}

		String pathName = "test/de/mpa/resources";
		
		for (int i = 0; i < fileNames.size(); i++) {
			ArrayList<String> str = fileNames.get(i);
			for (int j = 0; j < str.size(); j++) {
				ParserTest(pathName + "/" + folderNames.get(i) + "/" + str.get(j) + ".mgf");
			}
		}

	}
	
	@Test public static void ParserTest(String str) {
        try {
        	MascotGenericFileReader reader = new MascotGenericFileReader(new File(str));
        	List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
        	
        	for (MascotGenericFile file : spectrumFiles) {
				System.out.println("Filename: " + file.getFilename());
				HashMap<Double, Double> peaks = file.getPeaks();
				System.out.println("Number of value pairs: " + Integer.toString(peaks.size()));
				int i = 1;
				Set<Double> set = peaks.keySet();
				TreeSet<Double> treeset = new TreeSet<Double>(set);
				for (Double mz : treeset) {
					if (i%15 == 1) {
						System.out.println("#\t   m/z\t\t   I");
					}
					System.out.println(Integer.toString(i) + ":\t" + mz.toString() + "\t" + peaks.get(mz).toString());
					i++;
				}
//				System.out.println("Highest intensity: " + file.getHighestIntensity());
//				System.out.println("Total intensity: " + file.getTotalIntensity());
			}
        		
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
