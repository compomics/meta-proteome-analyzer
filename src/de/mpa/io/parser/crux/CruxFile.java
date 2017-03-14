package de.mpa.io.parser.crux;

import java.util.List;

public class CruxFile {
    private String filename;

    public CruxFile(String filename) {
        this.filename = filename;
    }

    private List<CruxHit> hits;
    
    public List<CruxHit> getHits() {
		return this.hits;
	}

	public void setHits(List<CruxHit> hits) {
		this.hits = hits;
	}

	public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
