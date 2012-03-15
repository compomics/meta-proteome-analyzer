package de.mpa.job.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.TreeMap;

/*
 * class to save rawdata
 */
public class SaveRawData {

	public SaveRawData(TreeMap<String, Description> protResults, String SaveName) {
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(new File(SaveName));
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			//header
			String row = "#" + "\t" + "Key"+ 
					"\t" +"Accession" + 
					"\t" +"Description" + 
					"\t" +"Filename" + 
					"\t"+ "Inspect" + 
					"\t" +"Xtandem" +
					"\t" +"Omssa" +
					"\t" + "Crux" +
					"\t" + "Mascot"+
					"\t" + "Sequences";
			osw.write(row + "\n");
			
			// count entries
			int nInspect = 0;
			int nXtandem = 0;
			int nOmssa	 = 0;
			int nCrux    = 0;
			int nMascot  = 0;
			for (String key : protResults.keySet()) {
				// count entries
				if (protResults.get(key).isInspectR() == true){
					nInspect++;
				}
				if (protResults.get(key).isXtandemR() == true){
					nXtandem++;
				}
				if (protResults.get(key).isOmssaR() == true){
					nOmssa++;
				}
				if (protResults.get(key).isCruxR() == true){
					nCrux++;
				}
				if (protResults.get(key).isMascotR() == true){
					nMascot++;
				}
				
				
				//enrties
				int i = 1;
				row = i +"\t" + key+ 
						"\t" + protResults.get(key).getAccession()+ 
						"\t" + protResults.get(key).getDesc() +
						"\t" + protResults.get(key).getFileName() +
						"\t" +protResults.get(key).isInspectR()  +
						"\t" +protResults.get(key).isXtandemR()  +
						"\t" +protResults.get(key).isOmssaR()  +
						"\t" +protResults.get(key).isCruxR()  +
						"\t" +protResults.get(key).isMascotR()+
						"\t" +protResults.get(key).getSequence();
				osw.write(row + "\n");
				i++;
			}
			//last row--> sum of entries
					row =	 "" + "\t"+ 
							 "" + "\t"+ 
							 "" + "\t"+ 
							"Gesamt" + "\t"+
							protResults.size()+   "\t" +
							 nInspect +"\t" +
							 nXtandem +"\t" +
							 nOmssa +"\t" +
							 nCrux +"\t" +
							nMascot +"\t";
				osw.write(row + "\n");
			osw.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}


