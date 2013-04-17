package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;
import org.neo4j.cypher.javacompat.ExecutionResult;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.GraphDatabaseResultPanel;
import de.mpa.graphdb.access.CypherQueryFactory;

/**
 * The graph query dialog allows three modes:
 * a) Predefined queries
 * b) Composite queries
 * c) Console Cypher queries
 * @author Thilo Muth
 * @date 21/02/2013
 */
public class GraphQueryDialog extends JDialog {
	private JXList predefinedList;
	private GraphDatabaseResultPanel parent;
	
	/**
	 * Graph query dialog
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 */
	public GraphQueryDialog(Frame owner, GraphDatabaseResultPanel parent, String title, boolean modal) {
		super(owner, title, modal);
		this.parent = parent;
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
		
		// Initialize task pane container
		final JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		tpc.setBackground(UIManager.getColor("ProgressBar.foreground"));
		
		// Predefined query section.
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
		
		predefinedList = new JXList(data);
		predefinedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		predefinedList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if(!evt.getValueIsAdjusting()) {
					queryTtf.setText(predefinedList.getSelectedValue().toString());
				}
			}
		});
		
		queryTaskPane.add(new JScrollPane(predefinedList), CC.xyw(2, 6, 3));
		
		JLabel paramLbl = new JLabel("Parameter X: ");
		queryTaskPane.add(paramLbl, CC.xy(4, 2));
		JTextField paramTtf = new JTextField(15);
		queryTaskPane.add(paramTtf, CC.xy(4, 4));
		tpc.add(queryTaskPane);
		
		// Compound query section.		
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
		
		// Console query section.
		JXTaskPane consoleQueryTaskPane = new JXTaskPane("Cypher Console");	
		consoleQueryTaskPane.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu"));
		consoleQueryTaskPane.setUI(new GlossyTaskPaneUI());
		
		JTextField consoleTtf = new JTextField(15);
		consoleQueryTaskPane.add(consoleTtf, CC.xy(2, 2));
		consoleTtf.setBackground(Color.BLACK);
		consoleTtf.setForeground(Color.GREEN.darker());
		consoleTtf.setCaretColor(Color.GREEN.darker());
		
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
				new ResultsTask().execute();
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
		
		okBtn.setPreferredSize(cancelBtn.getPreferredSize());
		
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
	
	/**
	 * Collect the query value.
	 * @return Query value for the graph database query.
	 */
	public String collectQueryValue() {
		return predefinedList.getSelectedValue().toString();
	}
	
	/**
	 * Class to fetch graph database search results in a background thread.
	 * @author Thilo Muth
	 */
	private class ResultsTask extends SwingWorker {
		private ExecutionResult result;
	
		@Override
		protected Object doInBackground() {
			try {
				// Begin appearing busy
				//TODO: setBusy(true);
				//String query = collectQueryValue();
				Client client = Client.getInstance();
//				CypherQuery cypherQuery = client.getGraphDatabaseHandler().getCypherQuery();
				//result = cypherQuery.getAllEnzymes();
//				result = cypherQuery.getAllProteins();
				result = CypherQueryFactory.getProteinsByPeptides(
						client.getGraphDatabaseHandler().getGraphDatabaseService());
				//resultObjects = QueryHandler.executePredefinedQuery(client.getGraphDatabaseHandler(), query, "");
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				//Client.getInstance().firePropertyChange("new message", null, "FAILED");
				//Client.getInstance().firePropertyChange("indeterminate", true, false);
			}
			return 0;
		}		
		
		/**
		 * Continues when the results retrieval has finished.
		 */
		public void done() {
			parent.updateResults(result);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			dispose();
		}
	}
}
