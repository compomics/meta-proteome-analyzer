package de.mpa.client.model;

import com.compomics.util.protein.Protein;

public class GeneralProtein extends Protein {
	
	//FIXME!
//	public GeneralProtein(ProteinAccessor protAcc) {
//		this(protAcc.getSource(), protAcc.getAccession(), protAcc.getDescription(), protAcc.getSequence());
//	}
	
	public GeneralProtein(String source, String accession, String description, String sequence) {
		super(source + "|" + accession + "|" + description, sequence);
	}
	
	public String getSource() {
		return getHeader().getID();
	}
	
	public String getAccession() {
		return getHeader().getAccession();
	}
	
	public String getDescription() {
		return getHeader().getDescription();
	}
}
