package de.mpa.job.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.TreeMap;

public class SaveProtList {
	public SaveProtList(TreeMap<String, ProtResultEntry> protResultsEntry, String SaveName) {
		
		// Save data****************************************************
				int gesamt 	 = 0;
				int nInspect = 0;
				int nXtandem = 0;
				int nOmssa	 = 0;
				int nCrux    = 0;
				int nMascot  = 0;
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(new File(SaveName));
			OutputStreamWriter osw = new OutputStreamWriter(fos);

			String row = 	"#" 		+ "\t" +
					"Accession" 		+ "\t" +
					"Description" 		+ "\t" +
					"Anzahl Peptide" 	+ "\t" +
					"Inspect" 		+ "\t" +
					"Xtandem" 	+ "\t" +
					"Omssa" 	+ "\t" +
					"Mascot" 	+ "\t" +
					"Sequence" 	+ "\t" +
			"SubordinaryHit" 	+ "\t";
			osw.write(row + "\n");
			
			int i = 0;
			for (String key : protResultsEntry.keySet()) {
				//save just the first hit 
				if (protResultsEntry.get(key).getSubOrdinaryHit()==0){
					i++;
					row = 	i 												+"\t" +
						protResultsEntry.get(key).getAccession() 			+"\t" +
						protResultsEntry.get(key).getDesc()					+"\t" +
						protResultsEntry.get(key).getAnzPeptide() 			+"\t" +
						protResultsEntry.get(key).getInpectHit() 			+"\t" +
						protResultsEntry.get(key).getXtandemHit() 			+"\t" +
						protResultsEntry.get(key).getOmssaHit() 			+"\t" +
						protResultsEntry.get(key).getMascotHit()			+"\t" +
						protResultsEntry.get(key).getSequence() 			+"\t" +
						protResultsEntry.get(key).getSubOrdinaryHit()		+"\t";
				// count entries
				if (protResultsEntry.get(key).getInpectHit() 	>0){
					nInspect++;
				}
				if (protResultsEntry.get(key).getXtandemHit() 	>0){
					nXtandem++;
				}
				if (protResultsEntry.get(key).getOmssaHit() 	>0){
					nOmssa++;
				}
				if (protResultsEntry.get(key).getMascotHit() 	>0){
					nMascot++;
				}
				
				if (( protResultsEntry.get(key).getInpectHit() 	>0)	||
				(protResultsEntry.get(key).getXtandemHit() 	>0)		||
				(protResultsEntry.get(key).getOmssaHit() 	>0)		||
				(protResultsEntry.get(key).getMascotHit() 	>0)){
				gesamt++;
				}
				osw.write(row + "\n");
				}
			}
					row =	 "" + "\t"+ 
							 "" + "\t"+ 
							"GesamtProtein" + "\t"+
							 gesamt+   "\t" +
							 nInspect +"\t" +
							 nXtandem +"\t" +
							 nOmssa +"\t" +
							nMascot +"\t";
				osw.write(row + "\n");
			osw.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}