package de.mpa.client.ui.panels;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;

import de.mpa.client.model.AbstractExperiment;


public class ComparePanelTableModel extends DefaultTableModel {
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of experiments.
	 */
	private List<AbstractExperiment> experiments;

	/**
	 * Mapping from description to experimental data counts.
	 */
	private Map<String, Long[]> dataMap = new TreeMap<String, Long[]>();
	
	/**
	 * List of descriptions.
	 */
	private List<String> descriptions;

    /**
     * Default empty constructor.
     */
    public ComparePanelTableModel() {}

    /**
     * Constructor that retrieves the underlying data
     * @param results PeakPicking results - chosen by the user in the comparison
     * @param comparisonPeaks Selected comparison peaks - chosen by the user previously
     * @param tableDataType TableDataType object. 
     */
    public ComparePanelTableModel(Map<String, Long[]> dataMap, List<AbstractExperiment> experiments) {
        this.dataMap = dataMap;
        descriptions = new ArrayList<String>(dataMap.keySet());
        this.experiments = experiments;
    }
    
	@Override
    public int getRowCount() {
        if (this.dataMap == null || this.dataMap.size() == 0) {
            return 0;
        }
        return this.descriptions.size();
    }

    @Override
    public int getColumnCount() {
        if (this.experiments == null) {
            return 0;
        }
        return this.experiments.size() + 1;
    }

    @Override
	public String getColumnName(int column) {
    	// First column.
		if (column == 0)
			return "Description";
		else
			return this.experiments.get(column - 1).getTitle();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return this.descriptions.get(row);
		} else if (column >= 1) {
			Long[] values = this.dataMap.get(this.descriptions.get(row));
			return values[column - 1];
		} 
		return 0L;
	}

    @Override
    public Class<?> getColumnClass(int columnIndex) {
    	if (columnIndex == 0) return String.class;
    	if (columnIndex > 0) return Long.class;
    	else return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}


