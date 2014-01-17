package de.mpa.db.job.blast;

import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.BIT;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.EVALUE;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.IDENTITY;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.LENGTH;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.MISMATCH;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.QUERY;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.TARGET;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.TEND;
import static de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum.TSTART;

import java.util.Map;

import de.mpa.db.job.blast.BlastQuery.BlastHitHeaderEnum;

/**
 * This class represents a single BlastHit.
 * 
 */
public class BlastHit {

	/**
	 * Identity of alignment
	 */
	private double identity = 0;

	/**
	 * Length of alignment
	 */
	private int length = 0;

	/**
	 * Expect value.
	 */
	private double eValue = Double.NaN;

	/**
	 * Target start.
	 */
	private int targetStart = 0;

	/**
	 * Target end.
	 */
	private int targetEnd = 0;

	/**
	 * Number of mismatches.
	 */
	private int nMismatches = -1;

	/**
	 * Value strings.
	 */
	private String[] values;
	
	/**
	 * Header map containing the BLAST header values.
	 */
	private Map<BlastHitHeaderEnum, Integer> headerMap;
	
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
		if(headerMap == null) {
			headerMap = BlastQuery.getHeaderMap();
		}
		this.values = values.split("\t");
		init();
	}

	/**
	 * This method will set frequently used values as instance vars.
	 */
	private void init() {
		this.queryId = values[headerMap.get(QUERY)];
		this.targetId = values[headerMap.get(TARGET)];
		this.targetStart = Integer.parseInt(values[headerMap.get(TSTART)]);
		this.targetEnd = Integer.parseInt(values[headerMap.get(TEND)]);
		this.length = Integer.parseInt(values[headerMap.get(LENGTH)]);
		this.identity = Double.parseDouble(values[headerMap.get(IDENTITY)]);
		this.nMismatches = Integer.parseInt(values[headerMap.get(MISMATCH)]);
		this.eValue = Double.parseDouble(values[headerMap.get(EVALUE)]);
		this.bitScore = Double.parseDouble(values[headerMap.get(BIT)]);
	}

	/**
	 * Returns the query identifier.
	 * @return QueryId
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * Returns the identity value.
	 * @return Identity value.
	 */
	public double getIdentity() {
		return identity;
	}

	/**
	 * Returns the length of blast hit.
	 * @return Blast hit length.
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Returns the parent query.
	 * @return Parent BLAST query.
	 */
	public BlastQuery getParentQuery() {
		return parentQuery;
	}
	
	/**
	 * Returns the target start value.
	 * @return Target start value.
	 */
	public int getTargetStart() {
		return targetStart;
	}
	
	/**
	 * Returns the target end value.
	 * @return Target end value.
	 */
	public int getTargetEnd() {
		return targetEnd;
	}

	/**
	 * Returns the target ID.
	 * @return Target ID.
	 */
	public String getTargetID() {
		return targetId;
	}

	/**
	 * Returns the number of mismatches.
	 * @return Number of mismatches.
	 */
	public int getNumberOfMismatches() {
		return nMismatches;
	}

	/**
	 * Returns the e-value.
	 * @return The e-value.
	 */
	public double getEValue() {
		return eValue;
	}

	/**
	 * Returns the bit score.
	 * @return
	 */
	public double getBitScore() {
		return bitScore;
	}
}
