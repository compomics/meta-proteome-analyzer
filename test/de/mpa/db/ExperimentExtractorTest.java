package de.mpa.db;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.DatabaseProject;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.ProjectAccessor;
import de.mpa.db.accessor.Property;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.extractor.SearchHitExtractor;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;

public class ExperimentExtractorTest {

	/**
	 * DB Connection.
	 */
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}

	@Test
	public void exportExperiments() throws Exception {
		//
		ProjectAccessor projectAcc = ProjectAccessor.findFromProjectID(31, conn);
		List<AbstractExperiment> experiments = new ArrayList<>();
		List<Property> projProps = Property.findAllPropertiesOfProject(projectAcc.getProjectid(), conn);

		AbstractProject project = new DatabaseProject(projectAcc, projProps, experiments);

		List<ExperimentAccessor> experimentAccs = ExperimentAccessor.findAllExperimentsOfProject(projectAcc.getProjectid(), conn);
		for (ExperimentAccessor experimentAcc : experimentAccs) {
			List<ExpProperty> expProps = ExpProperty.findAllPropertiesOfExperiment(experimentAcc.getExperimentid(), conn);
			experiments.add(new DatabaseExperiment(experimentAcc, expProps,	project));
		}
		
		// Iterate all experiments from the project.
		for (AbstractExperiment exp : experiments) {
			DatabaseExperiment experiment = (DatabaseExperiment) exp;
			// for (AbstractExperiment experiment : experiments) {
			DbSearchResult searchResult = new DbSearchResult(project.getTitle(), experiment.getTitle(), null);

			// gather search hits from remote database
			List<SearchHit> searchHits = SearchHitExtractor.findSearchHitsFromExperimentID(experiment.getID(), conn);
			
			// add search hits to result object
			System.out.println("Experiment: " + experiment.getTitle());
			System.out.println("Search Hits: " + searchHits.size());
			int counter = 0;
			for (SearchHit searchHit : searchHits) {
				experiment.addProteinSearchHit(searchResult, searchHit,	experiment.getID(), conn);
				counter++;
				if (counter % 1000 == 0) {
					System.out.print(counter + "... ");
				}
			}
			System.out.println("Search result building finshed.");
			
			// determine total spectral count
			searchResult.setTotalSpectrumCount(Searchspectrum.getSpectralCountFromExperimentID(experiment.getID(), conn));

			String pathname = "/scratch/results/kolmeder/" + experiment.getTitle() + ".mpa";
			Set<SpectrumMatch> spectrumMatches = ((ProteinHitList) searchResult.getProteinHitList()).getMatchSet();
			
			String prefix = pathname.substring(0, pathname.indexOf('.'));
			File mgfFile = new File(prefix + ".mgf");
			FileOutputStream fos = new FileOutputStream(mgfFile);
			long index = 0L;
			for (SpectrumMatch spectrumMatch : spectrumMatches) {
				spectrumMatch.setStartIndex(index);
				MascotGenericFile mgf = new SpectrumExtractor(conn).getSpectrumBySearchSpectrumID(spectrumMatch.getSearchSpectrumID());
				mgf.writeToStream(fos);
				index = mgfFile.length();
				spectrumMatch.setEndIndex(index);
				spectrumMatch.setTitle(mgf.getTitle());
			}
			fos.flush();
			fos.close();
			
			System.out.println("MGF dump finished.");
			// store as compressed binary object
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(new File(pathname)))));
			oos.writeObject(searchResult);
			oos.flush();
			oos.close();
			System.out.println("MPA dump finished.");
		}
		
	}

}
