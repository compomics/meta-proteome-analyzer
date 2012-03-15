package de.mpa.job.evaluation;

import java.sql.ResultSet;
import java.sql.SQLException;

// new class for hashmap
public class Description{

	private String desc;
	private String sequence;
	private String accession;
	private String fileName;
	private boolean inspectR = false, xtandemR = false, omssaR = false, cruxR = false, mascotR = false;

	
	public Description(String desc) {
		this.desc = desc;
	}

	public Description(ResultSet aResultSet) throws SQLException {
		this.desc 		= aResultSet.getString("description");
		this.accession 	= aResultSet.getString("accession");
		this.sequence 	= aResultSet.getString("sequence");
		this.fileName 	= aResultSet.getString("filename");
	}

	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public boolean isInspectR() {
		return inspectR;
	}
	public void setInspectR(boolean inspectR) {
		this.inspectR = inspectR;
	}

	public boolean isXtandemR() {
		return xtandemR;
	}
	public void setXtandemR(boolean xtandemR) {
		this.xtandemR = xtandemR;
	}

	public boolean isOmssaR() {
		return omssaR;
	}
	public void setOmssaR(boolean omssaR) {
		this.omssaR = omssaR;
	}

	public boolean isCruxR() {
		return cruxR;
	}
	public void setCruxR(boolean cruxR) {
		this.cruxR = cruxR;
	}

	public boolean isMascotR() {
		return mascotR;
	}
	public void setMascotR(boolean mascotR) {
		this.mascotR = mascotR;
	}
}
