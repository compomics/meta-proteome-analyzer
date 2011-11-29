package de.mpa.db.storager;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import de.mpa.db.accessor.Project;


/**
 * This class handles the storage of the project parameters.
 * 
 * @author Thilo Muth
 *
 */
public class ProjectStorager extends BasicStorager {
    /**
     * Long variable holds the title.
     */
    private final String title;
 
    /**
     * The Connection instance.
     */
    private final Connection conn;
    
    /**
     * The Project instance.
     */
	private Project project;

	/**
	 * This variable contains the projectid;
	 */
	private long projectid;
	
	/**
	 * This variable contains the taxonid;
	 */
	private long taxonid;
	
	/**
	 * This variable holds the species name aka. the search database (fasta-format).
	 */
	private final String species;
	
	/**
	 * This variable holds the fragment ion tolerance aka. the fragment mass error.
	 */
	private final double fragmentTol;
	
	/**
	 * This variable holds the precursor ion tolerance aka. the precursor mass error.
	 */
	private final double precursorTol;
	
	/**
	 * This variable holds Da or ppm as precursor unit.
	 */
	private final String precursorUnit;

	/**
     * Constructor with project title as parameter.
     * @param conn
     * @param projectid
     */
    public ProjectStorager(final Connection conn, final String title, final String species, final double fragmentTol, final double precursorTol, final String precursorUnit) {
    	this.conn = conn;
        this.title = title;
        this.species = species;
        this.fragmentTol = fragmentTol;
        this.precursorTol = precursorTol;
        this.precursorUnit = precursorUnit;
    }

    /**
     * Loads project + taxon if they already exist in the database.
     *
     * @param file
     */
    public void load() {
        try {
            project = Project.findFromTitle(title, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the MGF-file to the database.
     *
     * @param conn
     * @throws SQLException
     */
    public void store() throws IOException, SQLException {
            
            /* Project section */
            final HashMap<Object, Object> projectdata = new HashMap<Object, Object>(8);
            
            if(project == null){
            	// The taxon id                
                projectdata.put(Project.TITLE, title);
                // Create the project database object.
                final Project newProject = new Project(projectdata);
                newProject.persist(conn);
                projectid = (Long) newProject.getGeneratedKeys()[0];
            } else {
            	projectid = project.getProjectid();
            }
    }
	
	/**
	 * Returns the projectid
	 * @return
	 */
	public long getProjectid() {
		return projectid;
	}
	
	/**
	 * Returns the taxonid.
	 * @return
	 */
	public long getTaxonid() {
		return taxonid;
	}
	
}

