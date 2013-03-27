package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * The graph query dialog allows three modes:
 * a) Predefined queries
 * b) Composite queries
 * c) Console Cypher queries
 * @author Thilo Muth
 * @date 21/02/2013
 */
public class GraphQueryDialog extends JDialog {
	/**
	 * Graph query dialog
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 */
	public GraphQueryDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setupUI();
		initComponents();
	}
	
	/**
	 * Setup user interface.
	 */
	private void setupUI() {
		UIManager.put("TaskPane.titleForeground", PanelConfig.getTitleForeground());
		GradientPaint paint = (GradientPaint) (((MattePainter) PanelConfig.getTitlePainter()).getFillPaint());
		UIManager.put("TaskPane.titleBackgroundGradientStart", paint.getColor1());
		UIManager.put("TaskPane.titleBackgroundGradientEnd", paint.getColor2());
		UIManager.put("TaskPane.titleOver", paint.getColor2().darker());
		UIManager.put("TaskPane.borderColor", paint.getColor2());
	}

	/**
	 * Initializes and lays out all components inside this dialog.
	 */
	private void initComponents() {
		// Define dialog content pane layout
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, r:p:g, 5dlu",
				"5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Init task pane container
		final JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		tpc.setBackground(UIManager.getColor("ProgressBar.foreground"));
		
		// Create collapsible task pane for section
		JXTaskPane queryTaskPane = new JXTaskPane("Predefined Queries");
		queryTaskPane.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu "));
		queryTaskPane.setUI(new GlossyTaskPaneUI());
		
		// Apply component listener to synchronize task pane size with dialog size
		queryTaskPane.addComponentListener(new ComponentAdapter() {
			private Dimension size = null;
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension newSize = e.getComponent().getSize();
				if (size != null) {
					int delta = newSize.height - size.height;
					Dimension dialogSize = new Dimension(getSize());
					dialogSize.height += delta;
					setSize(dialogSize);
				}
				size = newSize;
			}
		});
		
		queryTaskPane.add(new JLabel("Query: "), CC.xy(2, 2));
		final JTextField queryTtf = new JTextField(30);
		queryTaskPane.add(queryTtf, CC.xy(2, 4));		
		
		String[] data = new String[6];
		
		data[0] = PredefinedQueries.GETALLSHAREDPEPTIDES;
		data[1] = PredefinedQueries.GETALLUNIQUEPEPTIDES;
		data[2] = PredefinedQueries.GETPEPTIDESFORPROTEIN;
		data[3] = PredefinedQueries.GETPROTEINSFORSPECIES;
		data[4] = PredefinedQueries.GETPEPTIDESFORSPECIES;
		data[5] = PredefinedQueries.GETPROTEINSFORENZYME;
		
		final JXList predefinedList = new JXList(data);
		predefinedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		predefinedList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(!evt.getValueIsAdjusting()) {
					queryTtf.setText(predefinedList.getSelectedValue().toString());
				}
			}
		});
		
		queryTaskPane.add(predefinedList, CC.xy(2, 6));
		
		JLabel paramLbl = new JLabel("Parameter X: ");
		queryTaskPane.add(paramLbl, CC.xy(4, 2));
		JTextField paramTtf = new JTextField(15);
		queryTaskPane.add(paramTtf, CC.xy(4, 4));
		tpc.add(queryTaskPane);
		
		// Create collapsible task pane for section
		/* COMPOUND QUERIES */
		JXTaskPane compoundQueryTaskPane = new JXTaskPane("Compound Queries");
		compoundQueryTaskPane.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		compoundQueryTaskPane.setUI(new GlossyTaskPaneUI());
		
		compoundQueryTaskPane.add(new JLabel("GET"), CC.xy(2, 2));
		JComboBox getEntititiesCbx = new JComboBox(Constants.QUERY_ENTITIES_GET);
		compoundQueryTaskPane.add(getEntititiesCbx, CC.xy(4, 2));
		
		compoundQueryTaskPane.add(new JLabel("BY"), CC.xy(6, 2));
		
		JComboBox byEntititiesCbx = new JComboBox(Constants.QUERY_ENTITIES_BY);
		compoundQueryTaskPane.add(byEntititiesCbx, CC.xy(8, 2));
				
		compoundQueryTaskPane.add(new JLabel("AND"), CC.xy(10, 2));
		
		JComboBox andEntititiesCbx = new JComboBox(Constants.QUERY_ENTITIES_BY);
		compoundQueryTaskPane.add(andEntititiesCbx, CC.xy(12, 2));
		
		JTextField byParameterTtf = new JTextField(15);
		compoundQueryTaskPane.add(byParameterTtf, CC.xy(8, 4));
		
		JTextField andEntititiesTtf = new JTextField(15);
		compoundQueryTaskPane.add(andEntititiesTtf, CC.xy(12, 4));
		
		// Create collapsible task pane for section
		JXTaskPane consoleQueryTaskPane = new JXTaskPane("Cypher Console");		
		queryTaskPane.setUI(new GlossyTaskPaneUI());
		tpc.add(compoundQueryTaskPane);
		tpc.add(consoleQueryTaskPane);
		
		// Configure button panel containing 'OK' and 'Cancel' options
		FormLayout layout = new FormLayout("p, 5dlu:g, p", "p");

		JPanel buttonPnl = new JPanel(layout);
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Configure 'Cancel' button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Lay out button panel
		buttonPnl.add(okBtn, CC.xy(1, 1));
		buttonPnl.add(cancelBtn, CC.xy(3, 1));
		
		// Add final components to content pane
		contentPane.add(tpc, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));
		
		// Configure size and position
		this.pack();
		Dimension size = this.getSize();
		this.setSize(new Dimension(size.width, size.height + 7));
		this.setResizable(false);
		ScreenConfig.centerInScreen(this);
		
		// Show dialog
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new GraphQueryDialog(null, "", true);
	}
	
	

}
