package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.analysis.taxonomy.Taxonomic;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.db.accessor.UniprotentryAccessor;

/**
 * The main uniprot entry informations.
 * @author R. Heyer
 *
 */
public class UniProtEntryMPA implements Serializable, Taxonomic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The uniprotentryID
	 */
	private Long uniProtID = -1L;
	
	/**
	 * The accession
	 */
	private String accession;
	
	/**
	 * The NCBI taxonomy ID
	 */
	private long taxid;
	
	/**
	 * The taxon node
	 */
	private TaxonomyNode taxNode;
	
	
	/**
	 * The ec numbers
	 */
	private List<String> ecnumbers = new ArrayList<String>();
	
	/**
	 * The KO-numbers
	 */
	private List<String> konumbers = new ArrayList<String>();

    /**
	 * The keywords
	 */
	private List<String> keywords = new ArrayList<String>();

    /**
	 * The uniRef entry.
	 */
	private UniRefEntryMPA uniRefMPA;
	
	/**
	 * Default constructur
	 */
	public UniProtEntryMPA() {
		
	}

	/**
	 * @param accession. The accesion of the protein entry.
	 * @param taxNode. The taxonomy node.
	 * @param ecnumbers. The list of ec numbers.
	 * @param konumbers. The list of KO numbers.
	 * @param keywords. The list of keywords.
	 * @param uniRefMPA. The uniRef entries.
	 */
	public UniProtEntryMPA(String accession,  TaxonomyNode taxNode, List<String> ecnumbers, List<String> konumbers, List<String> keywords, UniRefEntryMPA uniRefMPA) {
		this.accession = accession;
		this.taxNode = taxNode;
        taxid = taxNode.getID();
		this.ecnumbers = ecnumbers;
		this.konumbers = konumbers;
		this.keywords = keywords;
		this.uniRefMPA = uniRefMPA;
	}
	
	
	/**
	 * Constructor for UniProt entries from webservice
	 */
	public UniProtEntryMPA(UniProtEntry uniProtEntry, UniRefEntryMPA uniRefEntry) {
		
		// Fill uniProtEntry
        accession = uniProtEntry.getPrimaryUniProtAccession().toString();

        this.taxid = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());
        ecnumbers = uniProtEntry.getProteinDescription().getEcNumbers();
		List<DatabaseCrossReference> xRefs = uniProtEntry.getDatabaseCrossReferences(DatabaseType.KO);
		if (xRefs.size() > 0) {
			for (DatabaseCrossReference xRef : xRefs) {
                konumbers.add(xRef.getPrimaryId().getValue());

			}
		}
		List<Keyword> keywordsList = uniProtEntry.getKeywords();
		if (keywordsList.size() > 0) {
			for (Keyword kw : keywordsList) {
				
				if (kw.getValue() != null) {
                    this.keywords.add(kw.getValue());
				}
			}
		}
        uniRefMPA = uniRefEntry;
	}

	/**
	 * 
	 * Class to fill an uniProtEntryMPA from the SQL UniProt entry
	 * @param uniProtAccessor
	 */
	public UniProtEntryMPA(UniprotentryAccessor uniProtAccessor) {
			// uniprot ID
        uniProtID = uniProtAccessor.getUniprotentryid();
			// taxonomyID
        taxid = uniProtAccessor.getTaxid();
			// EC number
			String ecnumber = uniProtAccessor.getEcnumber();
			if (ecnumber != null) {
				String[] split = ecnumber.split(";");
				for (String string : split) {
                    ecnumbers.add(string);
				}
			}else{
                ecnumbers = null;
			}
			// KO numbers
			String konumber = uniProtAccessor.getKonumber();
			if (konumber != null) {
				String[] split = konumber.split(";");
				for (String string : split) {
                    konumbers.add(string);
				}
			}else{
                konumbers = null;
			}
			// Keywords
			String keywords = uniProtAccessor.getKeywords();
			if (keywords != null) {
				String[] split = keywords.split(";");
				for (String string : split) {
					this.keywords.add(string);
				}
			}else{
				this.keywords = null;
			}
			// uniRefs
			String uniref100 = uniProtAccessor.getUniref100();
			String uniref90 = uniProtAccessor.getUniref90();
			String uniref50 = uniProtAccessor.getUniref50();
        uniRefMPA = new UniRefEntryMPA(uniref100,uniref90, uniref50);
	}

	/**
	 * Get the uniProtID.
	 * @return. The uniProtID.
	 */
	public Long getUniProtID() {
		return this.uniProtID;
	}

	/**
	 * Get the uniProt accession
	 * @return The uniProt accession.
	 */
	public String getAccession() {
		return this.accession;
	}

	/**
	 * The NCBI taxonomy ID.
	 * @return. The NCBI taxonomy ID.
	 */
	public long getTaxid() {
		return this.taxid;
	}

	/**
	 * The list of EC numbers
	 * @return The list of EC numbers.
	 */
	public List<String> getEcnumbers() {
		return this.ecnumbers;
	}

	/**
	 * The list of KO numbers.
	 * @return The list of KO numbers.
	 */
	public List<String> getKonumbers() {
		return this.konumbers;
	}

	/**
	 * The list of uniProt keywords
	 * @return The list of uniProt keywords
	 */
	public List<String> getKeywords() {
		return this.keywords;
	}

	/**
	 * The uniRef entry.
	 * @return The uniRef entry
	 */
	public UniRefEntryMPA getUniRefMPA() {
		return this.uniRefMPA;
	}

	/**
	 * Sets the uniRef for an UniProt entry
	 * @param uniRefMPA. The uniref entries for an uniProt entry
	 */
	public void setUniRefMPA(UniRefEntryMPA uniRefMPA) {
		this.uniRefMPA = uniRefMPA;
	}

	/**
	 * Get TaxonNode for this taxID
	 */
	@Override
	public TaxonomyNode getTaxonomyNode() {
		return this.taxNode;
	}

	/**
	 * Sets the taxon node.
	 */
	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
        taxNode = taxonNode;
		
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return null;
	}
}