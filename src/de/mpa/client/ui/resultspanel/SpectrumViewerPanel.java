package de.mpa.client.ui.resultspanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.inputpanel.FilePanel;
import de.mpa.io.MascotGenericFile;
import de.mpa.model.algorithms.fragmentation.FragmentIon;
import de.mpa.model.algorithms.fragmentation.Fragmentizer;
import de.mpa.model.algorithms.fragmentation.Ion;
import de.mpa.model.analysis.Masses;

/**
 * Panel implementation for displaying a spectrum viewer with controls to filter
 * individual annotations.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class SpectrumViewerPanel extends JPanel {

	/**
	 * The spectrum panel.
	 */
	private SpectrumPanel specPnl;
	
	/**
	 * The filter panel.
	 */
	private SpectrumViewerPanel.FilterPanel filterPnl;

	/**
	 * The maximum x value to which the spectrum view shall be scaled.
	 */
	private double maxX;

	/**
	 * The currently displayable spectrum annotations.
	 */
	private Map<FragmentIon, SpectrumAnnotation> annotations;

	/**
	 * Constructs a spectrum viewer panel with annotation filter controls.
	 */
	public SpectrumViewerPanel() {
        initComponents();
	}

	/**
	 * Creates and lays out the panel components.
	 */
	private void initComponents() {
        setLayout(new FormLayout("p:g, 5dlu, r:p", "f:p:g"));

        this.specPnl = FilePanel.createDefaultSpectrumPanel();
        this.specPnl.setMinimumSize(new Dimension(200, 200));

        this.filterPnl = new SpectrumViewerPanel.FilterPanel();

        add(this.specPnl, CC.xy(1, 1));
        add(this.filterPnl, CC.xy(3, 1));
	}
	
	/**
	 * Resets the spectrum viewer to display the default placeholder spectrum.
	 */
	protected void clearSpectrum() {
        refreshSpectrum(null, null);
	}
	
	/**
	 * Refreshes the spectrum viewer using the provided spectrum file and
	 * annotates it by analyzing the provided peptide sequence.
	 * @param mgf the spectrum file
	 * @param sequence the peptide sequence
	 */
	protected void refreshSpectrum(MascotGenericFile mgf, String sequence) {
		// clear the spectrum panel
		Container specCont = this.specPnl.getParent();
		specCont.remove(this.specPnl);
		
		// add spectrum
		if (mgf != null && sequence != null) {
            this.specPnl = new SpectrumPanel(mgf) {
				@Override
				public void paint(Graphics g) {
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					super.paint(g);
				}
			};
            this.specPnl.showAnnotatedPeaksOnly(true);
            this.specPnl.setShowResolution(false);

            this.specPnl.setLayout(new FormLayout("0px:g, 0px:g, 15px", "b:p:g, 1px"));
			
			JTextField titleTtf = new JTextField(mgf.getTitle());
			titleTtf.setEditable(false);
			titleTtf.setBorder(null);
			titleTtf.setOpaque(false);
			titleTtf.setHorizontalAlignment(SwingConstants.RIGHT);
			titleTtf.setCaretPosition(0);
			
            this.specPnl.add(titleTtf, CC.xy(2, 1));

            this.maxX = Math.max(this.maxX, this.specPnl.getMaxXAxisValue());
            this.specPnl.rescale(0.0, this.maxX);

			Fragmentizer fragmentizer = new Fragmentizer(sequence, Masses.getInstance(), mgf.getCharge());
            addSpectrumAnnotations(fragmentizer.getFragmentIons());
		} else {
			// revert to defaults
            this.specPnl = FilePanel.createDefaultSpectrumPanel();
            this.maxX = 0.0;
		}
		specCont.add(this.specPnl, CC.xy(1, 1));
		specCont.validate();
	}

	/**
	 * Method to add annotations to spectrum plot.
	 * @param fragmentIons
	 */
	protected void addSpectrumAnnotations(Map<Ion.IonType, FragmentIon[]> fragmentIons) {

        this.annotations = new HashMap<FragmentIon, SpectrumAnnotation>();
		Set<Map.Entry<Ion.IonType, FragmentIon[]>> entrySet = fragmentIons.entrySet();
		int i = 0;
		for (Map.Entry<Ion.IonType, FragmentIon[]> entry : entrySet) {
			FragmentIon[] ions = entry.getValue();

			for (FragmentIon ion : ions) {
				double mzValue = ion.getMZ();
				Color color;
				if (i % 2 == 0) {
					color = Color.BLUE;
				} else {
					color = Color.BLACK;
				}

				// Use standard ion type names, such as y5++
				String ionDesc = ion.toString();

				// TODO: Get fragment ion mass error from db or settings file!
//				annotations.add(new DefaultSpectrumAnnotation(mzValue, 0.5, color, ionDesc));
				annotations.put(ion, new DefaultSpectrumAnnotation(mzValue, 0.5, color, ionDesc));
			}
			i++;
		}

		this.filterAnnotations();

	}

	/**
	 * This method updates the filtered annotations due to the respective selected checkboxes.
	 */
	protected void filterAnnotations() {
		if ((annotations != null) && !annotations.isEmpty()) {
			specPnl.setAnnotations(this.filterAnnotations(annotations));
			specPnl.validate();
			specPnl.repaint();
		}
	}

	/**
	 * Filters the collection of all annotations and returns the remaining annotations.
	 * @param annotations the annotations to be filtered
	 * @return the filtered annotations
	 */
	private Vector<SpectrumAnnotation> filterAnnotations(Map<FragmentIon, SpectrumAnnotation> annotations) {
		// init the collection of remaining annotations
		Vector<SpectrumAnnotation> filteredAnnotations = new Vector<SpectrumAnnotation>();

		// iterate collection of all annotations
		for (Map.Entry<FragmentIon, SpectrumAnnotation> entry : annotations.entrySet()) {
			// query filter panel
			if (this.filterPnl.filterAnnotation(entry.getKey())) {
				// append annotation
				filteredAnnotations.add(entry.getValue());
			}
		}
	
		return filteredAnnotations;
	}
	
	 /**
	  * Panel implementation containing controls for toggling the visibility of spectrum annotations.
	  * @author A. Behne
	  */
	private class FilterPanel extends JPanel {
		
		/**
		 * The a ions toggle button.
		 */
		private JToggleButton aIonsTgl;
		
		/**
		 * The b ions toggle button.
		 */
		private JToggleButton bIonsTgl;
		
		/**
		 * The c ions toggle button.
		 */
		private JToggleButton cIonsTgl;
		
		/**
		 * The x ions toggle button.
		 */
		private JToggleButton xIonsTgl;
		
		/**
		 * The y ions toggle button.
		 */
		private JToggleButton yIonsTgl;
		
		/**
		 * The z ions toggle button.
		 */
		private JToggleButton zIonsTgl;
		
		/**
		 * The water loss ions toggle button.
		 */
		private JToggleButton h2oIonsTgl;
		
		/**
		 * The ammonia loss ions toggle button.
		 */
		private JToggleButton nh3IonsTgl;
		
		/**
		 * The singly-charged ions toggle button.
		 */
		private JToggleButton sglIonsTgl;
		
		/**
		 * The doubly-charged ions toggle button.
		 */
		private JToggleButton dblIonsTgl;
		
		/**
		 * The multiply-charged ions toggle button.
		 */
		private JToggleButton mltIonsTgl;
		
		/**
		 * The precursor ions toggle button.
		 */
		private JToggleButton precIonsTgl;

		/**
		 * Constructs a filter panel.
		 */
		public FilterPanel() {
            initComponents();
		}
		
		/**
		 * Creates and lays out the filter panel's components.
		 */
		private void initComponents() {
            setLayout(new FormLayout("39px",
					"f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, "		// a, b, c
					+ "5dlu, p, 5dlu, "
					+ "f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, "	// x, y, z
					+ "5dlu, p, 5dlu, "
					+ "f:p:g, 2dlu, f:p:g, "				// °, *
					+ "5dlu, p, 5dlu, "
					+ "f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, "	// +, ++, >2
					+ "5dlu, p, 5dlu, "
					+ "f:p:g"));							// MH
		
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
                    SpectrumViewerPanel.this.filterAnnotations();
				}
			};

            add(this.aIonsTgl = createFilterButton("a", false, "Show a ions", action), CC.xy(1, 1));
            add(this.bIonsTgl = createFilterButton("b", true, "Show b ions", action), CC.xy(1, 3));
            add(this.cIonsTgl = createFilterButton("c", false, "Show c ions", action), CC.xy(1, 5));
            add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(1, 7));
            add(this.xIonsTgl = createFilterButton("x", false, "Show x ions", action), CC.xy(1, 9));
            add(this.yIonsTgl = createFilterButton("y", true, "Show y ions", action), CC.xy(1, 11));
            add(this.zIonsTgl = createFilterButton("z", false, "Show z ions", action), CC.xy(1, 13));
            add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(1, 15));
            add(this.h2oIonsTgl = createFilterButton("\u00B0", false, "<html>Show H<sub>2</sub>0 losses</html>", action), CC.xy(1, 17));
            add(this.nh3IonsTgl = createFilterButton("*", false, "<html>Show NH<sub>3</sub> losses</html>", action), CC.xy(1, 19));
            add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(1, 21));
            add(this.sglIonsTgl = createFilterButton("+", true, "Show ions with charge 1", action), CC.xy(1, 23));
            add(this.dblIonsTgl = createFilterButton("++", false, "Show ions with charge 2", action), CC.xy(1, 25));
            add(this.mltIonsTgl = createFilterButton(">2", false, "Show ions with charge >2", action), CC.xy(1, 27));
            add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(1, 29));
            add(this.precIonsTgl = createFilterButton("MH", true, "Show precursor ion", action), CC.xy(1, 31));
			
		}

		/**
		 * Convenience method to create a labeled toggle button for filtering annotations.
		 * @param label the button label
		 * @param selected the initial selection state
		 * @param toolTip the tooltip text
		 * @param action the action to perform on click
		 * @return the filter toggle button
		 */
		private JToggleButton createFilterButton(String label, boolean selected,
				String toolTip, Action action) {
			JToggleButton toggleButton = new JToggleButton(action);
			toggleButton.setText(label);
			toggleButton.setSelected(selected);
			toggleButton.setToolTipText(toolTip);
			return toggleButton;
		}
		
		/**
		 * Checks the provided ion annotation and returns whether it needs to be filtered out.
		 * @param ion the ion to check
		 * @return <code>true</code> if the annotation is not to be filtered out, <code>false</code> otherwise
		 */
		public boolean filterAnnotation(FragmentIon ion) {
			// check ion charge
			double charge = ion.getCharge();
			if (!this.mltIonsTgl.isSelected() && (charge > 2)) {
				return false;
			}
			if (!this.dblIonsTgl.isSelected() && (charge == 2)) {
				return false;
			}
			if (!this.sglIonsTgl.isSelected() && (charge == 1)) {
				return false;
			}
			
			// check ion type
			switch (ion.getType()) {
				case A_ION:
					return this.aIonsTgl.isSelected();
				case AH2O_ION:
					return this.aIonsTgl.isSelected() && this.h2oIonsTgl.isSelected();
				case ANH3_ION:
					return this.aIonsTgl.isSelected() && this.nh3IonsTgl.isSelected();
				case B_ION:
					return this.bIonsTgl.isSelected();
				case BH2O_ION:
					return this.bIonsTgl.isSelected() && this.h2oIonsTgl.isSelected();
				case BNH3_ION:
					return this.bIonsTgl.isSelected() && this.nh3IonsTgl.isSelected();
				case C_ION:
					return this.cIonsTgl.isSelected();
				case X_ION:
					return this.xIonsTgl.isSelected();
				case Y_ION:
					return this.yIonsTgl.isSelected();
				case YH2O_ION:
					return this.yIonsTgl.isSelected() && this.h2oIonsTgl.isSelected();
				case YNH3_ION:
					return this.yIonsTgl.isSelected() && this.nh3IonsTgl.isSelected();
				case Z_ION:
					return this.zIonsTgl.isSelected();
				case MH_ION:
					return this.precIonsTgl.isSelected();
				case MHH2O_ION:
					return this.precIonsTgl.isSelected() && this.h2oIonsTgl.isSelected();
				case MHNH3_ION:
					return this.precIonsTgl.isSelected() && this.nh3IonsTgl.isSelected();
				default:
					return true;
			}
			
		}
		
	}
	
}
