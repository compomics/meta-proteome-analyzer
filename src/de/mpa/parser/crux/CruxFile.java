package de.mpa.parser.crux;

import java.util.List;

public class CruxFile {
    private String filename;

    public CruxFile(String filename) {
        this.filename = filename;
    }

    private List<CruxHit> hits;
    
    public List<CruxHit> getHits() {
		return hits;
	}

	public void setHits(List<CruxHit> hits) {
		this.hits = hits;
	}

	public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
