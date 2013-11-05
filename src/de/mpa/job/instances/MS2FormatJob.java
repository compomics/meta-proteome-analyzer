package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.mpa.algorithms.Masses;
import de.mpa.io.Peak;
import de.mpa.job.Job;

/**
 * FIXME: By crux version 1.40 this class is not used anymore!
 * @author T. Muth
 *
 */
public class MS2FormatJob extends Job{
	
	private String outputfile;
	private File mgfFile;
	
	public MS2FormatJob(File mgfFile) {
		this.mgfFile = mgfFile;
	}
	
	public void run() {
			outputfile = mgfFile.getAbsolutePath().substring(0, mgfFile.getAbsolutePath().indexOf(".mgf")) + ".ms2";;
			setDescription("MS2 FORMAT JOB");
			BufferedWriter writer = null;
			Reader mgfReader = null;

			try {
				
				// Parse the mgf file
				mgfReader = new Reader(mgfFile);
				List<SpectrumFile> mgfList = mgfReader.getSpectrumFiles();
				writer = new BufferedWriter(new FileWriter(outputfile));
				
				// Write the MS2 HEADER
				writer.write("H" + "\t");
				writer.write("CreationDate" + "\t");
				writer.write(new Date() + "\n");
				writer.write("H" + "\t");
				writer.write("Extractor" + "\t");
				writer.write("MakeMS2" + "\n");
				writer.write("H" + "\t");
				writer.write("ExtractorVersion" + "\t");
				writer.write("1.0" + "\n");
				writer.write("H" + "\t");
				writer.write("Comments" + "\t");
				writer.write("Formatted with MS2FormatJob" + "\n");
				writer.write("H" + "\t");
				writer.write("ExtractorOptions" + "\t");
				writer.write("MS2/MS1" + "\n");
				
				
				// Iterate over all spectra.
				int i = 1;
				for (SpectrumFile mgf : mgfList) {
					writer.write("S" + "\t");
					writer.write(Integer.toString(i) + "\t");
					writer.write(Integer.toString(i) + "\t");
					writer.write(Double.toString(mgf.getPrecursorMZ()) + "\n");
					writer.write("Z" + "\t");
					int charge = mgf.getCharge();
					if(charge == 0) charge = 2;
					writer.write(charge + "\t");
					writer.write(Double.toString((mgf.getPrecursorMZ() * charge) - Masses.Hydrogen) + "\n");
					List<Peak> peaks = mgf.getPeaks();
					for (Peak p : peaks) {
						writer.write(p.getMz() + " " + p.getIntensity() + "\n");
					}
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
	}

}
