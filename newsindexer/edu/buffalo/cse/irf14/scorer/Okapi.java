package edu.buffalo.cse.irf14.scorer;

import java.util.HashMap;
import java.util.Map;

/**
 * Okapi relevance model. * 
 *
 */
public class Okapi extends ScoreModel {

	private long avgDocLen;
	private double k1;
	private double b;
	private double k3;
	private Map<Integer, Long> docLength;	// Map of documents length
	
	/**
	 * Constructor.
	 * 
	 * @param totalDocNum total number of documents
	 * @param avgDocLen average document length
	 */
	public Okapi (long totalDocNum, long avgDocLen) {
		super();
		k1 = 1.2;
		k3 = 2.0;
		b = 0.75;
		this.docLength = new HashMap<Integer, Long>(30);
		this.totalDocNum = totalDocNum;
		this.avgDocLen = avgDocLen;
	}
	
	/**
	 * Set Okapi parameters. Default values would be used if they
	 * are not set manually.
	 * 
	 * @param k1 calibrates term frequency scaling factor
	 * @param b document length scaling factor
	 * @param k3 query frequency scaling factor
	 */
	public void setParameters(double k1, double b, double k3) {
		this.k1 = k1;
		this.b = b;
		this.k3 = k3;
	}
	
	/**
	 * This step should be performed when setting up 
	 * a term.<br>
	 * 
	 * @param id document ID
	 * @param len document length
	 */
	public void setDocLength(int id, long len) {
		this.docLength.put(id, len);
	}

	/*
	 * A term per time algorithm update.
	 * Variables to update for each new run:
	 * 1. Document frequency of this term
	 * 2. Term frequencies among documents
	 * 3. Term frequency in query
	 */
	@Override
	public void run() {
		double tempScore;
		for (int i = 0; i < docIDs.size(); ++i) {
			tempScore = scores.get(i);
			tempScore += Math.log10(totalDocNum / docFreq)
					* ((k1 + 1) * docTermFreqs.get(docIDs.get(i))
					/ (k1 * (1 - b + b * (docLength.get(docIDs.get(i))
					/ avgDocLen)) + docTermFreqs.get(docIDs.get(i))))
					* ((k3 + 1) * queryTermFreq / (k3 + queryTermFreq));
			scores.set(i, tempScore);
		}
	}
}
