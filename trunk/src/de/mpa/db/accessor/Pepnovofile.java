package de.mpa.db.accessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

public class Pepnovofile extends PepnovofileTableAccessor{

    /**
    * Default constructor.
    */
   public Pepnovofile() {
       super();
   }

   /**
    * This constructor just maps the superclass constructor.
    *
    * @param aParams HashMap with the values to set.
    */
   public Pepnovofile(HashMap aParams) {
       super(aParams);

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

