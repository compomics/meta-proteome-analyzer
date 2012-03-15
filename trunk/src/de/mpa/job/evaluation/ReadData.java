package de.mpa.job.evaluation;
import java.util.ArrayList;
import java.util.TreeMap;

/*
 * class to read all datafiles
 */
public class ReadData {

	//ResultsList
	private TreeMap<String, Description> pepResults = new TreeMap<String, Description>();
	private ArrayList<Description> inspectHits;
	private ArrayList<Description> xtandemHits;
	private ArrayList<Description> omssaHits;
	private ArrayList<Description> mascotHits;
	public TreeMap<String, Description> getProtResults() {
		return pepResults;
	}

	public ReadData( ArrayList<Description> inspectHits, ArrayList<Description> xtandemHits, ArrayList<Description> omssaHits, ArrayList<Description> mascotHits){
		this.inspectHits 	= inspectHits;
		this.xtandemHits 	= xtandemHits;
		this.omssaHits 		= omssaHits;
		this.mascotHits 	= mascotHits;
		read();
	}


	public void read(){
		try {
			// InspectList
			for (int i = 0; i < inspectHits.size(); i++) {
				// necessary to take locus from desc to compare with Mascot results
				String desc      = inspectHits.get(i).getDesc();
				String accession = desc.substring(0, desc.indexOf(" ")-1);
				String desc2     = desc.substring(desc.indexOf(" ")+1, desc.length());
				String sequence  = inspectHits.get(i).getSequence();
				String key 		 = accession +"_"+  sequence;
				Description description = new Description(desc2);		
				description.setSequence(sequence);
				description.setAccession(accession);
				description.setFileName(inspectHits.get(i).getFileName());
				if ((pepResults.containsKey(key)==false) && !key.startsWith("gi|")){
					pepResults.put(key,  description);
					pepResults.get(key).setInspectR(true);
				}
			}
			
			// XtandemList
			for (int i = 0; i < xtandemHits.size(); i++) {
				// necessary to take locus from desc to compare with Mascot results
				String desc      = xtandemHits.get(i).getDesc();
				String accession = desc.substring(0, desc.indexOf(" ")-1);
				String desc2     = desc.substring(desc.indexOf(" ")+1, desc.length());
				String sequence  = xtandemHits.get(i).getSequence();
				String key 		 = accession +"_"+ sequence;
				Description description = new Description(desc2);		
				description.setSequence(sequence);
				description.setAccession(accession);
				description.setFileName(xtandemHits.get(i).getFileName());
				if ((pepResults.containsKey(key)==false) && !key.startsWith("gi|")){
					pepResults.put(key,  description);
					pepResults.get(key).setXtandemR(true);
				}
				else{
					if (!key.startsWith("gi|")){
						pepResults.get(key).setXtandemR(true);
					}
				}
			}

			// OmssaList
			for (int i = 0; i < omssaHits.size(); i++) {
				// necessary to take locus from desc to compare with Mascot results
				String desc      = omssaHits.get(i).getDesc();
				String accession = desc.substring(0, desc.indexOf(" ")-1);
				String desc2     = desc.substring(desc.indexOf(" ")+1, desc.length());
				String sequence  = omssaHits.get(i).getSequence();
				String key 		 = accession +"_"+ sequence;
				Description description = new Description(desc2);		
				description.setSequence(sequence);
				description.setAccession(accession);
				description.setFileName(omssaHits.get(i).getFileName());
				if ((pepResults.containsKey(key)==false) && !key.startsWith("gi|")){
					pepResults.put(key,  description);
					pepResults.get(key).setOmssaR(true);
				}
				else{
					if (!key.startsWith("gi|")){
						pepResults.get(key).setOmssaR(true);
					}
				}
			}		

			// Mascot
			for (int i = 0; i < mascotHits.size(); i++) {
				String accession = mascotHits.get(i).getAccession();
				String sequence  = mascotHits.get(i).getSequence();
				String key 		 = accession +"_"+ sequence;
				if ((pepResults.containsKey(key)==false) && !key.startsWith("gi|")){
					pepResults.put(key,  mascotHits.get(i));
					pepResults.get(key).setMascotR(true);
				}
				else{
					if (!key.startsWith("gi|")){
						pepResults.get(key).setMascotR(true);
					}
				}
			}		

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
