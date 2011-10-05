package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Project extends ProjectTableAccessor {
	 /**
    * Calls the super class.
    */
   public Project(){
       super();
   }

   /**
    * Calls the super class.
    * @param params
    */
   public Project(HashMap params){
       super(params);
   }
   
   /**
    * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
    * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
    *
    * @param aRS ResultSet to read the data from.
    * @throws SQLException when reading the ResultSet failed.
    */
   public Project(ResultSet aRS) throws SQLException {
       super(aRS);
   }
   
   /**
    * This method will find a spectrum file from the current connection, based on the filename.
    *
    * @param fileName String with the filename of the spectrum file to find.
    * @param aConn     Connection to read the spectrum File from.
    * @return Spectrumfile with the data.
    * @throws SQLException when the retrieval did not succeed.
    */
   public static Project findFromTitle(String title, Connection aConn) throws SQLException {
   	Project temp = null;
       PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where title = ?");
       ps.setString(1, title);
       ResultSet rs = ps.executeQuery();
       int counter = 0;
       while (rs.next()) {
           counter++;
           temp = new Project(rs);
       }
       rs.close();
       ps.close();

       return temp;
   }
   
}
