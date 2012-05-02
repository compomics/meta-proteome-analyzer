package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.LeftBelowPositioner;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class FilterBalloonTip extends BalloonTip {
	
	private BalloonTip innerBalloonTip;
	private JTextField filterTtf;
	private String filterString;
	private boolean confirmed = false;

	/**
	 * Constructor. TODO: API
	 * @param attachedComponent
	 * @param style
	 * @param orientation
	 * @param attachLocation
	 * @param horizontalOffset
	 * @param verticalOffset
	 * @param useCloseButton
	 */
	public FilterBalloonTip(JComponent attachedComponent, BalloonTipStyle style, Orientation orientation, AttachLocation attachLocation, int horizontalOffset, int verticalOffset, boolean useCloseButton) {
		super(attachedComponent, new JLabel(), style, orientation, attachLocation, horizontalOffset, verticalOffset, useCloseButton);
		this.setContents(createFilterPanel());
	}
	
	@Override
	public void closeBalloon() {
		innerBalloonTip.closeBalloon();
		super.closeBalloon();
//		setVisible(false);
	}

	/**
	 * Returns the filter text field's contents.
	 * @return The filter string.
	 */
	public String getFilterString() {
		return (filterString != null) ? filterString : filterTtf.getText();
	}
	
	/**
	 * Sets the filter text field's contents.
	 * @param filterString The filter string to be displayed.
	 */
	public void setFilterString(String filterString) {
		this.filterString = filterString;
		filterTtf.setText(filterString);
	}

	/**
	 * Returns the flag determining whether the confirmation button has been pressed.
	 * @return The confirmation flag.
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * Sets the confirmation flag.
	 * @param confirmed The confirmation flag.
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * constructing/setting pop-up button
	 */	
	private JPanel createFilterPanel() {

		JPanel panel = new JPanel(new FormLayout("2px, p:g, 2px, p, 2px, p, 2px", "2px, p, 2px"));

		filterTtf = new JTextField(20);
		int height = filterTtf.getPreferredSize().height;
		
		final JButton acceptBtn = new JButton(new ImageIcon(panel.getClass().getResource("/de/mpa/resources/icons/check16.png")));
		acceptBtn.setPreferredSize(new Dimension(height, height));
		acceptBtn.setOpaque(false);
		
		JButton cancelBtn = new JButton(new ImageIcon(panel.getClass().getResource("/de/mpa/resources/icons/cancel16.png")));
		cancelBtn.setPreferredSize(new Dimension(height, height));
		cancelBtn.setOpaque(false);

		panel.add(filterTtf, CC.xy(2, 2));
		panel.add(acceptBtn, CC.xy(4, 2));
		panel.add(cancelBtn, CC.xy(6, 2));

//		JLabel toolTipLbl = new JLabel("<html>Enter comma-separated patterns to exclude or include specific values in this column.<br>" +
//				"E.g. 'Escherichia, -aurescens' (without quotes) to include everything containing 'Escherichia', but not 'aurescens'.</html>",
		JLabel toolTipLbl = new JLabel("<html>Enter comma-separated text patterns to exclude or include<br>" +
				"specific values, e.g. 'Escherichia, -aurescens' (without quotes).</html>",
				new ImageIcon(panel.getClass().getResource("/de/mpa/resources/icons/bulb32.png")),
				SwingConstants.LEADING);
		innerBalloonTip = new BalloonTip(filterTtf, toolTipLbl, new RoundedBalloonStyle(6, 6, new Color(255, 255, 225), Color.BLACK), true);
		innerBalloonTip.setPositioner(new LeftBelowPositioner(15, 10));
		innerBalloonTip.setOpacity(0.95f);
		
		acceptBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// apply changes to text field
				filterString = filterTtf.getText();
				confirmed = true;
				closeBalloon();
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// revert text field contents to previous state
				filterTtf.setText(filterString);
				closeBalloon();
			}
		});

		//			final JPanel filterPanel = new JPanel();
		//			final JTextField textTtf = new JTextField(10);
		//			
		//			filterPanel.setLayout(new FormLayout("1dlu, p:g, 1dlu, p, 1dlu",
		//					"1dlu, p, 1dlu"));
		//			JButton acceptBtn = new JButton(new ImageIcon(filterPanel.getClass().getResource("/de/mpa/resources/icons/check16.png")));
		//			acceptBtn.setPreferredSize(new Dimension(21, 21));
		//			acceptBtn.setOpaque(false);
		//			BalloonTipStyle edgedLook = new EdgedBalloonStyle(Color.yellow, Color.BLACK);
		//
		//			filterPanel.add(textTtf, CC.xy(2, 2));
		//			filterPanel.add(acceptBtn, CC.xy(4,2));
		//			
		//			final JToggleButton button = new JToggleButton();
		//			button.setPreferredSize(new Dimension(15, 12));
		//			button.addActionListener(new ActionListener() {
		//				@Override
		//				public void actionPerformed(ActionEvent arg0) {
		//					if (button.isSelected()) {
		//						Rectangle rect = table.getTableHeader().getHeaderRect(
		//								table.convertColumnIndexToView(column));
		//						
		//							new BalloonTip(filterPanel, new JLabel("I'm a BalloonTip!"),
		//								new EdgedBalloonStyle(new Color(255,253,245), new Color(64,64,64)),
		//								new LeftBelowPositioner(15, 10), 
		//								null);
		//							textTtf.requestFocus();
		//					} else {
		////						popup.setVisible(false);
		//						table.requestFocus();
		//					}
		//				}
		//			});
		//			
		//			textTtf.addActionListener(new ActionListener() {
		//				public void actionPerformed(ActionEvent e) {
		////					popup.setVisible(false);
		//					table.requestFocus();
		//					button.setSelected(false);
		//					table.getTableHeader().repaint();
		//				}
		//			});
		//			
		//			textTtf.addFocusListener(new FocusAdapter() {
		//				public void focusLost(FocusEvent fe) {
		////					popup.setVisible(false);
		//					if (!button.getModel().isArmed()) {
		//						button.setSelected(false);
		//						table.requestFocus();
		//						table.getTableHeader().repaint();
		//					}
		//				};
		//			});
		return panel;
	}
}
