package de.mpa.db.job.instances;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpa.algorithms.Interval;
import de.mpa.algorithms.similarity.CrossCorrelation;
import de.mpa.algorithms.similarity.EuclideanDistance;
import de.mpa.algorithms.similarity.NormalizedDotProduct;
import de.mpa.algorithms.similarity.PearsonCorrelation;
import de.mpa.algorithms.similarity.SpectrumComparator;
import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.Vectorization;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.db.job.Job;
import de.mpa.db.job.JobStatus;
import de.mpa.io.MascotGenericFile;

/**
 * Job implementation for spectral similarity searching.
 * 
 * @author A. Behne
 */
public class SpecSimJob extends Job {

	/**
	 * The list of spectrum files.
	 */
	private List<MascotGenericFile> mgfList;
	
	/**
	 * The spectral similarity search settings reference.
	 */
	private SpecSimSettings settings;
	
	/**
	 * The list of spectrum-spectrum matches.
	 */
	private List<SpectrumSpectrumMatch> ssmList;

	/**
	 * Constructs a spectral similarity search job from the specified list of
	 * spetrum files and search settings.
	 * @param mgfList the list of spectrum files
	 * @param settings the spectral similarity search settings
	 */
	public SpecSimJob(List<MascotGenericFile> mgfList, SpecSimSettings settings) {
		this.mgfList = mgfList;
		this.settings = settings;
		setDescription("SPECTRAL SIMILARITY SEARCH");
	}

	@Override
	public void run() {
		setStatus(JobStatus.RUNNING);
		
		// store list of results in HashMap (with spectrum title as key)
		ssmList = new ArrayList<SpectrumSpectrumMatch>();
		
		List<Interval> intervals = this.buildMzIntervals();

		try {
			// extract list of candidates
			DBManager manager = DBManager.getInstance();
			SpectrumExtractor specEx = new SpectrumExtractor(manager.getConnection());
			List<SpectralSearchCandidate> candidates = 
					specEx.getCandidatesFromExperiment(intervals, settings.getExperimentID());
//			System.out.flush();
//			System.out.print("Fetching candidates... ");
//			List<SpectralSearchCandidate> candidates = 
//					specEx.getCandidatesFromExperiment(settings.getExperimentID());
//			System.out.flush();
//			System.out.println("done.\nIterating query spectra... ");
//			
//			int total = mgfList.size();
//			int i = 0;
			
			// iterate query spectra to determine similarity scores
			for (MascotGenericFile mgfQuery : mgfList) {
				String title = mgfQuery.getTitle().trim();
				long searchspectrumID = MapContainer.SpectrumTitle2IdMap.get(title);
				
				// Spectrum comparator method
				SpectrumComparator specComp = getComparatorMethod(settings);
				
				// Comparison preparation
				specComp.prepare(mgfQuery.getHighestPeaks(settings.getPickCount()));
				
				// iterate candidates
				for (SpectralSearchCandidate candidate : candidates) {
					// (re-)check precursor tolerance criterion to determine proper candidates
					if (Math.abs(mgfQuery.getPrecursorMZ() - candidate.getPrecursorMz()) < settings.getTolMz()) {
						// TODO: redundancy check in candidates (e.g. same spectrum from multiple peptide associations)
						// Score query and library spectra
						specComp.compareTo(candidate.getPeaks());
						double score = specComp.getSimilarity();
						
						// store result if score is above specified threshold
						if (score >= settings.getThreshScore()) {
							ssmList.add(new SpectrumSpectrumMatch(searchspectrumID, candidate.getLibpectrumID(), score));
						}
					}
				}
				specComp.getVectorization().cleanup();
				
//				if ((i%100) == 0) {
//					System.out.println("" + i + " / " + total);
//				}
//				i++;
				
				// TODO: re-implement progress event handling
//				pSupport.firePropertyChange("progressmade", 0, 1);
			}
			System.out.println("... done.");
			done();
		} catch (SQLException e) {
			setError(e);
		}
	}
	
	/**
	 * Method to build an interval tree from a list of precursor m/z's.
	 * @return
	 */
	private List<Interval> buildMzIntervals() {
		// iterate query spectra to gather precursor m/z values
		ArrayList<Double> precursorMZs = new ArrayList<Double>(mgfList.size());
		for (MascotGenericFile mgf : mgfList) {
			precursorMZs.add(mgf.getPrecursorMZ());
		}
		Collections.sort(precursorMZs);
		// build list of precursor m/z intervals using sorted list
		ArrayList<Interval> intervals = new ArrayList<Interval>();
		Interval current = null;
		for (double precursorMz : precursorMZs) {
			if (current == null) {	// first interval
				current = new Interval(((precursorMz - settings.getTolMz()) < 0.0) ?
						0.0 : precursorMz - settings.getTolMz(), precursorMz + settings.getTolMz());
				intervals.add(current);
			} else {
				// if left border of new interval intersects current interval extend the latter
				if ((precursorMz - settings.getTolMz()) < current.getRightBorder()) {
					current.setRightBorder(precursorMz + settings.getTolMz());
				} else {	// generate new interval
					current = new Interval(precursorMz - settings.getTolMz(), precursorMz + settings.getTolMz());
					intervals.add(current);
				}
			}
		}
		return intervals;
	}

	/**
	 * Returns the vectorization method
	 * 
	 * @param index The vectorization method index.
	 * @param binWidth
	 * @param binShift
	 * @param profileIndex
	 * @param baseWidth
	 * @return
	 */
	private Vectorization getVectorizationMethod(SpecSimSettings settings) {
		Vectorization vect = null;
		switch (settings.getVectIndex()) {
		case 0:
			vect = VectorizationFactory.createPeakMatching(settings.getBinWidth());
			break;
		case 1:
			vect = VectorizationFactory.createDirectBinning(settings.getBinWidth(), settings.getBinShift());
			break;
		case 2:
			vect = VectorizationFactory.createProfiling(settings.getBinWidth(), settings.getBinShift(), 
					settings.getProfileIndex(), settings.getBaseWidth());
			break;
		}
		return vect;
	}
	
	/**
	 * Returns the transformation method.
	 * @param index The specified index.
	 * @return The transformation method.
	 */
	private Transformation getTransformationMethod(SpecSimSettings settings) {
		Transformation trafo = null;
		switch (settings.getTrafoIndex()) {
		case 0:
			trafo = Transformation.NONE;
			break;
		case 1:
			trafo = Transformation.SQRT;
			break;
		case 2:
			trafo = Transformation.LOG;
			break;
		}
		return trafo;
	}
	
	/**
	 * Returns the spectrum comparator method.
	 * @param index The spectrum comparator method index.
	 * @param vect The vectorization method.
	 * @param trafo The transformation method.
	 * @param xCorrOffset The cross-correlation offset.
	 * @return The spectrum comparator method.
	 */
	private SpectrumComparator getComparatorMethod(SpecSimSettings settings) { //int index, Vectorization vect, Transformation trafo, int xCorrOffset) {
		SpectrumComparator specComp = null;
		Vectorization vect = getVectorizationMethod(settings);
		Transformation trafo = getTransformationMethod(settings);
		switch (settings.getCompIndex()) {
		case 0:
			specComp = new EuclideanDistance(vect, trafo);
			break;
		case 1:
			specComp = new NormalizedDotProduct(vect, trafo);
			break;
		case 2:
			specComp = new PearsonCorrelation(vect, trafo);
			break;
		case 3:
			specComp = new CrossCorrelation(vect, trafo, settings.getBinWidth(), settings.getXCorrOffset());
			break;
		}
		
		return specComp;
	}
	
	/**
	 * Returns the list containing found spectrum-to-spectrum matches.
	 * @return the SSM list
	 */
	public List<SpectrumSpectrumMatch> getResults() {
		return ssmList;
	}
	
}
