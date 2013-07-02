package de.mpa.db.storager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This interface declares two main methods used for storing in the database:
 * 1.) load the data 
 * 2.) store to the database
 * Additionally it's designed as a thread.
 * 
 * @author Thilo Muth
 */
public interface Storager extends Runnable {

    /**
     * Loads the file containing relevant data.
     */
    public void load();
    
    /**
     * Store all data persistently into the database.
     */
    public void store() throws IOException, SQLException;
}

