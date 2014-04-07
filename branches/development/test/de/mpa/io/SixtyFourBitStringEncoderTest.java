package de.mpa.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

public class SixtyFourBitStringEncoderTest extends TestCase {

	private Set<Double> unencodedMZ;
	private Collection<Double> unencodedIn;

	@Before
	public void setUp() {
		try {
			MascotGenericFileReader mgfReader = new MascotGenericFileReader(new File("test/de/mpa/resources/Test_30.mgf"));
			MascotGenericFile mgf = mgfReader.getSpectrumFiles().get(0);
			unencodedMZ = mgf.getPeaks().keySet();
			unencodedIn = mgf.getPeaks().values();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test64bitEncoding() {
		byte[] byteArrayMZ = new byte[unencodedMZ.size()*8];
		byte[] byteArrayIn = new byte[unencodedIn.size()*8];

        ByteBuffer bbmz = ByteBuffer.wrap(byteArrayMZ);
        bbmz.order(ByteOrder.LITTLE_ENDIAN);
        for (Double mz : unencodedMZ) {
			bbmz.putDouble(mz);
		}
        ByteBuffer bbin = ByteBuffer.wrap(byteArrayIn);
        bbin.order(ByteOrder.LITTLE_ENDIAN);
        for (Double in : unencodedIn) {
			bbin.putDouble(in);
		}
        
        String base64mz = Base64.encodeBase64String(byteArrayMZ);
        String base64in = Base64.encodeBase64String(byteArrayIn);
        
        assertEquals("V7JjIxAxcEB9s82Nadx6QHYaaak8sXhA9S1zuiwUdEBLWYY41sF3QLNeDOVEQH1AlPsdigJeZ0BKRs7CHqN2QN8Vwf9W4WRAvRjKiXaDaEC2EOSghDtuQL06x4Ds/WhAoMN8eQF/dEBwlLw6x4B2QA==",
        		base64mz);
        assertEquals("Fli2xevQ+z+UYLvFDan7P1cOiGsQAfw/jRgsi3hh/D/3FbVF41r8P4Zr/xTY7fs/XZ5zXDIT+z8pRvGFJI75P9AJoYMu01NAfe/KidEu+z/BKWLHNfT7P7IW+rvSDfk/GN72ThboC0CKdD+nID/8Pw==",
        		base64in);
	}
}
