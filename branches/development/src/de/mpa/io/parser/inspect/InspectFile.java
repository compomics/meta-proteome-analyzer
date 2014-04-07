package de.mpa.io.parser.inspect;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 07.09.2010
 * Time: 15:35:53
 * To change this template use File | Settings | File Templates.
 */
public class InspectFile {
    private String filename;

    public InspectFile(String filename) {
        this.filename = filename;
    }

    private List<InspectHit> identifications;

    public List<InspectHit> getIdentifications() {
        return identifications;
    }

    public void setIdentifications(List<InspectHit> identifications) {
        this.identifications = identifications;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
