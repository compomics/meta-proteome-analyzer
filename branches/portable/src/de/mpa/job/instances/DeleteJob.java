package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;

public class DeleteJob extends Job {
	
	private String filename;
	
	public DeleteJob(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
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
