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
 * @author Thilo Muth
 * @author Alexander Behne
 *
 */

public class MascotGenericFileReader {

    /**
     * This Vector will hold all the spectrum files in the mergefile.
     */
    protected List<MascotGenericFile> spectrumFiles = null;

    /**
     * The filename for this mergefile.
     */
    protected String filename = null;

    /**
     * This String holds the run identification for this mergefile.
     */
    protected String runName = null;

    /**
     * This String holds the comments located on top of the MascotGenericMergeFile.
     */
    protected String comments = null;
    
    /**
     * This Vector will hold all the line numbers of spectrum blocks in the mergefile.
     */
    protected List<Long> spectrumPositions = null;
    
    /**
     * This file stream will be used to read from the specified mergefile.
     */
    protected RandomAccessFile raf;

    /**
     * Amount of characters that indicate line breaks.
     */
	private int newlineCharCount = 0;
	
	// experimental!
	/**
	 * List of registered property change listeners.
	 */
	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void fireProgressMade(long oldProgress, long newProgress) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, "progress", oldProgress, newProgress));
		}
	}
    

    /**
     * This method reports on the spectrum files currently held in this mergefile and closes the reader.
     *
     * @return Vector with the currently held spectrumFiles.
     */
    public List<MascotGenericFile> getSpectrumFiles() {
        return this.getSpectrumFiles(true);
    }

    /**
     * This method reports on the spectrum files currently held in this mergefile and optionally closes the reader.
     *
     * @param doClose Boolean to determine whether file stream shall be closed.
     * @return Vector with the currently held spectrumFiles.
     */
    public List<MascotGenericFile> getSpectrumFiles(boolean doClose) {
		if (doClose) {
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.spectrumFiles;
	}

    /**
     * This method reports on the character offsets of spectrum blocks in this mergefile and closes the reader.
     *
     * @return Vector with the currently held spectrumPositions.
     */
    public List<Long> getSpectrumPositions() {
		return getSpectrumPositions(true);
	}

    /**
     * This method reports on the character offsets of spectrum blocks in this mergefile and optionally closes the reader.
     *
     * @return Vector with the currently held spectrumPositions.
     */
	public List<Long> getSpectrumPositions(boolean doClose) {
		if (doClose) {
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.spectrumPositions;
	}

	/**
     * Simple getter for the filename of this mergefile.
     *
     * @return String with the filename.
     */
    public String getFilename() {
        return this.filename;
    }
    
    
    /**
     * Type-safe enum to determine behavior on creation of reader.
     */
    public static class LoadMode { private LoadMode() { } }
    public static final LoadMode LOAD = new LoadMode();		// includes surveying
    public static final LoadMode SURVEY = new LoadMode();	// survey only
    public static final LoadMode NONE = new LoadMode();		// do nothing
    
	/**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param mgfFile The input MGF file
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotGenericFileReader(File file) throws IOException {
    	this(file, LOAD);
    }
        
    /**
     * This constructor opens the specified mergefile and optionally maps it to memory.
     *
     * @param mgfFile The input MGF file
     * @param doLoad Boolean to determine whether to auto-load the specified file.
     * @throws java.io.IOException when the file could not be read.
     */
	public MascotGenericFileReader(File file, LoadMode mode) throws IOException {
    	if (!file.exists()) {
            throw new IOException("Mergefile '" + file.getCanonicalPath() + "' could not be found!");
        } else {
            // Read the filename.
            this.filename = file.getName();
            this.raf = new RandomAccessFile(file, "r");
            if (mode == LOAD) {
            	this.load();
            } else if (mode == SURVEY) {
            	this.survey();
            }
        }
	}

	/**
     * This method loads the specified file in this MergeFileReader.
     *
     * @throws java.io.IOException when the loading operation failed.
     */
    public void load() throws IOException {
        spectrumFiles = new ArrayList<MascotGenericFile>();
    	spectrumPositions = new ArrayList<Long>();

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
        
        if (newlineCharCount == 0) {
        	newlineCharCount = determineNewlineCharCount();
        }
        
        int spectrumCounter = 0;
        boolean inSpectrum = false;
        StringBuffer tempComments = new StringBuffer();
        StringBuffer spectrum = new StringBuffer();
        // Cycle the file.
        boolean runnameNotYetFound = true;
        while ((line = raf.readLine()) != null) {
        	lineCounter++;
        	newPos = raf.getFilePointer();
            line = line.trim();
            // Skip empty lines and file-level charge statement.
            if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                continue;
            }
            // Comment lines.
            else if (line.startsWith("#") && !inSpectrum) {
                // First strip off the comment markings in a new String ('cleanLine').
                String cleanLine = this.cleanCommentMarks(line);
                // If cleanLine trimmed is empty String, it's an empty comment line
                // and therefore skipped without counting.
                String cleanLineTrimmed = cleanLine.trim();
                if (cleanLineTrimmed.equals("")) {
                    continue;
                }
                // If it is not empty String, yet starts with a space (note that we verify
                // using the untrimmed cleanLine!), it is a header
                // comment, so we start by counting it!
                else if (cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
                    commentLineCounter++;
                    // See if it is the second non-empty comment line.
                    if (runnameNotYetFound && commentLineCounter >= 2 && cleanLineTrimmed.indexOf("Instrument:") < 0 && cleanLineTrimmed.indexOf("Manufacturer:") < 0) {
                        // This line contains the run name.
                        this.runName = cleanLineTrimmed;
                        runnameNotYetFound = false;
                    }
                    // Every non-empty comment line is added to the tempComments
                    // StringBuffer, the contents of which are afterwards copied into
                    // the 'iComments' variable.
                    tempComments.append(line + "\n");
                }
                // Spectrum comment. Start a new Spectrum!
                else {
                    fireProgressMade(oldPos, newPos);
                    this.spectrumPositions.add(oldPos);
                    inSpectrum = true;
                    spectrum.append(line + "\n");
                }
            }
            // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
            // It could be 'BEGIN IONS', 'END IONS', 'TITLE=...', 'PEPMASS=...',
            // in-spectrum 'CHARGE=...' or, finally, a genuine peak line.
            // Whatever it is, add it to the spectrum StringBuffer.
            else if (inSpectrum) {
                // Adding this line to the spectrum StringBuffer.
                spectrum.append(line + "\n");
                // See if it was an 'END IONS', in which case we stop being in a spectrum.
                if (line.indexOf("END IONS") >= 0) {
                    // End detected. Much to do!
                    // Reset boolean.
                    inSpectrum = false;
                    // Increment the spectrumCounter by one.
                    spectrumCounter++;
                    // Create a filename for the spectrum, based on the filename of the mergefile, with
                    // an '_[spectrumCounter]' before the extension (e.g. myParent.mgf --> myParent_1.mgf).
                    String spectrumFilename = this.createSpectrumFilename(spectrumCounter);
                    // Parse the contents of the spectrum StringBuffer into a MascotGenericFile.
                    MascotGenericFile mgf = new MascotGenericFile(spectrumFilename, spectrum.toString());
                    // Add it to the collection of SpectrumFiles.
                    this.spectrumFiles.add(mgf);
                    // Reset the spectrum StringBuffer.
                    spectrum = new StringBuffer();
                }
            }
            
            // If we're not in a spectrum, see if the line is 'BEGIN IONS', which marks the begin of a spectrum!
            else if (line.indexOf("BEGIN IONS") >= 0) {
                fireProgressMade(oldPos, newPos);
                this.spectrumPositions.add(oldPos);
                inSpectrum = true;
                spectrum.append(line + "\n");
            }
            oldPos = newPos;
        }
        // Initialize the comments.
        this.comments = tempComments.toString();

        // If we do not have a run name by now, we just take the filename, minus the extension.
        if (this.runName == null) {
            // See if there is an extension,
            // and if there isn't, just take the filename as-is.
            int location = this.filename.lastIndexOf(".");
            if (location > 0) {
                runName = this.filename.substring(0, location);
            } else {
                runName = this.filename;
            }
        }

        // Fire final progress event
        fireProgressMade(spectrumPositions.get(spectrumPositions.size() - 1), newPos);
    }
    
    /**
     * Method to determine linebreak format.
     * 
     * @return amount of line-breaking characters per line
     */
    private int determineNewlineCharCount() {
    	int res = 0;
    	try {
            BufferedReader br = new BufferedReader(new FileReader(raf.getFD()));
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
    		raf.seek(0);	// reset file-pointer to start of file
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
     * This method loads a part of the specified file in this MergeFileReader.
     *
     * @param index number to append to the filename
     * @param pos position of the desired spectrum block inside the file
     * @return desired SpectrumFile
     * @throws java.io.IOException when the loading operation failed.
     */
    public MascotGenericFile loadNthSpectrum(int index, long pos) throws IOException {
    	MascotGenericFile mgf = null;

    	String line = null;
    	StringBuffer spectrum = new StringBuffer();

    	// Skip to specified line.
    	raf.seek(pos);

    	// Read through spectrum block.
    	while ((line = raf.readLine()) != null) {
    		line = line.trim();

    		// Skip empty lines.
    		if (line.equals("")) { continue; }

    		// Add this line to the spectrum StringBuffer.
    		spectrum.append(line + "\n");

    		// See if it was an 'END IONS', in which case we stop.
    		if (line.indexOf("END IONS") >= 0) {
    			// Create a filename for the spectrum, based on the filename of the mergefile, with
    			// an '_[index]' before the extension (e.g. myParent.mgf --> myParent_1.mgf).
    			String spectrumFilename = this.createSpectrumFilename(index);
    			// Parse the contents of the spectrum StringBuffer into a MascotGenericFile.
    			mgf = new MascotGenericFile(spectrumFilename, spectrum.toString());

    			break;
    		}
    	}
    	
		return mgf;
    }
    
    /**
     * This method browses the specified file and stores the positions of any spectrum blocks it encounters.
     * 
     * @throws java.io.IOException when the loading operation failed.
     */
    public void survey() throws IOException {
    	spectrumPositions = new ArrayList<Long>();

        String line = null;
        int lineCounter = 0;
        long oldPos = 0L;
        long newPos = 0L;
        
        if (newlineCharCount == 0) {
        	newlineCharCount = determineNewlineCharCount();
        }
        
        // Cycle the file.
        while ((line = raf.readLine()) != null) {
            lineCounter++;
            newPos = raf.getFilePointer();
            line = line.trim();
            
            // Skip empty lines and file-level charge statement.
            if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                continue;
            }

            // Mark position of spectrum block
            if (line.indexOf("BEGIN IONS") >= 0) {
                fireProgressMade(oldPos, newPos);
                this.spectrumPositions.add(oldPos);
            }
            oldPos = newPos;
        }
        
        // Fire final progress event
        fireProgressMade(oldPos, newPos);
	}
    
    /**
     * This method closes the reader's file stream.
     * 
     * @throws java.io.IOException when the closing operation failed.
     */
    public void close() throws IOException {
    	raf.close();
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
        int extensionStart = this.filename.lastIndexOf(".");
        return this.filename.substring(0, extensionStart) + "_" + number + this.filename.substring(extensionStart);
    }
}
