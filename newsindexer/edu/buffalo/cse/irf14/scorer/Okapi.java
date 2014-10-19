package edu.buffalo.cse.irf14.scorer;

import java.util.ArrayList;
import java.util.List;

/**
 * Okapi relevance model. * 
 *
 */
public class Okapi extends ScoreModel {

	private long avgDocLen;
	private List<Long> docLength;	// List of documents length
	
	/**
	 * Constructor.
	 * 
	 * @param totalDocNum total number of documents
	 * @param avgDocLen average document length
	 */
	public Okapi (long totalDocNum, long avgDocLen) {
		super();
		this.docLength = new ArrayList<Long>();
		this.totalDocNum = totalDocNum;
		this.avgDocLen = avgDocLen;
	}
	
	/**
	 * This step should be performed when setting up 
	 * 
	 * @param index
	 * @param len
	 */
	public void setDocLength(int index, long len) {
		this.docLength.add(len);
	}
	
	//public 
}
