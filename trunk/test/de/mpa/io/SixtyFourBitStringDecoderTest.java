package de.mpa.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

public class SixtyFourBitStringDecoderTest extends TestCase {

	private String encodedMZ, encodedInt;
	private int step = 8;	// 8 bytes = 64-bit double

	@Before
	public void setUp() {
//		 encodedMZ = "uycPC7UjYED6fmq8dGZrQFioNc27gnNAtoR80LMDekC7RPXWQCqA" +
//					 "QFA25QqvMoNA71UrEz6qh0CCqPsA5JuIQMZq8/+qRVVAwjQMHxHk" +
//					 "ZUDzyB8MPJN0QDgteNFX83hANKK0Nzh6gEAF+kSepKKCQA5Pr5Sl" +
//					 "ZYZA8pcW9UmEUUCto6oJooNlQOik942v0nBAE0TdByDTd0D9h/Tb" +
//					 "VzR/QBIxJZJo0oFAFqQZi6ZZhUBZ+tAF9cVcQJWfVPt0h21AuHU3" +
//					 "T/VTckAWGLK61ZR7QLTlXIorlH1AJSNnYY9CgkD9n8N8eWJjQLdF" +
//					 "mQ0yY2VAsaIG0zBDdkBWmpSC7kR7QCbHndJBuoBAHVVNEHXigUCP" +
//					 "pQ9dUEddQDHrxVBOo2ZAdOrKZ/kCckDw3Hu45JN8QJjdk4eF4oBA" +
//					 "cQM+P4x1g0CQSUbOwt9gQFbxRuYRwnBAe4MvTCbicUCHokCfSNN7" +
//					 "QG+e6pCb1H9AfbPNjenCY0A2k2+2ueNqQAT/W8mOwnZAXvQVpBli" +
//					 "fEDPSe8bXyKBQADjGTT0BV1A6DBfXoDGbEAsms5OhuJyQOcYkL1e" +
//					 "A3xAgEqVKHvCVUAf14aKcQVtQCtNSkG3InRAhugQOBJCUUBan3JM" +
//					 "FsRRQAPOUrKcQVNADXGsi9uFVUA/O+C6YoNWQKLxRBDnxFhAF7zo" +
//					 "K0gFWUBAGHjuPUJZQHhi1ouhQFpAsMka9RBFW0Cztb5IaEVcQDfE" +
//					 "eM2rwV1AFLNeDOVCXkCRYRVvZAJfQJBrQ8U4Q19Af4eiQJ+GX0Ab" +
//					 "TMPwEaNgQGq8dJMYAmFAaFw4EJJBYUCfPCzUmmFhQIIclDDTgWFA" +
//					 "DaZh+IiiYUBhMlUwKgRiQLO1vkhoRGJAArwFEhSiYkBn7bYLzeFi" +
//					 "QMxdS8gHBGRAkQpjC0GCZkCUh4Va08JmQLfu5qkOI2dA9Ik8Sbp9" +
//					 "Z0Bh4Ln3cOZnQLecS3FVQ2hA6uxkcJShaEAIclDCTMRoQM6qz9VW" +
//					 "42hA6DBfXoBkakAbgXhdv0ZrQHe+nxovxGtA2PULdsMDbEDNr+YA" +
//					 "wUNsQA5nfjUHRG5ALUMc6+KjbkAF3V7SGMNuQFmGONbFQ29AcLa5" +
//					 "MT3kb0A9CtejcDJwQCnQJ/IkQnBAJsed0sGScEBNhA1PbyWbQA==";
		 encodedMZ = "V7JjIxAxcEB9s82Nadx6QHYaaak8sXhA9S1zuiwUdEBLWYY41sF3QLNeDOVEQH1AlPsdigJeZ0BKRs7CHqN2QN8Vwf9W4WRAvRjKiXaDaEC2EOSghDtuQL06x4Ds/WhAoMN8eQF/dEBwlLw6x4B2QA==";
		
//		encodedInt = "16NwPQpHUkB7FK5H4XotQK5H4XoUrj1AzczMzMxMMkAzMzMzM3M1" +
//					 "QOxRuB6Fy1VAbjSAt0CC4j9xPQrXo3D5P9ejcD0K1y9A4XoUrkfh" +
//					 "JkDXo3A9ClcuQIPAyqFFNhhAH4XrUbieGEC4HoXrUfgxQBKDwMqh" +
//					 "ReI/CtejcD0KGEB/arx0k5gjQMP1KFyPQiZAWmQ7308NF0DhehSu" +
//					 "R2EXQHE9CtejsCBAlrIMcayL4T9kO99PjRcVQDEIrBxaJCJA0SLb" +
//					 "+X6qIkCmm8QgsHIHQB1aZDvfTwFA0SLb+X5qCkDhehSuR2ETQJqZ" +
//					 "mZmZmRtArBxaZDtfEEDl0CLb+X4GQKrx0k1iEPA/ZmZmZmZm+j9C" +
//					 "YOXQItsNQFyPwvUoXBVAWmQ730+NDEBGtvP91Hj5P7mNBvAWSO4/" +
//					 "fdCzWfW55j/pJjEIrBwIQIGVQ4ts5xRACKwcWmQ7BkDAWyBB8WPs" +
//					 "P9ejcD0K1+0/Rrbz/dR4B0D6fmq8dBMSQBBYObTIdvg/BcWPMXct" +
//					 "6z+J0t7gC5PtP4cW2c73UwVAiUFg5dAiD0A730+Nl274Pylcj8L1" +
//					 "KOo/PzVeukkMA0BOYhBYObQOQHsUrkfhevY/O99PjZdu8j9xPQrX" +
//					 "o3DzP9Xnaiv2l90/RIts5/up+z/mP6Tfvg7UP05iEFg5tOA/m+Yd" +
//					 "p+hI6j9kO99PjZf8P5huEoPAyuU/4XoUrkfh1j++nxov3ST4P3e+" +
//					 "nxov3QFAVg4tsp3v/T9NFYxK6gTYP0aU9gZfmNg/1XjpJjEI+D+r" +
//					 "z9VW7C/tP7Kd76fGS/c/uB6F61G44D8cfGEyVTDaP6abxCCwcvA/" +
//					 "nu+nxks39z9MN4lBYOX6P9NNYhBYOfY/K4cW2c738T/Jdr6fGi/x" +
//					 "P90kBoGVQwBANV66SQwC9z9QjZduEoP6P0a28/3UeP8/yJi7lpAP" +
//					 "4D97FK5H4Xr4P0a28/3UeAJAtMh2vp8a9T/TTWIQWDn4P8HKoUW2" +
//					 "8wlAx0s3iUFgA0BzaJHtfD8DQJtVn6ut2Os/O99PjZduB0Dy0k1i" +
//					 "EFjxP4PAyqFFtgtAYOXQItv5+j9mZmZmZmYBQLbz/dR46fw/9ihc" +
//					 "j8L1A0DjpZvEILAHQCuHFtnO9wFAoBov3SQG+T/jpZvEILD4Pw==";
		encodedInt = "Fli2xevQ+z+UYLvFDan7P1cOiGsQAfw/jRgsi3hh/D/3FbVF41r8P4Zr/xTY7fs/XZ5zXDIT+z8pRvGFJI75P9AJoYMu01NAfe/KidEu+z/BKWLHNfT7P7IW+rvSDfk/GN72ThboC0CKdD+nID/8Pw==";
	}

	@Test
	public void test64bitDecoding() {
		byte[] byteArrayMZ  = Base64.decodeBase64(encodedMZ);
		byte[] byteArrayInt = Base64.decodeBase64(encodedInt);
		
		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>();

        ByteBuffer bbmz = ByteBuffer.wrap(byteArrayMZ);
        bbmz.order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer bbint = ByteBuffer.wrap(byteArrayInt);
        bbint.order(ByteOrder.LITTLE_ENDIAN);
        for (int indexOut = 0; indexOut < byteArrayMZ.length; indexOut += step) {
        	peaks.put(bbmz.getDouble(indexOut), bbint.getDouble(indexOut));
        }
        
//        assertEquals(69.032362, peaks.firstKey());
//        assertEquals(1.152, peaks.firstEntry().getValue());
//        
//        assertEquals(1737.3587, peaks.lastKey());
//        assertEquals(1.543, peaks.lastEntry().getValue());
        
        assertEquals(167.04187, peaks.firstKey());
        assertEquals(79.299714, peaks.firstEntry().getValue());
        
        assertEquals(468.01682, peaks.lastKey());
        assertEquals(1.7455674, peaks.lastEntry().getValue());
	}
}
