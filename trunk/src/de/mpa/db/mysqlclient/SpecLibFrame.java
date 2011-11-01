package de.mpa.db.mysqlclient;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.mpa.db.DBConfiguration;
import de.mpa.db.accessor.Speclibentry;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Spectrumfile;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.parser.mascot.xml.MascotXMLParser;
import de.mpa.parser.mascot.xml.PeptideHit;

public class SpecLibFrame extends JFrame {

	private SpecLibFrame frame;

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
	// Methode die Frame aufbaut
	private void initComponents() {
		// read mgf
		JLabel captionMgfLlb = new JLabel("Mgf filename");
		final JTextField nameMGFTxt = new JTextField("");
		nameMGFTxt.setPreferredSize(new Dimension(250,25));
		JButton mgfReadBtn	= new JButton("Add Mgf");
		frame = this;
		mgfReadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser("test/de/mpa/resources");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("mgf files", "mgf");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					nameMGFTxt.setText(chooser.getSelectedFile().getAbsolutePath());			       		
				}
			}
		});
		// read xml
		JLabel captionXmlLlb = new JLabel("Xml filename");
		final JTextField nameXmlTxt = new JTextField("");
		JButton xmlReadBtn	= new JButton("Add Xml");
		xmlReadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser("test/de/mpa/resources");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files", "xml");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					nameXmlTxt.setText(chooser.getSelectedFile().getAbsolutePath());			       		
				}
			}
		});

		// Zusammenbau Panels
		Container contentPane = this.getContentPane();// Boxlayout needs extra container to add frame
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		JPanel mgfRowPnl = new JPanel();
		JPanel xmlRowPnl = new JPanel();

		mgfRowPnl.setLayout(new BoxLayout(mgfRowPnl, BoxLayout.X_AXIS));
		xmlRowPnl.setLayout(new BoxLayout(xmlRowPnl, BoxLayout.X_AXIS));

		mgfRowPnl.add(captionMgfLlb);
		mgfRowPnl.add(nameMGFTxt);
		mgfRowPnl.add(mgfReadBtn);
		xmlRowPnl.add(captionXmlLlb);
		xmlRowPnl.add(nameXmlTxt);
		xmlRowPnl.add(xmlReadBtn);


		// Upload frame
		JPanel uploadPnl = new JPanel();
		uploadPnl.setLayout(new BoxLayout(uploadPnl, BoxLayout.X_AXIS));
		JButton uploadBtn = new JButton("upload");
		uploadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parseStuff(nameMGFTxt.getText(), nameXmlTxt.getText());
			}
		});
		uploadPnl.add(uploadBtn);
		contentPane.add(mgfRowPnl);
		contentPane.add(xmlRowPnl);
		contentPane.add(uploadPnl);
		this.setContentPane(contentPane);
		this.pack();	
	}

	protected void parseStuff(String nameMgf, String nameXml) {
		// Mgf Parsing
		try {
			MascotGenericFileReader readerMgf = new MascotGenericFileReader(new File(nameMgf));
			List<MascotGenericFile> mgfFiles = readerMgf.getSpectrumFiles();
			MascotXMLParser readerXml = new MascotXMLParser(new File(nameXml));
			Map<String,PeptideHit> pepMap = readerXml.parse().getPepMap();
			pepMap.size();

			DBConfiguration dbconfig = new DBConfiguration("metaprot");
			Connection conn = dbconfig.getConnection();

			// Iterate all spectra.
			for (MascotGenericFile mgf : mgfFiles) {
				HashMap<Object, Object> data = new HashMap<Object, Object>(10);

				data.put(Spectrum.L_PROJECTID, Long.valueOf(1));
				data.put(Spectrum.FILENAME, mgf.getFilename());
				data.put(Spectrum.SPECTRUMNAME, mgf.getTitle());
				data.put(Spectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
				data.put(Spectrum.CHARGE, mgf.getCharge());
				data.put(Spectrum.TOTALINTENSITY, mgf.getTotalIntensity());
				data.put(Spectrum.MAXIMUMINTENSITY, mgf.getHighestIntensity());

			     // Create the database object.
	            Spectrum spectrum = new Spectrum(data);
	            spectrum.persist(conn);
	            
	            // Get the spectrumid from the generated keys.
				Long spectrumid = (Long) spectrum.getGeneratedKeys()[0];
				
	            // Create the spectrumFile instance.
	            Spectrumfile spectrumFile = new Spectrumfile();
	            spectrumFile.setL_spectrumid(spectrumid);

	            // Set the filecontent
	            // Read the contents for the file into a byte[].
	            byte[] fileContents = mgf.toString().getBytes();
	            // Set the byte[].
	            spectrumFile.setUnzippedFile(fileContents);
	            // Create the database object.
	            spectrumFile.persist(conn);
	            
				// Peptideinfo if found
				if (pepMap.containsKey(mgf.getTitle())) {

					PeptideHit pepHit = pepMap.get(mgf.getTitle());
					HashMap<Object, Object> dataSpecLib = new HashMap<Object, Object>(7);

					dataSpecLib.put(Speclibentry.L_SPECTRUMID, spectrumid);
					dataSpecLib.put(Speclibentry.PRECURSOR_MZ, pepHit.getMz());
					dataSpecLib.put(Speclibentry.SEQUENCE, pepHit.getSequence());
					dataSpecLib.put(Speclibentry.ANNOTATION, pepHit.getProteinAccession());
					
					Speclibentry libEntry = new Speclibentry(dataSpecLib);
					libEntry.persist(conn);
				}
			}
			conn.close();				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}

}
