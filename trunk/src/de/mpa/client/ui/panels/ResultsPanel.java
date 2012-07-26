package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTitledPanel;
import org.jfree.chart.ChartPanel;

import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.OntologyData;
import de.mpa.client.ui.chart.OntologyPieChart;
import de.mpa.client.ui.chart.OntologyPieChart.PieChartType;
import de.mpa.client.ui.icons.IconConstants;

public class ResultsPanel extends JPanel {
	
	private ClientFrame clientFrame;
	private JPanel ovPnl;
	private DbSearchResultPanel dbPnl;
	private SpecSimResultPanel ssPnl;
	private DeNovoResultPanel dnPnl;
	private JButton updateBtn;
	private Map<String, KeywordOntology> ontologyMap;
	private HashMap<String, Integer> molFunctionOccMap;
	private ChartPanel chartPnl;
	private JXTitledPanel chartTtlPnl;
	private OntologyData ontologyData;
	private HashMap<String, Integer> biolProcessOccMap;
	private HashMap<String, Integer> cellCompOccMap;
	private JComboBox chartTypeCbx;
	private ChartType pieChartType = PieChartType.BIOLOGICAL_PROCESS;
	private JPanel overviewBtnPnl;
	
	public ResultsPanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.dbPnl = new DbSearchResultPanel();
		this.ssPnl = new SpecSimResultPanel();
		this.dnPnl = new DeNovoResultPanel();
		initComponents();
	}

	private void initComponents() {
		// configure main panel layout
		this.setLayout(new FormLayout("p:g, 5dlu", "f:p:g, -40px, b:p, 5dlu"));
		
		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, contentBorderInsets.bottom, 0));
		final JTabbedPane resTpn = new JTabbedPane(JTabbedPane.BOTTOM);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets);
		
		ovPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		createOverviewPanel();
		
		resTpn.addTab(" ", ovPnl);
		resTpn.addTab(" ", dbPnl);
		resTpn.addTab(" ", ssPnl);
		resTpn.addTab(" ", dnPnl);
		
		// TODO: use proper icons, possibly slightly larger ones, e.g. 40x40?
		resTpn.setTabComponentAt(0, clientFrame.createTabButton("Overview",	new ImageIcon(getClass().getResource("/de/mpa/resources/icons/overview32.png")), resTpn));
		resTpn.setTabComponentAt(1, clientFrame.createTabButton("Database Search Results", new ImageIcon(getClass().getResource("/de/mpa/resources/icons/dbsearch32.png")), resTpn));
		resTpn.setTabComponentAt(2, clientFrame.createTabButton("Spectral Similarity Results", new ImageIcon(getClass().getResource("/de/mpa/resources/icons/view_page32.png")), resTpn));
		resTpn.setTabComponentAt(3, clientFrame.createTabButton("Blast Search Results",	new ImageIcon(getClass().getResource("/de/mpa/resources/icons/blast32.png")), resTpn));
		Component tabComp = resTpn.getTabComponentAt(0);
		tabComp.setPreferredSize(new Dimension(tabComp.getPreferredSize().width, 40));

		// create navigation button panel
		final JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));
		navPnl.setOpaque(false);
		
		JButton prevBtn = new JButton("Prev", IconConstants.PREV_ICON);
		prevBtn.setRolloverIcon(IconConstants.PREV_ROLLOVER_ICON);
		prevBtn.setPressedIcon(IconConstants.PREV_PRESSED_ICON);
		prevBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		prevBtn.setFont(prevBtn.getFont().deriveFont(Font.BOLD, prevBtn.getFont().getSize2D()*1.25f));
		prevBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(2);
			}
		});		
		JButton nextBtn = new JButton("Next", IconConstants.NEXT_ICON);
		nextBtn.setRolloverIcon(IconConstants.NEXT_ROLLOVER_ICON);
		nextBtn.setPressedIcon(IconConstants.NEXT_PRESSED_ICON);
		nextBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		nextBtn.setFont(nextBtn.getFont().deriveFont(Font.BOLD, nextBtn.getFont().getSize2D()*1.25f));
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(5);
			}
		});
		
		navPnl.add(prevBtn, CC.xy(1, 1));
		navPnl.add(nextBtn, CC.xy(3, 1));

		// add everything to main panel
		this.add(navPnl, CC.xy(1, 3));
		this.add(resTpn, CC.xyw(1, 1, 2));
	}

	
	private void createOverviewPanel() {
		// Defaults
		Map<String, Integer> defaultMap = new HashMap<String, Integer>();
		defaultMap.put("Resembles Pac-Man", 80);
		defaultMap.put("Does not resemble Pac-Man", 20);
		ontologyData = new OntologyData();
		ontologyData.setBiolProcessOccMap(defaultMap);
		updateBtn = new JButton("Update");
		updateBtn.setFocusPainted(false);
		updateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateButtonPressed();
			}
		});
		
		final String[] chartTypeLabels = new String[] {"Biological Process" , "Molecular Function", "Cellular Component"};
		
		chartTypeCbx = new JComboBox(chartTypeLabels);
		chartTypeCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int barTypeIndex = chartTypeCbx.getSelectedIndex();
					switch (barTypeIndex) {
					case 0:
						pieChartType = PieChartType.BIOLOGICAL_PROCESS;
						break;
					case 1:
						pieChartType = PieChartType.MOLECULAR_FUNCTION;
					case 2: 
						pieChartType = PieChartType.CELLULAR_COMPONENT;
					default:
						pieChartType = PieChartType.BIOLOGICAL_PROCESS;
						break;
					}
					updateChart();
				}
			}
		});
		chartTypeCbx.setPreferredSize(new Dimension(chartTypeCbx.getPreferredSize().width, 20));
		overviewBtnPnl = new JPanel(new FormLayout("p, 5dlu, p", "p"));
		overviewBtnPnl.add(chartTypeCbx, CC.xy(1, 1));
		overviewBtnPnl.add(updateBtn, CC.xy(3, 1));
		updateChart();
	}
	
	/**
	 * Updates the bar plot.
	 */
	private void updateChart(){
		OntologyPieChart ontologyPieChart = new OntologyPieChart(ontologyData, pieChartType);
		// Create chart panel
		chartPnl = new ChartPanel(ontologyPieChart.getChart());
		chartPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		chartPnl.updateUI();
		if(chartTtlPnl != null) {
			chartTtlPnl.removeAll();
		}
		chartTtlPnl = PanelConfig.createTitledPanel("Overview", chartPnl);
		chartTtlPnl.setRightDecoration(overviewBtnPnl);
		chartTtlPnl.validate();
		chartTtlPnl.repaint();
		
		ovPnl.removeAll();
		ovPnl.add(chartTtlPnl, CC.xy(2, 2));
		
	}
	
	protected void updateButtonPressed() {
		UpdateTask resultsTask = new UpdateTask();
		resultsTask.execute();
	}
	
	/**
	 * This method sets up the ontologies.
	 * @param dbSearchResult Database search result data.
	 */
	public void setupOntologies(DbSearchResult dbSearchResult) {
		// Get the ontology map.
		if(ontologyMap == null) {
			ontologyMap = UniprotAccessor.getOntologyMap();
		}
		
		// Map to count the occurrences of each molecular function.
		ontologyData.clear();
		
		biolProcessOccMap = new HashMap<String, Integer>();
		cellCompOccMap = new HashMap<String, Integer>();
		molFunctionOccMap = new HashMap<String, Integer>();
		
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			
			// Entry must be provided
			if(entry != null){
				List<Keyword> keywords = entry.getKeywords();
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if(ontologyMap.containsKey(keyword)) {
						//TODO: Do the calculation on spectral level.
						KeywordOntology kwOntology = ontologyMap.get(keyword);
						switch (kwOntology) {
						case BIOLOGICAL_PROCESS:
							if(biolProcessOccMap.containsKey(keyword)){
								biolProcessOccMap.put(keyword, biolProcessOccMap.get(keyword)+1);
							} else {
								biolProcessOccMap.put(keyword, 1);
							}
							break;
						case CELLULAR_COMPONENT:
							if(cellCompOccMap.containsKey(keyword)){
								cellCompOccMap.put(keyword, cellCompOccMap.get(keyword)+1);
							} else {
								cellCompOccMap.put(keyword, 1);
							}
							break;
						case MOLECULAR_FUNCTION:
							if(molFunctionOccMap.containsKey(keyword)){
								molFunctionOccMap.put(keyword, molFunctionOccMap.get(keyword)+1);
							} else {
								molFunctionOccMap.put(keyword, 1);
							}
							break;
						}
					}
				}
			}
		}
		ontologyData.setBiolProcessOccMap(biolProcessOccMap);
		ontologyData.setCellCompOccMap(cellCompOccMap);
		ontologyData.setMolFunctionOccMap(molFunctionOccMap);
	}
	
	private class UpdateTask extends SwingWorker {

		protected Object doInBackground() {
			DbSearchResult dbSearchResult = null;
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Fetch the database search result.
				try {
					dbSearchResult = Client.getInstance().getDbSearchResult();
					setupOntologies(dbSearchResult);
					finished();
				} catch (RemoteDataAccessException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * Continues when the results retrieval has finished.
		 */
		public void finished() {
			updateChart();
			ovPnl.repaint();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 * @return the dbPnl
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return dbPnl;
	}

	/**
	 * @return the ssPnl
	 */
	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
		return ssPnl;
	}

	/**
	 * @return the dnPnl
	 */
	public DeNovoResultPanel getDeNovoSearchResultPanel() {
		return dnPnl;
	}

}
