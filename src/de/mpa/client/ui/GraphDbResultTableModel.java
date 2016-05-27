package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.neo4j.graphdb.Node;

@SuppressWarnings("serial")
public class GraphDbResultTableModel extends DefaultTableModel {
		
		/**
		 * List of the column identifiers.
		 */
		private List<String> columnNames = new ArrayList<String>();;
		
		/**
		 * Table columns row count.
		 */
		private int rowCount;
		
		/**
		 * List of first nodes.
		 */
		private List<Node> firstNodes;
		
	    /**
	     * Constructor which sets a new empty table.
	     *
	     */
	    public GraphDbResultTableModel() {
	    }

	    /**
	     * Constructor which sets a new table model for the graph database result.
	     */
	    public GraphDbResultTableModel(Iterator<Object> resultObjects) {
	        setUpTableModel(resultObjects);
	    }

	    /**
	     * Update the data in the table model without having to reset the whole table model. 
	     *
	     * @param result ExecutionResult
	     */
	    public void updateDataModel(Iterator<Object> resultObjects) {
	        setUpTableModel(resultObjects);
	        fireTableStructureChanged();
	        //fireTableDataChanged();
	    }

	    /**
	     * Set up the table model.
	     *
	     * @param result ExecutionResult result
	     */
	    private void setUpTableModel(Iterator<Object> resultObjects) {
			rowCount = 0;
			firstNodes = new ArrayList<Node>();
	        while (resultObjects.hasNext()) {
	            final Object value = resultObjects.next();
	            if (value instanceof Node) {
	                Node n = (Node) value;
	                firstNodes.add(n);
	                rowCount++;
	                Iterator<String> iter = n.getPropertyKeys().iterator();
	                while(iter.hasNext()) {
	                	String propertyKey = iter.next();
	                	// Add only if column name is not already in the list.
	                	if(!columnNames.contains(propertyKey)){
	                		columnNames.add(propertyKey);
	                	}
	                }
	            }
	        }
	    }

	    @Override
	    public int getRowCount() {
	    	return rowCount;
	    }

	    @Override
	    public int getColumnCount() {
	        return 4;
	    }

	    @Override
	    public String getColumnName(int column) {
	        if (column == 0) {
	            return "";
	        } else if (column == 1) {
	            return "#";
	        } else if (column > 1 && column - 2 < columnNames.size()) {
	            return columnNames.get(column - 2);
	        } else {
	            return "";
	        }
	    }

	    @Override
	    public Object getValueAt(int row, int column) {
	    	if (column == 0) {
	    		return true;
	    	} else if (column == 1) {
                return row + 1;
            } else if (column > 1 && column - 2 < columnNames.size()) {
            	Node node = firstNodes.get(row);
            	
            	String propertyKey = columnNames.get(column);
				if(node.hasProperty(propertyKey)) {
            		return node.getProperty(propertyKey);
            	} 
            } else {
            	return "";
            }
			return column;
	    }

	    @Override
	    public Class<?> getColumnClass(int columnIndex) {
	        for (int i = 0; i < getRowCount(); i++) {
	            if (getValueAt(i, columnIndex) != null) {
	                return getValueAt(i, columnIndex).getClass();
	            }
	        }
	        return String.class;
	    }

	    @Override
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
	        return false;
	    }
	}
