package de.mpa.io.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.Test;

import de.mpa.task.ResourceProperties;
import junit.framework.TestCase;

public class ParseParametersTest extends TestCase {
	
	protected ResourceProperties algorithmProperties = ResourceProperties.getInstance();
	
	@Test
	public void testBuildParametersFile() {
		File cometExecutable = new File(algorithmProperties.getProperty("path.comet"));
		File parameterFile = new File(cometExecutable, "comet.params.new");
		File codeParameterFile = new File(cometExecutable, "comet.params.java");
		BufferedWriter bw = null; 
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(parameterFile));
			bw = new BufferedWriter(new FileWriter(codeParameterFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				bw.append("bw.append(\"" + line + "\");");
				bw.newLine();
				bw.append("bw.newLine();");
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
