package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;
import org.jdesktop.swingx.table.TableColumnExt;

import com.bric.swing.ColorPicker;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.Constants.UIColor;
import de.mpa.client.Constants.UITheme;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.BoundsPopupMenuListener;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.ThinBevelBorder;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.BarChartPanel;
import de.mpa.main.Starter;

/**
 * Dialog implementation for viewing and manipulating various UI colors.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class ColorsDialog extends JDialog {
	
	/**
	 * The singleton instance of the color settings dialog.
	 */
	private static ColorsDialog instance;
	
	/**
	 * Cache of color states to be restored on abort.
	 */
	private Map<UIColor, Color> colorCache;

	/**
	 * Hidden constructor for singleton instance of color settings dialog.
	 */
	private ColorsDialog() {
		super();
		this.initComponents();
	}
	
	/**
	 * Returns the singleton instance of the color settings dialog.
	 * @return the color settings dialog instance
	 */
	public static ColorsDialog getInstance() {
		if (instance == null) {
			instance = new ColorsDialog();
		}
		return instance;
	}

	/**
	 * Initializes and lays out the color settings dialog's components.
	 */
	@SuppressWarnings("unchecked")
	private void initComponents() {
		
		// Apply layout to content pane
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout(
				"5dlu, p:g, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu, f:p:g, 5dlu, p, 5dlu"));
		((FormLayout) contentPane.getLayout()).setColumnGroups(new int[][] { { 2, 4 } });
		
		// Init combo box for selecting UI colors
		JPanel compPnl = new JPanel(new BorderLayout(8, 0));
		final JComboBox<UIColor> compCbx = new JComboBox<>(UIColor.values());
		compCbx.setRenderer(new ColorIconComboBoxRenderer() {
			@Override
			protected Color getColor(Object value) {
				return ((UIColor) value).getColor();
			}
		});
		compCbx.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
		compCbx.setPreferredSize(new Dimension(0, 23));

		compPnl.add(new JLabel("Component"), BorderLayout.WEST);
		compPnl.add(compCbx, BorderLayout.CENTER);
		
		// Init combo box for selecting color themes
		JPanel themePnl = new JPanel(new BorderLayout(8, 0));
		Vector<Object> items = new Vector<Object>(Constants.THEMES);
		items.add("<html><i>Add New Theme...</i></html>");
		final JComboBox<Object> themeCbx = new JComboBox<>(items);
		themeCbx.setRenderer(new ColorIconComboBoxRenderer() {
			@Override
			protected Color getColor(Object value) {
				if (value instanceof UITheme) {
					return ((UITheme) value).getPrototypeColor();
				} else {
					return new Color(0, 0, 0, 0);
				}
			}
		});
		themeCbx.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
		themeCbx.setPreferredSize(new Dimension(0, 23));
		themeCbx.setSelectedItem(UITheme.valueOf(Constants.DEFAULT_THEME_NAME));
		
		JButton saveThemeBtn = new JButton(IconConstants.SAVE_ICON) {
			@Override
			protected void paintBorder(Graphics g) {
				if (getModel().isRollover() ||
						getModel().isArmed()) {
					super.paintBorder(g);
				}
			}
		};
		saveThemeBtn.setRolloverIcon(IconConstants.SAVE_ICON);
		saveThemeBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		saveThemeBtn.setFocusPainted(false);
		saveThemeBtn.setContentAreaFilled(false);
		saveThemeBtn.setMargin(new Insets(1, 0, 0, 1));
		saveThemeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object item = themeCbx.getSelectedItem();
				if (item instanceof UITheme) {
					// apply current colors to theme
					UITheme theme = (UITheme) item;
					Map<UIColor, Color> colorMap = theme.getColorMap();
					for (UIColor uiColor : UIColor.values()) {
						colorMap.put(uiColor, new Color(uiColor.getColor().getRGB()));
					}
					// init save file dialog
					ConfirmFileChooser chooser = null;
					chooser = new ConfirmFileChooser(Constants.THEME_FOLDER_JAR);

					// provide suggestion for filename (lower case theme title without whitespace)
					chooser.setSelectedFile(new File(Constants.THEME_FOLDER_JAR 
							+ theme.getTitle().toLowerCase().replaceAll("\\s+","") 
							+ ".theme"));
					int res = chooser.showSaveDialog(ColorsDialog.this);
					if (res == JFileChooser.APPROVE_OPTION) {
						// write theme configuration file
						File file = chooser.getSelectedFile();
						theme.writeToFile(file);
					}
					ColorsDialog.this.repaint();
				}
			}
		});

		themePnl.add(new JLabel("Theme"), BorderLayout.WEST);
		themePnl.add(themeCbx, BorderLayout.CENTER);
		themePnl.add(saveThemeBtn, BorderLayout.EAST);
		
		/* Configure left-hand preview panel containing colorizable components */
		JPanel previewPnl = new JPanel(new FormLayout(
				"5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, f:p:g, 5dlu, p, 5dlu"));

		// Init example bar chart panel
		BarChartPanel barChartPnl = new BarChartPanel(new JLabel("100"), new JLabel("50"));
		
		// Init example tables containing bar chart highlighters
		DefaultTableModel model;
		
		// Horizontal highlighters
		model = new DefaultTableModel(0, 3);
		JXTable previewHorzTbl = new JXTable(model);
		TableConfig.configureColumnControl(previewHorzTbl);
		previewHorzTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Add rows and highlighters
		model.addRow(new Object[] { 10, 10, 10 });
		model.addRow(new Object[] { 50, 50, 50 });
		model.addRow(new Object[] { 100, 100, 100 });
		model.addRow(new Object[0]);

		Color[] startColors;
		Color[] endColors;
		
		FontMetrics fm = getFontMetrics(UIManager.getFont("Label.font"));
		int baseline = 1 + fm.stringWidth("100");

		startColors = new Color[] {
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_A_START_COLOR.getDelegateColor(),
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_B_START_COLOR.getDelegateColor(),
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_C_START_COLOR.getDelegateColor() };
		endColors = new Color[] {
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_A_END_COLOR.getDelegateColor(),
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_B_END_COLOR.getDelegateColor(),
				UIColor.HORZ_BAR_CHART_HIGHLIGHTER_C_END_COLOR.getDelegateColor() };
		
		for (int i = 0; i < startColors.length; i++) {
			BarChartHighlighter hl = new BarChartHighlighter(0, 100, baseline,
					SwingConstants.HORIZONTAL, startColors[i], endColors[i]);
			((TableColumnExt) previewHorzTbl.getColumnModel().getColumn(i)).addHighlighter(hl);
		}
		
		// Wrap table in scrollpane
		JScrollPane previewHorzScpn = new JScrollPane(previewHorzTbl);
		previewHorzScpn.setPreferredSize(new Dimension(0, 75));
		
		// Vertical highlighters
		model = new DefaultTableModel(0, 5);
		JXTable previewVertTbl = new JXTable(model);
		TableConfig.configureColumnControl(previewVertTbl);
		previewVertTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Add rows and highlighters
		model.addRow(new Object[] { 50, 50, 50, 50, 50 });
		model.addRow(new Object[] { 75, 75, 75, 75, 75 });
		model.addRow(new Object[] { 100, 100, 100, 100, 100 });
		model.addRow(new Object[0]);

		startColors = new Color[] {
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_A_START_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_B_START_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_C_START_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_D_START_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_E_START_COLOR.getDelegateColor() };
		endColors = new Color[] {
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_A_END_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_B_END_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_C_END_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_D_END_COLOR.getDelegateColor(),
				UIColor.VERT_BAR_CHART_HIGHLIGHTER_E_END_COLOR.getDelegateColor() };
		
		for (int i = 0; i < startColors.length; i++) {
			BarChartHighlighter hl = new BarChartHighlighter(0, 100, 0,
					SwingConstants.VERTICAL, startColors[i], endColors[i]);
			((TableColumnExt) previewVertTbl.getColumnModel().getColumn(i)).addHighlighter(hl);
		}
		
		// Wrap table in scrollpane
		JScrollPane previewVertScpn = new JScrollPane(previewVertTbl);
		previewVertScpn.setPreferredSize(new Dimension(0, 75));
		
		JXTaskPaneContainer previewTpc = new JXTaskPaneContainer();
		previewTpc.setBorder(BorderFactory.createCompoundBorder(
				new ThinBevelBorder(BevelBorder.LOWERED),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		((VerticalLayout) previewTpc.getLayout()).setGap(0);
		
		JXTaskPane previewTaskPane = new JXTaskPane("Preview");
		previewTaskPane.setUI(new GlossyTaskPaneUI());
		
		previewTpc.add(previewTaskPane);
		
		// Init example progress bar
		JProgressBar previewBar = new JProgressBar();
		previewBar.setValue(50);
		previewBar.setStringPainted(true);
		previewBar.setBorder(new ThinBevelBorder(BevelBorder.LOWERED));
		
		previewPnl.add(barChartPnl, CC.xy(2, 2));
		previewPnl.add(previewHorzScpn, CC.xy(2, 4));
		previewPnl.add(previewVertScpn, CC.xy(2, 6));
		previewPnl.add(previewTpc, CC.xy(2, 8));
		previewPnl.add(previewBar, CC.xy(2, 10));
		
		// Wrap preview components panel in titled panel
		JXTitledPanel previewTtlPnl = PanelConfig.createTitledPanel("Preview", previewPnl);
		
		/* Configure right-hand color picker panel */
		JPanel pickerPnl = new JPanel(new FormLayout(
				"206px, p:g",
				"0dlu, f:206px:g, 6px, p"));

		// Init color picker widget
		final ColorPicker picker = new ColorPicker(true, false);
		picker.setPreviewSwatchVisible(false);
		picker.setHexControlsVisible(false);
		picker.setColor(UIManager.getColor("titledPanel.startColor"));
		picker.setLocale(Locale.US);
		
		// Pick apart picker widget, re-locate color slider and expert controls 
		JSlider colorSlider = (JSlider) picker.getComponent(1);
		colorSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
		colorSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				((Component) e.getSource()).requestFocus();
			}
		});
		
		JPanel expertPnl = new JPanel(new FormLayout(
				"5dlu, r:p, 5dlu, p, 5dlu:g, r:p, 5dlu, p, 5dlu",
				"p, 3dlu, p, 3dlu, p, 5dlu"));

		JPanel expertControls = picker.getExpertControls();
		expertControls.getParent().remove(expertControls);
		
		Container expCont = (Container) expertControls.getComponent(1);
		Component[] comps = expCont.getComponents();
		String[] labels = { "H", "S", "V", "R", "G", "B" };
		for (int i = 0; i < 18; i++) {
			Component comp = comps[i];
			if ((i % 3) == 1) {
				// spinner
				expertPnl.add(comp, CC.xy(4 + 4 * (i / 9), 1 + 2 * ((i % 9) / 3)));
			} else if ((i % 3) == 2) {
				// radio button
				expertPnl.add(comp, CC.xy(2 + 4 * (i / 9), 1 + 2 * ((i % 9) / 3)));
				AbstractButton button = (AbstractButton) comp;
				button.setText(labels[i / 3]);
				button.setHorizontalTextPosition(SwingConstants.LEFT);
			}
		}
		
		pickerPnl.add(picker, CC.xy(1, 2));
		pickerPnl.add(colorSlider, CC.xy(2, 2));
		pickerPnl.add(expertPnl, CC.xyw(1, 4, 2));
		
		JXTitledPanel pickerTtlPnl = PanelConfig.createTitledPanel("Color Picker", pickerPnl);
		
		/* Configure bottom button panel */
		JPanel buttonPnl = new JPanel(new FormLayout("p, 5dlu:g, p, 5dlu, p, 1px", "p"));
		((FormLayout) buttonPnl.getLayout()).setColumnGroups(new int[][] { { 3, 5 } });

		// Create button to reset UI colors to their default values
		JButton resetBtn = new JButton("Restore Defaults", IconConstants.UPDATE_ICON);
		resetBtn.setRolloverIcon(IconConstants.UPDATE_ROLLOVER_ICON);
		resetBtn.setPressedIcon(IconConstants.UPDATE_PRESSED_ICON);
		resetBtn.setHorizontalAlignment(SwingConstants.LEFT);
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorsDialog.this.reset(true);
				picker.setColor(((UIColor) compCbx.getSelectedItem()).getColor());
			}
		});
		
		// Create button to accept changes and close the dialog
		JButton okBtn = new JButton(" OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorsDialog.this.dispose();
			}
		});
		
		// Create button to dismiss changes and close the dialog
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorsDialog.this.reset(false);
				picker.setColor(((UIColor) compCbx.getSelectedItem()).getColor());
				ColorsDialog.this.dispose();
			}
		});

		buttonPnl.add(resetBtn, CC.xy(1, 1));
		buttonPnl.add(okBtn, CC.xy(3, 1));
		buttonPnl.add(cancelBtn, CC.xy(5, 1));
		
		// Put everything into content pane
		contentPane.add(compPnl, CC.xy(2, 2));
		contentPane.add(themePnl, CC.xy(4, 2));
		contentPane.add(previewTtlPnl, CC.xy(2, 4));
		contentPane.add(pickerTtlPnl, CC.xy(4, 4));
		contentPane.add(buttonPnl, CC.xyw(2, 6, 3));
		
		/* Register listeners */
		// synchronize combo box and color picker selections
		compCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				picker.setColor(((UIColor) evt.getItem()).getColor());
			}
		});
		picker.addPropertyChangeListener(ColorPicker.SELECTED_COLOR_PROPERTY,
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				UIManager.put(((UIColor) compCbx.getSelectedItem()).getKey(),
						((ColorPicker) evt.getSource()).getColor());
				ColorsDialog.this.repaint();
			}
		});

		// configure theme combo box behavior
		themeCbx.addItemListener(new ItemListener() {
			/** Reference to the last selected object */
			private UITheme lastSelected;

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object item = e.getItem();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// new object item has been selected
					if (item instanceof UITheme) {
						// a theme has been selected
						((UITheme) item).applyTheme();
						picker.setColor(((UIColor) compCbx.getSelectedItem()).getColor());
						ColorsDialog.this.repaint();
					} else {
						// the 'Add new theme...' entry has been selected
						@SuppressWarnings("rawtypes")
						JComboBox comboBox = (JComboBox) e.getSource();
						if (!comboBox.isEditable()) {
							// attach self-destroying action listener
							comboBox.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if ("comboBoxEdited".equals(e.getActionCommand())) {
										JComboBox<UITheme> comboBox = (JComboBox<UITheme>) e.getSource();

										String title = (String) comboBox.getSelectedItem();
										// create deep copy of last selected theme's color map
										Map<UIColor, Color> newColorMap = 
												new HashMap<Constants.UIColor, Color>();
										for (Entry<UIColor, Color> entry : 
											lastSelected.getColorMap().entrySet()) {
											newColorMap.put(entry.getKey(),
													new Color(entry.getValue().getRGB()));
										}
										// create new theme based on last selected theme
										UITheme newTheme = new UITheme(title, null,
												newColorMap);
										// insert new theme into list
										((DefaultComboBoxModel<UITheme>) comboBox.getModel()).insertElementAt(
												newTheme, comboBox.getItemCount() - 1);
										comboBox.setSelectedItem(newTheme);

										comboBox.setEditable(false);
										comboBox.removeActionListener(this);
									}
								}
							});
							comboBox.setEditable(true);
							comboBox.setSelectedItem("Please enter a name");
						}
					}
				} else {
					// an item has been de-selected, cache it
					if (item instanceof UITheme) {
						this.lastSelected = (UITheme) item;
					}
				}
			}
		});
		
		// override window closing operation
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// same as cancel button
				ColorsDialog.this.reset(false);
				picker.setColor(((UIColor) compCbx.getSelectedItem()).getColor());
				ColorsDialog.this.dispose();
			}
		});
		
		// Configure dialog properties and position
		this.setIconImage(IconConstants.COLOR_SETTINGS_ICON.getImage());
		this.setTitle("Color Settings");
		this.setModal(true);
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		ScreenConfig.centerInComponent(this, ClientFrame.getInstance());
		
	}
	
	/**
	 * Resets all colors.
	 */
	private void reset(boolean toDefault) {
		if (toDefault) {
			// restore default colors
			for (UIColor uicolor : UIColor.values()) {
				uicolor.reset();
			}
		} else {
			// restore cached colors
			for (UIColor uicolor : UIColor.values()) {
				uicolor.setColor(this.colorCache.get(uicolor));
			}
		}
		// force re-paint on dialog
		this.repaint();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		// clear cache
		this.colorCache = null;
		// force re-paint on client frame
		ClientFrame.getInstance().repaint();
//		((JComponent) ClientFrame.getInstance().getContentPane()).revalidate();
	}
	
	@Override
	public void setVisible(boolean b) {
		// cache colors
		this.colorCache = new HashMap<UIColor, Color>();
		for (UIColor uicolor : UIColor.values()) {
			colorCache.put(uicolor, uicolor.getColor());
		}
		// TODO: maybe re-center dialog?
		super.setVisible(b);
	}
	
	/**
	 * Convenience class allowing to display a color swatch icon next to a label
	 * in combo boxes.
	 * @author A. Behne
	 */
	protected abstract class ColorIconComboBoxRenderer extends BasicComboBoxRenderer {
		
		/** Color swatch icon */
		private ColorIcon icon = new ColorIcon();
		
		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
			// set icon
			this.icon.setColor(this.getColor(value));
			label.setIcon(this.icon);
			// adjust border for selected (editor) item
			label.setBorder(
					BorderFactory.createEmptyBorder((index == -1) ? 0 : 1, 1, 1, 1));
			return label;
		}
		
		/**
		 * Abstract method to return a color based on the item value.
		 * @param value the item value
		 * @return a color
		 */
		protected abstract Color getColor(Object value);
		
		/**
		 * Convenience class for rendering a color swatch icon.
		 * @author A. Behne
		 */
		private class ColorIcon implements Icon {
			
			private Color color;
			private Insets insets = new Insets(3, 2, 0, 0);

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(this.color);
				g.fillRect(this.insets.left, this.insets.top,
						this.getIconWidth() - this.insets.right,
						this.getIconHeight() - this.insets.bottom);
				g.setColor(Color.BLACK);
				g.drawRect(this.insets.left, this.insets.top,
						this.getIconWidth() - this.insets.right,
						this.getIconHeight() - this.insets.bottom);
			}

			public int getIconWidth() { return 12; }
			public int getIconHeight() { return 12; }

			public void setColor(Color color) {
				this.color = color;
			}
			
		}
	}
	
}
