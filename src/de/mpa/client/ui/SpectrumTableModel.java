package de.mpa.client.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

/**
 * This class implements a model for a spectrum table.
 *
 * @author T. Muth
 */
public class SpectrumTableModel extends DefaultTableModel {

    /**
     * MGF reader instance.
     */
    private MascotGenericFileReader reader = null;
    
    /**
     * Constructor.
     */
    public SpectrumTableModel() {
    }

    /**
     * Constructor for default SpectrumTableModel.
     * @param mgfFile MGF spectrum file
     */
    public SpectrumTableModel(File mgfFile) {
        this.reader = GenericContainer.MGFReaders.get(mgfFile.getAbsolutePath());
    }

    @Override
    public int getRowCount() {
    	if (reader == null) 
    		return 0;
        return reader.getSpectrumPositions().size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return " ";
            case 1:
                return "Title";
            case 2:
                return "m/z";
            case 3:
                return "Charge";
            case 4:
                return "Total Intensity";
            case 5:
                return "Highest Intensity";
            case 6:
                return "No. Peaks";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
    	
        MascotGenericFile spectrumFile;
		try {
			spectrumFile = reader.loadSpectrum(row);
		
	        switch (column) {
	            case 0:
	                return row + 1;
	            case 1:
	                return spectrumFile.getTitle();
	            case 2:
	            	return spectrumFile.getPrecursorMZ();
	            case 3:
	            	return spectrumFile.getCharge();
	            case 4: 
	            	return spectrumFile.getTotalIntensity();
	            case 5:
	            	return spectrumFile.getHighestIntensity();
	            case 6:
	            	return spectrumFile.getPeaks().size();
	            default:
	                return null;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 3:
            case 6:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
            case 4:
            case 5:
                return Double.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
