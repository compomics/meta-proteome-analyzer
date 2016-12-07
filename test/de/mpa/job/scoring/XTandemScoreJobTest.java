package de.mpa.job.scoring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.task.scoring.XTandemScoreExtractor;
import de.proteinms.xtandemparser.xtandem.Domain;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;
import junit.framework.TestCase;

public class XTandemScoreJobTest extends TestCase {
	
	@Test 
	public void testTDAAnalysis() throws SAXException, ParserConfigurationException, IOException {
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("P://Temp//PFU//xtandem//pyro_scores.csv"));
		XTandemScoreExtractor scoreExtractor = new XTandemScoreExtractor(new File("P://Temp//PFU//xtandem//pyro.xml"), new File("P://Temp//PFU//xtandem//pyro_decoy.xml"));
		TargetDecoyAnalysis tda = new TargetDecoyAnalysis(scoreExtractor.getTargetScores(),	scoreExtractor.getDecoyScores());

		XTandemFile xTandemFile = new XTandemFile("P://Temp//PFU//xtandem//pyro.xml");

		@SuppressWarnings("unchecked")
		Iterator<de.proteinms.xtandemparser.xtandem.Spectrum> iter = xTandemFile.getSpectraIterator();

		// Prepare everything for the peptides.
		PeptideMap pepMap = xTandemFile.getPeptideMap();

		while (iter.hasNext()) {
			// Get the next spectrum.
			Spectrum spectrum = iter.next();
			int spectrumNumber = spectrum.getSpectrumNumber();

			// Get all identifications from the spectrum
			ArrayList<Peptide> pepList = pepMap.getAllPeptides(spectrumNumber);
			List<String> peptides = new ArrayList<String>();

			// Iterate over all peptide identifications aka. domains
			for (Peptide peptide : pepList) {
				List<Domain> domains = peptide.getDomains();
				for (Domain domain : domains) {
					String sequence = domain.getDomainSequence();
					if (!peptides.contains(sequence)) {
						double qValue = tda.getQValue((float) domain.getDomainHyperScore());
						bWriter.append(spectrum.getSpectrumId() + "\t" + sequence + "\t" + domain.getDomainHyperScore() + "\t" + qValue);
						bWriter.newLine();
					}
				}
			}
		}
		bWriter.flush();
		bWriter.close();
	}
}
