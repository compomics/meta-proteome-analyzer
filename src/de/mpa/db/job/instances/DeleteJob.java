package de.mpa.db.job.instances;

import java.io.File;

import de.mpa.db.job.Job;

/**
 * Wrapper job class for executing a bash script to recursively delete files in a folder and its subfolders:
 * clearfolders.sh: find /folder/subfolder -type f -exec rm -f {} \;
 * 
 * @author T.Muth
 * @date 11/07/2012
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
