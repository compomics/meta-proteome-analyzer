package de.mpa.db.mysqlclient;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mpa.client.ui.ScreenConfig;
import de.mpa.db.ConnectionType;
import de.mpa.db.DBConfiguration;
import de.mpa.db.DbConnectionSettings;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.db.accessor.Spectrum;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.SixtyFourBitStringSupport;
import de.mpa.parser.mascot.xml.MascotXMLParser;
import de.mpa.parser.mascot.xml.PeptideHit;
import de.mpa.parser.mascot.xml.ProteinHit;

public class SpecLibFrame extends JFrame {

	private SpecLibFrame frame;
	private File[] mgfFiles;
	private File[] xmlFiles;
	private JSpinner idSpn; 
	private JButton uplBtn;
	private JProgressBar uplPrg;
	
	private String startPath = "/data/bpt/bptprot/MetaProteomeAnalyzer/Daten fuer MetaProteomeAnalyzer";
	
	private ParseTask task;

	public SpecLibFrame() {
		initComponents();
		this.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SpecLibFrame();
	}
	
	// method to build frame
	private void initComponents() {
		
		frame = this;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// components for mgf panel
		JLabel mgfLbl = new JLabel("MGF files");
		
		final JTextField mgfTtf = new JTextField();
		mgfTtf.setPreferredSize(new Dimension(250,mgfTtf.getHeight()));
		
		JButton mgfBtn = new JButton("Browse...");
		mgfBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(startPath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Mascot Generic Format (*.mgf)", "mgf");
				chooser.setFileFilter(filter);
				chooser.setMultiSelectionEnabled(true);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					mgfFiles = chooser.getSelectedFiles();
					String str = "";
					for (File file : mgfFiles) {
						str += file.getName() + " ";
					}
					mgfTtf.setText(str);
//				} else {
//					mgfFiles = null;
//					mgfTtf.setText("");
				}
			}
		});
		
		// components for xml panel
		JLabel xmlLbl = new JLabel("XML files");
		
		final JTextField xmlTtf = new JTextField();
		
		JButton xmlBtn = new JButton("Browse...");
		xmlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(startPath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files", "xml");
				chooser.setFileFilter(filter);
				chooser.setMultiSelectionEnabled(true);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					xmlFiles = chooser.getSelectedFiles();
					String str = "";
					for (File file : xmlFiles) {
						str += file.getName() + " ";
					}
					xmlTtf.setText(str);	       		
//				} else {
//					xmlFiles = null;
//					xmlTtf.setText("");
				}
			}
		});

		Container contentPane = this.getContentPane();	// BoxLayout requires this
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		// components for upload panel
		JLabel idLbl = new JLabel("ExperimentID");
		
		idSpn = new JSpinner(new SpinnerNumberModel(Long.valueOf(1L), Long.valueOf(1L), Long.valueOf(Long.MAX_VALUE), Long.valueOf(1L)));
		
		uplBtn = new JButton("Upload");
		uplBtn.setPreferredSize(new Dimension(
				(int)(uplBtn.getPreferredSize().width*2.5),
				uplBtn.getPreferredSize().height));
		uplBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((mgfFiles != null) && (xmlFiles != null) && (mgfFiles.length == xmlFiles.length)) {
					if ((Long) idSpn.getValue() > 0L) {
						
						uplBtn.setEnabled(false);
				        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

						task = new ParseTask();
				        task.addPropertyChangeListener(new PropertyChangeListener() {
				        	@Override
							public void propertyChange(PropertyChangeEvent evt) {
				        		if ("progress" == evt.getPropertyName()) {
				        			int progress = (Integer) evt.getNewValue();
				        			uplPrg.setValue(progress);
				        		} 

				        	}
				        });
				        task.execute();

					} else {
						JOptionPane.showMessageDialog(frame, "ExperimentID needs to be larger than 0.", "Error", JOptionPane.ERROR_MESSAGE);
					}					
				} else {
					JOptionPane.showMessageDialog(frame, "An equal number of mgf and xml files must be selected.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		uplPrg = new JProgressBar(0,100);
		uplPrg.setStringPainted(true);
				
		// construct panels
		JPanel mgfPnl = new JPanel();
		mgfPnl.setLayout(new BoxLayout(mgfPnl, BoxLayout.X_AXIS));
		mgfPnl.add(mgfLbl);
		mgfPnl.add(mgfTtf);
		mgfPnl.add(mgfBtn);
		
		JPanel xmlPnl = new JPanel();
		xmlPnl.setLayout(new BoxLayout(xmlPnl, BoxLayout.X_AXIS));
		xmlPnl.add(xmlLbl);
		xmlPnl.add(xmlTtf);
		xmlPnl.add(xmlBtn);
		
		JPanel uplPnl = new JPanel();
		uplPnl.setLayout(new BoxLayout(uplPnl, BoxLayout.X_AXIS));
		uplPnl.add(idLbl);
		uplPnl.add(idSpn);
		uplPnl.add(uplBtn);
		uplPnl.add(uplPrg);	
		
		// insert panels into content pane
		contentPane.add(mgfPnl);
		contentPane.add(xmlPnl);
		contentPane.add(uplPnl);
		
		this.setContentPane(contentPane);
		this.pack();
		ScreenConfig.centerInScreen(this);
		
	}
	
	class ParseTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	
        	long currentProgress = 0L;
        	long totalProgress = 0L;
        	// inefficiently determine maximum progress value
//        	int max = getNumSpectra();
        	long maxProgress = 0L;
        	for (File mgfFile : mgfFiles) {
				maxProgress += mgfFile.length();
			}
        	
            //Initialize progress property.
            setProgress(0);
        	
        	try {
    			// connect to server
    			DBConfiguration dbconfig = new DBConfiguration("metaprot", ConnectionType.REMOTE, new DbConnectionSettings());
    			Connection conn = dbconfig.getConnection();
    			
    			// this list will store identified protein hits to avoid redundant sql queries further down 
//    			ArrayList<ProteinHit> proteinHits = new ArrayList<ProteinHit>();
    			Map<ProteinHit, ArrayList<ProteinAccessor>> proteinMap = new HashMap<ProteinHit, ArrayList<ProteinAccessor>>(10);

    			// grab experiment id from GUI
    			long experimentID = (Long) idSpn.getValue();
    			
    			// attention: the order in which files were selected does matter!
    			int numFiles = mgfFiles.length;
    			for (int i = 0; i < numFiles; i++) {
    				// parse mgf
    				uplBtn.setText("Reading MGF " + (i+1) + "/" + numFiles + "...");
    				MascotGenericFileReader reader = new MascotGenericFileReader(mgfFiles[i]);
    				List<MascotGenericFile> mgfList = reader.getSpectrumFiles(false);
    				
    				// grab spectrum position byte indices for progress reporting
    				List<Long> spectrumPositions = reader.getSpectrumPositions();
    				Iterator<Long> specPosIterator = spectrumPositions.iterator();
					currentProgress = specPosIterator.next();

    				// parse xml
					uplBtn.setText("Reading XML " + (i+1) + "/" + numFiles  + "...");
//    				MascotXMLParser readerXML = new MascotXMLParser(xmlFiles[i], MascotXMLParser.SUPPRESS_WARNINGS);
    				MascotXMLParser readerXML = new MascotXMLParser(xmlFiles[i]);
    				Map<String, ArrayList<PeptideHit>> pepMap = readerXML.parse().getPepMap();

    				// Iterate over all spectra.
    				uplBtn.setText("Processing " + (i+1) + "/" + numFiles  + "...");
    				for (MascotGenericFile mgf : mgfList) {

    					// remove leading/trailing whitespaces
    					String title = mgf.getTitle().trim();
    					
    					/* Spectrum section */
    		            HashMap<Object, Object> data = new HashMap<Object, Object>(12);
    	            
    		            // The spectrum title
    	                data.put(Spectrum.TITLE, title);
    	                
    	                // The precursor mass.
    	                data.put(Spectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
    	                
    	                // The precursor intensity
    	                data.put(Spectrum.PRECURSOR_INT, mgf.getIntensity());
    	                
    	                // The precursor charge
    	                data.put(Spectrum.PRECURSOR_CHARGE, Long.valueOf(mgf.getCharge()));
    	                
    	                // The m/z array
    	                TreeMap<Double, Double> peakMap = new TreeMap<Double, Double>(mgf.getPeaks());
    					Double[] mzDoubles = peakMap.keySet().toArray(new Double[0]);
    	                data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(mzDoubles));
    	                
    	                // The intensity array
    					Double[] inDoubles = peakMap.values().toArray(new Double[0]);
    	                data.put(Spectrum.INTARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(inDoubles));
    	                
    	                // The charge array
    	                TreeMap<Double, Integer> chargeMap = new TreeMap<Double, Integer>(mgf.getCharges());
    	                for (Double mz : peakMap.keySet()) {
							if (!chargeMap.containsKey(mz)) {
								chargeMap.put(mz, 0);
							}
						}
    					Integer[] chInts = chargeMap.values().toArray(new Integer[0]);
//    					Integer[] chInts = mgf.getCharges().values().toArray(new Integer[0]);
    					data.put(Spectrum.CHARGEARRAY, SixtyFourBitStringSupport.encodeIntsToBase64String(chInts));
    	                
    	                // The total intensity.
    	                data.put(Spectrum.TOTAL_INT, mgf.getTotalIntensity());
    	                
    	                // The highest intensity.
    	                data.put(Spectrum.MAXIMUM_INT, mgf.getHighestIntensity());

    	                // Create the database object.
    	                Spectrum spectrum = new Spectrum(data);
    	                spectrum.persist(conn);
    	                
    	                // Get the spectrum ID
    	                Long spectrumID = (Long) spectrum.getGeneratedKeys()[0];
    					
    	                /* libspectrum section */
    					HashMap<Object, Object> libdata = new HashMap<Object, Object>(6);
    					
    					libdata.put(Libspectrum.FK_SPECTRUMID, spectrumID);
    					libdata.put(Libspectrum.FK_EXPERIMENTID, experimentID);

    					// Create the database object.
    					Libspectrum libspectrum = new Libspectrum(libdata);
    					libspectrum.persist(conn);
    					
    					// grab peptide hits if key exists
    					if (pepMap.containsKey(title)) {

    						ArrayList<PeptideHit> pepHits = pepMap.get(title);
    						for (PeptideHit pepHit : pepHits) {

								Long peptideID;
    							String sequence = pepHit.getSequence();
    							if (sequence.isEmpty()) {	// we don't store empty sequences!
    								continue;
    							}
    							
    							PeptideAccessor peptide = PeptideAccessor.findFromSequence(sequence, conn);
    							if (peptide == null) {	// sequence not yet in database
        							HashMap<Object, Object> dataPeptide = new HashMap<Object, Object>(2);
        							dataPeptide.put(PeptideAccessor.SEQUENCE, pepHit.getSequence());
        							
        							peptide = new PeptideAccessor(dataPeptide);
        	    					peptide.persist(conn);

        	    					// Get the peptide id from the generated keys.
        	    					peptideID = (Long) peptide.getGeneratedKeys()[0];
    							} else {
    								peptideID = peptide.getPeptideid();
    							}
        						
    							Spec2pep s2p = Spec2pep.findLink(spectrumID, peptideID, conn);
    							if (s2p == null) {	// link doesn't exist yet
    								HashMap<Object, Object> dataSpecLib = new HashMap<Object, Object>(7);

    								dataSpecLib.put(Spec2pep.FK_SPECTRUMID, spectrumID);
    								dataSpecLib.put(Spec2pep.FK_PEPTIDEID, peptideID);

    								Spec2pep libEntry = new Spec2pep(dataSpecLib);
    								libEntry.persist(conn);
    							}
        						
        						// associate peptides with proteins
        						ProteinHit proteinHit = pepHit.getParentProteinHit();
        						
        						if (proteinMap.containsKey(proteinHit)) {
        							// link new peptide to old hit
        							ArrayList<ProteinAccessor> proteinList = proteinMap.get(proteinHit);
        							for (ProteinAccessor protein : proteinList) {
        								long proteinID = protein.getProteinid();
	    								// check whether pep2prot link already exists, otherwise create new one
	    								Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
	    								if (pep2prot == null) {	// link doesn't exist yet
	    									// Link peptide to protein.
	    									pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
	    								}
									}
        						} else {
        							// link peptide to new hit
            						ArrayList<String> accessions   = (ArrayList<String>) proteinHit.getAccessions();
            						ArrayList<String> descriptions = (ArrayList<String>) proteinHit.getDescriptions();
        							ArrayList<ProteinAccessor> proteinList = new ArrayList<ProteinAccessor>();
            						
            						for (int j = 0; j < accessions.size(); j++) {
        								Long proteinID;
    									String accession = accessions.get(j);
    									String description = descriptions.get(j);
    									if ((accession == null) && (description == null)) {
    										continue;	// no use looking for proteins without attributes!
    									}
    									
    	        						ProteinAccessor protein = ProteinAccessor.findFromAttributes(accession, description, conn);
    	        						if (protein == null) {	// protein not yet in database
    	        							// Add new protein to the database
    	        							protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, "", conn);
    	    							} else {
    	    								proteinID = protein.getProteinid();
    	    								// check whether pep2prot link already exists, otherwise create new one
    	    								Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
    	    								if (pep2prot == null) {	// link doesn't exist yet
    	    									// Link peptide to protein.
    	    									pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
    	    								}
    	    							}
    	        						proteinList.add(protein);
    								}
        							proteinMap.put(proteinHit, proteinList);
        						}
							}
    					}
						currentProgress = (specPosIterator.hasNext()) ?
								specPosIterator.next() : mgfFiles[i].length();
                        setProgress((int)((double)(totalProgress+currentProgress)/maxProgress*100));
    				}
    				totalProgress += currentProgress;
    			}
    			conn.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            uplBtn.setEnabled(true);
            uplBtn.setText("Upload");
            setCursor(null); //turn off the wait cursor
			JOptionPane.showMessageDialog(frame, "Upload complete.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

//	private int getNumSpectra() {
//		int res = 0;
//		for (File mgf : mgfFiles) {
//			try {
//				MascotGenericFileReader reader = new MascotGenericFileReader(mgf, MascotGenericFileReader.SURVEY);
//				res += reader.getSpectrumPositions().size();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return res;
//	}

}
