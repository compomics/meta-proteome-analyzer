package de.mpa.graphdb.io;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import de.mpa.client.Client;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.graphdb.cypher.CypherQueryFactory;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.properties.ExperimentProperty;

public class ExperimentComparison {
	
	/**
	 * Graph database handler.
	 */
	private GraphDatabaseHandler graphHandler;
	
	/**
	 * Type level.
	 */
	private ChartType typeLevel;
	
	/**
	 * Hierarchy level.
	 */
	private HierarchyLevel countLevel;
	
	/**
	 * Execution Result instance.
	 */
	private ExecutionResult executionResult;
	
	/**
	 * List of experiments.
	 */
	private List<AbstractExperiment> experiments;
	
	/**
	 * Concatenated list of metaproteins.
	 */
	private ProteinHitList metaproteins;
	
	/**
	 * Data map.
	 */
	private Map<String, Long[]> dataMap;
	
	/**
	 * Client instance.
	 */
	private Client client = Client.getInstance();
	
	public ExperimentComparison(List<AbstractExperiment> experiments, ProteinHitList metaprot, GraphDatabaseHandler graphHandler, ChartType typeLevel, HierarchyLevel countLevel) {
		this.experiments = experiments;
		this.metaproteins = metaprot;
		this.graphHandler = graphHandler;
		this.typeLevel = typeLevel;
		this.countLevel = countLevel;
		executeQuery();
		formatData();
	}
	
	/**
	 * This method executes the query.
	 */
	private void executeQuery() {
		client.firePropertyChange("new message", null, "QUERYING COMPARISON DATA");
		client.firePropertyChange("indeterminate", false, true);
		try {
			if (typeLevel == HierarchyLevel.META_PROTEIN_LEVEL) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getMetaProteinsWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == HierarchyLevel.PROTEIN_LEVEL) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getProteinsWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == HierarchyLevel.PEPTIDE_LEVEL) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getPeptidesWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == OntologyChartType.BIOLOGICAL_PROCESS) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getBiologicalProcessesWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == OntologyChartType.CELLULAR_COMPONENT) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getCellularComponentsWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel == OntologyChartType.MOLECULAR_FUNCTION) {
				executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getMolecularFunctionsWithCountsByExperiments(countLevel.getCountIdentifier()));
			} else if (typeLevel instanceof TaxonomyChartType) {
				typeLevel = (TaxonomyChartType) typeLevel;
				if (((TaxonomyChartType) typeLevel).getDepth() == 0) {
					executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getSubspeciesWithCountsByExperiments(countLevel.getCountIdentifier()));
				} else {
					executionResult = graphHandler.executeCypherQuery(CypherQueryFactory.getTaxonomyWithCountsByExperiments(countLevel.getCountIdentifier(), typeLevel.getTitle()));
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
    	dataMap = new TreeMap<String, Long[]>();
    	List<String> resultColumns = executionResult.columns();
    	int expIndex = 0;
		for (Map<String, Object> map : executionResult) {
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
						for (int i = 0; i < experiments.size(); i++) {
							if (experiments.get(i).getTitle().equals(value.toString())) {
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
							for (ProteinHit prot : metaproteins) {
								// Fetch additional metaprotein information from the result object
								if (prot.getDescription().equals(metaKey)) {
									StringBuilder build = new StringBuilder(
											"MP|"
											+ metaKey
											+ "|"
											+ prot.getTaxonomyNode()
													.getName() + "|");
									for (String str : prot.getUniProtEntry().getEcnumbers()) {
										build.append(str + ";");
									}
									build.deleteCharAt(build.length() - 1);
									build.append("|");
									for (String str : prot.getUniProtEntry().getKonumbers()) {
										build.append(str + ";");
									}
									build.deleteCharAt(build.length() - 1);
									key = build.toString();
								}
							}
						}
						Long[] countList;
						
						if (dataMap.get(key) != null) {
							countList = dataMap.get(key);
						} else {
							countList = new Long[experiments.size()];
						}
						countList[expIndex] = count;		
						dataMap.put(key.toString(), countList);
					}
				}
			}
		}
		client.firePropertyChange("new message", null, "QUERYING COMPARISON DATA FINISHED");
		client.firePropertyChange("indeterminate", true, false);
	}
    
    /**
     * Returns the comparison data map.
     * @return Comparison data map.
     */
	public Map<String, Long[]> getDataMap() {
		return dataMap;
	}
}
