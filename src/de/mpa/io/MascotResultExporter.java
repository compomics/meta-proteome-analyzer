package de.mpa.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.mpa.parser.mascot.xml.MascotPeptideHit;
import de.mpa.parser.mascot.xml.MascotRecord;
import de.mpa.parser.mascot.xml.MascotXMLParser;

public class MascotResultExporter {
	
	private File mgfFile;
	private File xmlFile;
	private MascotXMLParser parser; 
	
	public MascotResultExporter(File mgfFile, File xmlFile) {
		this.mgfFile = mgfFile;
		this.xmlFile = xmlFile;
	}
	
	public void export(String outputFile) throws IOException{
		FileWriter writer = new FileWriter(new File(outputFile));
		
		parser = new MascotXMLParser(xmlFile);
		
		MascotGenericFileReader reader = new MascotGenericFileReader(mgfFile);
		List<MascotGenericFile> mgfList = reader.getSpectrumFiles();
		
		// MascotRecord
		MascotRecord record = parser.parse();
		
		Map<String, List<MascotPeptideHit>> pepMap = record.getPeptideMap();
		// Iterate over all spectra.
		for (MascotGenericFile mgf : mgfList) {
			if (pepMap.containsKey(mgf.getTitle())) {
				
				List<MascotPeptideHit> pepHits = pepMap.get(mgf.getTitle());
				for (MascotPeptideHit pepHit : pepHits) {
					
					String sequence = pepHit.getSequence();
					if (!sequence.isEmpty()) {	// we don't store empty sequences!
						writer.write(mgf.getTitle() + ";");
						writer.write(sequence + ";");
						// Get the first protein hit
						writer.write(pepHit.getParentProteinHit().getAccessions().get(0) + ";");
					}
					writer.write("\n");
				}
				
			}
		}
		
	writer.close();
	}
	
	public static void main(String[] args) {
		File mgfFile = new File("/home/muth/Metaproteomics/Bande1.mgf");
		File xmlFile = new File("/home/muth/Metaproteomics/Bande1.xml");
		MascotResultExporter exporter = new MascotResultExporter(mgfFile, xmlFile);
		
		try {
			exporter.export("/home/muth/Metaproteomics/Bande1.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
