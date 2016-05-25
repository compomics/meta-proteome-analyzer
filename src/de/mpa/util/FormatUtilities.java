package de.mpa.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import gnu.trove.list.array.TCharArrayList;

/**
 * This class provides several utility methods, e.g. for string formatting. 
 * @author Thilo Muth
 *
 */
public class FormatUtilities {

	/**
	 * CSV FIELD SEPARATOR
	 */
	public final static String CSVFIELDSEPARATOR = ";";
	
	/**
	 * Check whether an input string is numeric.
	 * @param string Input string
	 * @return Condition if numeric or not
	 */
	public static boolean isNumeric(String string) {
		Pattern pattern = Pattern.compile( "\\d+" );

		Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	} 
	
	/**
	 * Checks whether an input string contains a white space character.
	 * @param string Input string
	 * @return Condition whether it contains a white space or not
	 */
	public static boolean containsWhiteSpace(final String string){
	    if(string != null){
	        for(int i = 0; i < string.length(); i++){
	            if(Character.isWhitespace(string.charAt(i))){
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	/**
	 * generate a string representation of the given double with defined precision
	 * @param aValue value
	 * @param aPrecision number of fractional digits (>0 -> at least one digit, <0 exact number of digits)
	 * @return string with numeric value
	 */
	public static StringBuffer doubleToString(double aValue, int aPrecision) {
		StringBuffer tBack = new StringBuffer();
		if (Double.isNaN(aValue)) {
			tBack.append("NaN");
		} else if (Double.isInfinite(aValue)) {
			tBack.append("Inf");
		} else {
			StringBuffer tFomatString = new StringBuffer();
			double tFactor;
			int tFracs, tAbsPrec;
			DecimalFormat tFormat;
			tAbsPrec = Math.abs(aPrecision);
			tFracs = tAbsPrec;
			tFomatString.append("0");
			if (tFracs>0) {
				tFomatString.append(".0");
				tFracs--;
			} else {
				tFracs=0;
			}
			if (aPrecision > 0) {
				appendToString(tFomatString, '#', tFracs);
			} else {
				appendToString(tFomatString, '0', tFracs);
			}
			tFormat = new DecimalFormat(tFomatString.toString());
			tFactor = Math.pow(10.0, tAbsPrec+1);
			aValue = Math.round(aValue*tFactor)/tFactor;
			tBack.append(tFormat.format(aValue));
		}
		return tBack;
	}
	
	/**
	 * Removes the last character from a string.
	 * @param str Input string
	 * @return String with last character removed.
	 */
	public static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

	/**
	 * appends c n times to buf, that may be newly created, if null
	 * @param aStrBuf
	 * @param aChar
	 * @param aCount
	 * @return
	 */
	public static StringBuffer appendToString(StringBuffer aStrBuf, char aChar, int aCount) {
		if (aStrBuf==null) {
			aStrBuf = new StringBuffer();
		}
		while (aCount>0) {
			aStrBuf.append(aChar);
			aCount--;
		}
		return aStrBuf;
	}

	/**
	 * parse a double from the given string
	 * @param aString
	 */
	public static double stringToDouble(String aString) {
		double tBack;
		DecimalFormat tFormat;
		tFormat = new DecimalFormat("0.##########################");
		try {
			Number tNumber;
			tNumber = tFormat.parse(aString);
			tBack = tNumber.doubleValue();
		} catch (ParseException e) {
			tBack = Double.NaN;
		}
		return tBack;
	}


	/**
	 * Rounds up to the nearest decimal with a predefined precision.
	 * @param d The double number
	 * @param decimalPlaces The number of decimals.
	 * @return The rounded double
	 */
	public static double roundBigDecimalDouble(double d, int decimalPlaces) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	/**
     * Rounds a double value to the wanted number of decimal places.
     *
     * @param d the double to round of
     * @param places number of decimal places wanted
     * @return double - the new double
     */
    public static double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10, (double) places);
    }


	/**
	 * This method parses an integer value from the given string
	 * @param str
	 */
	public static int stringToInteger(String str) {
		int tBack;
		try {
			tBack = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			tBack = 0;
		}
		return tBack;
	}
	
	/**
	 * This method reverses a string.
	 * @param string String to be reversed.
	 * @return The reversed String.
	 */
	public static String reverseString(String str) {
		return new StringBuffer(str).reverse().toString();
	}
	
	/**
	 * boolean to string
	 * @param aValue
	 * @return
	 */
	public static String booleanToString(boolean aValue) {
		return aValue?"1":"0";
	}

	/**
	 * parse a version string in an array of integer values
	 * @param aStr
	 * @return
	 */
	public static int[] stringToVersionArray(String aStr) {
		int[] tBack;
		int i;
		if (aStr!=null && aStr.length()>0) {
			ArrayList<Integer> tElements = new ArrayList<Integer>();
			StringTokenizer tTokenizer = new StringTokenizer(aStr, ".", false);
			while (tTokenizer.hasMoreTokens()) {
				String tStr = tTokenizer.nextToken();
				tElements.add(new Integer(stringToInteger(tStr)));
			}
			tBack = new int[tElements.size()];
			for(i=0; i<tBack.length; i++) {
				tBack[i]=tElements.get(i).intValue();
			}
		} else {
			tBack = new int[0];
		}
		return tBack;
	}

	/**
	 * compare two version strings
	 * @param aStr1
	 * @param aStr2
	 * @return 0 if equal, -1 if str1&lt;str2, +1 if str1&gt;str2
	 */
	public static int compareVersionStrings(String aStr1, String aStr2) {
		int[] tVersion1, tVersion2;
		int i;
		int tBack = 0;
		tVersion1 = stringToVersionArray(aStr1);
		tVersion2 = stringToVersionArray(aStr2);
		for(i=0; ; i++) {
			if (i<tVersion1.length && i<tVersion2.length) {
				if (tVersion1[i] > tVersion2[i]) {
					tBack = 1;
					break;
				} else if (tVersion1[i] < tVersion2[i]) {
					tBack = -1;
					break;
				}
			} else if (i<tVersion1.length && i>=tVersion2.length) {
				tBack = 1;
				break;
			} else if (i>=tVersion1.length && i<tVersion2.length) {
				tBack = -1;
				break;
			} else {
				break;
			}
		}
		return tBack;
	}

	/**
	 *
	 * @param aValue
	 * @return
	 */
	public static boolean isEven(int aValue) {
		return (aValue & 1) == 0;
	}

	/**
	 *
	 * @param aValue
	 * @return
	 */
	public static boolean isOdd(int aValue) {
		return !isEven(aValue);
	}

	/**
	 * is aValue between aLeft and aRight?
	 * @param aVal
	 * @param aLeft
	 * @param aRight
	 * @return
	 */
	public static boolean between(double aVal, double aLeft, double aRight) {
		return (aLeft <= aVal) && (aVal <= aRight);
	}

	/**
	 * is aValue between aLeft and aRight?
	 * @param aVal
	 * @param aLeft
	 * @param aRight
	 * @return
	 */
	public static boolean between(int aVal, int aLeft, int aRight) {
		return (aLeft <= aVal) && (aVal <= aRight);
	}

	/**
	 * scale up/down to next integer mult. of the given modulo
	 * @param aUpwards
	 * @param aValue
	 * @param aModulo
	 * @return
	 */
	public static double findNextValue(boolean aUpwards, double aValue, double aModulo) {
		// scale dblval to next integer mult. of dblmod
		// blnHigher  true -> upwards, falst -> downward
		double tCnt;

		tCnt = ((int)(aValue / aModulo));
		if ((tCnt * aModulo) != aValue) {
			if (aUpwards) {
				while ((tCnt * aModulo) < aValue) {
					tCnt++;
				}
				aValue = tCnt * aModulo;
			} else {
				while ((tCnt * aModulo) > aValue) {
					tCnt--;
				}
				aValue = tCnt * aModulo;
			}
		}
		return aValue;
	}

	/**
	 * the square of x
	 * @param aX
	 * @return the square of x
	 */
	public static double Square(double aX) {
		return aX*aX;
	}

	/**
	 * @param aX
	 * @return the log2 of x
	 */
	public static double log2(double aX) {
		return Math.log(aX) / Math.log(2.0);
	}

	/**
	 * @param aX
	 * @return the loge of x
	 */
	public static double loge(double aX) {
		return Math.log(aX);
	}

	/**
	 * @param aX
	 * @return the log10 of x
	 */
	public static double log10(double aX) {
		return Math.log(aX) / Math.log(10.0);
	}

	/**
	 * random number between xlow and xhigh, both including
	 * @param aRnd
	 * @param aLow
	 * @param aHigh
	 * @return
	 */
	public static int RandomIntegerRange(Random aRnd, int aLow, int aHigh) {
		return aRnd.nextInt((aHigh - aLow + 1)  + aLow);
	}

	/**
	 * convert string to a valid filename
	 * @param str
	 * @return
	 */
	public static String makeValidFileName(String str) {
		StringBuffer tBack;
		int i, l;
		char tAllowedChars[];
		char tLastChar;
		HashSet<Character> tAllowed = new HashSet<Character>();
		char tChars[];
		tAllowedChars = "�����ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.,".toCharArray();
		for (i=0; i< tAllowedChars.length; i++) {
			tAllowed.add(new Character(tAllowedChars[i]));
		}
		tChars = str.toUpperCase().toCharArray();
		l = tChars.length;
		tLastChar = '\0';
		tBack = new StringBuffer();
		for(i=0; i<l; i++) {
			if (tAllowed.contains(new Character(tChars[i]))) {
				tLastChar = str.charAt(i);
				tBack.append(tLastChar);
			} else {
				if (tLastChar != '_') {
					tLastChar = '_';
					tBack.append(tLastChar);
				}
			}
		}
		return tBack.toString();
	}

	/**
	 * center the given component in the visible screen
	 * @param aComponent
	 */
	public static void centerInScreen(Component aComponent) {
		Dimension tDim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (tDim.width - aComponent.getSize().width) / 2;
		int y = (tDim.height - aComponent.getSize().height) / 2;
		aComponent.setLocation(x, y);
	}

	/**
	 * center the given component in the given parent
	 * @param aComponent
	 * @param aParent
	 */
	public static void centerInComponent(Component aComponent, Component aParent) {
		Point tPoint = aParent.getLocation();
		Dimension tDimComp = aComponent.getSize();
		Dimension tDimParent = aParent.getSize();
		int x = (int)tPoint.getX() + (tDimParent.width - tDimComp.width) / 2;
		int y = (int)tPoint.getY() + (tDimParent.height - tDimComp.height) / 2;
		aComponent.setLocation(x, y);
	}

	/**
	 * removes all ; and " from the given String
	 * @param aString
	 * @return
	 */
	public static String makeCsvCompatibleString(String aString) {
		aString = aString.replaceAll("\"", "");
		aString = aString.replaceAll(CSVFIELDSEPARATOR, "");
		return aString;
	}

	/**
	 * splits aString by aSeparator into a list
	 * @param aString
	 * @param aSeparator
	 * @return
	 */
	public static List<String> split2list(String aString, String aSeparator)
	{
		List<String> tBack = new ArrayList<String>();

		if (aString != null)
		{
			if (aString.indexOf(aSeparator) != -1)
			{
				int tPos, tLen, tLenSep;
				ArrayList<Integer> tListSepPos = new ArrayList<Integer>();
				Iterator<Integer> tIterSepPos;

				tLen = aString.length();
				tLenSep = aSeparator.length();
				tPos=0;
				while (tPos<tLen) {
					if ((tPos + tLenSep)<=tLen && aString.substring(tPos, tPos + tLenSep).equals(aSeparator)) {
						tListSepPos.add(new Integer(tPos));
						tPos+=tLenSep;
					} else {
						tPos++;
					}
				}

				tPos = 0;
				for (tIterSepPos=tListSepPos.iterator(); tIterSepPos.hasNext(); ) {
					int tPosEnd;
					tPosEnd = tIterSepPos.next().intValue();
					tBack.add(aString.substring(tPos, tPosEnd));
					tPos = tPosEnd + tLenSep;
				}
				if (tPos<=tLen) {
					tBack.add(aString.substring(tPos));
				}

			}
			else
			{
				tBack.add(aString);
			}
		}
		return tBack;
	}

	/**
	 * joins an array of Strings, separated by aSep
	 * @param aStrs
	 * @param aSep
	 * @param aFromIndex
	 * @param aToIndex
	 * @return
	 */
	public static String joinStringArray(String[] aStrs, String aSep, int aFromIndex, int aToIndex)
	{
		StringBuffer tStr = new StringBuffer("");
		if (aStrs!=null && aStrs.length>0)
		{
			if (aFromIndex<0)
			{
				aFromIndex=0;
			}
			if (aToIndex<0)
			{
				aToIndex=aStrs.length;
			}
			for (int i=aFromIndex; (i<=aToIndex) && (i<aStrs.length); i++)
			{
				if (i>aFromIndex)
				{
					tStr.append(aSep);
				}
				tStr.append(aStrs[i]);
			}
		}
		return tStr.toString();
	}

	/**
	 * joins an array of Strings, separated by aSep
	 * @param aStrs
	 * @param aSep
	 * @return
	 */
	public static String joinStringArray(String[] aStrs, String aSep)
	{
		return joinStringArray(aStrs, aSep, -1, -1);
	}

	/**
	 * joins an ArrayList of String, separated by aSep
	 * @param aList  list of strings
	 * @param aSep   separator
	 * @return
	 */
	public static String joinStringArrayList(List<String> aList, String aSep)
	{
		StringBuffer tStr = new StringBuffer("");
		if (aList!=null && aList.size()>0)
		{
			for (int i=0; i<aList.size(); i++)
			{
				if (i>0)
				{
					tStr.append(aSep);
				}
				tStr.append((String) aList.get(i));
			}
		}
		return tStr.toString();
	}

	/**
	 * sleep for the given time (in milliseconds)
	 * @param aMillis
	 */
	public static void sleep(long aMillis) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// never mind...
		}
	}

	/**
	 * 
	 * @param aFilePath
	 * @param aWithExt true, if extension is wanted, too
	 * @return the filename from the given filepath
	 */
	public static String getFileNameFromFilePath(String aFilePath, boolean aWithExt)
	{
		String tBack = null;
		if (aFilePath!=null)
		{
			String tPattern;
			String tParts[];
			tPattern = escapeForRegex(File.separator);
			tParts = makeNoPath(aFilePath).split(tPattern);
			if (tParts!=null && tParts.length>0)
			{
				tBack = tParts[tParts.length-1];
				if (aWithExt == false) {
					tParts = tBack.split(escapeForRegex("."));
					if (tParts.length>1) {
						tBack = joinStringArray(tParts, ".", 0, tParts.length-2);
					} else {
						tBack = tParts[0];
					}
				}
			}
			else
			{
				tBack = "";
			}
		}
		return tBack;
	}

	/**
	 * @param aFilePath
	 * @return the extension (without heading dot) from the given filepath
	 */
	public static String getFileExtensionFromFilePath(String aFilePath)
	{
		String tBack = null;
		if (aFilePath!=null)
		{
			final String tFileExtSeparator = ".";
			int tPos;

			tPos = aFilePath.lastIndexOf(tFileExtSeparator);
			if (tPos>=0)
			{
				tBack = aFilePath.substring(tPos+1);
			}
			else
			{
				tBack = "";

			}
		}
		return tBack;
	}

	/**
	 * @param aFilePath
	 * @return the path (excluding trailing separator) from the given filepath
	 */
	public static String getPathFromFilePath(String aFilePath)
	{
		String tBack = null;
		if (aFilePath!=null)
		{
			String tPattern;
			String tParts[];
			tPattern = escapeForRegex(File.separator);
			aFilePath=replaceAllOccurencesOfSubstring(aFilePath, File.separator + File.separator, File.separator);
			tParts = makeNoPath(aFilePath).split(tPattern);
			if (tParts!=null && tParts.length>1)
			{
				tBack = joinStringArray(tParts, File.separator, 0, tParts.length-2);
			}
			else
			{
				tBack = "";
			}
		}
		return tBack;
	}


	/**
	 * removes all trailing File.separator
	 * @param aPath
	 * @return aPath w/o trailing File.separator
	 */
	public static String makeNoPath(String aPath)
	{
		while (aPath!=null && aPath.length()>0 &&
				aPath.charAt(aPath.length()-1) == File.separatorChar)
		{
			aPath = aPath.substring(0, aPath.length()-1);
		}
		return aPath;
	}

	/**
	 * adds a trailing File.separator, if no one is present
	 * @param aPath
	 * @return aPath wit a single trailing File.separator
	 */
	public static String makePath(String aPath)
	{
		aPath = makeNoPath(aPath);
		if (aPath!=null)
		{
			aPath += File.separator;
		}
		return aPath;
	}

	/**
	 * replaces all occurences of aSubSequence in aString and returns the
	 * modified string
	 * @param aString
	 * @param aSubSequence
	 * @return
	 */
	public static String replaceAllOccurencesOfSubstring(String aString, String aSubSequence, 
			String aReplacement)
	{
		StringBuffer tBack = new StringBuffer();
		if (aString==null)
		{
			return null;
		}
		else if (aString.length()<1)
		{
			return "";
		}
		else
		{
			if ((aSubSequence!=null) && (aSubSequence.length()>0) && (aString.indexOf(aSubSequence)>=0))
			{
				int i;
				int l;
				int ml;
				ml=aString.length();
				l=aSubSequence.length();
				i=0;
				while (i<ml)
				{
					if (aString.substring(i, (i+l)<ml?i+l:ml).equals(aSubSequence))
					{
						tBack.append(aReplacement);
						i+=l;
					}
					else
					{
						tBack.append(aString.charAt(i));
						i++;
					}
				}
				return tBack.toString();
			}
			else
			{
				return aString;
			}
		}
	}

	/**
	 * escapes a string for regular expressions. Escapes all ^$.*+{}[]?()\
	 * @param aStr
	 * @return
	 */
	public static String escapeForRegex(String aStr)
	{
		String tBack = "";
		if (aStr!=null && aStr.length()>0)
		{
			int i,n;
			n = aStr.length();
			for (i=0;i<n; i++)
			{
				char tChar;
				tChar=aStr.charAt(i);
				if ("^$.*+{}[]?()\\".indexOf(tChar)>=0)
				{
					tBack += "\\";
				}
				tBack += tChar;
			}
		}
		return tBack;
	}

	/**
	 * appends a file extension, if not already present
	 * @param aFileName
	 * @param aExtWithDot
	 * @return
	 */
	public static String addFileExtension(String aFileName, String aExtWithDot) {
		if (aFileName!=null && aExtWithDot!=null && aExtWithDot.length()>0) {
			if (aFileName.toLowerCase().endsWith(aExtWithDot.toLowerCase()) == false) {
				aFileName += aExtWithDot;
			}
		}
		return aFileName;
	}

	/**
	 * parses a line of a property file (i.e. foo=bar) to a 2-element String array
	 * whose first element contains the name (foo) and the second the rest of the line (bar)
	 * @param aPropertyFileLine
	 * @param aResults after execution: empty or 2-element string array
	 * @return false, if line was a comment, otherwise true 
	 */
	public static boolean parsePropertyLine(String aPropertyFileLine, ArrayList<String> aResults) {
		boolean tBack=false;
		if (aResults==null) {
			throw new IllegalArgumentException("aResults must not be null.");
		}
		aResults.clear();
		if (aPropertyFileLine!=null && aPropertyFileLine.trim().length()>0) {
			while(aPropertyFileLine.charAt(0)==' ' || aPropertyFileLine.charAt(0)=='\t') {
				aPropertyFileLine=aPropertyFileLine.substring(1);
			}
			if (aPropertyFileLine.charAt(0)!='#') {
				if (aPropertyFileLine.contains("=")) {
					int tPos;
					String[] tResults = new String[] {"",""};
					for(tPos=0; tPos < aPropertyFileLine.length() && aPropertyFileLine.charAt(tPos) != '='; tPos++) {
						tResults[0] += aPropertyFileLine.charAt(tPos);
					}
					if (tPos < aPropertyFileLine.length() ) {
						tResults[1]=aPropertyFileLine.substring(tPos+1);
					} 
					aResults.add(tResults[0]);
					aResults.add(tResults[1]);
					tBack = true;
				}      
			}
		} else {
			tBack = true;
		}
		return tBack;
	}

	/**
	 * sets a double in a text field
	 * @param aTextField
	 * @param aValue
	 */
	public static void setDoubleInTextField(JTextField aTextField, double aValue) {
		aTextField.setText(Double.toString(aValue));
	}
	
	/**
	 * Method to determine linebreak format.
	 * @return amount of line-breaking characters per line
	 */
	public static char[] determineNewlineChars(File nodesFile) {
		TCharArrayList res = new TCharArrayList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(nodesFile));
			int character;
			boolean eol = false;
			while ((character = br.read()) != -1) {
				if ((character == 13) || (character == 10)) {	// 13 = carriage return '\r', 10 = newline '\n'
					res.add((char) character);
					eol = true;
				} else if (eol) {
					break;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res.toArray();
	}

}
