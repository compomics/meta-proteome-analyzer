package de.mpa.task.instances;

import java.io.File;

import de.mpa.task.Task;

public class DeleteTask extends Task {
	
	private String filename;
	
	public DeleteTask(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		String parent = new File(filename).getParent();
		String filenamePrefix = "";
		
		if (filename.endsWith("xml")) {
			if (filename.contains("_target")){
				filenamePrefix = filename.substring(0, filename.indexOf("_target"));
			} else if (filename.contains("_unrestricted")) {
				filenamePrefix = filename.substring(0, filename.indexOf("_unrestricted"));
			}
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
