package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.TreeCellContext;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.Keyword;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.SortableCheckBoxTreeTable.TableColumnExt2;
import de.mpa.client.ui.TableConfig.FormatHighlighter;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.parser.ec.ECNode;
import de.mpa.io.parser.ec.ECReader;
import de.mpa.io.parser.kegg.KEGGNode;
import de.mpa.io.parser.kegg.KEGGOrthologyNode;
import de.mpa.util.ColorUtils;

/**
 * Enumeration holding protein tree table identifiers.
 */
public enum ProteinTreeTables {
	FLAT("Basic View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();
		
			((PhylogenyTreeTableNode) treeTblMdl.getRoot()).add(protNode);
		}
	},
	META("Meta-Protein View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode metaNode) {
			if (metaNode.getChildCount() == 1) {
				metaNode = (PhylogenyTreeTableNode) metaNode.getChildAt(0);
			}
			
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();
		
			((PhylogenyTreeTableNode) treeTblMdl.getRoot()).add(metaNode);
		}
	},
	ONTOLOGY("Ontology View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			// get taxonomic tree table root
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();
			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

			// extract protein hit from provided node
			ProteinHit ph = (ProteinHit) protNode.getUserObject();
			
			// init insertion node as root
			PhylogenyTreeTableNode parent = root;
			
			ReducedUniProtEntry upe = ph.getUniProtEntry();
			if (upe != null) {
				Map<String, Keyword> ontologies = UniProtUtilities.ONTOLOGY_MAP;
				// iterate keywords, insert a cloned instance of the protein node into the tree for each keyword
				for (String keyword : upe.getKeywords()) {
					Keyword ontology = ontologies.get(keyword);
					if (ontology != null) {
						Keyword category = ontology.getCategory();
						// look for ontology category node
						parent = this.getChildByUserObject(root, category);
						PhylogenyTreeTableNode child;
						if (parent != null) {
							// look for keyword node
							child = this.getChildByUserObject(parent, keyword);
							if (child == null) {
								// keyword node does not exist yet, therefore create it
								child = new PhylogenyTreeTableNode(keyword, ontology.getDescription());
								parent.add(child);
							}
						} else {
							// ontology category node does not exist yet, therefore create it
							parent = new PhylogenyTreeTableNode(category, category.getDescription());
							root.add(parent);
							// keyword node cannot exist yet either, therefore also create it
							child = new PhylogenyTreeTableNode(keyword, ontology.getDescription());
							parent.add(child);
						}
						// add clone of protein node to keyword node
						PhylogenyTreeTableNode cloneNode = protNode.clone();
						child.add(cloneNode);
					} else {
						// unknown keyword, put node under 'Unknown'
						parent = this.getChildByUserObject(root, "Unknown");
						if (parent == null) {
							// 'Unknown' node does not exist yet, therefore create it
							parent = new PhylogenyTreeTableNode("Unknown");
							root.add(parent);
						}
						// add clone of protein node to parent
						PhylogenyTreeTableNode cloneNode = protNode.clone();
						parent.add(cloneNode);
					}
				}
			} else {
				// no keywords available, put node under 'Unclassified'
				parent = this.getChildByUserObject(root, "Unclassified");
				if (parent == null) {
					// 'Unclassified' node does not exist yet, therefore create it
					parent = new PhylogenyTreeTableNode("Unclassified");
					root.add(parent);
				}
				parent.add(protNode);
			}
		}
		
		/**
		 * Searches the child nodes of the provided parent node for one featuring a
		 * user object matching the provided one.
		 * @param parent the parent node
		 * @param userObject the user object to identify the desired node by
		 * @return the desired child node or <code>null</code> if no such node exists
		 */
		private PhylogenyTreeTableNode getChildByUserObject(PhylogenyTreeTableNode parent, Object userObject) {
			Enumeration<? extends MutableTreeTableNode> children = parent.children();
			while (children.hasMoreElements()) {
				MutableTreeTableNode child = (MutableTreeTableNode) children.nextElement();
				if (userObject.equals(child.getUserObject())) {
					return (PhylogenyTreeTableNode) child;
				}
			}
			return null;
		}
	},
	TAXONOMY("Taxonomy View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			// get taxonomic tree table root
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();
			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

			// extract protein hit from provided node
			ProteinHit ph = (ProteinHit) protNode.getUserObject();
			
			// traverse tree along the taxonomy path
			TaxonomyNode[] taxPath = ph.getTaxonomyNode().getPath();
			PhylogenyTreeTableNode parent = root;
			for (TaxonomyNode taxNode : taxPath) {
				// get child node associated with current taxonomy node
				PhylogenyTreeTableNode child = (PhylogenyTreeTableNode) parent.getChildByUserObject(taxNode);
				if (child == null) {
					// no child with this taxonomy node exists currently, create a new one
					child = new PhylogenyTreeTableNode(taxNode);
					// add new child to parent
					parent.add(child);
				}
				// child node becomes the parent in the next iteration
				parent = child;
			}
			// insert protein node as leaf
			parent.add(protNode);
		}
	},
	ENZYME("Enzyme View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();

			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

			ProteinHit ph = (ProteinHit) protNode.getUserObject();

			List<String> ecNumbers;
			ReducedUniProtEntry upe = ph.getUniProtEntry();
			if (upe != null) {
				ecNumbers = upe.getEcNumbers();
			} else {
				ecNumbers = new ArrayList<String>();
			}

			String unclassifiedStr = "Unclassified";
			if (ecNumbers.isEmpty()) {
				ecNumbers.add(unclassifiedStr);
			}
			
			PhylogenyTreeTableNode unclassifiedNode = null;

			// iterate E.C. numbers, insert cloned instance of protein node for each number
			ECNode ecRoot = Constants.ENZYME_ROOT;
			for (String ecNumber : ecNumbers) {
				// init insertion node using tree root
				PhylogenyTreeTableNode parent = root;
				
				// check for 'Unclassified'
				if (unclassifiedStr.equals(ecNumber)) {
					if (unclassifiedNode == null) {
						// create 'Unclassified' node
						unclassifiedNode = new PhylogenyTreeTableNode(unclassifiedStr);
						root.add(unclassifiedNode);
					}
					parent = unclassifiedNode;
				} else {
					// get path to root in enzyme tree
					ECNode[] path = ECReader.getPath(ecRoot, ecNumber);
					
					if (path != null) {
						// determine insertion point
						for (int i = 1; i < path.length; i++) {
							// find child node corresponding to enzyme node identifier
							PhylogenyTreeTableNode child =
									(PhylogenyTreeTableNode) parent.getChildByName(path[i].getIdentifier());
							if (child == null) {
								// child node does not exist yet, therefore create it
								child = new PhylogenyTreeTableNode(path[i], path[i].getDescription());
								// create link to ExPASy website
								URI uri = URI.create("http://enzyme.expasy.org/EC/" + path[i]);
								child.setURI(uri);
								
								parent.add(child);
							}
							parent = child;
						}
					} else {
						System.err.println("ERROR: unrecognized E.C. number: " + ecNumber);
					}
				}
				
				// add clone of protein node to parent
				PhylogenyTreeTableNode cloneNode = protNode.clone();
				parent.add(cloneNode);
			}
		}
	},
	KO("KO View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();

			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

			ProteinHit ph = (ProteinHit) protNode.getUserObject();
			
			// gather KO numbers for pathway lookup
						List<String> keys = new ArrayList<>();
						ReducedUniProtEntry upe = ph.getUniProtEntry();
						if (upe != null) {
							keys.addAll(upe.getKNumbers());
						}
			
			// write KEGG keys to nodes
			Set<KEGGOrthologyNode> koNodes = new LinkedHashSet<>();
			for (String key : keys) {
				KEGGOrthologyNode node = new KEGGOrthologyNode(key);
				if (node != null) {
					koNodes.add(node);
				}
			}
			
			PhylogenyTreeTableNode parent = root;
			
			// iterate found pathways
			if (!koNodes.isEmpty()) {
				// iterate pathway IDs
				for (KEGGOrthologyNode koNode : koNodes) {
					// look for pathway in existing tree
					parent = (PhylogenyTreeTableNode) koNode.getParent();

					// check whether pathway retrieval succeeded
					if (parent == null) {
						// extract path to root
						TreeNode[] path = koNode.getPath();

						parent = root;

						KEGGOrthologyNode node = (KEGGOrthologyNode) path[0];
						PhylogenyTreeTableNode child = 
								(PhylogenyTreeTableNode) parent.getChildByName(node.getName());
						if (child == null) {
								child = new PhylogenyTreeTableNode(
										node.getName(), node.getDescription());
										parent.add(child);
								parent = child;
						} else {
							parent = child;
						}					
					}

					// add clone of protein node to each pathway
					PhylogenyTreeTableNode cloneNode = protNode.clone();
					parent.add(cloneNode);
				}
			} else {
				// no pathways were found, therefore look for 'Unclassified' node in tree
				Enumeration<? extends MutableTreeTableNode> children = root.children();
				while (children.hasMoreElements()) {
					MutableTreeTableNode child = (MutableTreeTableNode) children.nextElement();
					if (child.getUserObject().equals("Unclassified")) {
						parent = (PhylogenyTreeTableNode) child;
						break;
					}
				}
				if (parent == root) {
					// 'Unclassified' node does not exist yet, therefore create it
					parent = new PhylogenyTreeTableNode("Unclassified");
//								treeTblMdl.insertNodeInto(parent, root, root.getChildCount());
					root.add(parent);
				}
				// add clone of protein node to 'Unclassified' branch
				PhylogenyTreeTableNode cloneNode = protNode.clone();
				parent.add(cloneNode);
			}
		}

		@SuppressWarnings("unused")
		private PhylogenyTreeTableNode findPathway(String pw) {
			Enumeration<? extends MutableTreeTableNode> children = 
					((MutableTreeTableNode) ProteinTreeTables.PATHWAY.getTreeTable().getTreeTableModel().getRoot()).children();
					while (children.hasMoreElements()) {
						MutableTreeTableNode childC = (MutableTreeTableNode) children.nextElement();
						if (((String) childC.getUserObject()).startsWith(pw)) {
							return (PhylogenyTreeTableNode) childC;
						}
					}
			return null;
		}

	},
	PATHWAY("Pathway View") {
		@Override
		public void insertNode(PhylogenyTreeTableNode protNode) {
			DefaultTreeTableModel treeTblMdl =
					(DefaultTreeTableModel) this.getTreeTable().getTreeTableModel();

			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) treeTblMdl.getRoot();

			ProteinHit ph = (ProteinHit) protNode.getUserObject();

			// gather K and EC numbers for pathway lookup
			List<String> keys = new ArrayList<>();
			ReducedUniProtEntry upe = ph.getUniProtEntry();
			if (upe != null) {
				keys.addAll(upe.getKNumbers());
				keys.addAll(upe.getEcNumbers());
			}

			// perform pathway lookup
			Set<KEGGNode> koNodes = new LinkedHashSet<>();
			for (String key : keys) {
				List<KEGGNode> nodes = Constants.KEGG_ORTHOLOGY_MAP.get(key);
				if (nodes != null) {
					koNodes.addAll(nodes);
				}
			}

			PhylogenyTreeTableNode parent = root;

			// iterate found pathways
			if (!koNodes.isEmpty()) {
				// iterate pathway IDs
				for (KEGGNode koNode : koNodes) {
					// look for pathway in existing tree
					KEGGOrthologyNode pathwayNode = (KEGGOrthologyNode) koNode.getParent();
					parent = this.findPathway(pathwayNode.getName());

					// check whether pathway retrieval succeeded
					if (parent == null) {
						// extract path to root
						TreeNode[] path = pathwayNode.getPath();

						parent = root;
						for (int i = 1; i < 4; i++) {
							KEGGOrthologyNode node = (KEGGOrthologyNode) path[i];
							PhylogenyTreeTableNode child = 
									(PhylogenyTreeTableNode) parent.getChildByName(node.getName());
							if (child == null) {
								for (int j = i; j < 4; j++) {
									node = (KEGGOrthologyNode) path[j];
									child = new PhylogenyTreeTableNode(
											node.getName(), node.getDescription());
									parent.add(child);
									parent = child;
								}
								break;
							} else {
								parent = child;
							}
						}						
					}

					// add clone of protein node to each pathway
					PhylogenyTreeTableNode cloneNode = protNode.clone();
					parent.add(cloneNode);
				}
			} else {
				// no pathways were found, therefore look for 'Unclassified' node in tree
				Enumeration<? extends MutableTreeTableNode> children = root.children();
				while (children.hasMoreElements()) {
					MutableTreeTableNode child = (MutableTreeTableNode) children.nextElement();
					if (child.getUserObject().equals("Unclassified")) {
						parent = (PhylogenyTreeTableNode) child;
						break;
					}
				}
				if (parent == root) {
					// 'Unclassified' node does not exist yet, therefore create it
					parent = new PhylogenyTreeTableNode("Unclassified");
//					treeTblMdl.insertNodeInto(parent, root, root.getChildCount());
					root.add(parent);
				}
				// add clone of protein node to 'Unclassified' branch
				PhylogenyTreeTableNode cloneNode = protNode.clone();
				parent.add(cloneNode);
			}
			// TODO: add redundancy check in case a protein has multiple KOs pointing to the same pathway
		}
		/**
		 * Auxiliary method to find the pathway node which is identified by the
		 * provided pathway ID in the pathway tree.
		 * @param pw The pathway ID
		 * @return The desired pathway node or <code>null</code> if it could not be found
		 */
		private PhylogenyTreeTableNode findPathway(String pw) {
			Enumeration<? extends MutableTreeTableNode> childrenA = 
					((MutableTreeTableNode) ProteinTreeTables.PATHWAY.getTreeTable().getTreeTableModel().getRoot()).children();
			while (childrenA.hasMoreElements()) {
				MutableTreeTableNode childA = (MutableTreeTableNode) childrenA.nextElement();
				Enumeration<? extends MutableTreeTableNode> childrenB = childA.children();
				while (childrenB.hasMoreElements()) {
					MutableTreeTableNode childB = (MutableTreeTableNode) childrenB.nextElement();
					Enumeration<? extends MutableTreeTableNode> childrenC = childB.children();
					while (childrenC.hasMoreElements()) {
						MutableTreeTableNode childC = (MutableTreeTableNode) childrenC.nextElement();
//						if (((String) childC.getUserObject()).startsWith(String.format("%05d", pw))) {
						if (((String) childC.getUserObject()).startsWith(pw)) {
							return (PhylogenyTreeTableNode) childC;
						}
					}
				}
			}
			return null;
		}
	};

	/**
	 * The model index of the accession column
	 */
	public static final int ACCESSION_COLUMN = 0;
	
	/**
	 * The model index of the description column.
	 */
	public static final int DESCRIPTION_COLUMN = 1;
	
	/**
	 * The model index of the taxonomy column.
	 */
	public static final int TAXONOMY_COLUMN = 2;
	
	/**
	 * The model index of the UniRef column.
	 */
	public static final int UNIREF_COLUMN = 3;
	
	/**
	 * The model index of the protein sequence column.
	 */
	public static final int SEQUENCE_COLUMN = 4;
	
	/**
	 * The model index of the sequence coverage column.
	 */
	public static final int SEQUENCE_COVERAGE_COLUMN = 5;
	
	/**
	 * The model index of the molecular weight column.
	 */
	public static final int MOLECULAR_WEIGHT_COLUMN = 6;
	
	/**
	 * The model index of the isoelectric point column.
	 */
	public static final int ISOELECTRIC_POINT_COLUMN = 7;
	
	/**
	 * The model index of the peptide count column.
	 */
	public static final int PEPTIDE_COUNT_COLUMN = 8;
	
	/**
	 * The model index of the spectral count column.
	 */
	public static final int SPECTRAL_COUNT_COLUMN = 9;
	
	/**
	 * The model index of the emPAI column.
	 */
	public static final int EMPAI_COLUMN = 10;
	
	/**
	 * The model index of the NSAF column.
	 */
	public static final int NSAF_COLUMN = 11;
	
	/**
	 * The model index of the web resources column.
	 */
	public static final int WEB_RESOURCES_COLUMN = 12;
	
	/**
	 * The descriptive label of the protein tree table.
	 */
	private String label;
	
	/**
	 * The tree table associated with this enum member.
	 */
	private CheckBoxTreeTable treeTable;
	
	/**
	 * Flag indicating whether checkbox selections inside the tree table are
	 * currently in the middle of being synched programmatically.
	 */
	private boolean synching = false;
	
	/**
	 * The cached list of checkbox selection paths of the tree table.
	 */
	private Set<TreePath> checkSelection;
	
	/**
	 * Flag indicating whether checkbox selections inside the tree table are in
	 * need of updating.
	 */
	private boolean checkSelectionNeedsUpdating = false;
	
	/**
	 * Creates an enum member using the provided label string.
	 * @param label the label
	 */
	private ProteinTreeTables(String label) {
		this.label = label;
		this.treeTable = this.createTreeTable(new PhylogenyTreeTableNode(
				label, null, null, null, null, null, null, null, null, null, null, null));
	}
	
	/**
	 * Returns the descriptive label string.
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	public CheckBoxTreeTable getTreeTable() {
		return treeTable;
	}
	
	/**
	 * Inserts a protein node into the hierarchy.
	 * @param protNode the protein node to insert
	 */
	public abstract void insertNode(PhylogenyTreeTableNode protNode);
	
	/**
	 * Returns the enum member with the specified label.
	 * @param label the label identifying the enum member
	 * @return the labeled enum member or <code>null</code> if no such member exists
	 */
	public static ProteinTreeTables valueOfLabel(String label) {
		for (ProteinTreeTables ptt : values()) {
			if (ptt.getLabel().equals(label)) {
				return ptt;
			}
		}
		return null;
	}

	/**
	 * Creates and returns a protein tree table anchored to the specified root node.
	 * @param root the root node of the tree table
	 * @return he generated tree table
	 */
	@SuppressWarnings("rawtypes")
	private SortableCheckBoxTreeTable createTreeTable(final SortableCheckBoxTreeTableNode root) {

		// Set up table model
		SortableTreeTableModel treeTblMdl = new SortableTreeTableModel(root) {
			// Install column names
			{
				setColumnIdentifiers(Arrays.asList(new String[] {
						"Accession", "Description", "Taxonomy", "UniRef", "Sequence",
						"SC", "MW", "pI", "PepC", "SpC", "emPAI", "NSAF", null }));
			}
			// Fool-proof table by allowing only one type of node
			@Override
			public void insertNodeInto(MutableTreeTableNode newChild,
					MutableTreeTableNode parent, int index) {
				if (newChild instanceof PhylogenyTreeTableNode) {
					super.insertNodeInto(newChild, parent, index);
				} else {
					throw new IllegalArgumentException("This tree table requires Phylogeny nodes!");
				}
			}
			@Override
			public boolean isCellEditable(Object node, int column) {
				switch (column) {
					case ACCESSION_COLUMN:
					case WEB_RESOURCES_COLUMN:
						return true;
					default:
						return false;
				}
			}
		};

		// Create table from model, redirect tooltip text generation
		@SuppressWarnings("serial")
		final SortableCheckBoxTreeTable treeTbl = new SortableCheckBoxTreeTable(treeTblMdl) {
			@Override
			public String getToolTipText(MouseEvent me) {
				String text = null;
				JXTreeTable table = (JXTreeTable) me.getSource();
				int col = table.columnAtPoint(me.getPoint());
				if (col != -1) {
					col = table.convertColumnIndexToModel(col);
					if (col != WEB_RESOURCES_COLUMN) {
						int row = table.rowAtPoint(me.getPoint());
						TreePath pathForRow = table.getPathForRow(row);
						if (pathForRow != null) {
							PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) pathForRow.getLastPathComponent();
							if ((table == ENZYME.getTreeTable()) && (col == DESCRIPTION_COLUMN)) {
								Object userObject = node.getUserObject();
								if (userObject instanceof ECNode) {
									text = ((ECNode) userObject).getComments();
									if (text == null) {
										Object value = node.getValueAt(col);
										if (value != null) {
											text = value.toString();
										}
									}
								}
							} else {
								Object value = node.getValueAt(col);
								if (value != null) {
									text = value.toString();
								}
							}
							// insert line breaks on overly long pieces of text
							if (text != null) {
								text = "<html>" + text + "</html>";
								StringBuffer sb = new StringBuffer (text);  
								String newLine = "<br>"; 
								for (int i = 70; i < text.length(); i += 70) {
									int linebreak = sb.indexOf(" ", i);
									if (linebreak != -1) {
										sb.insert(linebreak, newLine);
									}
								}
								text = sb.toString();
							}
						}
					}
				}
				return text;
			}
			@Override
			public void updateHighlighters(int column, Object... params) {
				int viewIndex = this.convertColumnIndexToView(column);
				if (viewIndex != -1) {
					TableColumnExt columnExt = this.getColumnExt(viewIndex);
					Highlighter[] highlighters = columnExt.getHighlighters();
					if (highlighters.length > 0) {
						if (highlighters.length > 1) {
							// typically there should be only a single highlighter per column
							System.err.println("WARNING: multiple highlighters specified for column " + column
									+ " of tree table " + this.getTreeTableModel().getRoot().toString());
						}
						Highlighter hl = highlighters[0];
						if (hl instanceof BarChartHighlighter) {
							// we may need to update the highlighter's baseline width to accommodate for aggregate values
							BarChartHighlighter bchl = (BarChartHighlighter) hl;
							FontMetrics fm = this.getFontMetrics(UIManager.getFont("Label.font"));
							NumberFormat formatter = bchl.getFormatter();
							// iterate all nodes to get elements in desired column
							int maxWidth = this.getMaximumStringWidth(
									(TreeTableNode) this.getTreeTableModel().getRoot(), column, formatter, fm);
							bchl.setBaseline(maxWidth + 1);
							if (params.length > 1) {
								bchl.setRange(((Number) params[0]).doubleValue(), ((Number) params[1]).doubleValue());
							}
						}
					}
					// repaint column
					Rectangle rect = this.getTableHeader().getHeaderRect(this.convertColumnIndexToView(column));
					rect.height = this.getHeight();
					this.repaint(rect);
				}
			}
			/** Convenience method to recursively traverse the tree in
			 *  search of the widest string inside the specified column. */
			private int getMaximumStringWidth(TreeTableNode node, int column, Format formatter, FontMetrics fm) {
				int strWidth = 0;
				Object value = node.getValueAt(column);
				if (value != null) {
					strWidth = fm.stringWidth(formatter.format(value));
				}
				Enumeration<? extends TreeTableNode> children = node.children();
				while (children.hasMoreElements()) {
					TreeTableNode child = (TreeTableNode) children.nextElement();
					strWidth = Math.max(strWidth, this.getMaximumStringWidth(child, column, formatter, fm));
				}
				return strWidth;
			}
		};
		
		// Install component header
		TableColumnModel tcm = treeTbl.getColumnModel();
		String[] columnToolTips = {
				"Protein Accession",
				"Protein Description",
				"Taxonomy",
				"UniRef Cluster ID",
				"Protein Sequence",
				"Sequence Coverage",
				"Molecular Weight",
				"Isoelectric Point",
				"Peptide Count",
				"Spectral Count",
//				"Exponentially Modified Protein Abundance Index",
				"emPAI",
//				"Normalized Spectral Abundance Factor",
				"NSAF",
				"External Web Resources"
		};
		final ComponentTableHeader ch = new ComponentTableHeader(tcm, columnToolTips);
		treeTbl.setTableHeader(ch);
		treeTbl.getColumn(WEB_RESOURCES_COLUMN).setHeaderValue(IconConstants.WWW_ICON);
		for (int i = 0; i < columnToolTips.length; i++) {
			treeTbl.getColumnExt(i).setToolTipText(columnToolTips[i]);
		}

		// Install mouse listeners in header for right-click popup capabilities
		MouseAdapter ma = createHeaderMouseAdapter(treeTbl);
		ch.addMouseListener(ma);
		ch.addMouseMotionListener(ma);
		
		// Force column factory to generate columns to properly cache widths
		((AbstractTableModel) treeTbl.getModel()).fireTableStructureChanged();
		
		// Initialize table column aggregate functions (all NONE except for accession, description 
		// and web resource columns, which are not aggregatable)
		for (int i = SEQUENCE_COVERAGE_COLUMN; i <= NSAF_COLUMN; i++) {
			((TableColumnExt2) tcm.getColumn(i)).setAggregateFunction(AggregateFunction.NONE);
		}
		((TableColumnExt2) tcm.getColumn(PEPTIDE_COUNT_COLUMN)).setAggregateFunction(AggregateFunction.DISTINCT);
		((TableColumnExt2) tcm.getColumn(SPECTRAL_COUNT_COLUMN)).setAggregateFunction(AggregateFunction.DISTINCT);
		
		// Configure column widths
		TableConfig.setColumnWidths(treeTbl, new double[] { 8.25, 20, 10, 8, 0, 5, 4, 3, 4, 4, 4.5, 5, 1 });
		TableConfig.setColumnMinWidths(
				treeTbl, UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(), 22, treeTbl.getFont());
		TableColumnExt webColumn = (TableColumnExt) tcm.getColumn(WEB_RESOURCES_COLUMN);
		webColumn.setMinWidth(19);
		webColumn.setMaxWidth(19);
		webColumn.setResizable(false);
		webColumn.setSortable(false);
		
		// Create shared web resource action
		@SuppressWarnings("serial")
		Action webResourceAction = new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				createWebResourceMenu(treeTbl);
			}
		};
		ButtonColumn bc = new ButtonColumn(treeTbl, webResourceAction, WEB_RESOURCES_COLUMN);
		
		webColumn.setCellRenderer(bc);
		webColumn.setCellEditor(bc);
		treeTbl.addMouseListener(bc);
		
		// Pre-select root node
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(new TreePath(root));
		checkSelection = new HashSet<>(Arrays.asList(cbtsm.getSelectionPaths()));

		// Set default sort order (spectral count, descending)
		((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(SPECTRAL_COUNT_COLUMN, SortOrder.DESCENDING);
		
		// Add listener to synchronize selection state of nodes throughout multiple trees
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				// Check whether synching is in progress
				if (!synching) {
					// Skip hidden tree tables
					if (treeTbl.getParent().getParent().isVisible()) {
						// Perform synchronization only on final event (checkbox selection changes typically fire multiple events)
						if (tse.getPath() == null) {
							synching = true;
							
							// Force re-sort (i.e. re-apply filtering)
							treeTbl.setRowFilter((RowFilter<TreeModel, Integer>) treeTbl.getRowFilter());
							
							// Forward selection change to result object in background thread
							new SwingWorker<Object, Object>() {
								@Override
								protected Object doInBackground() throws Exception {
									if (treeTbl != null) {
										CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
										DefaultTreeTableModel model = (DefaultTreeTableModel) treeTbl.getTreeTableModel();
										PhylogenyTreeTableNode root =
												(PhylogenyTreeTableNode) model.getRoot();
										Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
										while (dfe.hasMoreElements()) {
											TreeNode treeNode = (TreeNode) dfe.nextElement();
											if (treeNode.isLeaf()) {
												Object userObject = ((TreeTableNode) treeNode).getUserObject();
												// sanity check
												if (userObject instanceof ProteinHit) {
													TreePath path = new TreePath(model.getPathToRoot((TreeTableNode) treeNode));
													boolean selected = cbtsm.isPathSelected(path, true);
													((ProteinHit) userObject).setSelected(selected);
												}
											}
										}
									}
									return null;
								}
								
								@Override
								protected void done() {
									synching = false;
									// notify listeners
									treeTbl.firePropertyChange("checkboxSelectionDone", false, true);
								}
							}.execute();
						}
					}
				}

			}
		});

		// Reduce node indents to make tree more compact horizontally
		treeTbl.setIndents(6, 4, 2);
		// Hide root node
		treeTbl.setRootVisible(false);

		// Set up node icons
		// TODO: add icons for non-leaves, e.g. for different taxonomic levels
		@SuppressWarnings("serial")
		IconValue iv = new IconValue() {
			@Override
			public Icon getIcon(Object value) {
				TreeCellContext context = (TreeCellContext) value;
				PhylogenyTreeTableNode node = (PhylogenyTreeTableNode) context.getValue();
				if (context.isLeaf()) {
					return IconConstants.PROTEIN_TREE_ICON;
				} else if (node != null) {
					if (node.isProtein()) {
						ProteinHit ph = (ProteinHit) node.getUserObject();
						if (ph.getAccession().startsWith("Meta-Protein")) {
							return IconConstants.METAPROTEIN_TREE_ICON;
						}
					}
				}
				// fall back to defaults
				return context.getIcon();
			}
		};
		treeTbl.setIconValue(iv);
		
		// Install renderers and highlighters
		FontMetrics fm = treeTbl.getFontMetrics(treeTbl.getFont());

		FormatHighlighter leftHL = new FormatHighlighter(SwingConstants.LEFT);
		treeTbl.getColumnExt(DESCRIPTION_COLUMN).addHighlighter(leftHL);
		treeTbl.getColumnExt(TAXONOMY_COLUMN).addHighlighter(leftHL);
		treeTbl.getColumnExt(UNIREF_COLUMN).addHighlighter(leftHL);

		BarChartHighlighter bch = new BarChartHighlighter(ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.0"));
		bch.setBaseline(1 + fm.stringWidth(bch.getFormatter().format(100.0)));

		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		treeTbl.getColumnExt(SEQUENCE_COVERAGE_COLUMN).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, ColorUtils.DARK_GREEN, ColorUtils.LIGHT_GREEN, x100formatter));

		treeTbl.getColumnExt(MOLECULAR_WEIGHT_COLUMN).addHighlighter(
				new FormatHighlighter(SwingConstants.CENTER, "0.000"));

		treeTbl.getColumnExt(ISOELECTRIC_POINT_COLUMN).addHighlighter(
				new FormatHighlighter(SwingConstants.CENTER, "0.00"));

		treeTbl.getColumnExt(PEPTIDE_COUNT_COLUMN).addHighlighter(new BarChartHighlighter());

		treeTbl.getColumnExt(SPECTRAL_COUNT_COLUMN).addHighlighter(new BarChartHighlighter());

		treeTbl.getColumnExt(EMPAI_COLUMN).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00")));

		treeTbl.getColumnExt(NSAF_COLUMN).addHighlighter(new BarChartHighlighter(
				ColorUtils.DARK_RED, ColorUtils.LIGHT_RED, new DecimalFormat("0.00000")));
		
		// Install non-leaf highlighter
		Color hlCol = new Color(237, 246, 255);	// light blue
//		Color hlCol = new Color(255, 255, 237);	// light yellow
		HighlightPredicate notLeaf = new NotHighlightPredicate(HighlightPredicate.IS_LEAF);
		HighlightPredicate notSel = new NotHighlightPredicate(HighlightPredicate.IS_SELECTED);
		treeTbl.addHighlighter(new CompoundHighlighter(
				new ColorHighlighter(new AndHighlightPredicate(
						notSel, notLeaf, HighlightPredicate.EVEN), hlCol, null),
						new ColorHighlighter(new AndHighlightPredicate(
								notSel, notLeaf, HighlightPredicate.ODD), ColorUtils.getRescaledColor(hlCol, 0.95f), null)));
		
		// Initially hide UniRef and sequence columns
		TableColumnExt urCol = treeTbl.getColumnExt(UNIREF_COLUMN);
		TableColumnExt sqCol = treeTbl.getColumnExt(SEQUENCE_COLUMN);
		urCol.setVisible(false);
		sqCol.setVisible(false);

		// Enable column control widget
		TableConfig.configureColumnControl(treeTbl);
		
		return treeTbl;
	}

	/**
	 * Creates and configures a mouse adapter for tree table headers to display a context menu.
	 * @return the header mouse adapter
	 */
	public static MouseAdapter createHeaderMouseAdapter(final JXTreeTable treeTbl) {
		// TODO: maybe integrate functionality into tree table class
		final ComponentTableHeader ch = (ComponentTableHeader) treeTbl.getTableHeader();
		MouseAdapter ma = new MouseAdapter() {
			/**
			 * The column view index of the last pressed column header.<br>
			 */
			private int col = -1;
	
			/**
			 * Creates and configures the current column header's context menu.
			 * @return the context menu
			 */
			private JPopupMenu createPopup() {
				@SuppressWarnings("serial")
				JPopupMenu popup = new JPopupMenu() {
					@Override
					public void setVisible(boolean b) {
						// automatically raise the column header when popup is dismissed
						if (!b) {
							raise();
						}
						super.setVisible(b);
					}
				};
	
				// Create sub-menu containing sorting-related items
				JMenu sortMenu = new JMenu("Sort");
				sortMenu.setIcon(IconConstants.SORT_ICON);
				
				int modelCol = treeTbl.convertColumnIndexToModel(col);
				@SuppressWarnings("rawtypes")
				SortOrder order = ((TreeTableRowSorter) treeTbl.getRowSorter()).getSortOrder(modelCol);
				JMenuItem ascChk = new JRadioButtonMenuItem("Ascending", order == SortOrder.ASCENDING);
				JMenuItem desChk = new JRadioButtonMenuItem("Descending", order == SortOrder.DESCENDING);
				JMenuItem unsChk = new JRadioButtonMenuItem("Unsorted", order == SortOrder.UNSORTED);
				
				ActionListener sortListener = new ActionListener() {
					@SuppressWarnings("rawtypes")
					@Override
					public void actionPerformed(ActionEvent evt) {
						SortOrder order = SortOrder.valueOf(
								((AbstractButton) evt.getSource()).getText().toUpperCase());
						((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(
								treeTbl.convertColumnIndexToModel(col), order);
					}
				};
				ascChk.addActionListener(sortListener);
				desChk.addActionListener(sortListener);
				unsChk.addActionListener(sortListener);
				
				sortMenu.add(ascChk);
				sortMenu.add(desChk);
				sortMenu.add(unsChk);
				
				// Create sub-menu containing non-leaf value aggregation functions
				JMenu aggrMenu = new JMenu("Aggregate Function");
				aggrMenu.setIcon(IconConstants.CALCULATOR_ICON);
	
				ActionListener aggrListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						TableColumnExt2 column = (TableColumnExt2) treeTbl.getColumnExt(col);
						AggregateFunction aggFcn =
								(AggregateFunction) ((JComponent) evt.getSource()).getClientProperty("aggFcn");
						column.setAggregateFunction(aggFcn);
					}
				};
				TableColumnExt2 column = (TableColumnExt2) treeTbl.getColumnExt(col);
				if (column.canAggregate()) {
					AggregateFunction colFcn = column.getAggregateFunction();
					for (AggregateFunction aggFcn : AggregateFunction.values()) {
						String text = StringUtils.capitalize(aggFcn.name().toLowerCase());
						JMenuItem aggrItem = new JRadioButtonMenuItem(text, aggFcn == colFcn);
						aggrItem.putClientProperty("aggFcn", aggFcn);
						aggrItem.addActionListener(aggrListener);
						aggrMenu.add(aggrItem);
					}
				} else {
					aggrMenu.add(new JMenuItem());
					aggrMenu.setEnabled(false);
				}
				
				// Create item for hiding column
				JMenuItem hideItem = new JMenuItem("Hide Column", IconConstants.CROSS_ICON);
				hideItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						TableColumnExt2 column = (TableColumnExt2) treeTbl.getColumnExt(col);
						column.setVisible(false);
					}
				});
				hideItem.setEnabled(col != treeTbl.getHierarchicalColumn());
	
				popup.add(sortMenu);
				popup.add(aggrMenu);
				popup.add(hideItem);
				return popup;
			}
	
			@Override
			public void mousePressed(MouseEvent me) {
				int col = ch.columnAtPoint(me.getPoint());
				// Check whether right mouse button has been pressed
				if ((col != -1) && (me.getButton() == MouseEvent.BUTTON3)) {
					this.col = col;
					lower();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me) {
				if ((me.getButton() == MouseEvent.BUTTON3) && 
						(ch.getBounds().contains(me.getPoint()))) {
					// don't show popup for web resources column
					if (!" ".equals(treeTbl.getColumn(this.col).getIdentifier())) {
						this.createPopup().show(ch, ch.getHeaderRect(this.col).x - 1, ch.getHeight() - 1);
					} else {
						this.raise();
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent me) {
				TableColumn draggedColumn = ch.getDraggedColumn();
				if (draggedColumn != null) {
					int col = treeTbl.convertColumnIndexToView(draggedColumn.getModelIndex());
					if ((col != -1) && (col != this.col)) {
						this.col = col;
					}
				}
			}
			
			@Override
			public void mouseExited(MouseEvent me) {
				if ((this.col != -1) && 
						((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
					raise();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent me) {
				if ((this.col != -1) && 
						((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
					lower();
				}
			}
			
			/**
			 * Convenience method to configure the column header to appear pressed.
			 */
			private void lower() {
				TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
				if (hr instanceof ComponentHeaderRenderer) {
					ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
					chr.getPanel().setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Color.GRAY),
							BorderFactory.createEmptyBorder(1, 1, 0, -1)));
					chr.getPanel().setOpaque(true);
					ch.repaint(ch.getHeaderRect(this.col));
				}
			}
			
			/**
			 * Convenience method to configure the column header to not appear pressed.
			 */
			private void raise() {
				TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
				if (hr instanceof ComponentHeaderRenderer) {
					ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
					chr.getPanel().setBorder(UIManager.getBorder("TableHeader.cellBorder"));
					chr.getPanel().setOpaque(false);
					ch.repaint(ch.getHeaderRect(this.col));
				}
			}
		};
		
		return ma;
	}

	/**
	 * Creates a menu containing external web resource links for the selected
	 * row of the specified table.
	 * @param table the targeted table
	 * @return a web resources menu
	 */
	private JPopupMenu createWebResourceMenu(JXTable table) {
		JPopupMenu webresourceMenu = new JPopupMenu();		
		ActionListener popupMenuItemEventListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JMenuItem menuItem = (JMenuItem) evt.getSource();
				String url = menuItem.getClientProperty("url").toString();
				try {
					if (url != null) {
						Desktop.getDesktop().browse(new URI(url));
					}
				} catch (Exception e) {
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				}
			}
		};
		JMenuItem uniProtMenuItem = new JMenuItem("UniProt", IconConstants.WEB_UNIPROT_ICON);
		int selRow = table.getSelectedRow();
		String accession = (String) table.getValueAt(selRow, table.convertColumnIndexToView(ACCESSION_COLUMN));
		uniProtMenuItem.putClientProperty("url", "http://www.uniprot.org/uniprot/" + accession);
		uniProtMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(uniProtMenuItem);
		
		JMenuItem ncbiMenuItem = new JMenuItem("NCBI", IconConstants.WEB_NCBI_ICON);
		ncbiMenuItem.putClientProperty("url", "http://www.ncbi.nlm.nih.gov/protein/" + accession);
		ncbiMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(ncbiMenuItem);
		
		JMenuItem keggMenuItem = new JMenuItem("KEGG", IconConstants.WEB_KEGG_ICON);
		String keggURL = "";
		ProteinHit proteinHit = Client.getInstance().getDatabaseSearchResult().getProteinHit(accession);
		if (proteinHit != null) {
			ReducedUniProtEntry uniprotEntry = proteinHit.getUniProtEntry();
			if (uniprotEntry != null) {
				List<String> kNumbers = uniprotEntry.getKNumbers();
				int size = kNumbers.size();
	
				for (int i = 0; i < size; i++) {
					keggURL  = "http://www.genome.jp/dbget-bin/www_bget?ko:" + kNumbers.get(i);
					if(i < size - 1) keggURL += "+";
				}
			}
		}
		keggMenuItem.putClientProperty("url", keggURL);
		keggMenuItem.addActionListener(popupMenuItemEventListener);
		// disable KEGG item if no UniProt entry is present or it contains no K numbers
		keggMenuItem.setEnabled(!keggURL.isEmpty());
		webresourceMenu.add(keggMenuItem);
		
		JMenuItem blastMenuItem = new JMenuItem("BLAST", IconConstants.WEB_BLAST_ICON);
		String sequence = proteinHit.getSequence();
		String blastURL = "http://blast.ncbi.nlm.nih.gov/Blast.cgi?PROGRAM=blastp&BLAST_PROGRAMS=blastp&PAGE_TYPE=BlastSearch&SHOW_DEFAULTS=on&LINK_LOC=blasthome&QUERY=" + sequence;
		blastMenuItem.putClientProperty("url", blastURL);
		blastMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(blastMenuItem);
		
		JMenuItem pfamMenuItem = new JMenuItem("PFAM", IconConstants.WEB_PFAM_ICON);
		pfamMenuItem.putClientProperty("url", "http://pfam.sanger.ac.uk/protein/" + accession);
		pfamMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(pfamMenuItem);
		
		JMenuItem interproMenuItem = new JMenuItem("InterPro", IconConstants.WEB_INTERPRO_ICON);
		interproMenuItem.putClientProperty("url", "http://www.ebi.ac.uk/interpro/protein/" + accession);
		interproMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(interproMenuItem);
		
		JMenuItem pdbMenuItem = new JMenuItem("PDB", IconConstants.WEB_PDB_ICON);
		pdbMenuItem.putClientProperty("url", "http://www.rcsb.org/pdb/protein/" + accession);
		pdbMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(pdbMenuItem);
		
		JMenuItem reactomeMenuItem = new JMenuItem("Reactome", IconConstants.WEB_REACTOME_ICON);
		reactomeMenuItem.putClientProperty("url", "http://www.reactome.org/cgi-bin/search2?DB=test_reactome_46&SPECIES=&OPERATOR=ALL&QUERY=" + accession);
		reactomeMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(reactomeMenuItem);
		
		JMenuItem quickGoMenuItem = new JMenuItem("QuickGO", IconConstants.WEB_QUICKGO_ICON);
		quickGoMenuItem.putClientProperty("url", "http://www.ebi.ac.uk/QuickGO/GProtein?ac=" + accession);
		quickGoMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(quickGoMenuItem);
		
		JMenuItem eggNogMenuItem = new JMenuItem("eggNOG", IconConstants.WEB_EGGNOG_ICON);
		eggNogMenuItem.putClientProperty("url", "http://eggnog.embl.de/uniprot=" + accession);
		eggNogMenuItem.addActionListener(popupMenuItemEventListener);
		webresourceMenu.add(eggNogMenuItem);
		
		Rectangle cellRect = table.getCellRect(selRow, table.getSelectedColumn(), true);
		webresourceMenu.show(table, cellRect.x, cellRect.y + cellRect.height);
		return webresourceMenu;
	}

	/**
	 * Returns whether the checkbox selection paths of the tree table have changed.
	 * @return <code>true</code> if the selection has changed, <code>false</code> otherwise
	 */
	public boolean hasCheckSelectionChanged() {
		Set<TreePath> currentPaths = new HashSet<>(Arrays.asList(
				this.getTreeTable().getCheckBoxTreeSelectionModel().getSelectionPaths()));
		return !currentPaths.equals(checkSelection);
	}

	/**
	 * Caches the current checkbox selection paths.
	 */
	public void cacheCheckSelection() {
		checkSelection = new HashSet<>(Arrays.asList(
				this.getTreeTable().getCheckBoxTreeSelectionModel().getSelectionPaths()));
		checkSelectionNeedsUpdating = false;
	}
	
	/**
	 * Sets whether the checkbox selection needs to be updated.
	 * @param needsUpdating <code>true</code> if the selection is in need of updating
	 */
	public void setCheckSelectionNeedsUpdating(boolean needsUpdating) {
		checkSelectionNeedsUpdating |= needsUpdating;
	}
	
	/**
	 * Convenience method to update the checkbox selection state of the
	 * tree table to what's stored separately in its nodes.
	 */
	@SuppressWarnings("unchecked")
	public void updateCheckSelection() {
		CheckBoxTreeTable treeTbl = this.getTreeTable();
		RowFilter<TreeModel, Integer> filter = (RowFilter<TreeModel, Integer>) treeTbl.getRowFilter();
		
		if (checkSelectionNeedsUpdating) {
			// prevent selection listener from capturing events fired during synchronization
			synching = true;
			
			CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
			SortableTreeTableModel model = (SortableTreeTableModel) treeTbl.getTreeTableModel();
			PhylogenyTreeTableNode root = (PhylogenyTreeTableNode) model.getRoot();
			
			// temporarily disable row filter
			treeTbl.setRowFilter(null);
			
			// iterate nodes
			Enumeration<TreeNode> dfe = root.depthFirstEnumeration();
			while (dfe.hasMoreElements()) {
				TreeNode treeNode = dfe.nextElement();
				// only leaf nodes contain relevant data
				if (treeNode.isLeaf()) {
					Object userObject = ((TreeTableNode) treeNode).getUserObject();
					// just one more sanity check
					if (userObject instanceof ProteinHit) {
						TreePath path = new TreePath(model.getPathToRoot((TreeTableNode) treeNode));
						boolean nodeSel = ((ProteinHit) userObject).isSelected();
						if (nodeSel) {
							cbtsm.addSelectionPath(path);
						} else {
							cbtsm.removeSelectionPath(path);
						}
					}
				}
			}
			
			// cache selection
			this.cacheCheckSelection();
			
			synching = false;
		}
		
		// re-apply row filter
		treeTbl.setRowFilter(filter);
	}

}