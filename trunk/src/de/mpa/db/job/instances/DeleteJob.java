package de.mpa.db.job.instances;

import java.io.File;

import de.mpa.db.job.Job;

/**
 * Delete job class for removing X!Tandem and OMSSA result files.
 * 
 * @author T.Muth
 * 
 */
public class DeleteJob extends Job {	
private String filename;
	
	public DeleteJob() {}
	
	public DeleteJob(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		
		// Wait for 10 seconds before removing of files starts... to account for other processes to finish first.
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String parent = new File(filename).getParent();
		String filenamePrefix = "";
		
		if (filename.endsWith("xml")) {
			filenamePrefix = filename.substring(0, filename.indexOf("_target"));
		} else if (filename.endsWith("omx")) {
			filenamePrefix = filename.substring(0, filename.indexOf("_target"));
		}
		File parentFolder = new File(parent);
		// Iterate the files in the directory
		if (parentFolder.isDirectory()){
			for (File file : parentFolder.listFiles()) {
				if (file.getAbsolutePath().startsWith(filenamePrefix) && file.isFile()) {
					file.delete();
				}
			}
		}
	}
}
