package de.mpa.client.ui.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Scrollable pane wrapping chart views.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class ScrollableChartPane extends JScrollPane implements Busyable {

	/**
	 * The chart panel instance.
	 */
	private ChartPanel chartPnl;

	/**
	 * Combobox for specifying the displayed hierarchy level of pie plots
	 * (protein, peptide, spectrum level).
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox chartHierarchyCbx;

	/**
	 * Checkbox for flagging whether proteins grouped as 'Unknown' in pie charts
	 * shall be hidden.
	 */
	private JCheckBox chartHideUnknownChk;

	/**
	 * Panel containing a checkbox and a spinner for flagging whether pie
	 * segments of a relative size below a certain value threshold shall be
	 * merged into a category labeled 'Others'.
	 */
	private JPanel chartGroupMinorPnl;
	
	/**
	 * Cached value for rotation angle of pie charts.
	 */
	private double chartPieAngle = 36.0;
	
	/**
	 * Flag denoting whether this component is currently loading/manipulating its contents.
	 */
	private boolean busy;

	/**
	 * Constructs a scrollable chart panel view from the specified chart.
	 * @param chart the chart
	 */
	public ScrollableChartPane(JFreeChart chart) {
		super();
		
		this.initComponents();
		
		this.setChart(chart, true);
		
		this.setViewportView(this.chartPnl);
	}

	/**
	 * Creates and lays out the pane's components.
	 */
	private void initComponents() {
		
		chartPnl = new ChartPanel(null) {
			@Override
			protected void paintChildren(Graphics g) {
				// Fade chart if disabled
				if (!this.isEnabled()) {
					g.setColor(new Color(255, 255, 255, 192));
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					
					// TODO: Paint notification string if no data has been loaded yet
//					if (!ResultsPanel.this.isBusy()) {
//						Graphics2D g2d = (Graphics2D) g;
//						String str = "no results loaded";
//						int strWidth = g2d.getFontMetrics().stringWidth(str);
//						int strHeight = g2d.getFontMetrics().getHeight();
//						float xOffset = this.getWidth() / 2.0f - strWidth / 2.0f;
//						float yOffset = this.getHeight() / 1.95f;
//						g2d.fillRect((int) xOffset - 2, (int) yOffset - g2d.getFontMetrics().getAscent() - 1, strWidth + 4, strHeight + 4);
//						
//						g2d.setColor(Color.BLACK);
//						g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//		                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//						g2d.drawString(str, xOffset, yOffset);
//					}
				}
				super.paintChildren(g);
			}
			
			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				chartHierarchyCbx.setEnabled(enabled);
				chartHideUnknownChk.setEnabled(enabled);
				chartGroupMinorPnl.setEnabled(enabled);
			}
		};
		
		chartPnl.setLayout(new FormLayout(
				"r:p:g, 2dlu, p, 2dlu, l:p:g",
				"0px:g, p, 2dlu"));
		chartPnl.setMinimumDrawHeight(144);
		chartPnl.setMaximumDrawHeight(1440);
		chartPnl.setMinimumDrawWidth(256);
		chartPnl.setMaximumDrawWidth(2560);
		chartPnl.setOpaque(false);
		chartPnl.setPreferredSize(new Dimension(256, 144));
		chartPnl.setMinimumSize(new Dimension(256, 144));
		
		// create mouse adapter to interact with plot sections
		MouseAdapter ma = new InteractionMouseAdapter(chartPnl);
		chartPnl.removeMouseListener(chartPnl.getMouseListeners()[1]);
		chartPnl.removeMouseMotionListener(chartPnl.getMouseMotionListeners()[1]);
		chartPnl.addMouseListener(ma);
		chartPnl.addMouseMotionListener(ma);
		
		// create combobox to control what counts to display in the plots (protein/peptide/spectrum count)
		chartHierarchyCbx = new JComboBox<HierarchyLevel>(HierarchyLevel.values());
		chartHierarchyCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					HierarchyLevel hl = (HierarchyLevel) evt.getItem();

					firePropertyChange("hierarchy", null, hl);
				}
			}
		});
		chartHierarchyCbx.setEnabled(false);

		chartHideUnknownChk = new JCheckBox("Hide Unknown", false);
		chartHideUnknownChk.setOpaque(false);
		chartHideUnknownChk.setEnabled(false);
		chartHideUnknownChk.addItemListener(new ItemListener() {
			/** Flag denoting whether the 'Unknown' category shall be hidden. */
			private boolean doHide;
			/** The value of the category to hide. */
			private Number hiddenVal;
			@Override
			public void itemStateChanged(ItemEvent evt) {
				this.doHide = (evt.getStateChange() == ItemEvent.SELECTED);
				firePropertyChange("hideUnknown", null, this.doHide);
				
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
						ScrollableChartPane.this.chartHideUnknownChk.setEnabled(false);
						Plot plot = chartPnl.getChart().getPlot();
						DefaultPieDataset dataset;
						if (plot instanceof PiePlot) {
							dataset = (DefaultPieDataset) ((PiePlot) plot).getDataset();
						} else if (plot instanceof PieToCategoryPlot) {
							dataset = (DefaultPieDataset) ((PieToCategoryDataset) ((PieToCategoryPlot) plot).getDataset()).getPieDataset();
						} else {
							// abort
							return null;
						}
						String unknownKey = "Unknown";
						if (doHide) {
							double val = dataset.getValue(unknownKey).doubleValue();
							for (int i = 0; i < 11; i++) {
								double tmp = 1.0 - i / 10.0;
								double newVal = val * tmp * tmp;
								dataset.setValue(unknownKey, newVal);
								if (newVal > 0.0) {
									Thread.sleep(33);
								} else {
									break;
								}
							}
							dataset.remove(unknownKey);
							// cache value
							hiddenVal = val;
						} else {
							double val = hiddenVal.doubleValue();
							dataset.insertValue(0, unknownKey, 0);
							for (int i = 0; i < 11; i++) {
								double tmp = i / 10.0;
								double newVal = val * tmp * tmp;
								if (newVal <= 0.0) {
									continue;
								}
								dataset.setValue(unknownKey, newVal);
								if (newVal < val) {
									Thread.sleep(33);
								} else {
									break;
								}
							}
						}
						return null;
					}
					@Override
					protected void done() {
						chartHideUnknownChk.setEnabled(true);
					};

				}.execute();
			}
		});

		final JCheckBox chartGroupMinorChk = new JCheckBox("Group segments <", true);
		chartGroupMinorChk.setOpaque(false);
		chartGroupMinorChk.setEnabled(false);
		
		final JSpinner chartGroupMinorSpn = new JSpinner(new SpinnerNumberModel(0.01, 0.0, 1.0, 0.001));
		chartGroupMinorSpn.setEditor(new JSpinner.NumberEditor(chartGroupMinorSpn, "0.0%"));
		final ChangeListener groupListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				Double value = (chartGroupMinorSpn.isEnabled()) ?
						(Double) chartGroupMinorSpn.getValue() : 0.0;
				firePropertyChange("groupingLimit", null, value);
			}
		};
		chartGroupMinorSpn.addChangeListener(groupListener);

		chartGroupMinorChk.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				chartGroupMinorSpn.setEnabled((evt.getStateChange() == ItemEvent.SELECTED));
				// delegate event handling to spinner
				groupListener.stateChanged(new ChangeEvent(chartGroupMinorChk));
			}
		});

		chartGroupMinorPnl = new JPanel(new FormLayout("p, 2dlu, 50px", "p")) {
			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
				chartGroupMinorChk.setEnabled(enabled);
				chartGroupMinorSpn.setEnabled(enabled);
			}
		};
		chartGroupMinorPnl.setOpaque(false);
		chartGroupMinorPnl.add(chartGroupMinorChk, CC.xy(1, 1));
		chartGroupMinorPnl.add(chartGroupMinorSpn, CC.xy(3, 1));
		
		JXBusyLabel busyLbl = new JXBusyLabel(new Dimension(70, 70));
		busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
		busyLbl.setVisible(false);
		
		chartPnl.add(busyLbl, CC.xywh(1, 1, 5, 3));
		chartPnl.add(chartHierarchyCbx, CC.xy(1, 2));
		chartPnl.add(chartHideUnknownChk, CC.xy(3, 2));
		chartPnl.add(chartGroupMinorPnl, CC.xy(5, 2));
		
		for (ChangeListener cl : viewport.getChangeListeners()) {
			viewport.removeChangeListener(cl);
		}
		viewport.setBackground(Color.WHITE);

		JScrollBar chartBar = this.getVerticalScrollBar();
		chartBar.setValues(0, 0, 0, 0);
		chartBar.setBlockIncrement(36);
		DefaultBoundedRangeModel chartBarMdl = (DefaultBoundedRangeModel) chartBar.getModel();
		ChangeListener[] cbcl = chartBarMdl.getChangeListeners();
		chartBarMdl.removeChangeListener(cbcl[0]);

		chartBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				JFreeChart chart = chartPnl.getChart();
				if (chart != null) {
					if (chart.getPlot() instanceof PiePlot) {
						chartPieAngle = evt.getValue();
						((PiePlot) chart.getPlot()).setStartAngle(chartPieAngle);
					}
				}
			}
		});
		
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * Returns the displayed chart.
	 * @return the chart
	 */
	public JFreeChart getChart() {
		return chartPnl.getChart();
	}
	
	/**
	 * Sets the chart to display.
	 * @param chart the chart
	 * @param showAdditionalControls <code>true</code> if additional controls
	 *  shall be displayed for this chart, <code>false</code> otherwise
	 */
	public void setChart(JFreeChart chart, boolean showAdditionalControls) {
		this.chartPnl.setChart(chart);
		JScrollBar chartBar = this.getVerticalScrollBar();

		final Plot plot = chart.getPlot();
		boolean isPie = plot instanceof PiePlot;
		if (isPie) {
			// enable chart scroll bar
			chartBar.getModel().setRangeProperties((int) chartPieAngle, 1, 0, 360, true);
			((PiePlot) plot).setStartAngle(chartPieAngle);
		} else {
			// disable chart scroll bar
			double temp = chartPieAngle;
			chartBar.setMaximum(0);
			chartPieAngle = temp;
		}
		// hide/show additional controls
		chartHierarchyCbx.setVisible(showAdditionalControls);
		chartHierarchyCbx.setEnabled(showAdditionalControls);
		chartHideUnknownChk.setVisible(showAdditionalControls);
		chartHideUnknownChk.setEnabled(showAdditionalControls);
		chartGroupMinorPnl.setVisible(showAdditionalControls);
		chartGroupMinorPnl.setEnabled(showAdditionalControls);
	}
	
	/**
	 * Returns the currently selected hierarchy level.
	 * @return either one of <code>HierarchyLevel.META_PROTEIN_LEVEL</code>,
	 *  <code>PROTEIN_LEVEL</code>, <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 */
	public HierarchyLevel getHierarchyLevel() {
		return (HierarchyLevel) this.chartHierarchyCbx.getSelectedItem();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.getVerticalScrollBar().setEnabled(enabled);
		this.chartPnl.setEnabled(enabled);
	}
	
	@Override
	public boolean isBusy() {
		return this.busy;
	}

	@Override
	public void setBusy(boolean busy) {
		this.busy = busy;
		JXBusyLabel busyLbl = (JXBusyLabel) this.chartPnl.getComponent(0);
		busyLbl.setBusy(busy);
		busyLbl.setVisible(busy);
		this.setEnabled(!busy);
	}

	/**
	 * Mouse handler for interacting with the chart view.
	 * @author A. Behne
	 */
	private class InteractionMouseAdapter extends MouseAdapter {
		
		/**
		 * The parent chart panel to interact with.
		 */
		private ChartPanel chartPnl;
		
		/**
		 * The currently highlighted section's identifier.
		 */
		@SuppressWarnings("rawtypes")
		private Comparable highlightedKey = null;
		
		/**
		 * The currently selected section's identifier.
		 */
		@SuppressWarnings("rawtypes")
		private Comparable selectedKey = null;
		
		/**
		 * The re-usable 'Save As...' action instance.
		 */
		private Action saveAsAction;
		
		/**
		 * 
		 * @param chartPnl
		 */
		public InteractionMouseAdapter(ChartPanel chartPnl) {
			this.chartPnl = chartPnl;
			this.saveAsAction = new SaveAsAction(chartPnl);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void mouseMoved(MouseEvent me) {
			Plot plot = this.getPlot();
			if (plot instanceof PiePlot3DExt) {
				PiePlot3DExt piePlot = (PiePlot3DExt) plot;
				Comparable key = piePlot.getSectionKeyForPoint(me.getPoint());
				if (key != highlightedKey) {
					for (Object dataKey : piePlot.getDataset().getKeys()) {
						if (dataKey != selectedKey) {
							piePlot.setExplodePercent((Comparable) dataKey,
									0.0);
						}
					}
					if ((key != null) && (key != selectedKey)) {
						piePlot.setExplodePercent(key, 0.1);
					}
					highlightedKey = key;
				}
			} else if (plot instanceof PieToCategoryPlot) {
				PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
				EntityCollection entities = this.chartPnl.getChartRenderingInfo().getEntityCollection();
				if (entities != null) {
	                Insets insets = this.chartPnl.getInsets();
	                ChartEntity entity = entities.getEntity(
	                        (int) ((me.getX() - insets.left) / this.chartPnl.getScaleX()),
	                        (int) ((me.getY() - insets.top) / this.chartPnl.getScaleY()));
	                if ((entity != null) && (entity instanceof CategoryItemEntity)) {
	                	CategoryItemEntity catEntity = (CategoryItemEntity) entity;
	                	Comparable key = catEntity.getColumnKey();
	                	if (key != highlightedKey) {
	                		catPlot.setHighlightedKey(key);
							highlightedKey = key;
	                	}
	                } else {
	                	catPlot.setHighlightedKey(null);
						highlightedKey = null;
	                }
	            }
			}
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			if (me.isPopupTrigger()) {
				maybeShowPopup(me);
			} else {
				Plot plot = this.getPlot();
				if (plot instanceof PiePlot3DExt) {
					PiePlot3DExt piePlot = (PiePlot3DExt) plot;
					if (selectedKey != null) {
						// clear old selection
						piePlot.setExplodePercent(selectedKey, 0.0);
					}
					if (highlightedKey != null) {
						piePlot.setExplodePercent(highlightedKey, 0.2);
						if (highlightedKey != selectedKey) {
//							// update table if new section got clicked
							firePropertyChange("selection", "", highlightedKey);
						}
					} else {
						firePropertyChange("selection", selectedKey, "");
					}
					selectedKey = highlightedKey;
				} else if (plot instanceof PieToCategoryPlot) {
					PieToCategoryPlot catPlot = (PieToCategoryPlot) plot;
					if (highlightedKey != null) {
						if (highlightedKey != selectedKey) {
//							// update table if new section got clicked
							firePropertyChange("selection", null, highlightedKey);
						}
					} else {
						firePropertyChange("selection", selectedKey, null);
					}
					catPlot.setSelectedKey(highlightedKey);
					selectedKey = highlightedKey;
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent me) {
			// re-validate fields in case chart type has been changed
			highlightedKey = null;
			selectedKey = null;
			Plot plot = this.getPlot();
			if (plot instanceof PiePlot3DExt) {
				PiePlot3DExt piePlot = (PiePlot3DExt) plot;
				for (Object dataKey : piePlot.getDataset().getKeys()) {
					@SuppressWarnings("rawtypes")
					Comparable key = (Comparable) dataKey;
					double explodePercent = piePlot.getExplodePercent(key);
					if (explodePercent > 0.1) {
						selectedKey = key;
					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent me) {
			maybeShowPopup(me);
		}

		@Override
		public void mouseReleased(MouseEvent me) {
			maybeShowPopup(me);
		}

		/**
		 * Convenience method to show a 'Save as CSV...' context menu.
		 * @param me the mouse event
		 */
		private void maybeShowPopup(MouseEvent me) {
			if (me.isPopupTrigger()) {
				Plot plot = this.getPlot();
				if (plot != null) {
					JPopupMenu popup = new JPopupMenu();

					JMenuItem item = new JMenuItem(saveAsAction);
					item.setText("Save as CSV...");
					item.setIcon(IconConstants.FILE_CSV);

					popup.add(item);

					popup.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		}

		/** 
		 * Utility method to get valid pie plot.
		 */
		private Plot getPlot() {
			if (this.chartPnl.isEnabled()) {
				JFreeChart chart = this.chartPnl.getChart();
				if (chart != null) {
					return chart.getPlot();
				}
			}
			return null;
		}
	}

	/**
	 * Action implementation for dumping a chart view to a *.csv file.
	 * @author A. Behne
	 */
	private class SaveAsAction extends AbstractAction {
		
		/**
		 * The parent chart panel.
		 */
		private ChartPanel chartPnl;

		/**
		 * Creates a 'Save As...' action for use with the provided parent chart panel.
		 * @param chartPnl the parent chart panel
		 */
		public SaveAsAction(ChartPanel chartPnl) {
			this.chartPnl = chartPnl;
		}
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			Plot plot = this.getPlot();
			if (plot != null) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(Constants.CSV_FILE_FILTER);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setMultiSelectionEnabled(false);
				int option = fc.showSaveDialog(ClientFrame.getInstance());

				if (option == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();

					if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
						selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
					}

//					this.chartPnl.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					setBusy(true);

					try {
						if (selectedFile.exists()) {
							selectedFile.delete();
						}
						selectedFile.createNewFile();

						BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));

						if (plot instanceof PiePlot) {
							PieDataset dataset = ((PiePlot) plot).getDataset();
							for (int i = 0; i < dataset.getItemCount(); i++) {
								@SuppressWarnings("rawtypes")
								Comparable key = dataset.getKey(i);
								Number value = dataset.getValue(key);
								writer.append("\"" + key + "\"\t" + value);
								writer.newLine();
							}
						} else if (plot instanceof PieToCategoryPlot) {
							CategoryDataset dataset = ((PieToCategoryPlot) plot).getDataset();
							for (int i = 0; i < dataset.getColumnCount(); i++) {
								@SuppressWarnings("rawtypes")
								Comparable key = dataset.getColumnKey(i);
								Number value = dataset.getValue(0, i);
								writer.append("\"" + key + "\"\t" + value);
								writer.newLine();
							}
						}
						writer.flush();
						writer.close();

					} catch (IOException ex) {
						JXErrorPane.showDialog(ClientFrame.getInstance(),
								new ErrorInfo("Severe Error", ex.getMessage(), null, null, ex, ErrorLevel.SEVERE, null));
					}

//					setCursor(null);
					setBusy(false);
				}
			}
		}

		/** 
		 * Utility method to get valid pie plot.
		 */
		private Plot getPlot() {
			if (this.chartPnl.isEnabled()) {
				JFreeChart chart = this.chartPnl.getChart();
				if (chart != null) {
					return chart.getPlot();
				}
			}
			return null;
		}
	}
	
}
