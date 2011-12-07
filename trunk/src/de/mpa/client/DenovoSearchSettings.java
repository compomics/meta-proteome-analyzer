package de.mpa.client;

import java.util.ArrayList;
import java.util.List;

public class DenovoSearchSettings {

	private String dnDatabase= "";
	private String dnEnzyme ="";
	private String dnMS ="";
	private Double dnFragmentTolerance = 0.0;
	private int dnPeptideIntThresh =0;
	private int dnCountPept =0;
	private boolean dnRemoveAllPep = false;
	private List<String> dnPTM ;
	public String getDnDatabase() {
		return dnDatabase;
	}
	public void setDnDatabase(String dnDatabase) {
		this.dnDatabase = dnDatabase;
	}
	public String getDnEnzyme() {
		return dnEnzyme;
	}
	public void setDnEnzyme(String dnEnzyme) {
		this.dnEnzyme = dnEnzyme;
	}
	public String getDnMS() {
		return dnMS;
	}
	public void setDnMS(String dnMS) {
		this.dnMS = dnMS;
	}
	public Double getDnFragmentTolerance() {
		return dnFragmentTolerance;
	}
	public void setDnFragmentTolerance(Double dnFragmentTolerance) {
		this.dnFragmentTolerance = dnFragmentTolerance;
	}
	public int getDnPeptideIntThresh() {
		return dnPeptideIntThresh;
	}
	public void setDnPeptideIntThresh(int dnPeptideIntThresh) {
		this.dnPeptideIntThresh = dnPeptideIntThresh;
	}
	public int getDnCountPept() {
		return dnCountPept;
	}
	public void setDnCountPept(int dnCountPept) {
		this.dnCountPept = dnCountPept;
	}
	public boolean isDnRemoveAllPep() {
		return dnRemoveAllPep;
	}
	public void setDnRemoveAllPep(boolean dnRemoveAllPep) {
		this.dnRemoveAllPep = dnRemoveAllPep;
	}
	public List<String> getDnPTM() {
		return dnPTM;
	}
	public void setDnPTM(List<String> dnPTM) {
		this.dnPTM = dnPTM;
	}



	
	
}
