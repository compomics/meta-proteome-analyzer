package de.mpa.client.ui.resultspanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXBusyLabel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.sharedelements.Busyable;
import de.mpa.model.algorithms.Interval;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.ProteinHit;

/**
 * A component for displaying the sequence coverage of proteins.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class CoverageViewerPane extends JScrollPane implements Busyable {
	
	/**
	 * The pane's background panel
	 */
	private JPanel backgroundPnl;
	
	/**
	 * The busy label.
	 */
	private JXBusyLabel busyLbl;
	
	/**
	 * The update worker instance.
	 */
	private SwingWorker<Object, Object> updateWorker;

	/**
	 * The cache of selectable peptide sequence labels.
	 */
	private Map<String, CoverageViewerPane.HoverLabel> hoverLabels;

//	/**
//	 * The button group linking together peptide sequence labels.
//	 */
//	private ButtonGroup hoverGroup;

	/**
	 * The sequence string of the currently selected peptide.
	 */
	private String selSequence;

	/**
	 * Creates a sequence coverage viewer panel.
	 */
	public CoverageViewerPane() {
		super();

		hoverLabels = new HashMap<String, CoverageViewerPane.HoverLabel>();

		this.initComponents();
	}

	/**
	 * Creates and lays out the panel's components.
	 */
	private void initComponents() {
		this.setPreferredSize(new Dimension(350, 100));
		this.getVerticalScrollBar().setUnitIncrement(16);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.getHorizontalScrollBar().setUnitIncrement(16);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		backgroundPnl = new JPanel(new BorderLayout());
		backgroundPnl.setBackground(Color.WHITE);
		backgroundPnl.setOpaque(false);

		// create custom viewport for painting the busy label as static overlay
		final JViewport viewport = new JViewport() {
			/** Dummy container for painting purposes */
			private JPanel dummyPnl = new JPanel();
			/** The text to display on top of the table when it's empty */
			private final String emptyStr = "no protein(s) selected";
			/** The cached width of the empty table text */
			private int emptyStrW = 0;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				// paint
				if (getRowCount() == 0) {
					if (emptyStrW == 0) {
						// cache string width
						emptyStrW = g.getFontMetrics().stringWidth(emptyStr);
					}
					// enable text anti-aliasing
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					// draw string on top of empty table
					g.drawString(emptyStr, (this.getWidth() - emptyStrW) / 2, this.getHeight() / 2);
				}
				// paint busy label on top
				if (isBusy()) {
					SwingUtilities.paintComponent(g, busyLbl, dummyPnl, this.getVisibleRect());
				}
			}
		};
		viewport.setBackground(Color.WHITE);

		busyLbl = new JXBusyLabel(new Dimension(100, 100)) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(255, 255, 255, 192));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
			@Override
			public void repaint() {
				viewport.repaint();
			}
		};
		busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
		busyLbl.setOpaque(false);

		this.setViewport(viewport);
		this.setViewportView(backgroundPnl);

		this.clear();
	}

	/**
	 * Updates the coverage viewer by mapping out the peptide sequences inside the provided proteins.
	 * @param proteins the proteins to be mapped out
	 * @param peptides the peptides to map onto the proteins
	 */
	public void setData(final Collection<ProteinHit> proteins, final Collection<PeptideHit> peptides) {

		// cancel existing update process if one is in progress
		if (updateWorker != null) {
			updateWorker.cancel(true);
		}

		// process data in background thread
		updateWorker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// start appearing busy
				setBusy(true);

				// clear panel
				hoverLabels.clear();
//				hoverGroup = new ButtonGroup();
				backgroundPnl.removeAll();

				try {

					Border headerBorder = UIManager.getBorder("TableHeader.cellBorder");
					JLabel headerFillerLbl = new JLabel();
					headerFillerLbl.setPreferredSize(
							new Dimension(getRowHeader().getView().getPreferredSize().width, 0));
					headerFillerLbl.setBorder(headerBorder);
					setRowHeaderView(headerFillerLbl);

					// create coverage mapping
					Map<ProteinHit, List<Interval>> coverageMap = createCoverageMapping(proteins, peptides);
					// create color map
					Map<String, Color> colorMap = createColorMapping(peptides);

					// create row header panel containing protein accessions
					PanelBuilder headerBuilder = new PanelBuilder(new FormLayout("p:g"));
					// create panel wrapping all protein-specific coverage panels
					PanelBuilder coverageBuilder = new PanelBuilder(new FormLayout("l:p"));
					coverageBuilder.setOpaque(false);

					int i = 1;
					for (Entry<ProteinHit, List<Interval>> entry : coverageMap.entrySet()) {
						ProteinHit protein = entry.getKey();

						Component covPnl = createCoveragePanel(protein, entry.getValue(), colorMap);

						JLabel headerLbl = new JLabel(protein.getAccession());
						Insets insets = headerBorder.getBorderInsets(headerLbl);
						headerLbl.setBorder(BorderFactory.createCompoundBorder(headerBorder, BorderFactory.createEmptyBorder(
								covPnl.getPreferredSize().height - headerLbl.getPreferredSize().height - 5 - insets.top,
								5, 5 - insets.bottom, 5)));

						if (this.isCancelled()) {
							return null;
						}

						headerBuilder.appendRow("p");
						headerBuilder.add(headerLbl, CC.xy(1, i));

						coverageBuilder.appendRow("p");
						coverageBuilder.add(covPnl, CC.xy(1, i));

						coverageBuilder.getPanel().revalidate();
						if (i == 1) {
							setRowHeaderView(headerBuilder.getPanel());
							backgroundPnl.add(coverageBuilder.getPanel(), BorderLayout.CENTER);
						}
						i++;
					}
					if (this.isCancelled()) {
						return null;
					}

					// add final filler objects
					headerBuilder.appendRow("f:0px:g");
					headerBuilder.add(headerFillerLbl, CC.xy(1, i));

					backgroundPnl.revalidate();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void done() {
				setBusy(false);
			}
		};
		updateWorker.execute();
	}

	/**
	 * Helper method to map peptide sequence occurrences inside protein sequences as index intervals.
	 * @param proteins the proteins to be mapped out
	 * @param peptides the peptides to map onto the proteins
	 * @return the coverage mapping
	 */
	private Map<ProteinHit, List<Interval>> createCoverageMapping(
			Collection<ProteinHit> proteins, Collection<PeptideHit> peptides) {
		Map<ProteinHit, List<Interval>> coverageMap = new LinkedHashMap<ProteinHit, List<Interval>>();

		for (ProteinHit protein : proteins) {
			List<Interval> intervals = new ArrayList<Interval>(peptides.size());

			String protSeq = protein.getSequence();

			if (protSeq !=null) {
//				int row = 0;
				for (PeptideHit peptide : peptides) {
					// find all occurences of peptide sequences in protein sequence
					String pepSeq = peptide.getSequence();
					int index = -1;
					while (true) {
						index = protSeq.indexOf(pepSeq, index + 1);
						if (index == -1) break;
						// store position of peptide sequence match in interval object
						Interval interval = new Interval(index, index + pepSeq.length(), pepSeq);
						intervals.add(interval);
					}
				}

				coverageMap.put(protein, intervals);
			}else{
				coverageMap.put(protein, null);
			}
		}

		return coverageMap;
	}

	/**
	 * Creates a map of peptide sequence-to-color pairs.
	 * @param peptides the peptides
	 * @return the color map
	 */
	private Map<String, Color> createColorMapping(Collection<PeptideHit> peptides) {
//		DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier();
		Map<String, Color> colorMap = new HashMap<String, Color>();
		for (PeptideHit peptide : peptides) {
//			colorMap.put(peptide.getSequence(), ((Color) drawingSupplier.getNextPaint()).darker());
			colorMap.put(peptide.getSequence(), Color.RED);
		}
		return colorMap;
	}

	/**
	 * Helper method to Create a protein coverage panel for a single protein hit.
	 * @param proteinHit the protein hit
	 * @param peptideIntervals the list of peptide intervals
	 * @return the protein coverage panel
	 */
	private JPanel createCoveragePanel(ProteinHit proteinHit, List<Interval> peptideIntervals,
			Map<String, Color> colorMap) {
		// Init return value
		JPanel covPnl = new JPanel();
		covPnl.setOpaque(false);

		String protSeq = proteinHit.getSequence();

		if (protSeq != null) {
			// Iterate protein sequence blocks
			int blockSize = 10;
			int length = protSeq.length();
			int openIntervals = 0;
			String lastOpened = null;
			for (int start = 0; start < length; start += blockSize) {
				int end = start + blockSize;
				end = (end > length) ? length : end;
				// Create upper label containing position index on light green background
				StringBuilder indexRow = new StringBuilder("<html><code>");
				int spaces = blockSize - 2 - (int) Math.floor(Math.log10(end));
				for (int i = 0; i < spaces; i++) {
					indexRow.append("&nbsp");
				}
				indexRow.append(" " + (start + blockSize));
				indexRow.append("</html></code>");

				JPanel blockPnl = new JPanel(new BorderLayout());
				blockPnl.setOpaque(false);
				JLabel indexLbl = new JLabel(indexRow.toString());
				indexLbl.setBackground(new Color(0, 255, 0, 32));
				indexLbl.setOpaque(true);
				blockPnl.add(indexLbl, BorderLayout.NORTH);

				// Create lower panel containing label-like buttons and plain labels
				JPanel subBlockPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				subBlockPnl.setOpaque(false);

				JComponent label = null;

				// Iterate characters inside block to find upper/lower interval boundaries
				String blockSeq = protSeq.substring(start, end);
				int blockLength = blockSeq.length();
				int subIndex = 0;
				for (int i = 0; i < blockLength; i++) {
					int pos = start + i;
					boolean isBorder = false;
					// Compare all intervals' bounds with current absolute character position
					for (Interval interval : peptideIntervals) {
						// check position for left border
						if ((pos == (int) interval.getLeftBorder())) {
							// Highlightable part begins here, store contents up to this point in label
							label = new JLabel("<html><code>" + blockSeq.substring(subIndex, i) + "</code></html>");

							isBorder = true;
							openIntervals++;
							lastOpened = (String) interval.getUserObject();
						}
						// check position for right border
						if ((pos == (int) interval.getRightBorder())) {
							// Highlightable part ends here, store contents in hover label
							final String sequence = (String) interval.getUserObject();
							label = new CoverageViewerPane.HoverLabel(blockSeq.substring(subIndex, i), colorMap.get(sequence));

							CoverageViewerPane.HoverLabel cachedLbl = hoverLabels.get(sequence);
							if (cachedLbl != null) {
								((AbstractButton) label).setModel(cachedLbl.getModel());
							} else {
								// cache label
								hoverLabels.put(sequence, (CoverageViewerPane.HoverLabel) label);
//								// put label in button group
//								hoverGroup.add((AbstractButton) label);

								// install action listener
								((CoverageViewerPane.HoverLabel) label).addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent evt) {
										firePropertyChange("selection",
												((AbstractButton) evt.getSource()).isSelected(), sequence);
									}
								});
							}
							((AbstractButton) label).setSelected(sequence == selSequence);

							isBorder = true;
							openIntervals--;
						}
						if (isBorder) {
							// Add new label to panel, move index pointer forward
							subBlockPnl.add(label);
							subIndex = i;
						}
					}
				}
				// Store any remaining subsequences in (hover) label
				if (openIntervals > 0) {
					final String sequence = lastOpened;
					label = new CoverageViewerPane.HoverLabel(blockSeq.substring(subIndex), colorMap.get(sequence));

					CoverageViewerPane.HoverLabel cachedLbl = hoverLabels.get(sequence);
					if (cachedLbl != null) {
						((AbstractButton) label).setModel(cachedLbl.getModel());
					} else {
						// cache label
						hoverLabels.put(sequence, (CoverageViewerPane.HoverLabel) label);
//						// put label in button group
//						hoverGroup.add((AbstractButton) label);

						// install action listener
						((CoverageViewerPane.HoverLabel) label).addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent evt) {
								firePropertyChange("selection",
										((AbstractButton) evt.getSource()).isSelected(), sequence);
							}
						});
					}
					((AbstractButton) label).setSelected(sequence == selSequence);
				} else {
					label = new JLabel("<html><code>" + blockSeq.substring(subIndex) + "</code></html>");
				}
				subBlockPnl.add(label);

				blockPnl.add(subBlockPnl, BorderLayout.SOUTH);

				covPnl.add(blockPnl);
			}
		} else {
			covPnl.add(new JLabel("Disabled if no sequence informations are available"));
		}

		return covPnl;
	}

	/**
	 * Clears the coverage viewer pane's contents.
	 */
	public void clear() {
		if (updateWorker != null) {
			updateWorker.cancel(true);
		}
		backgroundPnl.removeAll();
		this.setBusy(false);

		Border headerBorder = UIManager.getBorder("TableHeader.cellBorder");
		JLabel headerLbl = new JLabel();
		headerLbl.setPreferredSize(new Dimension(63, 0));
		headerLbl.setBorder(headerBorder);

		this.setRowHeaderView(headerLbl);
	}

	/**
	 * Clears the selection of all sequence labels.
	 */
	public void clearSelection() {
		for (CoverageViewerPane.HoverLabel label : hoverLabels.values()) {
			label.setSelected(false);
		}
	}

	/**
	 * Sets the selection state of the peptide label associated with the
	 * specified peptide sequence to the specified selection value.
	 * @param sequence the peptide sequence
	 * @param selected the selection value to set
	 */
	public void setSelected(String sequence, boolean selected) {
		selSequence = sequence;
		CoverageViewerPane.HoverLabel label = this.hoverLabels.get(sequence);
		if (label != null) {
			label.setSelected(selected);
//			// scroll to the selected label
//			Container cont = label.getParent();
//			if (cont != null) {
//				((JComponent) cont).scrollRectToVisible(cont.getBounds());	// far from perfect, but close enough
//			}
		}
	}
	
	/**
	 * Returns the currently selected peptide sequence.
	 * @return the currently selected peptide sequence
	 */
	public String getSelectedSequence() {
		return this.selSequence;
	}
	
	/**
	 * Returns the number of protein rows currently being displayed.
	 * @return the number of protein rows
	 */
	public int getRowCount() {
		if (this.backgroundPnl.getComponentCount() > 0) {
			return ((Container) this.backgroundPnl.getComponent(0)).getComponentCount();
		}
		return 0;
	}

	@Override
	public boolean isBusy() {
		return this.busyLbl.isBusy();
	}

	@Override
	public void setBusy(boolean busy) {
        this.busyLbl.setBusy(busy);
	}

	/**
	 * A clickable label with rollover effect.
	 * @author A. Behne
	 */
	private class HoverLabel extends JToggleButton {
	
		/**
		 * The background color for when this label is selected.
		 */
		private Color selectionColor;
		
		/**
		 * The default font.
		 */
		private final Font normalFont;
		
		/**
		 * The font used when the mouse cursor is hovering over this label.
		 */
		private final Font underlineFont;
	
		/**
		 * Creates a hover label using the specified label text and foreground color.
		 * @param text the text
		 * @param foreground the foreground color
		 */
		public HoverLabel(String text, Color foreground) {
			super(text);
            setRolloverEnabled(true);
            setForeground(foreground);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(null);

            this.normalFont = new Font("Monospaced", Font.PLAIN, 12);
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            this.underlineFont = this.normalFont.deriveFont(attributes);

            setFont(this.normalFont);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	
		@Override
		public void setForeground(Color foreground) {
            this.selectionColor = new Color(foreground.getRed(), foreground.getGreen(),
						foreground.getBlue(), (foreground.getAlpha() + 1) / 8);
			super.setForeground(foreground);
		}
	
		@Override
		protected void paintComponent(Graphics g) {
			if (isSelected()) {
				g.setColor(this.selectionColor);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
            setFont(this.model.isRollover() ? this.underlineFont : this.normalFont);
			super.paintComponent(g);
		}
	}

}
