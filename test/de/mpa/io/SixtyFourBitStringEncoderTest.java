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
	public void test64bitDecoding() {
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
        
        System.out.println(base64mz);
        System.out.println(base64in);
	}
}
