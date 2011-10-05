package de.mpa.db.accessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 07.09.2010
 * Time: 10:40:34
 * To change this template use File | Settings | File Templates.
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
