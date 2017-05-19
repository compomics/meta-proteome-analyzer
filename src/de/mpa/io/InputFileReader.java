package de.mpa.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.io.parser.mascot.dat.MascotDatFileReader;

/**
 * 
 * 
 * @author heyer
 */
public abstract class InputFileReader {

    /**
     * The file descriptor of the mergefile.
     */
	protected File file;

    /**
     * This Vector will hold all the spectrum files in the mergefile.
     */
    protected List<MascotGenericFile> spectrumFiles;
    
    /**
     * This Vector will hold all the line numbers of spectrum blocks in the mergefile.
     */
    protected List<Long> spectrumPositions;

    /**
     * Amount of characters that indicate line breaks.
     */
	protected int newlineCharCount;
	
	/**
	 * List of registered property change listeners.
	 */
	private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public InputFileReader(File file) throws IOException {
    	if (!file.exists()) {
            throw new IOException("Mergefile '" + file.getCanonicalPath() + "' could not be found!");
    	}
		this.file = file;
        this.newlineCharCount = this.determineNewlineCharCount();
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static InputFileReader createInputFileReader(File file) throws IOException {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".mgf")) {
			return new MascotGenericFileReader(file, MascotGenericFileReader.LoadMode.NONE);
		} else if (fileName.endsWith(".dat")) {
			return new MascotDatFileReader(file);
		}
		return null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

    /**
     * This method reports on the spectrum files currently held in this mergefile and closes the reader.
     *
     * @return Vector with the currently held spectrumFiles.
     */
    public List<MascotGenericFile> getSpectrumFiles() {
        return getSpectrumFiles(true);
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
                close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return spectrumFiles;
	}

    /**
     * This method reports on the character offsets of spectrum blocks in this mergefile and closes the reader.
     *
     * @return Vector with the currently held spectrumPositions.
     */
    public List<Long> getSpectrumPositions() {
		return this.getSpectrumPositions(true);
	}

    /**
     * This method reports on the character offsets of spectrum blocks in this mergefile and optionally closes the reader.
     *
     * @return Vector with the currently held spectrumPositions.
     */
	public List<Long> getSpectrumPositions(boolean doClose) {
		if (doClose) {
			try {
                close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return spectrumPositions;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.remove(listener);
	}
	
	/**
	 * 
	 * @param oldProgress
	 * @param newProgress
	 */
	private void fireProgressMade(long oldProgress, long newProgress) {
		for (PropertyChangeListener listener : this.listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, "progress", oldProgress, newProgress));
		}
	}

	/**
     * Simple getter for the filename of this mergefile.
     *
     * @return String with the filename.
     */
    public String getFilename() {
        return this.file.getName();
    }
	
	/**
	 * 
	 * @param index
	 * @param pos1
	 * @param pos2
	 * @return
	 * @throws IOException
	 */
	public abstract MascotGenericFile loadSpectrum(int index) throws IOException;
	
	/**
	 * 
	 * @param index
	 * @param pos1
	 * @param pos2
	 * @return
	 * @throws IOException
	 */
	public abstract MascotGenericFile loadSpectrum(int index, long pos1, long pos2) throws IOException;
	
	/**
	 * 
	 * @throws IOException
	 */
	public abstract void survey() throws IOException;

	/**
	 * 
	 * @throws IOException
	 */
	public abstract void load() throws IOException;

	/**
	 * 
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
}
