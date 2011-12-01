package de.mpa.db.accessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * @author Thilo Muth
 */
public class Spectrumfile extends SpectrumfileTableAccessor {

    /**
     * Default constructor.
     */
    public Spectrumfile() {
        super();
    }

    /**
     * This constructor just maps the superclass constructor.
     *
     * @param aParams HashMap with the values to set.
     */
    public Spectrumfile(HashMap aParams) {
        super(aParams);

    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Spectrumfile(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    /**
     * This method returns the actual Spectrum_file instance for this Spectrum.
     *
     * @param spectrumID The spectrumid of the requested spectrum
     * @param conn Connection to read the spectrum File from.
     * @return Spectrumfile with the actual Spectrum.
     */
    public static Spectrumfile findFromID(long spectrumID, Connection conn) throws SQLException {
    	Spectrumfile temp = null;
        PreparedStatement ps = conn.prepareStatement(Spectrumfile.getBasicSelect() + " where fk_libspectrumid = ?");
        ps.setLong(1, spectrumID);
        ResultSet rs = ps.executeQuery();
        int lCounter = 0;
        while (rs.next()) {
            temp = new Spectrumfile(rs);
            lCounter++;
        }
        rs.close();
        ps.close();
  
        return temp;
    }
    
    /**
     * This method returns the spectrum file as unzipped bytes.
     *
     * @return byte[]  with the unzipped bytes for the spectrum file.
     * @throws IOException when the unzipping process goes wrong.
     */
    public byte[] getUnzippedFile() throws IOException {
        byte[] result = null;

        byte[] zipped = super.getFile();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int read = -1;
        while ((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        result = baos.toByteArray();
        bos.close();
        bis.close();
        bais.close();
        baos.close();

        return result;
    }

    /**
     * This method allows the on-the fly zipping of data that is put in the DB.
     *
     * @param aBytes byte[] with the data for the PKL file. This data will be zipped and subsequently sent to the
     *               superclass.
     * @throws java.io.IOException when the zipping process fails.
     */
    public void setUnzippedFile(byte[] aBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        BufferedOutputStream bos = new BufferedOutputStream(gos);
        ByteArrayInputStream bais = new ByteArrayInputStream(aBytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        int read = -1;
        while ((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        gos.finish();
        super.setFile(baos.toByteArray());
        bis.close();
        bos.close();
        gos.close();
        bais.close();
        baos.close();
    }
}
