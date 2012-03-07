
//package de.mpa.client.ui.dialogs;
//
//import java.awt.Point;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//
//import javax.swing.JDialog;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.table.DefaultTableModel;
//
//import de.mpa.db.accessor.Experiment;
//import de.mpa.db.accessor.Property;
//
//public class ProjectDialog extends JDialog {
//	public ProjectDialog() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	private void init() {
//		projectsTbl.addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				// if component is enabled and left click and one click--> create other tables
//				if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1) {
//
//					Point p = e.getPoint();
//					int row = projectsTbl.convertRowIndexToModel(projectsTbl.rowAtPoint(p));
//
//					if (e.getClickCount() == 1) {
//						// create Property and Experiment Table
//						if (row > 0) {
//							long fk_projectid = (Long)projectsTbl.getValueAt(row, 0);
//							// Use table.convertRowIndexToModel / table.convertColumnIndexToModle to convert to view indices
//							//empty properties table
//							while (projectPropertiesTbl.getRowCount()>0) {
//								((DefaultTableModel)projectPropertiesTbl.getModel()).removeRow(projectPropertiesTbl.getRowCount()-1);
//							}
//							//refill properties table
//							((DefaultTableModel)projectPropertiesTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW PROJECT PROPERTY</b></html>", null});
//
//							// query database for properties
//							ArrayList<Property> projectPropertyList= new ArrayList<Property>(); 
//							try {
//								client.initDBConnection();
//								projectPropertyList = new ArrayList<Property>(client.getProjectProperties(fk_projectid));
//								client.closeDBConnection();
//							} catch (SQLException e1) {
//								e1.printStackTrace();
//							}
//							for (int i = 0; i < projectPropertyList.size(); i++) {
//								((DefaultTableModel)projectPropertiesTbl.getModel()).addRow(new Object[]{projectPropertyList.get(i).getPropertyid(),projectPropertyList.get(i).getName(),projectPropertyList.get(i).getValue()});
//								// justify column width
//								packColumn(projectPropertiesTbl,0,5);
//								projectPropertiesTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
//								projectPropertiesTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);
//							}
//							// fill experiment column
//							//empty experiments table
//							while (experimentsNameTbl.getRowCount()>0) {
//								((DefaultTableModel)experimentsNameTbl.getModel()).removeRow(experimentsNameTbl.getRowCount()-1);
//							}
//							//refill properties table
//							((DefaultTableModel)experimentsNameTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW EXPERIMENT</b></html>", null});
//							// query database for properties
//							ArrayList<Experiment> projectExperimentList = new ArrayList<Experiment>(); 
//							try {
//								client.initDBConnection();
//								projectExperimentList = new ArrayList<Experiment>(client.getProjectExperiments(fk_projectid));
//								client.closeDBConnection();
//							} catch (SQLException e1) {
//								e1.printStackTrace();
//							}
//							for (int i = 0; i < projectExperimentList.size(); i++) {
//								Object[] test = new Object[] {projectExperimentList.get(i).getExperimentid(),
//										projectExperimentList.get(i).getTitle(),
//										projectExperimentList.get(i).getCreationdate()};
//								((DefaultTableModel)experimentsNameTbl.getModel()).addRow(test);
//								// justify column width
//								packColumn(experimentsNameTbl,0,5);
//								experimentsNameTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
//								packColumn(experimentsNameTbl,2,5);
//							}
//							//empty experiment properties
//							((DefaultTableModel)experimentPropertiesTbl.getModel()).setRowCount(0);
//						}	
//						// edit cells
//					} else if (e.getClickCount() == 2) {
//					//	String oldVal = projectsTbl.getValueAt(row, 1).toString();
//						if (projectsTbl.getSelectedRow() == 0) {
//							projectsTbl.setValueAt("", row, 1);
//						}
//						projectsTbl.editCellAt(row, 1);
//					}
//				}
//			}
//		});
//
//		projectsTbl.getModel().addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				if (e.getType() == TableModelEvent.UPDATE) {
//					int row = projectsTbl.convertRowIndexToModel(e.getFirstRow());
//					if (projectsTbl.getValueAt(row, 1) != "") {
//						try {
//							client.initDBConnection();
//							if (row == 0) {
//								// create new project
//								String pTitle= (String) projectsTbl.getValueAt(e.getFirstRow(), 1);
//								Timestamp pCreationdate= new Timestamp(new Date().getTime());
//								Timestamp pModificationdate = new Timestamp(new Date().getTime());
//								client.createNewProject((String)pTitle,(Timestamp)pCreationdate,(Timestamp)pModificationdate);
//								recreateTable();
//							} else {
//								//change project
//								client.modifyProject((Long)projectsTbl.getValueAt(e.getFirstRow(), 0),
//										projectsTbl.getValueAt(e.getFirstRow(), e.getColumn()).toString());
//							}
//							recreateTable();
//						} catch (SQLException e1) {
//							e1.printStackTrace();
//						}
//					}
//				}
//			}
//		});
//
//	}

	
//		manageProjectsPnl.add(deleteProjectBtn,cc.xy(2, 4));
//		//Table for project properties
//		projectPropertiesTbl = new JTable(new DefaultTableModel(){
//			{ setColumnIdentifiers(new Object[] {"#","project property","value"}); }
//			public boolean isCellEditable(int row, int col) {
//				return ((col == 0) ? false :true);
//			}
//			public Class<?> getColumnClass(int col) {
//				switch (col) {
//				case 0:
//					return Long.class;
//				case 1:
//					return String.class;
//				case 2:
//					return String.class;
//				default:
//					return getValueAt(0,col).getClass();
//				}
//			}	
//		});
//		// change and add to the projectProperty Table
//		projectPropertiesTbl.getModel().addTableModelListener(new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				if (e.getType() == TableModelEvent.UPDATE) {
//					int row = projectPropertiesTbl.convertRowIndexToModel(e.getFirstRow());
//					if (projectPropertiesTbl.getValueAt(row, 1) != "") {
//						try {
//							client.initDBConnection();
//							if (row == 0) {
//								// create new project
//								String pTitle= (String) projectPropertiesTbl.getValueAt(e.getFirstRow(), 1);
//								Timestamp pCreationdate= new Timestamp(new Date().getTime());
//								Timestamp pModificationdate = new Timestamp(new Date().getTime());
//								client.createNewProject((String)pTitle,(Timestamp)pCreationdate,(Timestamp)pModificationdate);
//								recreateTable();
//							} else {
//								//change project property
//								client.modifyProjectProperty((Long)projectPropertiesTbl.getValueAt(e.getFirstRow(), 0),  //propertyid,
//										projectPropertiesTbl.getValueAt(e.getFirstRow(), 1).toString(),//propertyName,
//										projectPropertiesTbl.getValueAt(e.getFirstRow(), 2).toString());//propertyValue
//							}
//							recreateTable();
//						} catch (SQLException e1) {
//							e1.printStackTrace();
//						}
//					}
//				}
//			}
//		});		
//}
