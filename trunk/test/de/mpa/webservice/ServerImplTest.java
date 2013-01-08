package de.mpa.webservice;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.db.DBManager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class ServerImplTest extends TestCase {

	@Test
	public void testRepairSpectra() {
		try {
			DBManager dbManager = DBManager.getInstance();
			Connection conn = dbManager.getConnection();
			
			File file = new File(getClass().getClassLoader().getResource("FewProteins2.mgf").getPath());
			
			File newFile = new File(file.getParent() + File.separator + "FewProteins2_fixed.mgf");
			ServerImpl.repairSpectra(file, conn, newFile.getPath());
			
			MascotGenericFileReader reader = new MascotGenericFileReader(newFile);
			
			MascotGenericFile mgf = reader.getSpectrumFiles().get(2);
			
			ArrayList<Double> masses = new ArrayList<Double>(mgf.getPeaks().keySet());
			Collections.sort(masses);
			assertEquals(848.5350, masses.get(masses.size() - 1), 1e-4);
			
			assertEquals(305023L, mgf.getSpectrumID().longValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
