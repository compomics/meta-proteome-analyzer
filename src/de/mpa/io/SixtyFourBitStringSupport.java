package de.mpa.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

/**
 * Auxiliary class for 64-bit related methods.
 * 
 * @author Alex Behne
 */
public class SixtyFourBitStringSupport {

	/**
	 * Method to build a peak HashMap from double arrays representing m/z and intensity pairs.
	 * 
	 * @param mzArray The array of m/z values.
	 * @param inArray The array of intensity values.
	 * @return
	 */
	public static HashMap<Double, Double> buildPeakMap(double[] mzArray, double[] inArray) {
		HashMap<Double, Double> peaks = new HashMap<Double, Double>(mzArray.length);
		for (int i = 0; i < mzArray.length; i++) {
			peaks.put(mzArray[i], inArray[i]);
		}
		return peaks;
	}
	
	/**
	 * Method to build a peak HashMap from double and int arrays representing m/z and charge pairs.
	 * 
	 * @param mzArray The array of m/z values.
	 * @param chArray The array of charge values.
	 * @return
	 */
	public static HashMap<Double, Integer> buildChargeMap(double[] mzArray, int[] chArray) {
		HashMap<Double, Integer> peaks = new HashMap<Double, Integer>(mzArray.length);
		for (int i = 0; i < mzArray.length; i++) {
			if (chArray[i] != 0) {
				peaks.put(mzArray[i], chArray[i]);
			}
		}
		return peaks;
	}

	/**
	 * Method to decode a 64-bit String into an array of doubles.<br>
	 * Will use default byte order (big endian).
	 * 
	 * @param encodedString The encoded String.
	 * @return
	 */
	public static double[] decodeBase64StringToDoubles(String encodedString) {
		return decodeBase64StringToDoubles(encodedString, ByteOrder.BIG_ENDIAN);
	}
	
	/**
	 * Method to decode a 64-bit String into an array of doubles using a specified byte order.
	 * 
	 * @param encodedString The encoded string.
	 * @param byteOrder The byte order.
	 * @return double[]
	 */
	public static double[] decodeBase64StringToDoubles(String encodedString, ByteOrder byteOrder) {
		byte[] byteArray = Base64.decodeBase64(encodedString);

        ByteBuffer bb = ByteBuffer.wrap(byteArray);
        bb.order(byteOrder);

        double[] res = new double[byteArray.length/8];
        for (int i = 0; i < res.length; i++) {
        	res[i] = bb.getDouble(i*8);
        }
		return res;
	}

	/**
	 * Method to decode a 64-bit String into an array of doubles.<br>
	 * Will use default byte order (big endian).
	 * 
	 * @param encodedString The encoded String.
	 * @return
	 */
	public static int[] decodeBase64StringToInts(String encodedString) {
		return decodeBase64StringToInts(encodedString, ByteOrder.BIG_ENDIAN);
	}
	
	/**
	 * Method to decode a 64-bit String into an array of doubles using a specified byte order.
	 * 
	 * @param encodedString The encoded string.
	 * @param byteOrder The byte order.
	 * @return double[]
	 */
	public static int[] decodeBase64StringToInts(String encodedString, ByteOrder byteOrder) {
		byte[] byteArray = Base64.decodeBase64(encodedString);

        ByteBuffer bb = ByteBuffer.wrap(byteArray);
        bb.order(byteOrder);

        int[] res = new int[byteArray.length/4];
        for (int i = 0; i < res.length; i++) {
        	res[i] = bb.getInt(i*4);
        }
		return res;
	}
	
	public static String encodeDoublesToBase64String(Double[] doubles) {
		byte[] bytes = new byte[doubles.length*8];
		ByteBuffer bb = ByteBuffer.wrap(bytes);
	    for (Double dbl : doubles) {
	        bb.putDouble((dbl != null) ? dbl.doubleValue() : 0.0);
	    }
		return Base64.encodeBase64String(bytes);
	}
	
	public static String encodeIntsToBase64String(Integer[] ints) {
		byte[] bytes = new byte[ints.length*4];
		ByteBuffer bb = ByteBuffer.wrap(bytes);
	    for (Integer intgr : ints) {
	        bb.putInt((intgr != null) ? intgr.intValue() : 0);
	    }
		return Base64.encodeBase64String(bytes);
	}

}