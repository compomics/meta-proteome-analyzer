package de.mpa.io;

import java.io.File;
import java.io.IOException;

import de.mpa.io.parser.mascot.dat.MascotDatFileReader;

public enum FileType {
	
	MGF_FILE {
		@Override
		public InputFileReader createInputFileReader(File file) throws IOException {
			return new MascotGenericFileReader(file, MascotGenericFileReader.LoadMode.NONE);
		}
	},
	DAT_FILE {
		@Override
		public InputFileReader createInputFileReader(File file) throws IOException {
			return new MascotDatFileReader(file);
		}
	};
	
	public abstract InputFileReader createInputFileReader(File file) throws IOException;
	
}
