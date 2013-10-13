package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.table.ColumnControlButton;

import com.bric.swing.ColorPicker;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants.UIColor;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.BarChartPanel;

public class ColorsDialog extends JDialog {
	
	private static ColorsDialog instance;
	
	private ColorsDialog() {
		super();
		this.initComponents();
	}
	
	public static ColorsDialog getInstance() {
		if (instance == null) {
			instance = new ColorsDialog();
		}
		return instance;
	}

	private void initComponents() {
		
		this.setTitle("Color Settings");
		
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout(
				"6px, p, 2dlu, p:g, 2dlu",
				"2dlu, f:p:g, 3px, p, 2dlu"));
		
		// 
		JPanel previewPnl = new JPanel(new FormLayout("p", "8px, p, 6px, f:p:g, 2px"));
		
		// Init combo box for selecting UI colors
		final JComboBox previewCbx = new JComboBox(UIColor.values());
		previewCbx.setRenderer(new BasicComboBoxRenderer() {
			private ColorIcon icon = new ColorIcon();
			
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);
				// set icon
				this.icon.setColor(((UIColor) value).getColor());
				label.setIcon(this.icon);
				// adjust border for selected (editor) item
				label.setBorder(
						BorderFactory.createEmptyBorder((index == -1) ? 0 : 1, 1, 1, 1));
				return label;
			}
			
			/**
			 * Convenience class for rendering a color swatch icon.
			 * @author A. Behne
			 */
			class ColorIcon implements Icon {
				
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
		});
		previewCbx.setPreferredSize(new Dimension(previewCbx.getPreferredSize().width, 23));
		
		JPanel previewCompsPnl = new JPanel(new FormLayout("6px, p:g, 6px", "6px, p, 6px, f:p:g, 6px, p, 6px"));
		
		JXTable previewTbl = new JXTable(3, 1);
		previewTbl.setColumnControlVisible(true);
		previewTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		previewTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) previewTbl.getColumnControl()).setAdditionalActionsVisible(false);
		JScrollPane previewScpn = new JScrollPane(previewTbl);
		previewScpn.setPreferredSize(new Dimension());
		
		JProgressBar previewBar = new JProgressBar();
		previewBar.setValue(50);
		previewBar.setStringPainted(true);

		previewCompsPnl.add(new BarChartPanel(new JLabel("100"), new JLabel("50")), CC.xy(2, 2));
		previewCompsPnl.add(previewScpn, CC.xy(2, 4));
		previewCompsPnl.add(previewBar, CC.xy(2, 6));
		
		// Wrap preview components panel in titled panel
		JXTitledPanel previewTtlPnl = PanelConfig.createTitledPanel("Preview", previewCompsPnl);
		previewTtlPnl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 0, 1), PanelConfig.getTitleBorder()));

		previewPnl.add(previewCbx, CC.xy(1, 2));
		previewPnl.add(previewTtlPnl, CC.xy(1, 4));
		
		//
		JPanel pickerPnl = new JPanel(new FormLayout("204px:g, p", "-10px, c:186px"));
		pickerPnl.setBorder(BorderFactory.createTitledBorder("Color Picker"));

		// Init color picker, main component
		final ColorPicker picker = new ColorPicker(true, false);
		picker.setPreviewSwatchVisible(false);
		picker.setHexControlsVisible(false);
		picker.setColor(UIManager.getColor("titledPanel.startColor"));
		picker.setLocale(Locale.US);
		
		pickerPnl.add(picker, CC.xy(1, 2));
		pickerPnl.add(picker.getExpertControls(), CC.xy(2, 2));
		
		//
		JPanel buttonPnl = new JPanel(new FormLayout("p, 0px:g, p, 2dlu, p, 1px", "p"));
		((FormLayout) buttonPnl.getLayout()).setColumnGroups(new int[][] { { 1, 3, 5 } });

		JButton resetBtn = new JButton("Reset", IconConstants.UPDATE_ICON);
		resetBtn.setRolloverIcon(IconConstants.UPDATE_ROLLOVER_ICON);
		resetBtn.setPressedIcon(IconConstants.UPDATE_PRESSED_ICON);
		resetBtn.setHorizontalAlignment(SwingConstants.LEFT);
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);

		buttonPnl.add(resetBtn, CC.xy(1, 1));
		buttonPnl.add(okBtn, CC.xy(3, 1));
		buttonPnl.add(cancelBtn, CC.xy(5, 1));
		
		//
		contentPane.add(previewPnl, CC.xy(2, 2));
		contentPane.add(pickerPnl, CC.xy(4, 2));
		contentPane.add(buttonPnl, CC.xyw(2, 4, 3));
		
		// Register listeners
		previewCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				picker.setColor(((UIColor) evt.getItem()).getColor());
			}
		});
		
		picker.addPropertyChangeListener(ColorPicker.SELECTED_COLOR_PROPERTY,
				new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				UIManager.put(((UIColor) previewCbx.getSelectedItem()).getKey(),
						((ColorPicker) evt.getSource()).getColor());
				repaint();
//				((JComponent) getContentPane()).revalidate();
			}
		});
		
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				picker.setColor(((UIColor) previewCbx.getSelectedItem()).getColor());
			}
		});
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				picker.setColor(((UIColor) previewCbx.getSelectedItem()).getColor());
				dispose();
			}
		});
		
		//
		this.setModal(true);
		this.pack();
		this.setResizable(false);
		ScreenConfig.centerInComponent(this, ClientFrame.getInstance());
		
	}
	
	/**
	 * Resets all colors.
	 */
	private void reset() {
		for (UIColor uicolor : UIColor.values()) {
			uicolor.reset();
		}
		this.repaint();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ClientFrame.getInstance().repaint();
	}
	
}
