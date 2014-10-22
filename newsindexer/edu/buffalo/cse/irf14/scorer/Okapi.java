package edu.buffalo.cse.irf14.scorer;

import java.util.HashMap;
import java.util.Map;

/**
 * Okapi relevance model. * 
 *
 */
public class Okapi extends ScoreModel {

	private double maxScore;
	private double avgDocLen;
	private double k1;
	private double b;
	private double k3;
	private Map<Integer, Integer> docLength;	// Map of documents length
	
	/**
	 * Constructor.
	 * 
	 * @param totalDocNum total number of documents
	 * @param avgDocLen average document length
	 */
	public Okapi (long totalDocNum, double avgDocLen) {
		super();
		k1 = 1.5;
		k3 = 1.8;
		b = 0.75;
		this.docLength = new HashMap<Integer, Integer>(30);
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
	public Okapi setParameters(double k1, double b, double k3) {
		this.k1 = k1;
		this.b = b;
		this.k3 = k3;
		return this;
	}

	@Override
	public void clear() {
		super.clear();
		docLength.clear();
	}
	
	/**
	 * This step should be performed when setting up 
	 * a term.<br>
	 * 
	 * @param id document ID
	 * @param len document length
	 */
	protected void setDocLength(int id, int len) {
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
		if (docFreq > 0) {
			for (int i = 0; i < docIDs.size(); ++i) {
				if ((docTermFreqs.get(docIDs.get(i)) > 0)) {
					double docScore = (Math.log10(totalDocNum - docFreq + 0.5)
							- Math.log10(docFreq + 0.5))
							* ((k1 + 1) * docTermFreqs.get(docIDs.get(i))
							/ (k1 * (1 - b + b * (docLength.get(docIDs.get(i))
							/ avgDocLen)) + docTermFreqs.get(docIDs.get(i))))
							* ((k3 + 1) * queryTermFreq / (k3 + queryTermFreq));
					tempScore = scores.get(i);
					tempScore += docScore;
					scores.set(i, tempScore);
					if (tempScore > maxScore) {
						maxScore = tempScore;
					}
				}
			}
		}
	}

	@Override
	protected void normalize() {
	}
}
