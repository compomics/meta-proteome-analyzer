package de.mpa.client.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.mpa.analysis.MetaProteinFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.SearchHit;

/**
 * Custom tree table node that supports multiple parents by index.
 * 
 * @author A. Behne
 */
public class PhylogenyTreeTableNode extends SortableCheckBoxTreeTableNode implements Cloneable {
	
	/**
	 * Constructs a phylogenetic tree table node from an array of arbitrary Objects.<br>
	 * <b>Note</b>: Use {@link ProteinHit} as first parameter for leaves!
	 * @param userObjects
	 */
	public PhylogenyTreeTableNode(Object... userObjects) {
		super(userObjects);
	}
	
	@Override
	public int getColumnCount() {
		return 13;
	}
	
	@Override
	public Object getValueAt(int column) {
		if (isProtein()) {
			ProteinHit ph = (ProteinHit) this.userObject;
			if (ph instanceof MetaProteinHit) {
				switch (column) {
				case ProteinTreeTables.ACCESSION_COLUMN:
					return toString();
				case ProteinTreeTables.DESCRIPTION_COLUMN:
					return ph.getDescription();
				case ProteinTreeTables.TAXONOMY_COLUMN:
					return ph.getTaxonomyNode();
				case ProteinTreeTables.WEB_RESOURCES_COLUMN:
					// web resource column
					return null;
				default:
					return super.getValueAt(column);
				}
			} else {
				switch (column) {
				case ProteinTreeTables.ACCESSION_COLUMN:
					return toString();
				case ProteinTreeTables.DESCRIPTION_COLUMN:
					String description = ph.getDescription();
					if (description == null) {
						description = "";
					}
					int underscore = description.indexOf("_");
					if (underscore > 0) {
						int whitespace = description.indexOf(" ");
						if (whitespace > underscore) {
							return description.substring(whitespace + 1);
						}
					}
					return description;
				case ProteinTreeTables.TAXONOMY_COLUMN:
					return ph.getTaxonomyNode();
				case ProteinTreeTables.SEQUENCE_COLUMN:
					return ph.getSequence();
				case ProteinTreeTables.UNIREF_COLUMN:
					if (ph.getUniProtEntry() == null || ph.getUniProtEntry().getAccession() == null) {
						return null;
					}
					ResultParameters parameters =
                            Client.getInstance().getResultParameters();
					MetaProteinFactory.ClusterRule clusterRule =
							(MetaProteinFactory.ClusterRule) parameters.get("clusterRule").getValue();
					switch (clusterRule) {
						case UNIREF100:
							return ph.getUniProtEntry().getUniRefMPA().getUniRef100();
						case UNIREF90:
							return ph.getUniProtEntry().getUniRefMPA().getUniRef90();
						case UNIREF50:
							return ph.getUniProtEntry().getUniRefMPA().getUniRef50();
						default:
							return ph.getUniProtEntry().getUniRefMPA().getUniRef100();
					}
				case ProteinTreeTables.SEQUENCE_COVERAGE_COLUMN:
					return ph.getCoverage();
				case ProteinTreeTables.MOLECULAR_WEIGHT_COLUMN:
					return ph.getMolecularWeight();
				case ProteinTreeTables.ISOELECTRIC_POINT_COLUMN:
					return ph.getIsoelectricPoint();
				case ProteinTreeTables.PEPTIDE_COUNT_COLUMN:
					return ph.getPeptideCount();
				case ProteinTreeTables.SPECTRAL_COUNT_COLUMN:
					return ph.getSpectralCount();
				case ProteinTreeTables.EMPAI_COLUMN:
					return ph.getEmPAI();
				case ProteinTreeTables.NSAF_COLUMN:
					return ph.getNSAF();
				case ProteinTreeTables.WEB_RESOURCES_COLUMN:
					return IconConstants.WEB_RESOURCE_ICON;
				default:
					return super.getValueAt(column);
				}
			}
		} else if (isTaxonomy()) {
			TaxonomyNode tn = (TaxonomyNode) this.userObject;
			switch (column) {
			case 0:
				return tn.getName();
			case 1:
				return tn.getRank();
			default:
				return super.getValueAt(column);
			}
		} else if (isPeptide()) {
			PeptideHit ph = (PeptideHit) this.userObject;
			switch (column) {
			case 0:
				return toString();
			case 1:
				return ph.getProteinHits().size();
			case 2:
				Object value = super.getValueAt(2);
				if (value == null) {
					String accessions = "";
					Iterator<ProteinHit> iter = ph.getProteinHits().iterator();
					while (iter.hasNext()) {
						ProteinHit protein = iter.next();
						accessions += protein.getAccession();
						if (iter.hasNext()) {
							accessions += ", ";
						}
					}
                    setValueAt(accessions, 2);
					value = accessions;
				}
				return value;
			case 3:
				return ph.getSpectrumMatches().size();
			case 4:
				return ph.getTaxonomyNode();
			default:
				return super.getValueAt(column);
			}
		} else if (isSpectrumMatch()) {
			PeptideSpectrumMatch psm = (PeptideSpectrumMatch) this.userObject;
			SearchHit searchHit;
			switch (column) {
			case 0:
				return psm.getSearchSpectrumID();
			case 1:
				return psm.getCharge();
			case 2:
				Object value = super.getValueAt(2);
				if (value == null) {
					String sequences = "";
					Iterator<PeptideHit> iter = psm.getPeptideHits().iterator();
					while (iter.hasNext()) {
						PeptideHit peptide = iter.next();
						sequences += peptide.getSequence();
						if (iter.hasNext()) {
							sequences += ", ";
						}
					}
                    setValueAt(sequences, 2);
					value = sequences;
				}
				return value;
			case 3:
				searchHit = psm.getSearchHit(SearchEngineType.XTANDEM);
				return searchHit == null ? 0.0 : 1.0 - searchHit.getQvalue().doubleValue();
			case 4:
				searchHit = psm.getSearchHit(SearchEngineType.OMSSA);
				return searchHit == null ? 0.0 : 1.0 - searchHit.getQvalue().doubleValue();
			case 5:
				searchHit = psm.getSearchHit(SearchEngineType.CRUX);
				return searchHit == null ? 0.0 : 1.0 - searchHit.getQvalue().doubleValue();
			case 6:
				searchHit = psm.getSearchHit(SearchEngineType.INSPECT);
				return searchHit == null ? 0.0 : 1.0 - searchHit.getQvalue().doubleValue();
			case 7:
				searchHit = psm.getSearchHit(SearchEngineType.MASCOT);
				return searchHit == null ? 0.0 : 1.0 - searchHit.getQvalue().doubleValue();
			default:
				return super.getValueAt(column);
			}
		}
		// fall-back for when none of the above applies
		return super.getValueAt(column);
	}
	
	@Override
	public Collection<?> getValuesAt(int column) {
		if (isProtein()) {
			ProteinHit ph = (ProteinHit) this.userObject;
			switch (column) {
				case ProteinTreeTables.PEPTIDE_COUNT_COLUMN:
					return ph.getPeptideHitList();
				case ProteinTreeTables.SPECTRAL_COUNT_COLUMN:
					return ph.getSpectrumIDs();
			}
		}
		return super.getValuesAt(column);
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		// obligatory range check
		if (column < this.getColumnCount()) {
			// pad user objects array with nulls if it is too small
			if (column >= this.userObjects.length) {
                userObjects = Arrays.copyOf(this.userObjects, column + 1);
			}
			super.setValueAt(aValue, column);
		}
	}

	/**
	 * Returns whether this node stores meta-protein data.
	 * @return <code>true</code> if this node contains meta-protein data,
	 *  <code>false</code> otherwise
	 */
	public boolean isMetaProtein() {
		return (this.userObject instanceof MetaProteinHit);
	}

	/**
	 * Returns whether this node stores protein hit data.
	 * @return <code>true</code> if this node contains protein hit data,
	 *  <code>false</code> otherwise
	 */
	public boolean isProtein() {
		return (this.userObject instanceof ProteinHit);
	}
	
	/**
	 * Returns whether this node stores peptide hit data.
	 * @return <code>true</code> if this node contains peptide hit data,
	 *  <code>false</code> otherwise
	 */
	public boolean isPeptide() {
		return (this.userObject instanceof PeptideHit);
	}
	
	/**
	 * Returns whether this node stores spectrum match data.
	 * @return <code>true</code> if this node contains match data,
	 *  <code>false</code> otherwise
	 */
	public boolean isSpectrumMatch() {
		return (this.userObject instanceof SpectrumMatch);
	}
	
	/**
	 * Returns whether this node stores taxonomy data.
	 * @return <code>true</code> if this node contains taxonomy data,
	 *  <code>false</code> otherwise
	 */
	public boolean isTaxonomy() {
		return (userObject instanceof TaxonomyNode);
	}
	
	/**
	 * Returns a child node identified by its string representation.<br>
	 * Make sure to use unique names!
	 * @param name The string identifier.
	 * @return The child identified by the provided string or 
	 * <code>null</code> if no such child exists.
	 */
	public MutableTreeTableNode getChildByName(String name) {
		for (MutableTreeTableNode child : this.children) {
			if (child.toString().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	/**
	 * Returns a child identified by its user object.<br>
	 * Make sure to always provide non-null user objects!
	 * @param usrObj the userObject identifier
	 * @return the child identified by the provided user object or
	 *  <code>null</code> if no such child exists.
	 */
	public MutableTreeTableNode getChildByUserObject(Object usrObj) {
		for (MutableTreeTableNode child : this.children) {
			if (child.getUserObject().equals(usrObj)) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public PhylogenyTreeTableNode clone() {
		PhylogenyTreeTableNode clone = new PhylogenyTreeTableNode(this.userObjects);
		clone.setURI(getURI());
		return clone;
	}
	
	@Override
	public String toString() {
		if (this.isProtein()) {
			return ((ProteinHit) this.userObject).getAccession();
		}
		return super.toString();
	}

    @SuppressWarnings("deprecation")
	@Override
	public boolean equals(Object obj) {
		// check whether the other object inherits from CheckBoxTreeTableNode
		boolean res = (obj instanceof CheckBoxTreeTableNode);
		if (res) {
			// check whether both objects have the same number of columns
			CheckBoxTreeTableNode that = (CheckBoxTreeTableNode) obj;
			res = (getColumnCount() == that.getColumnCount());
			if (res) {
				// check whether all column values match
				for (int i = 0; i < getColumnCount(); i++) {
					res &= ObjectUtils.equals(getValueAt(i), that.getValueAt(i));
					if (!res) {
						// abort
						break;
					}
				}
			}
		}
		return res;
	}
	
}
