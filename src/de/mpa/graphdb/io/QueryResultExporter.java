package de.mpa.graphdb.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import de.mpa.client.Constants;
import de.mpa.client.ui.CheckBoxTreeTableNode;

public class QueryResultExporter {
	
	/**
	 * This method exports all content from the graph database query table.
	 * @param filePath The path string pointing to the target file.
	 * @param treeTable The query result table.
	 * @throws IOException
	 */
	public static void exportResults(String filePath, JXTreeTable treeTable) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		TreeTableModel tableModel = treeTable.getTreeTableModel();
		
		// Write content of table.
        CheckBoxTreeTableNode rootNode = (CheckBoxTreeTableNode) tableModel.getRoot();
        
        Enumeration<TreeNode> dfe = rootNode.preOrderEnumeration();
		while (dfe.hasMoreElements()) {
			CheckBoxTreeTableNode treeNode = (CheckBoxTreeTableNode) dfe.nextElement();
			
			for (int i = 0; i < treeNode.getColumnCount(); i++) {
				Object value = treeNode.getValueAt(i);
				
				if (value != null) {
					writer.append(value.toString() + Constants.TSV_FILE_SEPARATOR);
				}
				
			}
			writer.newLine();
		}       
		writer.close();
	}

}
