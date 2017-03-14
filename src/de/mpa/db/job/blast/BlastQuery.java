package de.mpa.db.job.blast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents a query from a BlastP results file.
 */
public class BlastQuery {
	
	/**
	 * Enum for the different BlastHit headers.
	 * e.g.: query id, subject id, % identity, alignment length, mismatches, gap opens, q. start, q. end, s. start, s. end, evalue, bit score
	 */
	public enum BlastHitHeaderEnum {
	    QUERY("query id"),
	    TARGET("subject id"),
	    IDENTITY("% identity"),
	    LENGTH("alignment length"),
	    MISMATCH("mismatches"),
	    GAP("gap opens"),
	    QSTART("q. start"),
	    QEND("q. end"),
	    TSTART("s. start"),
	    TEND("s. end"),
	    EVALUE("evalue"),
	    BIT("bit score");

	    public String name = "";

	    BlastHitHeaderEnum(String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	        return this.name;
	    }
	}
	
    private final List<BlastHit> blastHits = new ArrayList<BlastHit>();
    private static Map<BlastQuery.BlastHitHeaderEnum, Integer> headerMap;
    private int queryLength;
    private static String[] headers;
    private String content;
    private String queryName;

    /**
     * Constructs a new object from a single Query.
     *
     * @param aContent One section (multiple lines, including comments) from the results file.
     */
    public BlastQuery(String content) {
        this.content = content;
        init();
    }

    private void init() {
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("#")) {
                // Comment line.
                if (line.indexOf("Query:") != -1) {
                    queryName = line.substring(line.indexOf("Query:") + 6).trim();
                } else if (line.indexOf("Fields:") != -1) {
                    headers = line.substring(line.indexOf("Fields:") + 7).split(",");
                }
            } else {
                blastHits.add(new BlastHit(line));
            }
        }
    }


    /**
     * Gets the essential headers into a HashMap.
     */
    public static Map<BlastQuery.BlastHitHeaderEnum, Integer> getHeaderMap() {
            headerMap = new HashMap<BlastQuery.BlastHitHeaderEnum, Integer>();
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim();
                if (header.equals(BlastQuery.BlastHitHeaderEnum.QUERY.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.QUERY, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.TARGET.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.TARGET, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.IDENTITY.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.IDENTITY, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.LENGTH.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.LENGTH, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.TSTART.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.TSTART, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.TEND.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.TEND, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.MISMATCH.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.MISMATCH, i);
                } else if (header.equals(BlastQuery.BlastHitHeaderEnum.EVALUE.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.EVALUE, i);
                }else if (header.equals(BlastQuery.BlastHitHeaderEnum.BIT.name)) {
                    headerMap.put(BlastQuery.BlastHitHeaderEnum.BIT, i);
                }
        }
        return BlastQuery.headerMap;
    }

    public String getQueryName() {
        return this.queryName;
    }

    public int getQueryLength() {
        return this.queryLength;
    }

    public List<BlastHit> getAllBlastHits() {
        return this.blastHits;
    }

    /**
     * Returns the 100% Blast hits.
     *
     * @return 100% identity Blast hits.
     */
    public List<BlastHit> getPerfectBlastHits() {
        List<BlastHit> resultHits = new ArrayList<BlastHit>();
        for (Iterator<BlastHit> hitIterator = this.blastHits.iterator(); hitIterator.hasNext();) {
            BlastHit hit = hitIterator.next();
            if ((hit.getIdentity() == 100) && (hit.getLength() == getQueryLength())) {
                resultHits.add(hit);
            }
        }
        return resultHits;
    }
    
    /**
     * Returns the best Blast hit.
     * @return BlastHit The highest ranked Blast hit.
     */
    public BlastHit getFirstHit() {
    	if(this.blastHits.size() > 0){
    		return this.blastHits.get(0);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns the Blast hits above bit threshold.
     * @return Blast hits above bit threshold
     */
    public List<BlastHit> getBlastHitsAboveBitThreshold(double bitThreshold) {
        List<BlastHit> resultHits = new ArrayList<BlastHit>();
        
        for (Iterator<BlastHit> hitIterator = this.blastHits.iterator(); hitIterator.hasNext();) {
            BlastHit hit = hitIterator.next();
            if ((hit.getBitScore() > bitThreshold)) {
                resultHits.add(hit);
            }
        }
        return resultHits;
    }
    
    /**
     * Returns the number of mismatch hits.
     * @param numMismatches
     * @return misMatch Blast hits
     */
    public List<BlastHit> getMismatchHits(int numMismatches) {
        List<BlastHit> resultHits = new ArrayList<BlastHit>();
        for (Iterator<BlastHit> hitIterator = this.blastHits.iterator(); hitIterator.hasNext();) {
            BlastHit hit = hitIterator.next();
            if ((hit.getLength() == getQueryLength()) && (hit.getNumberOfMismatches() == numMismatches)) {
                resultHits.add(hit);
            }
        }
        return resultHits;
    }
}
