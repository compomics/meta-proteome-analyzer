package de.mpa.job.instances;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpa.algorithms.Interval;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.job.Job;

public class SpecSimJob extends Job {

	private List<MascotGenericFile> mgfList;
	private SpecSimSettings settings;
	private List<SpectrumSpectrumMatch> ssmList;

	public SpecSimJob(List<MascotGenericFile> mgfList, SpecSimSettings settings) {
		this.mgfList = mgfList;
		this.settings = settings;
	}

	@Override
	public void execute() {
		setDescription("SPECTRAL SIMILARITY SEARCH");
		
		// store list of results in HashMap (with spectrum title as key)
		ssmList = new ArrayList<SpectrumSpectrumMatch>();
		
		List<Interval> intervals = buildMzIntervals();

		try {
			// extract list of candidates
			DBManager manager = DBManager.getInstance();
			SpectrumExtractor specEx = new SpectrumExtractor(manager.getConnection());
			List<SpectralSearchCandidate> candidates = 
				specEx.getCandidatesFromExperiment(intervals, settings.getExperimentID());
			
			// iterate query spectra to determine similarity scores
			for (MascotGenericFile mgfQuery : mgfList) {
				
				long searchspectrumID = MapContainer.FileName2IdMap.get(mgfQuery.getTitle());
				
				// prepare query spectrum for similarity comparison with candidate spectra,
				// e.g. vectorize peaks, calculate auto-correlation, etc.
				settings.getSpecComp().prepare(mgfQuery.getHighestPeaks(settings.getPickCount()));
				
				// iterate candidates
				for (SpectralSearchCandidate candidate : candidates) {
					// re-check precursor tolerance criterion to determine proper candidates
					if (Math.abs(mgfQuery.getPrecursorMZ() - candidate.getPrecursorMz()) < settings.getTolMz()) {
						// TODO: redundancy check in candidates (e.g. same spectrum from multiple peptide associations)
						// score query and library spectra
						settings.getSpecComp().compareTo(candidate.getPeaks());
						double score = settings.getSpecComp().getSimilarity();
						
						// store result if score is above specified threshold
						if (score >= settings.getThreshScore()) {
							ssmList.add(new SpectrumSpectrumMatch(searchspectrumID, candidate.getLibpectrumID(), score));
						}
					}
				}
				settings.getSpecComp().cleanup();
				// TODO: re-implement progress event handling
//				pSupport.firePropertyChange("progressmade", 0, 1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e.getCause());
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
	 * Returns the list containing found spectrum-to-spectrum matches.
	 * @return the SSM list
	 */
	public List<SpectrumSpectrumMatch> getResults() {
		return ssmList;
	}
	
}
