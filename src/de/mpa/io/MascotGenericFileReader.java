package de.mpa.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the I/O-read functionality for Mascot generic files.
 * 
 * @author Thilo Muth
 * @author Alexander Behne
 */
public class MascotGenericFileReader extends InputFileReader {

    /**
     * This String holds the run identification for this mergefile.
     */
    protected String runName;

    /**
     * This String holds the comments located on top of the MascotGenericMergeFile.
     */
    protected String comments;
    
    /**
     * This file stream will be used to read from the specified mergefile.
     */
    protected RandomAccessFile raf;

	// experimental!
	/**
	 * List of registered property change listeners.
	 */
	private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.remove(listener);
	}
	
	private void fireProgressMade(long oldProgress, long newProgress) {
		for (PropertyChangeListener listener : this.listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, "progress", oldProgress, newProgress));
		}
	}
    

    /**
     * Enum to determine behavior on creation of reader.
     */
    public enum LoadMode {
    	LOAD,
    	SURVEY,
    	NONE
    }
    
	/**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param mgfFile The input MGF file
     * @throws IOException when the file could not be read.
     */
    public MascotGenericFileReader(File file) throws IOException {
    	this(file, MascotGenericFileReader.LoadMode.LOAD);
    }

    /**
     * This constructor opens the specified mergefile and optionally maps it to memory.
     *
     * @param mgfFile The input MGF file
     * @param doLoad Boolean to determine whether to auto-load the specified file.
     * @throws IOException when the file could not be read.
     */
	public MascotGenericFileReader(File file, MascotGenericFileReader.LoadMode mode) throws IOException {
		super(file);
        this.raf = new RandomAccessFile(file, "r");
        if (mode == MascotGenericFileReader.LoadMode.LOAD) {
        	this.load();
        } else if (mode == MascotGenericFileReader.LoadMode.SURVEY) {
            survey();
        }
	}

	/**
     * This method loads the specified file in this MergeFileReader.
     *
     * @throws IOException when the loading operation failed.
     */
    public void load() throws IOException {
        this.spectrumFiles = new ArrayList<MascotGenericFile>();
        this.spectrumPositions = new ArrayList<Long>();

        // First parse the header.
        // First (non-empty?) line can be CHARGE= --> omit it if present.
        // Next up are comment blocks.
        // First non-empty comment line is raw filename (fully qualified).
        // Second non-empty comment line holds the run title.
        // Rest holds additional info which will be stored in the 'iComments' variable.
        // First comment line without spaces after the '###' is part of the first spectrum.
        String line = null;
        int commentLineCounter = 0;
        int lineCounter = 0;
        long oldPos = 0L;
        long newPos = 0L;
        
        if (this.newlineCharCount == 0) {
            this.newlineCharCount = this.determineNewlineCharCount();
        }
        
        int spectrumCounter = 0;
        boolean inSpectrum = false;
        StringBuffer tempComments = new StringBuffer();
        StringBuffer spectrum = new StringBuffer();
        // Cycle the file.
        boolean runnameNotYetFound = true;
        while ((line = this.raf.readLine()) != null) {
        	lineCounter++;
        	newPos = this.raf.getFilePointer();
            line = line.trim();
            // Skip empty lines and file-level charge statement.
            if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                continue;
            } else if (line.startsWith("#") && !inSpectrum) {
                // Comment lines.
            	
                // First strip off the comment markings in a new String ('cleanLine').
                String cleanLine = cleanCommentMarks(line);
                // If cleanLine trimmed is empty String, it's an empty comment line
                // and therefore skipped without counting.
                String cleanLineTrimmed = cleanLine.trim();
                if (cleanLineTrimmed.equals("")) {
                    continue;
                } else if (cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
                    // If it is not empty String, yet starts with a space (note that we verify
                    // using the untrimmed cleanLine!), it is a header
                    // comment, so we start by counting it!
                    commentLineCounter++;
                    // See if it is the second non-empty comment line.
                    if (runnameNotYetFound && commentLineCounter >= 2 && cleanLineTrimmed.indexOf("Instrument:") < 0 && cleanLineTrimmed.indexOf("Manufacturer:") < 0) {
                        // This line contains the run name.
                        runName = cleanLineTrimmed;
                        runnameNotYetFound = false;
                    }
                    // Every non-empty comment line is added to the tempComments
                    // StringBuffer, the contents of which are afterwards copied into
                    // the 'iComments' variable.
                    tempComments.append(line + "\n");
                } else {
                    // Spectrum comment. Start a new Spectrum!
                    this.fireProgressMade(oldPos, newPos);
                    spectrumPositions.add(oldPos);
                    inSpectrum = true;
                    spectrum.append(line + "\n");
                }
            } else if (inSpectrum) {
                // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
                // It could be 'BEGIN IONS', 'END IONS', 'TITLE=...', 'PEPMASS=...',
                // in-spectrum 'CHARGE=...' or, finally, a genuine peak line.
                // Whatever it is, add it to the spectrum StringBuffer.
            	
                // Adding this line to the spectrum StringBuffer.
                spectrum.append(line + "\n");
                // See if it was an 'END IONS', in which case we stop being in a spectrum.
                if (line.startsWith("END")) {
                    // End detected. Much to do!
                    // Reset boolean.
                    inSpectrum = false;
                    // Increment the spectrumCounter by one.
                    spectrumCounter++;
                    // Create a filename for the spectrum, based on the filename of the mergefile, with
                    // an '_[spectrumCounter]' before the extension (e.g. myParent.mgf --> myParent_1.mgf).
                    String spectrumFilename = createSpectrumFilename(spectrumCounter);
                    // Parse the contents of the spectrum StringBuffer into a MascotGenericFile.
                    MascotGenericFile mgf = new MascotGenericFile(spectrumFilename, spectrum.toString());
                    // Add it to the collection of SpectrumFiles.
                    spectrumFiles.add(mgf);
                    // Reset the spectrum StringBuffer.
                    spectrum = new StringBuffer();
                }
            } else if (line.startsWith("BEGIN")) {
                // If we're not in a spectrum, see if the line is 'BEGIN IONS', which marks the begin of a spectrum!
                this.fireProgressMade(oldPos, newPos);
                spectrumPositions.add(oldPos);
                inSpectrum = true;
                spectrum.append(line + "\n");
            }
            oldPos = newPos;
        }
        // Initialize the comments.
        comments = tempComments.toString();

        // If we do not have a run name by now, we just take the filename, minus the extension.
        if (runName == null) {
            // See if there is an extension,
            // and if there isn't, just take the filename as-is.
            int location = this.getFilename().lastIndexOf(".");
            if (location > 0) {
                this.runName = this.getFilename().substring(0, location);
            } else {
                this.runName = this.getFilename();
            }
        }

        // Fire final progress event
        this.fireProgressMade(-1L, newPos);
    }
    
    /**
     * Method to determine linebreak format.
     * 
     * @return amount of line-breaking characters per line
     */
    private int determineNewlineCharCount() {
    	int res = 0;
    	try {
            BufferedReader br = new BufferedReader(new FileReader(this.file));
            int character;
            boolean eol = false;
            while ((character = br.read()) != -1) {
            	if ((character == 13) || (character == 10)) {	// 13 = carriage return '\r', 10 = newline '\n'
            		res++;
            		eol = true;
            	} else if (eol) {
            		break;
            	}
            }
            br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return res;
	}

    /**
     * This method browses the specified file and stores the positions of any spectrum blocks it encounters.
     * 
     * @throws IOException when the loading operation failed.
     */
    public void survey() throws IOException {
        this.spectrumPositions = new ArrayList<Long>();

        String line = null;
        int lineCounter = 0;
        long oldPos = 0L;
        long newPos = 0L;
        
        if (this.newlineCharCount == 0) {
            this.newlineCharCount = this.determineNewlineCharCount();
        }

        boolean inSpectrum = false;
        
//        // Cycle the file.
//        while ((line = raf.readLine()) != null) {
//            lineCounter++;
//            oldPos = newPos;
//            newPos = raf.getFilePointer();
//            
////            line = line.trim();
////            
////            // Skip empty lines and file-level charge statement.
////            if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
////                continue;
////            } else if (line.startsWith("#") && !inSpectrum) {            	
////                String cleanLine = this.cleanCommentMarks(line);
////                String cleanLineTrimmed = cleanLine.trim();
////                if (cleanLineTrimmed.equals("")) {
////                	// Empty comment
////                    continue;
////                } else if (cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
////                	// Header comment
////                    continue;
////                } else {
////                    // Spectrum comment, start a new Spectrum!
////                    fireProgressMade(oldPos, newPos);
////                    this.spectrumPositions.add(oldPos);
////                    inSpectrum = true;
////                }
////            } else if (inSpectrum) {
////				if (line.startsWith("END")) {
////					inSpectrum = false;
////				}
////            } else if (line.startsWith("BEGIN")) {
////                fireProgressMade(oldPos, newPos);
////                this.spectrumPositions.add(oldPos);
////            }
//        }
        
		// Instantiate forward-only buffered reader for lightning-fast parsing
		// to determine spectrum positions
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        // Read file line by line
        while ((line = br.readLine()) != null) {
        	// Keep track of line number and byte position
        	lineCounter++;
        	oldPos = newPos;
        	newPos += line.getBytes().length + this.newlineCharCount;
        	
        	line = line.trim();

        	// Skip empty lines and file-level charge statement
        	if (line.isEmpty() || (lineCounter == 1 && line.startsWith("CHARGE"))) {
        		continue;
        	} else if (line.startsWith("#") && !inSpectrum) {            	
        		String cleanLine = cleanCommentMarks(line);
        		String cleanLineTrimmed = cleanLine.trim();
        		if (cleanLineTrimmed.isEmpty()) {
        			// Empty comment
        			continue;
        		} else if (cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
        			// Header comment
        			continue;
        		} else {
        			// Spectrum comment detected, start a new Spectrum
                    this.fireProgressMade(oldPos, newPos);
                    spectrumPositions.add(oldPos);
        			inSpectrum = true;
        		}
        	} else if (inSpectrum) {
        		if (line.startsWith("END")) {
        			// End of spectrum reached
        			inSpectrum = false;
        		}
        	} else if (line.startsWith("BEGIN")) {
        		// New spectrum found, store byte position of start of line
                this.fireProgressMade(oldPos, newPos);
                spectrumPositions.add(oldPos);
        	}
		}
        
        br.close();
        // Fire final progress event
        this.fireProgressMade(oldPos, newPos);
	}
    
    /**
     * This method closes the reader's file stream.
     * 
     * @throws IOException when the closing operation failed.
     */
    public void close() throws IOException {
        this.raf.close();
    }

    /**
     * This method strips a line of its prefixed '#' markings. Note that no trimming is performed.
     *
     * @param commentLine String with the comment line to strip prefixed '#'-ings from.
     * @return String with the comment line minus the prefixed '#'-ings.
     */
    protected String cleanCommentMarks(String commentLine) {
        StringBuffer result = new StringBuffer(commentLine);
        while (result.length() > 0 && result.charAt(0) == '#') {
            result.deleteCharAt(0);
        }
        return result.toString();
    }

    /**
     * This method creates a filename for an individual spectrum, based on the filename of the mergefile (in variable
     * 'iFilename'), with an '_[aNumber]' spliced in before the extension (eg., myParent.mgf --> myParent_1.mgf).
     *
     * @param number int with the number to splice into the filename.
     * @return String with a filename for this spectrum file.
     */
    protected String createSpectrumFilename(int number) {
    	String filename = this.getFilename();
        int extensionStart = filename.lastIndexOf(".");
        return filename.substring(0, extensionStart) + "_" + number + filename.substring(extensionStart);
    }

	@Override
	public MascotGenericFile loadSpectrum(int index) throws IOException {
		long pos1 = this.spectrumPositions.get(index);
		long pos2;
		index++;
		if (index < this.spectrumPositions.size()) {
			pos2 = this.spectrumPositions.get(index);
		} else {
			pos2 = this.file.length();
		}
		return this.loadSpectrum(index, pos1, pos2);
	}

	@Override
	public MascotGenericFile loadSpectrum(int index, long pos1, long pos2)
			throws IOException {

    	// Skip to specified line
        this.raf.seek(pos1);
    	
    	// Prepare byte buffer
    	int len = (int) (pos2 - pos1);
    	byte[] bytes = new byte[len];
    	
    	// Store file contents into buffer
    	int res = this.raf.read(bytes);
    	
    	// Throw exceptions when number of bytes read do not match
    	if (res != len) {
    		if (res < 0) {
    			throw new IOException("End of file has been reached prematurely.");
    		} else {
        		throw new IOException("Less bytes were read than expected.");
    		}
    	}
    	
    	// Generate MGF from buffered bytes (interpreted as String)
    	MascotGenericFile mgf = new MascotGenericFile(
                this.createSpectrumFilename(index), new String(bytes));
    	
		return mgf;
	}
}
