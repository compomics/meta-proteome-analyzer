package de.mpa.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the I/O-read functionality for mascot generic files.
 * @author Thilo Muth
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
    private List<Integer> spectrumPositions = null;
    

    /**
     * This method reports on the spectrum files currently held in this merge file.
     *
     * @return Vector  with the currently held SpectrumFiles.
     */
    public List<MascotGenericFile> getSpectrumFiles() {
        return this.spectrumFiles;
    }

    /**
     * Simple getter for the filename for this Mergefile.
     *
     * @return String  with the filename.
     */
    public String getFilename() {
        return this.filename;
    }

    public List<Integer> getSpectrumPositions() {
		return spectrumPositions;
	}
    
    /**
     * Default constructor.
     */
    public MascotGenericFileReader() {}

    
	/**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param mgfFile The input MGF file
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotGenericFileReader(File mgfFile) throws IOException {
        this.load(mgfFile);
    }
    
    
    /**
     * This constructor opens the specified mergefile and optionally maps it to memory.
     * 
     * @param mgfFile The input MGF file
     * @param doSurvey Flag to determine scan mode. Survey mode will not map file to memory;
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotGenericFileReader(File mgfFile, boolean doSurvey) throws IOException {
    	if (doSurvey) {
    		this.survey(mgfFile);
    	} else {
    		this.load(mgfFile);
    	}
    }

	/**
     * This method loads the specified file in this MergeFileReader.
     *
     * @param file File with the file to load.
     * @throws java.io.IOException when the loading operation failed.
     */
    public void load(File file) throws IOException {
        spectrumFiles = new ArrayList<MascotGenericFile>();
        if (!file.exists()) {
            throw new IOException("Mergefile '" + file.getCanonicalPath() + "' could not be found!");
        } else {
            // Read the filename.
            this.filename = file.getName();

            BufferedReader br = new BufferedReader(new FileReader(file));

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
            int spectrumCounter = 0;
            boolean inSpectrum = false;
            StringBuffer tempComments = new StringBuffer();
            StringBuffer spectrum = new StringBuffer();
            // Cycle the file.
            boolean runnameNotYetFound = true;
            while ((line = br.readLine()) != null) {
                lineCounter++;
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
                    inSpectrum = true;
                    spectrum.append(line + "\n");
                }
            }
            // Initialize the comments.
            this.comments = tempComments.toString();

            br.close();
        }
        // Set the filename as well!
        this.filename = file.getName();

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
    }

	/**
     * This method loads a part of the specified file in this MergeFileReader.
     *
     * @param file File with the file to load.
     * @param index number to append to the filename
     * @param pos position of the desired spectrum block inside the file
     * @return
     * @throws java.io.IOException when the loading operation failed.
     */
    public MascotGenericFile loadNthSpectrum(File file, int index, int pos) throws IOException {
    	MascotGenericFile mgf = null;
        if (!file.exists()) {
            throw new IOException("Mergefile '" + file.getCanonicalPath() + "' could not be found!");
        } else {
            // Read the filename.
            this.filename = file.getName();

            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = null;
            StringBuffer spectrum = new StringBuffer();
            
            // Skip to specified line.
//            for (int i = 1; i < pos; i++) { line = br.readLine(); }
            br.skip(pos);

            // Read through spectrum block.
            while ((line = br.readLine()) != null) {
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
            br.close();
        }
		return mgf;
    }
    

    /**
     * This method browses the specified file and stores the positions of any spectrum blocks it encounters.
     * 
     * @param file File with the file to load.
     * @throws java.io.IOException when the loading operation failed.
     */
    public void survey(File file) throws IOException {
    	spectrumPositions = new ArrayList<Integer>();
        if (!file.exists()) {
            throw new IOException("Mergefile '" + file.getCanonicalPath() + "' could not be found!");
        } else {
            // Read the filename.
            this.filename = file.getName();

            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = null;
            int lineCounter = 0;
            int posCounter = 0;
            
            // Cycle the file.
            while ((line = br.readLine()) != null) {
                lineCounter++;
                posCounter += line.length()+2;	// length of line plus newline character, TODO: why 2?
                line = line.trim();
                
                // Skip empty lines and file-level charge statement.
                if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                    continue;
                }
                
                if (line.indexOf("BEGIN IONS") >= 0) {
                    // Mark position of spectrum block
                    this.spectrumPositions.add(posCounter - (line.length()+2));
                }

            }
            br.close();
        }
	}

    /**
     * This method strips a line of its prefixed '#' markings. Note that no trimming is performed.
     *
     * @param commentLine String with the commentline to strip prefixed '#'-ings from.
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
     * @return String with a filename for this spectrumfile.
     */
    protected String createSpectrumFilename(int number) {
        int extensionStart = this.filename.lastIndexOf(".");
        return this.filename.substring(0, extensionStart) + "_" + number + this.filename.substring(extensionStart);
    }
}
