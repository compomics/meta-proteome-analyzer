package de.mpa.db.accessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.algorithms.CrossCorrelation;
import de.mpa.algorithms.NormalizedDotProduct;
import de.mpa.algorithms.PearsonCorrelation;
import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.Vectorization;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.db.DBManager;
import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.SixtyFourBitStringSupport;
import de.mpa.parser.mascot.xml.MascotPeptideHit;
import de.mpa.parser.mascot.xml.MascotXMLParser;

/**
 * Test methods for running spectral similarity searches locally or retrieving
 * results stored in the remote database.
 * 
 * @author A. Behne
 */
public class SpecSearchHitAccessorTest extends TestCase {
	
	@Test
	public void textFixZeroChargeSpectraUsingMascot() throws SQLException, IOException {
		
		// init database connection
		Connection conn = DBManager.getInstance().getConnection();
		
		// init experiment data
		long queryExp = 54L;	// E. coli
		long libExp = 55L;
		String[] xmlNames = new String[] { "Ecoli_01.xml",
				"Ecoli_02.xml", "Ecoli_03.xml", "Ecoli_04.xml", "Ecoli_05.xml" };
		
		// parse and cache xml files
		List<Map<String, List<MascotPeptideHit>>> pepMaps = new ArrayList<Map<String, List<MascotPeptideHit>>>();
		for (String xmlName : xmlNames) {
			MascotXMLParser readerXML = new MascotXMLParser(new File(xmlName));
			pepMaps.add(readerXML.parse().getPeptideMap());
		}
		
		// get library spectra of experiments which have a precursor charge of 0
		PreparedStatement ps = conn.prepareStatement("SELECT spectrum.* FROM spectrum "
				+ "INNER JOIN libspectrum ON spectrumid = libspectrum.fk_spectrumid "
				+ "INNER JOIN spec2pep ON spectrumid = spec2pep.fk_spectrumid "
				+ "WHERE precursor_charge = 0 "
				+ "AND fk_experimentid IN (?, ?)");
		ps.setLong(1, queryExp);
		ps.setLong(2, libExp);
		
		ResultSet rs = ps.executeQuery();
		
		// iterate result set
		while (rs.next()) {
			Spectrum spectrum = new Spectrum(rs);
			List<MascotPeptideHit> hits = null;
			// find spectrum in Mascot data
			for (Map<String, List<MascotPeptideHit>> pepMap : pepMaps) {
				hits = pepMap.get(spectrum.getTitle());
				if (hits != null) {
					break;
				}
			}
			if (hits != null) {
				// set spectrum precursor charge and update database entry
				spectrum.setPrecursor_charge(hits.get(0).getCharge());
				spectrum.update(conn);
			} else {
				System.out.println("No hit(s) found for spectrum \"" + spectrum.getTitle() + "\"");
			}
		}
		rs.close();
		ps.close();
		conn.commit();
	}
	
	/**
	 * Performs spectral similarity searching on spectra belonging to
	 * (hard-coded) experiments and exports the results as score matrix image.<br>
	 * <b>Not for inclusion in test suite.</b>
	 * @throws SQLException if a database error occurs
	 * @throws IOException if a file error occurs
	 */
	@Test
	public void textRunSimilaritySearch() throws SQLException, IOException {
		
		// init database connection
		Connection conn = DBManager.getInstance().getConnection();
		
//		String outName = "01_default_BSA.png";
//		String outName = "02_default_EC.png";
//		String outName = "03_default_BG.png";
		
		// init experiment ids
//		long queryExp = 3L;
//		long libExp = 5L;
//		String prefix = "BSA/";
		
//		long queryExp = 54L;
//		long libExp = 55L;
//		String prefix = "E. coli/";
		
		long queryExp = 275L;
		long libExp = 276L;
		String prefix = "Biogas/";
		
		float precTol = 1.0f;
		float[] binWidths = { 1.0f, 0.5f };
		float[] baseWidths = { 1.0f, 0.5f };
		Transformation[] trafos = { Transformation.SQRT, Transformation.SQRT };
		int[] ks = { 0, 25 };
		int[] measures = { 0, 2 };
		int[] offsets = { 0, 200 };
		
//		String[] outNames = new String[binWidths.length];
//		for (int i = 0; i < outNames.length; i++) {
//			String measure;
//			switch (measures[i]) {
//				case 0:
//					measure = "cCorr";
//					break;
//				case 1:
//					measure = "pCorr";
//					break;
//				default:
//					measure = "xCorr";
//					break;
//			}
//			outNames[i] = prefix + "Measure Tuning " + i + " " + measure + (offsets[i] > 0 ? " " + offsets[i] : "") + ".png";
//		}
		String[] outNames = {
				prefix + "Parameter Tuning Default.png",
				prefix + "Parameter Tuning Optimal.png" };
		
		for (int o = 0; o < outNames.length; o++) {
			float binWidth = binWidths[o];
			float baseWidth = baseWidths[o];
			Transformation trafo = trafos[o];
			int k = ks[o];
			int measure = measures[o];
			int offset = offsets[o];
			String outName = outNames[o];
			
			PreparedStatement ps;
			ResultSet rs;
			
			// fetch query spectra
			System.out.print("Fetching query spectra... ");
//			ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, p.sequence, s.precursor_charge, s.precursor_mz FROM spectrum s "
//					+ "INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid "
//					+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
//					+ "INNER JOIN searchspectrum ss ON s.spectrumid = ss.fk_spectrumid "
//					+ "WHERE ss.fk_experimentid = ?");
			ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, p.sequence, s.precursor_charge, s.precursor_mz FROM spectrum s "
					+ "INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid "
					+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
					+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
					+ "WHERE ls.fk_experimentid = ?");
			ps.setLong(1, queryExp);
			
			rs = ps.executeQuery();
			
			System.out.print("done!\nCaching query spectra... ");
			
			// iterate and cache query results
			List<Integer> queryHashes = new ArrayList<Integer>();
			List<String> queryMzs = new ArrayList<String>();
			List<String> queryInts = new ArrayList<String>();
			List<Float> queryPreMzs = new ArrayList<Float>();
			
//			double avgPeaks = 0;
			while (rs.next()) {
				queryHashes.add((rs.getString("sequence") + rs.getString("precursor_charge")).hashCode());
				String mzArray = rs.getString("mzarray");
				queryMzs.add(mzArray);
				String intArray = rs.getString("intarray");
				queryInts.add(intArray);
				queryPreMzs.add(rs.getFloat("precursor_mz"));
				
//				avgPeaks += SixtyFourBitStringSupport.buildPeakMap(mzArray, intArray).size();
			}
			rs.close();
			ps.close();
			int querySize = queryHashes.size();
			
			System.out.print("done!\nFetching library spectra... ");
			
			// fetch library spectra
			ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, p.sequence, s.precursor_charge, s.precursor_mz FROM spectrum s "
					+ "INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid "
					+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
					+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
					+ "WHERE ls.fk_experimentid = ? "
					+ "GROUP BY s.spectrumid",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, libExp);

			rs = ps.executeQuery();
			
			System.out.print("done!\nScoring spectra... ");
			
			File dumpFile = new File("dump.mgf");
			dumpFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(dumpFile);

			// iterate results
			final List<Integer> libHashes = new ArrayList<Integer>();
			List<Float> libPreMzs = new ArrayList<Float>();
			final List<List<Byte>> pixelData = new ArrayList<List<Byte>>();
			
			SpectrumComparator specComp = initComparator(binWidth, baseWidth, trafo, measure, offset);
			
			while (rs.next()) {
				// score single library spectrum against all query spectra
				// (equals one pixel column inside score matrix image)
				List<Byte> columnData = new ArrayList<Byte>();
				
				// get library spectrum peaks
				String mzArray = rs.getString("mzarray");
				String intArray = rs.getString("intarray");
				HashMap<Double, Double> libPeaks = SixtyFourBitStringSupport.buildPeakMap(mzArray, intArray);
//				avgPeaks += libPeaks.size();
				specComp.prepare(getHighestPeaks(libPeaks, k));
				
				libHashes.add((rs.getString("sequence") + rs.getString("precursor_charge")).hashCode());
				float libPreMz = rs.getFloat("precursor_mz");
				libPreMzs.add(libPreMz);
				
				// iterate cached query spectra
				for (int j = 0; j < querySize; j++) {
					// calculate similarity
					double similarity = 0.0;
					if (Math.abs(libPreMz - queryPreMzs.get(j)) <= precTol) {
						HashMap<Double, Double> queryPeaks = SixtyFourBitStringSupport.buildPeakMap(queryMzs.get(j), queryInts.get(j));
						specComp.compareTo(getHighestPeaks(queryPeaks, k));
						similarity = specComp.getSimilarity();
					}
					Byte pixel = null;
					if (similarity > 0.0) {
						// truncate similarity to 8-bit integer
						pixel = new Byte((byte) ((int) (similarity * 255.0) - 128));
					}
					columnData.add(pixel);
				}
				pixelData.add(columnData);
			}
			rs.close();
			ps.close();
			int libSize = pixelData.size();
			
			fos.close();
			
			System.out.print("done!\nCreating score matrix image... ");
			
			// init score matrix image
			BufferedImage img = new BufferedImage(libSize + 2, querySize + 2, BufferedImage.TYPE_INT_ARGB);

			// insert peptide annotations and precursor masses
			for (int i = 0; i < querySize; i++) {
				img.setRGB(0, i + 1, queryHashes.get(i));
				img.setRGB(libSize + 1, i + 1, Float.floatToIntBits(queryPreMzs.get(i)));
			}
			for (int i = 0; i < libSize; i++) {
				img.setRGB(i + 1, 0, libHashes.get(i));
				img.setRGB(i + 1, querySize + 1, Float.floatToIntBits(libPreMzs.get(i)));
			}

			// iterate cached pixel values and insert them into the score matrix image
			int x = 1;
			for (List<Byte> columnData : pixelData) {
				int y = 1;
				for (Byte pixel : columnData) {
					if (pixel != null) {
						// store 8-bit pixel value inside red channel, set alpha to maximum
						// (results in opaque red-on-black image)
						img.setRGB(x, y, (((int) pixel + 128) << 16) + (255 << 24));
					}
					y++;
				}
				x++;
			}
			
			System.out.print("done!\nWriting image to disk... ");
			
//			// Create color model mapping scores to a red-yellow-green-cyan-blue gradient
//			ColorModel cm = new ColorModel(32) {
//				private int numCols = 256;
//				double val2ind = 255.0 / (numCols-1);
//				private Color[] colors = ColorUtils.getRainbowGradient(numCols);
//				
//				public int getAlpha(int pixel) {
//					return 255;
//				}
//				public int getRed(int pixel) {
//					int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
//					return colors[index].getRed();
//				}
//				public int getGreen(int pixel) {
//					int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
//					return colors[index].getGreen();
//				}
//				public int getBlue(int pixel) {
//					int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
//					return colors[index].getBlue();
//				}
//				@Override	// this is pretty hacky, but it works :)
//				public boolean isCompatibleRaster(Raster raster) {
//					return true;
//				}
//			};
//			img = new BufferedImage(cm, img.getSubimage(1, 1, img.getWidth()-2, img.getHeight()-2).getRaster(), false, null);
			
			// write image to file
			File outFile = new File("out/" + outName);
			if (!outFile.getParentFile().exists()) {
				outFile.mkdirs();
			}
			ImageIO.write(img, "png", outFile);
			
//			BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2d = bi.createGraphics();
//			g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
//			
//			ImageIO.write(bi, "png", new File("out/" + outName));
			
			System.out.println("done!");
			
//			avgPeaks /= libSize + querySize;
//			System.out.println("Average number of peaks: " + avgPeaks);
		}
		
	}
	
	@Test
	public void textRunBlindSimilaritySearch() throws SQLException, IOException {
		
		// init database connection
		Connection conn = DBManager.getInstance().getConnection();
		
		long queryExp = 275L;
		long libExp = 276L;
		String prefix = "Biogas/";

//		float precTol = 1.0f;
		float precTol = Float.MAX_VALUE;
		float binWidth = 1.0f;
		float baseWidth = 1.0f;
		Transformation trafo = Transformation.SQRT;
		int k = 0;
		int measure = 0;
		int offset = 0;
		String outName = prefix + "Blind Search Default All.csv";
		
		PreparedStatement ps;
		ResultSet rs;
		
		// fetch query spectra
		System.out.print("Fetching query spectra... ");
		ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, s.precursor_charge, s.precursor_mz FROM spectrum s "
				+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
				+ "WHERE ls.fk_experimentid = ?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setFetchSize(Integer.MIN_VALUE);
		ps.setLong(1, queryExp);
		
		rs = ps.executeQuery();
		
		System.out.print("done!\nCaching query spectra... ");
		
		// iterate and cache query results
		List<String> queryMzs = new ArrayList<String>();
		List<String> queryInts = new ArrayList<String>();
		List<Float> queryPreMzs = new ArrayList<Float>();

		int querySize = 0;
		while (rs.next()) {
			String mzArray = rs.getString("mzarray");
			queryMzs.add(mzArray);
			String intArray = rs.getString("intarray");
			queryInts.add(intArray);
			queryPreMzs.add(rs.getFloat("precursor_mz"));
			querySize++;
		}
		rs.close();
		ps.close();
		
		System.out.print("done!\nFetching library spectra... ");
		
		// fetch library spectra
		ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, s.precursor_charge, s.precursor_mz FROM spectrum s "
				+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
				+ "WHERE ls.fk_experimentid = ? ",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setFetchSize(Integer.MIN_VALUE);
		ps.setLong(1, libExp);

		rs = ps.executeQuery();
		
		System.out.println("done!\nScoring spectra... ");
		
		// iterate results
//		List<Float> libPreMzs = new ArrayList<Float>();
		int[] bins = new int[256];
		SpectrumComparator specComp = initComparator(binWidth, baseWidth, trafo, measure, offset);
		int count = 0;
		int max = 31055;
		long elapsed = System.currentTimeMillis();
		long delta = 0;
		long sum = 0;
		while (rs.next()) {
//			if ((count % 100) == 0) {
				double avg = sum / (count * 1.0);
				double eta = avg * (max - count);
				int s = (int) ((eta / 1000) % 60);
				int m = (int) ((eta / (1000 * 60)) % 60);
				int h = (int) (eta / (1000 * 60 * 60));
				System.out.println("" + count +
						"\t" + (delta / 1000.0) +
						"\t" + ((long) avg / 1000.0) +
						"\t" + String.format("%d:%02d:%02d", h, m, s));
//			}
			// score single library spectrum against all query spectra
			String mzArray = rs.getString("mzarray");
			String intArray = rs.getString("intarray");
			HashMap<Double, Double> libPeaks = SixtyFourBitStringSupport.buildPeakMap(mzArray, intArray);
			specComp.prepare(this.getHighestPeaks(libPeaks, k));
			
			float libPreMz = rs.getFloat("precursor_mz");
//			libPreMzs.add(libPreMz);
			
			// iterate cached query spectra
			for (int j = 0; j < querySize; j++) {
				// calculate similarity
				double similarity = 0.0;
				if (Math.abs(libPreMz - queryPreMzs.get(j)) <= precTol) {
					HashMap<Double, Double> queryPeaks = SixtyFourBitStringSupport.buildPeakMap(queryMzs.get(j), queryInts.get(j));
					specComp.compareTo(this.getHighestPeaks(queryPeaks, k));
					similarity = specComp.getSimilarity();
				}
				// truncate similarity, increment corresponding bin value
				bins[(int) (similarity * 255.0)]++;
			}
			count++;
			delta = System.currentTimeMillis() - elapsed;
//			sum = (long) (sum * ((count - 1.0) / count) + delta * (1.0 / count));
			sum += delta;
			elapsed = System.currentTimeMillis();
		}
		ps.close();
		
		System.out.print("...done!\nWriting histogram data to disk... ");
		File outFile = new File("out/" + outName);
		FileWriter fw = new FileWriter(outFile);
		for (int i = 0; i < bins.length; i++) {
			fw.append("" + i + ";" + bins[i] + "\n");
		}
		fw.flush();
		fw.close();
		
		System.out.println("done!");
		
	}
	
//	@Test
//	public void testRunBlindSimilaritySearch() throws SQLException, IOException {
//		
//		// init database connection
//		Connection conn = DBManager.getInstance().getConnection();
//		
//		long queryExp = 275L;
//		long libExp = 276L;
//		String prefix = "Biogas/";
//
////		float precTol = 1.0f;
//		float precTol = Float.MAX_VALUE;
//		float binWidth = 1.0f;
//		float baseWidth = 1.0f;
//		Transformation trafo = Transformation.SQRT;
//		int k = 0;
//		int measure = 0;
//		int offset = 0;
//		String outName = prefix + "Blind Search Default All.csv";
//		
//		PreparedStatement ps;
//		ResultSet rs;
//		
//		// fetch query spectra
//		System.out.print("Fetching spectra... ");
//		
//		// fetch library spectra
//		ps = conn.prepareStatement("SELECT s1.mzarray, s1.intarray, s1.precursor_mz, s2.mzarray, s2.intarray, s2.precursor_mz FROM libspectrum ls1 "
//				+ "RIGHT JOIN libspectrum ls2 ON ls1.fk_experimentid = ? "
//				+ "INNER JOIN spectrum s1 ON ls1.fk_spectrumid = s1.spectrumid "
//				+ "INNER JOIN spectrum s2 ON ls2.fk_spectrumid = s2.spectrumid "
//				+ "WHERE ls2.fk_experimentid = ?",
//				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//		ps.setFetchSize(Integer.MIN_VALUE);
//		ps.setLong(1, queryExp);
//		ps.setLong(2, libExp);
//
//		rs = ps.executeQuery();
//		
//		System.out.println("done!\nScoring spectra... ");
//		
//		// iterate results
////		List<Float> libPreMzs = new ArrayList<Float>();
//		int[] bins = new int[256];
//		SpectrumComparator specComp = initComparator(binWidth, baseWidth, trafo, measure, offset);
//		int count = 0;
//		double max = 322816725;
//		long startTime = System.currentTimeMillis();
//		while (rs.next()) {
//			
//			String mzArray, intArray;
//			// get library spectrum data
//			mzArray = rs.getString("s2.mzarray");
//			intArray = rs.getString("s2.intarray");
//			HashMap<Double, Double> libPeaks = SixtyFourBitStringSupport.buildPeakMap(mzArray, intArray);
//			
//			specComp.prepare(this.getHighestPeaks(libPeaks, k));
//
//			float queryPreMz = rs.getFloat("s1.precursor_mz");
//			float libPreMz = rs.getFloat("s2.precursor_mz");
//			
//			// check precursor mass tolerance
//			double similarity = 0.0;
//			if (Math.abs(libPreMz - queryPreMz) <= precTol) {
//				// get query spectrum data
//				mzArray = rs.getString("s1.mzarray");
//				intArray = rs.getString("s1.intarray");
//				HashMap<Double, Double> queryPeaks = SixtyFourBitStringSupport.buildPeakMap(mzArray, intArray);
//
//				// calculate similarity
//				specComp.compareTo(this.getHighestPeaks(queryPeaks, k));
//				similarity = specComp.getSimilarity();
//			}
//			// truncate similarity, increment corresponding bin value
//			bins[(int) (similarity * 255.0)]++;
//			count++;
//
//			long elapsed = System.currentTimeMillis() - startTime;
////			double eta = (1.0 - (count / max)) * elapsed;
//			double eta = (max - count) / count * elapsed;
//			int s = (int) ((eta / 1000) % 60);
//			int m = (int) ((eta / (1000 * 60)) % 60);
//			int h = (int) (eta / (1000 * 60 * 60));
//			System.out.println("" + count +
//					"\t" + String.format("%d:%02d:%02d", h, m, s));
//			
//		}
//		ps.close();
//		
//		System.out.print("...done!\nWriting histogram data to disk... ");
//		File outFile = new File("out/" + outName);
//		FileWriter fw = new FileWriter(outFile);
//		for (int i = 0; i < bins.length; i++) {
//			fw.append("" + i + ";" + bins[i] + "\n");
//		}
//		fw.flush();
//		fw.close();
//		
//		System.out.println("done!");
//		
//	}
	
	
	@Test
	public void testRunBlindSimilaritySearch() throws SQLException, IOException {
		
		// init database connection
		Connection conn = DBManager.getInstance().getConnection();

//		long queryExp = 3L;
//		long libExp = 5L;
//		String prefix = "BSA/";
		long queryExp = 275L;
		long libExp = 276L;
		String prefix = "Biogas";

		float precTol = 1.0f;
//		float precTol = Float.MAX_VALUE;
//		float binWidth = 1.0f;
//		float baseWidth = 1.0f;
//		Transformation trafo = Transformation.SQRT;
//		int k = 0;
//		int measure = 0;
//		int offset = 0;
//		String outName = prefix + "/" + prefix + " Blind Search Default tolMz 1.0";
		float binWidth = 0.5f;
		float baseWidth = 0.5f;
		Transformation trafo = Transformation.SQRT;
		int k = 25;
		int measure = 2;
		int offset = 200;
		String outName = prefix + "/" + prefix + " Blind Search Optimal tolMz 1.0";

//		int ci95 = 104;
//		int ci99 = 176;
		int ci95 = 71;
		int ci99 = 187;
		
		PreparedStatement ps;
		ResultSet rs;
		
		// fetch query spectra
		System.out.print("Fetching query spectra... ");
		ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, s.precursor_mz FROM spectrum s "
				+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
				+ "WHERE ls.fk_experimentid = ?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setFetchSize(Integer.MIN_VALUE);
		ps.setLong(1, queryExp);
		
		rs = ps.executeQuery();
		
		System.out.print("done!\nCaching query spectra... ");
		
		// iterate and cache query results
		List<String> queryMzs = new ArrayList<String>();
		List<String> queryInts = new ArrayList<String>();
		List<Float> queryPreMzs = new ArrayList<Float>();

		int querySize = 0;
		while (rs.next()) {
			String mzArray = rs.getString("mzarray");
			queryMzs.add(mzArray);
			String intArray = rs.getString("intarray");
			queryInts.add(intArray);
			queryPreMzs.add(rs.getFloat("precursor_mz"));
			querySize++;
		}
		ps.close();
		
		System.out.print("done!\nFetching library spectra... ");
		
		// fetch library spectra
		ps = conn.prepareStatement("SELECT s.mzarray, s.intarray, s.precursor_mz FROM spectrum s "
				+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
				+ "WHERE ls.fk_experimentid = ? ",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setFetchSize(Integer.MIN_VALUE);
		ps.setLong(1, libExp);

		rs = ps.executeQuery();
		
		System.out.println("done!\nCaching library spectra... ");
		
		// iterate and cache query results
		List<String> libMzs = new ArrayList<String>();
		List<String> libInts = new ArrayList<String>();
		List<Float> libPreMzs = new ArrayList<Float>();
		
		int libSize = 0;
		while (rs.next()) {
			String mzArray = rs.getString("mzarray");
			libMzs.add(mzArray);
			String intArray = rs.getString("intarray");
			libInts.add(intArray);
			libPreMzs.add(rs.getFloat("precursor_mz"));
			libSize++;
		}
		ps.close();
		
		System.out.println("done!\nScoring spectra... ");
		
		// iterate results
		int[] score_bins = new int[256];
		int[] ci00_bins = new int[querySize];
		int[] ci95_bins = new int[querySize];
		int[] ci99_bins = new int[querySize];
		SpectrumComparator specComp = initComparator(binWidth, baseWidth, trafo, measure, offset);
		long elapsed = System.currentTimeMillis();
		long delta = 0;
		long sum = 0;
		for (int i = 0; i < libSize; i++) {
			
			// display progress
			double avg = sum / (i * 1.0);
			double eta = avg * (libSize - i);
			int s = (int) ((eta / 1000) % 60);
			int m = (int) ((eta / (1000 * 60)) % 60);
			int h = (int) (eta / (1000 * 60 * 60));
			System.out.println("" + i +
					"\t" + (delta / 1000.0) +
					"\t" + ((long) avg / 1000.0) +
					"\t" + String.format("%d:%02d:%02d", h, m, s));
			
			// score single library spectrum against all query spectra
			HashMap<Double, Double> libPeaks = SixtyFourBitStringSupport.buildPeakMap(libMzs.get(i), libInts.get(i));
			specComp.prepare(this.getHighestPeaks(libPeaks, k));
			
			float libPreMz = libPreMzs.get(i);
			
			// iterate cached query spectra
			for (int j = 0; j < querySize; j++) {
				// calculate similarity
				int similarity = 0;
				if (Math.abs(libPreMz - queryPreMzs.get(j)) <= precTol) {
					HashMap<Double, Double> queryPeaks = SixtyFourBitStringSupport.buildPeakMap(queryMzs.get(j), queryInts.get(j));
					specComp.compareTo(this.getHighestPeaks(queryPeaks, k));
					// truncate similarity
					similarity = (int) (specComp.getSimilarity() * 255.0);
				}
				// increment score bin value corresponding to similarity
				score_bins[similarity]++;
				
				// increment spectrum bin value
				if (similarity >= ci95) {
					if (similarity >= ci99) {
						ci99_bins[j]++;
//					} else {
//						ci95_bins[j]++;
					}
					ci95_bins[j]++;
				} else {
					ci00_bins[j]++;
				}
			}
			
			// update progress
			delta = System.currentTimeMillis() - elapsed;
			sum += delta;
			elapsed = System.currentTimeMillis();
		}
		ps.close();
		
		System.out.print("...done!\nWriting histogram data to disk... ");
		File outFile = new File("out/" + outName + ".csv");
		FileWriter fw = new FileWriter(outFile);
		for (int i = 0; i < score_bins.length; i++) {
			fw.append("" + i + ";" + score_bins[i] + "\n");
		}
		fw.flush();
		fw.close();
		
		outFile = new File("out/" + outName + " Recovery.csv");
		fw = new FileWriter(outFile);
		for (int i = 0; i < querySize; i++) {
			fw.append("" + i + ";" + ci00_bins[i] + ";" + ci95_bins[i] + ";" + ci99_bins[i] + "\n");
		}
		fw.flush();
		fw.close();
		
		System.out.println("done!");
		
	}

	/**
	 * Convenience method to return a trimmed-down peak list containing only the
	 * specified number of highest-intensity peaks.
	 * @param peaks the peak list to trim
	 * @param k the number of highest-intensity peaks, a value of <code>0</code> will return the original peak list instead
	 * @return a trimmed-down peak list
	 */
	private Map<Double, Double> getHighestPeaks(Map<Double, Double> peaks, int k) {
		if (k == 0) {
    		return peaks;
    	} else {
    		Map<Double, Double> res = new HashMap<Double, Double>(peaks);
    		List<Double> sortedList = new ArrayList<Double>(res.values());
    		Collections.sort(sortedList);
    		Iterator<Double> iter = sortedList.listIterator();
    		while (res.size() > k) {
    			res.values().remove(iter.next());
    			iter.remove();
    		}
    		return res;
    	}
	}

	/**
	 * Convenience method for configuring a spectrum comparator.
	 * @param binWidth 
	 * @param offset 
	 * @return a spectrum comparator
	 */
	private SpectrumComparator initComparator(float binWidth, float baseWidth, Transformation trafo, int measure, int offset) {
		SpectrumComparator comp;

		// define vectorization method
		Vectorization vect = VectorizationFactory.createDirectBinning(binWidth, 0.0);
//		Vectorization vect = VectorizationFactory.createProfiling(binWidth, 0.0, 0, baseWidth);
		
		// define comparator algorithm
		switch (measure) {
		case 0:
			comp = new NormalizedDotProduct(vect, trafo);
			break;
		case 1:
			comp = new PearsonCorrelation(vect, trafo);
			break;
		default:
			comp = new CrossCorrelation(vect, trafo, binWidth, offset);
			break;
		}
		
		return comp;
	}
	
	@Test
	public void textRetrieveResults() throws SQLException, IOException {
		
		// init database connection
		Connection conn = DBManager.getInstance().getConnection();


		// BSA
		long libExp = 5L;
		long[] resultExps = new long[] {
//				31L, 83L, 84L, 85L, 86L, 88L, 89L, 90L, 393L,	// Precursor Mass Tolerance Tuning
				93L, 94L, 95L, 96L, 97L, 98L, 99L,				// Direct Binning Bin Width Tuning
				100L, 101L, 102L, 103L, 104L, 105L, 106L, 107L,	// Profiling Bin Width Tuning
				108L, 109L, 110L, 111L, 124L, 125L, 126L,		// Profiling Peak Width Tuning
				112L, 113L, 114L,								// Peak Picking Tuning
				115L, 116L,										// Transformation Tuning
				117L, 118L, 119L, 120L, 121L, 122L, 123L		// Measure Tuning
		};
		// E. coli
//		long libExp = 55L;
//		long[] resultExps = new long[] {
//				129L, 130L, 131L,								// Precursor Mass Tolerance Tuning
//				132L, 133L, 154L, 155L, 156L, 157L,				// Direct Binning Bin Width Tuning
//				134L, 135L, 136L,								// Profiling Bin Width Tuning
//				137L, 138L,										// Profiling Peak Width Tuning
//				140L, 141L, 142L,								// Peak Picking Tuning
//				143L, 144L,										// Transformation Tuning
//				145L, 146L, 147L, 148L, 149L					// Measure Tuning
//		};
		// Biogas
//		long libExp = 276L;	
//		long[] resultExps = new long[] {
//				279L, 280L, 281L, 282L, 283L,					// Precursor Mass Tolerance Tuning
//				284L, 285L, 286L, 287L,							// Direct Binning Bin Width Tuning
//				288L, 289L, 290L, 291L, 292L,					// Profiling Bin Width Tuning
//				293L, 294L,										// Profiling Peak Width Tuning
//				295L, 296L, 297L,								// Peak Picking Tuning
//				298L, 299L,										// Transformation Tuning
//				300L, 301L, 302L, 303L							// Measure Tuning
//		};
		
		int i = 1;
		for (long resultExp : resultExps) {
			System.out.println("\nProcessing experiment " + i + "/" + resultExps.length + "...");
			retrieveResults(libExp, resultExp, conn);
			i++;
		}
		System.out.println("\nProcessing done!");
		
		conn.close();
	}
	
	/**
	 * Retrieves spectral similarity results and encodes them in a score matrix
	 * image which is written to disk using the experiment's title as filename.
	 * @param libExp the database id of the experiment associated with the library spectra
	 * @param resultExp the database id of the experiment associated with the spectral similarity search
	 * @param conn the database connection
	 * @throws SQLException if a database error occurs
	 * @throws IOException if a file error occurs
	 */
	public void retrieveResults(long libExp, long resultExp, Connection conn) throws SQLException, IOException {
		
		String resultTitle = Experiment.findExperimentByID(resultExp, conn).getTitle();
		
		PreparedStatement ps;
		ResultSet rs;
		
		// fetch query annotations
		System.out.print("Fetching query annotations... ");
		
		ps = conn.prepareStatement("SELECT ss.searchspectrumid, p.sequence, s.precursor_charge, s.precursor_mz "
				+ "FROM searchspectrum ss "
				+ "INNER JOIN spectrum s ON ss.fk_spectrumid = s.spectrumid "
				+ "INNER JOIN spec2pep s2p ON ss.fk_spectrumid = s2p.fk_spectrumid "
				+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
				+ "WHERE ss.fk_experimentid = ? "
				+ "ORDER BY s.spectrumid");
		ps.setLong(1, resultExp);
		
		rs = ps.executeQuery();

		System.out.print("done!\nCaching query annotations... ");
		
		List<Long> queryIds = new ArrayList<Long>();
		List<Integer> queryHashes = new ArrayList<Integer>();
		List<Float> queryPreMzs = new ArrayList<Float>();
		while (rs.next()) {
			queryIds.add(rs.getLong("searchspectrumid"));
			queryHashes.add((rs.getString("sequence") + rs.getString("precursor_charge")).hashCode());
			queryPreMzs.add(rs.getFloat("precursor_mz"));
		}
		rs.close();
		ps.close();
		int querySize = queryIds.size();

		System.out.print("done!\nFetching library annotations... ");

		ps = conn.prepareStatement("SELECT ls.libspectrumid, p.sequence, s.precursor_charge, s.precursor_mz "
				+ "FROM libspectrum ls "
				+ "INNER JOIN spectrum s ON ls.fk_spectrumid = s.spectrumid "
				+ "INNER JOIN spec2pep s2p ON ls.fk_spectrumid = s2p.fk_spectrumid "
				+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
				+ "WHERE ls.fk_experimentid = ? "
				+ "GROUP BY ls.libspectrumid "
				+ "ORDER BY s.spectrumid");
		ps.setLong(1, libExp);
		
		rs = ps.executeQuery();

		System.out.print("done!\nCaching library annotations... ");
		
		List<Long> libIds = new ArrayList<Long>();
		List<Integer> libHashes = new ArrayList<Integer>();
		List<Float> libPreMzs = new ArrayList<Float>();
		while (rs.next()) {
			libIds.add(rs.getLong("libspectrumid"));
			libHashes.add((rs.getString("sequence") + rs.getString("precursor_charge")).hashCode());
			libPreMzs.add(rs.getFloat("precursor_mz"));
		}
		rs.close();
		ps.close();
		int libSize = libIds.size();

		System.out.print("done!\nCreating score matrix image... ");
		
		ps = conn.prepareStatement("SELECT ssh.fk_searchspectrumid, ssh.fk_libspectrumid, ssh.similarity "
				+ "FROM specsearchhit ssh "
				+ "INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid "
				+ "WHERE ss.fk_experimentid = ?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setLong(1, resultExp);
		
		rs = ps.executeQuery();
		
		BufferedImage img = new BufferedImage(libSize + 2, querySize + 2, BufferedImage.TYPE_INT_ARGB);
		while(rs.next()) {
			int y = 1 + queryIds.indexOf(rs.getLong("fk_searchspectrumid"));
			int x = 1 + libIds.indexOf(rs.getLong("fk_libspectrumid"));
			double similarity = rs.getDouble("similarity");
			int pixel = (int) (similarity * 255.0);
			pixel = ((pixel) << 16) + (255 << 24);
//			System.out.println("" + String.format("%2.4f", similarity) + "\t" + Integer.toHexString(pixel));
			img.setRGB(x, y, pixel);
		}
		rs.close();
		ps.close();

		// insert peptide annotations and precursor masses
		for (int i = 0; i < querySize; i++) {
			img.setRGB(0, i + 1, queryHashes.get(i));
			img.setRGB(libSize + 1, i + 1, Float.floatToIntBits(queryPreMzs.get(i)));
		}
		for (int i = 0; i < libSize; i++) {
			img.setRGB(i + 1, 0, libHashes.get(i));
			img.setRGB(i + 1, querySize + 1, Float.floatToIntBits(libPreMzs.get(i)));
		}
		
		System.out.print("done!\nWriting image to disk... ");
		
		// write image to file
		File outDir = new File("out/");
		if (!outDir.exists()) {
			outDir.mkdir();
		}
		ImageIO.write(img, "png", new File("out/" + resultTitle + ".png"));
		
		System.out.println("done!");
		
	}
	
}
