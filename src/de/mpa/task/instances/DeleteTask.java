package de.mpa.task.instances;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mpa.task.Task;

public class DeleteTask extends Task {
	
	private String filename;
	
	public DeleteTask(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		String parent = new File(filename).getParent();
		List<String> prefixes = new ArrayList<String>();
		
		if (filename.endsWith("xml")) {
			if (filename.contains("_target")){
				prefixes.add(filename.substring(0, filename.indexOf("_target")));
			} else if (filename.contains("_unrestricted")) {
				prefixes.add(filename.substring(0, filename.indexOf("_unrestricted")));
			}
		} 
		if (filename.endsWith("txt")) {
			if (filename.contains("_comet")){
				prefixes.add(filename.substring(0, filename.indexOf("txt")));
			} else if (filename.contains("_comet.decoy")) {
				prefixes.add(filename.substring(0, filename.indexOf("txt")));
			}
		} 
		if (filename.endsWith("mzid")) {
			prefixes.add(filename.substring(0, filename.indexOf("mzid")));
		}
		if (filename.endsWith("tsv")) {
			prefixes.add(filename.substring(0, filename.indexOf("tsv")));
		}
		
		File parentFolder = new File(parent);
		// Iterate the files in the directory
		if (parentFolder.isDirectory()){
			for (File file : parentFolder.listFiles()) {
				for (String prefix : prefixes) {
					if (file.getAbsolutePath().startsWith(prefix) && file.isFile()) {
						file.delete();
					}
				}
			}
		}
	}
}
