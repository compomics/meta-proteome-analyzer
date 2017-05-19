package de.mpa.db.neo4j.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jvnet.jaxb2_commons.xml.bind.model.MPackaged;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import de.mpa.client.Client;
import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart;
import de.mpa.db.neo4j.cypher.CypherQueryFactory;
import de.mpa.db.neo4j.insert.GraphDatabaseHandler;
import de.mpa.db.neo4j.properties.ExperimentProperty;
import de.mpa.model.MPAExperiment;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.unused.ProteinHitList;

public class ExperimentComparison {
	
	/**
	 * Graph database handler.
	 */
	private final GraphDatabaseHandler graphHandler;
	
	/**
	 * Type level.
	 */
	private ChartType typeLevel;
	
	/**
	 * Hierarchy level.
	 */
	private final HierarchyLevel countLevel;
	
	/**
	 * Execution Result instance.
	 */
	private ExecutionResult executionResult;
	
	/**
	 * List of experiments.
	 */
	private final ArrayList<MPAExperiment> experiments;
	
	/**
	 * Concatenated list of metaproteins.
	 */
	private final ProteinHitList metaproteins;
	
	/**
	 * Data map.
	 */
	private Map<String, Long[]> dataMap;
	
	/**
	 * Client instance.
	 */
	private final Client client = Client.getInstance();
	
	public ExperimentComparison(ArrayList<MPAExperiment> experiments, ProteinHitList metaprot, GraphDatabaseHandler graphHandler, ChartType typeLevel, HierarchyLevel countLevel) {
		this.experiments = experiments;
        this.metaproteins = metaprot;
		this.graphHandler = graphHandler;
		this.typeLevel = typeLevel;
		this.countLevel = countLevel;

        this.executeQuery();
        this.formatData();
	}
	
	/**
	 * This method executes the query.
	 */
	private void executeQuery() {
        this.client.firePropertyChange("new message", null, "QUERYING COMPARISON DATA");
        this.client.firePropertyChange("indeterminate", false, true);
		try {
			if (this.typeLevel == HierarchyLevel.META_PROTEIN_LEVEL) {
                this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getMetaProteinsWithCountsByExperiments(this.countLevel.getCountIdentifier()));
			} else if (this.typeLevel == HierarchyLevel.PROTEIN_LEVEL) {
                this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getProteinsWithCountsByExperiments(this.countLevel.getCountIdentifier()));
			} else if (this.typeLevel == HierarchyLevel.PEPTIDE_LEVEL) {
                this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getPeptidesWithCountsByExperiments(this.countLevel.getCountIdentifier()));
			} else if (this.typeLevel == OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getBiologicalProcessesWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == OntologyChart.OntologyChartType.CELLULAR_COMPONENT) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getCellularComponentsWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == OntologyChart.OntologyChartType.MOLECULAR_FUNCTION) {
                this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getMolecularFunctionsWithCountsByExperiments(this.countLevel.getCountIdentifier()));
			} else if (this.typeLevel instanceof TaxonomyChart.TaxonomyChartType) {
//				typeLevel = typeLevel;
				if (((TaxonomyChart.TaxonomyChartType) this.typeLevel).getDepth() == 0) {
                    this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getSubspeciesWithCountsByExperiments(this.countLevel.getCountIdentifier()));
				} else {
                    this.executionResult = this.graphHandler.executeCypherQuery(CypherQueryFactory.getTaxonomyWithCountsByExperiments(this.countLevel.getCountIdentifier(), this.typeLevel.getTitle()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
     * Formats the data for the table model.
     */
    private void formatData() {
        this.dataMap = new TreeMap<String, Long[]>();
    	List<String> resultColumns = this.executionResult.columns();
    	int expIndex = 0;
		for (Map<String, Object> map : this.executionResult) {
			Long count = 0L;
			
			// Iterate result columns.
			for (String col : resultColumns) {
				// Counts can only be of type Long.
				if (map.get(col) instanceof Long) {
					count = (Long) map.get(col);
				} else {
					Node node = (Node) map.get(col);
					
					// Experiment node.
					if (node.hasProperty(ExperimentProperty.IDENTIFIER.toString())) {
						Object value = node.getProperty(ExperimentProperty.IDENTIFIER.toString());
						for (int i = 0; i < this.experiments.size(); i++) {
							if (this.experiments.get(i).getTitle().equals(value.toString())) {
								expIndex = i;
							}
						}
					}
					
					// Entity node.
					if (node.hasProperty("Identifier")) {
						Object key = node.getProperty("Identifier");
						if (key.toString().startsWith("Meta-Protein")) {
							key = node.getProperty("Description");
							String metaKey = key.toString();
							for (ProteinHit prot : this.metaproteins) {
								// Fetch additional metaprotein information from the result object
								if (prot.getDescription().equals(metaKey)) {
									StringBuilder build = new StringBuilder(
											"MP|"
											+ metaKey
											+ "|"
											+ prot.getTaxonomyNode()
													.getName() + "|");
									if (prot.getUniProtEntry() != null) {
										for (String str : prot.getUniProtEntry().getEcnumbers()) {
											build.append(str + ";");
										}
									}
									build.deleteCharAt(build.length() - 1);
									build.append("|");
									if (prot.getUniProtEntry() != null) {
										for (String str : prot.getUniProtEntry().getKonumbers()) {
											build.append(str + ";");
										}
									}
									build.deleteCharAt(build.length() - 1);
									key = build.toString();
								}
							}
						}
						Long[] countList;
						
						if (this.dataMap.get(key) != null) {
							countList = this.dataMap.get(key);
						} else {
							countList = new Long[this.experiments.size()];
						}
						countList[expIndex] = count;
                        this.dataMap.put(key.toString(), countList);
					}
				}
			}
		}
        this.client.firePropertyChange("new message", null, "QUERYING COMPARISON DATA FINISHED");
        this.client.firePropertyChange("indeterminate", true, false);
	}
    
    /**
     * Returns the comparison data map.
     * @return Comparison data map.
     */
	public Map<String, Long[]> getDataMap() {
		return this.dataMap;
	}
}
