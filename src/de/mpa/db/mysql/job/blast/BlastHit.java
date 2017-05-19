package de.mpa.db.mysql.job.blast;

import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.BIT;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.EVALUE;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.IDENTITY;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.LENGTH;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.MISMATCH;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.QUERY;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.TARGET;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.TEND;
import static de.mpa.db.mysql.job.blast.BlastQuery.BlastHitHeaderEnum.TSTART;

import java.util.Map;

/**
 * This class represents a single BlastHit.
 * 
 */
public class BlastHit {

	/**
	 * Identity of alignment
	 */
	private double identity;

	/**
	 * Length of alignment
	 */
	private int length;

	/**
	 * Expect value.
	 */
	private double eValue = Double.NaN;

	/**
	 * Target start.
	 */
	private int targetStart;

	/**
	 * Target end.
	 */
	private int targetEnd;

	/**
	 * Number of mismatches.
	 */
	private int nMismatches = -1;

	/**
	 * Value strings.
	 */
	private final String[] values;
	
	/**
	 * Header map containing the BLAST header values.
	 */
	private Map<BlastQuery.BlastHitHeaderEnum, Integer> headerMap;
	
	/**
	 * Query identifier.
	 */
	private String queryId;
	
	/**
	 * Target protein identifier.
	 */
	private String targetId;

	/**
	 * This variable holds the BitScore
	 */
	private double bitScore;

	/**
	 * Parent blast query
	 */
	private BlastQuery parentQuery;

	/**
	 * Constructs a new BlastHit.
	 * 
	 * @param values
	 *            tab separated values of the blasthit.
	 * @param parentQuery
	 */
	public BlastHit(String values) {
		if(this.headerMap == null) {
            this.headerMap = BlastQuery.getHeaderMap();
		}
		this.values = values.split("\t");
        this.init();
	}

	/**
	 * This method will set frequently used values as instance vars.
	 */
	private void init() {
        queryId = this.values[this.headerMap.get(QUERY)];
        targetId = this.values[this.headerMap.get(TARGET)];
        targetStart = Integer.parseInt(this.values[this.headerMap.get(TSTART)]);
        targetEnd = Integer.parseInt(this.values[this.headerMap.get(TEND)]);
        length = Integer.parseInt(this.values[this.headerMap.get(LENGTH)]);
        identity = Double.parseDouble(this.values[this.headerMap.get(IDENTITY)]);
        nMismatches = Integer.parseInt(this.values[this.headerMap.get(MISMATCH)]);
        eValue = Double.parseDouble(this.values[this.headerMap.get(EVALUE)]);
        bitScore = Double.parseDouble(this.values[this.headerMap.get(BIT)]);
	}

	/**
	 * Returns the query identifier.
	 * @return QueryId
	 */
	public String getQueryId() {
		return this.queryId;
	}

	/**
	 * Returns the identity value.
	 * @return Identity value.
	 */
	public double getIdentity() {
		return this.identity;
	}

	/**
	 * Returns the length of blast hit.
	 * @return Blast hit length.
	 */
	public int getLength() {
		return this.length;
	}
	
	/**
	 * Returns the parent query.
	 * @return Parent BLAST query.
	 */
	public BlastQuery getParentQuery() {
		return this.parentQuery;
	}
	
	/**
	 * Returns the target start value.
	 * @return Target start value.
	 */
	public int getTargetStart() {
		return this.targetStart;
	}
	
	/**
	 * Returns the target end value.
	 * @return Target end value.
	 */
	public int getTargetEnd() {
		return this.targetEnd;
	}

	/**
	 * Returns the target ID.
	 * @return Target ID.
	 */
	public String getTargetID() {
		return this.targetId;
	}

	/**
	 * Returns the number of mismatches.
	 * @return Number of mismatches.
	 */
	public int getNumberOfMismatches() {
		return this.nMismatches;
	}

	/**
	 * Returns the e-value.
	 * @return The e-value.
	 */
	public double getEValue() {
		return this.eValue;
	}

	/**
	 * Returns the bit score.
	 * @return
	 */
	public double getBitScore() {
		return this.bitScore;
	}
}
