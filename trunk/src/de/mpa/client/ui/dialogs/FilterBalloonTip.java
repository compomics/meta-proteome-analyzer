package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.CustomBalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.positioners.LeftBelowPositioner;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;

import org.jdesktop.swingx.JXErrorPane;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.icons.IconConstants;

public class FilterBalloonTip extends CustomBalloonTip {
	
	private JTextField filterTtf;
	private String filterString;
	private boolean confirmed = false;

	/**
	 * Constructs a custom balloon tip attached to
	 * <code>attachedComponent</code> using a specified <code>offset</code>
	 * containing several components related to column filtering.
	 * 
	 * @param attachedComponent
	 *            The custom component to attach the balloon tip to (may not be
	 *            null).
	 * @param offset
	 *            Specifies a rectangle within the attached component; the
	 *            balloon tip will attach to this rectangle. (may not be null)
	 *            Do note that the coordinates should be relative to the
	 *            attached component's top left corner.
	 */
	public FilterBalloonTip(JComponent attachedComponent, Rectangle offset) {
		super(attachedComponent, new JLabel(), offset, new EdgedBalloonStyle(
				UIManager.getColor("Panel.background"), new Color(64, 64, 64)),
				Orientation.LEFT_BELOW, AttachLocation.WEST, 125, 10, false);
		try {
			setContents(createFilterPanel());
		} catch (IOException e) {
			JXErrorPane.showDialog(e);
		}
	}

	@Override
	public void requestFocus() {
		filterTtf.requestFocus();
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
	 * @throws IOException 
	 */	
	private JComponent createFilterPanel() throws IOException {

		JPanel panel = new JPanel(new FormLayout("2px, l:m, 4px, p:g, 2px, p, 2px, p, 2px", "2px, p, 2px"));
		panel.setOpaque(false);
		
		JButton helpBtn = new JButton(IconConstants.HELP_ICON);
		helpBtn.setRolloverIcon(IconConstants.HELP_ROLLOVER_ICON);
		helpBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
		helpBtn.setFocusPainted(false);
		helpBtn.setBorderPainted(false);
		helpBtn.setContentAreaFilled(false);
		helpBtn.setFocusable(false);

		JLabel toolTipLbl = new JLabel("<html><p style=\"text-align:justify;width:187px\">" +
				"Enter comma-separated text patterns to exclude  or include specific values, " +
				"e.g. <font color=#009900>>0.52, &lt=11.4</font> for numerical data " +
				"or <font color=#009900>Escherichia, -aurescens</font> for text.</p></html>",
				new ImageIcon(panel.getClass().getResource("/de/mpa/resources/icons/bulb32.png")),
				SwingConstants.LEADING);
		final BalloonTip innerBalloonTip = new BalloonTip(helpBtn, toolTipLbl, new RoundedBalloonStyle(6, 6, new Color(255, 255, 225), Color.BLACK), false);
		BalloonTipPositioner positioner = new LeftBelowPositioner(15, 10);
		innerBalloonTip.setPositioner(positioner);
//		innerBalloonTip.setOpacity(0.95f);
		innerBalloonTip.setVisible(false);
		
		helpBtn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				innerBalloonTip.setVisible(true);
			}
			public void mouseExited(MouseEvent me) {
				innerBalloonTip.setVisible(false);
			}
		});

		filterTtf = new JTextField(20);
		filterTtf.setPreferredSize(new Dimension(filterTtf.getPreferredSize().width, 20));
		
		final JButton acceptBtn = new JButton(IconConstants.CHECK_ICON);
		acceptBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		acceptBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		acceptBtn.setPreferredSize(new Dimension(20, 20));
		acceptBtn.setOpaque(false);
		acceptBtn.setMargin(new Insets(0, 0, 0, 1));
		final DefaultButtonModel dbma = (DefaultButtonModel) acceptBtn.getModel();
		
		JButton cancelBtn = new JButton(IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setPreferredSize(new Dimension(20, 20));
		cancelBtn.setOpaque(false);
		cancelBtn.setMargin(new Insets(1, 0, 0, 1));
		final DefaultButtonModel dbmc = (DefaultButtonModel) cancelBtn.getModel();

		panel.add(helpBtn, CC.xy(2, 2));
		panel.add(filterTtf, CC.xy(4, 2));
		panel.add(acceptBtn, CC.xy(6, 2));
		panel.add(cancelBtn, CC.xy(8, 2));
		
		filterTtf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				// make appropriate buttons appear pressed
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					dbma.setPressed(true);
					dbma.setArmed(true);
				} else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dbmc.setPressed(true);
					dbmc.setArmed(true);
				}
			}
			public void keyReleased(KeyEvent ke) {
				// make appropriate buttons fire a pressed event
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					dbma.setPressed(false);
				} else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dbmc.setPressed(false);
				}
			}
		});
		
		acceptBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// apply changes to text field
				filterString = filterTtf.getText();
				confirmed = true;
				setVisible(false);
			}
		});

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// revert text field contents to previous state
				filterTtf.setText(filterString);
				setVisible(false);
			}
		});

		return panel;
	}
}
