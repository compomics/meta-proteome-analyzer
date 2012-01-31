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
import java.util.List;
import java.util.Map;

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
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mpa.client.DbConnectionSettings;
import de.mpa.db.DBConfiguration;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.db.accessor.Spectrumfile;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
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
//				JFileChooser chooser = new JFileChooser("test/de/mpa/resources");
				JFileChooser chooser = new JFileChooser("/data/bpt/bptprot/MetaproteomeAnalyser/Daten fuer Metaproteomce analyser/Qstar");
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
//				JFileChooser chooser = new JFileChooser("test/de/mpa/resources");
				JFileChooser chooser = new JFileChooser("/data/bpt/bptprot/MetaproteomeAnalyser/Daten fuer Metaproteomce analyser/Qstar");
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
		
		idSpn = new JSpinner();
		idSpn.setValue(1);
		
		uplBtn = new JButton("Upload");
		uplBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((mgfFiles != null) && (xmlFiles != null) && (mgfFiles.length == xmlFiles.length)) {
					if ((Integer)idSpn.getValue() > 0) {
						
//						parseStuff();
						
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
	}
	
	class ParseTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	
        	int progress = 0;
        	// inefficiently determine maximum progress value
        	int max = getNumSpectra();
        	
        	
            //Initialize progress property.
            setProgress(0);
        	
        	try {
    			// connect to server
    			DBConfiguration dbconfig = new DBConfiguration("metaprot", false,new DbConnectionSettings());
    			Connection conn = dbconfig.getConnection();
    			
    			// this list will store identified protein hits to avoid redundant sql queries further down 
//    			ArrayList<ProteinHit> proteinHits = new ArrayList<ProteinHit>();
    			Map<ProteinHit, ArrayList<ProteinAccessor>> proteinMap = new HashMap<ProteinHit, ArrayList<ProteinAccessor>>(10);

    			// attention: the order in which files were selected does matter!
    			for (int i = 0; i < mgfFiles.length; i++) {
    				// parse mgf
    				MascotGenericFileReader reader = new MascotGenericFileReader(mgfFiles[i]);
    				List<MascotGenericFile> mgfList = reader.getSpectrumFiles();

    				// parse xml
//    				MascotXMLParser readerXML = new MascotXMLParser(xmlFiles[i], MascotXMLParser.SUPPRESS_WARNINGS);
    				MascotXMLParser readerXML = new MascotXMLParser(xmlFiles[i]);
    				Map<String, ArrayList<PeptideHit>> pepMap = readerXML.parse().getPepMap();

    				// Iterate over all spectra.
    				for (MascotGenericFile mgf : mgfList) {
    					HashMap<Object, Object> data = new HashMap<Object, Object>(11);

    					data.put(Libspectrum.FK_EXPERIMENTID, Long.valueOf((Integer)idSpn.getValue()));
    					data.put(Libspectrum.FILENAME, mgf.getFilename());
    					data.put(Libspectrum.SPECTRUMNAME, mgf.getTitle());
    					data.put(Libspectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
    					data.put(Libspectrum.CHARGE, Long.valueOf((Integer) mgf.getCharge()));
    					data.put(Libspectrum.TOTALINTENSITY, mgf.getTotalIntensity());
    					data.put(Libspectrum.MAXIMUMINTENSITY, mgf.getHighestIntensity());

    					// Create the database object.
    					Libspectrum spectrum = new Libspectrum(data);
    					spectrum.persist(conn);

    					// Get the spectrumid from the generated keys.
    					Long spectrumID = (Long) spectrum.getGeneratedKeys()[0];

    					// Create the spectrumFile instance.
    					Spectrumfile spectrumFile = new Spectrumfile();
    					spectrumFile.setFk_libspectrumid(spectrumID);

    					// Set the file contents
    					// Read the contents for the file into a byte[].
    					byte[] fileContents = mgf.toString().getBytes();
    					// Set the byte[].
    					spectrumFile.setUnzippedFile(fileContents);
    					// Create the database object.
    					spectrumFile.persist(conn);

    					// grab peptide hits if key exists
    					if (pepMap.containsKey(mgf.getTitle())) {

    						ArrayList<PeptideHit> pepHits = pepMap.get(mgf.getTitle());
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
        						
    							Spec2pep sle = Spec2pep.findLink(spectrumID, peptideID, conn);
    							if (sle == null) {	// link doesn't exist yet
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
    	        							protein = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, conn);
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
                        progress += 1;
                        setProgress((int)((double)progress/max*100));
    				}
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
            setCursor(null); //turn off the wait cursor
			JOptionPane.showMessageDialog(frame, "Upload complete.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

	private int getNumSpectra() {
		int res = 0;
		for (File mgf : mgfFiles) {
			try {
				MascotGenericFileReader reader = new MascotGenericFileReader(mgf, MascotGenericFileReader.SURVEY);
				res += reader.getSpectrumPositions().size();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

}
