package edu.buffalo.cse.irf14.scorer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic scoring model interface. This requires works
 * to be done in the fashion of {@code term at a time}.<br>
 * <b>Example steps:</b><br>
 * 1. Set up parameters (k1, b, k3).<br>
 * 2. Set up a collection of documents (to get document frequencies).<br>
 * 3. Set up a token (to get term frequency).<br>
 * 4. Perform algorithm once, and repeat step 3 until all<br>
 * 	  query terms are exhausted.
 * 5. Return an ranked list.<br>
 * 
 */
public abstract class ScoreModel {

	protected long totalDocNum;
	protected List<Integer> docIDs;
	protected List<Double> scores;
	
	public ScoreModel() {
		docIDs = null;
		scores = new ArrayList<Double>();
	}
	
	/**
	 * Set document ID collection
	 * @param docIDs collection of document IDs
	 */
	public void setDocuments(List<Integer> docIDs) {
		if (!(docIDs instanceof ArrayList)) {
			this.docIDs = new ArrayList<Integer>(docIDs);
		} else {
			this.docIDs = docIDs;
		}
	}
	
	/**
	 * Get a document's score.
	 * 
	 * @param index the score index position that is the
	 * same as the one in document ID list
	 * @return document score
	 */
	public double getScore(int index) {
		return scores.get(index);
	}
	
	/**
	 * Set score to a document.<br>
	 * <b>NOTE:</b> It is assumed that each score in the list
	 * has a corresponding document ID in another list at the
	 * same position.
	 * 
	 * @param index position index in score list
	 * @param score a given score
	 */
	protected void setScore(int index, double score) {
		scores.set(index, score);
	}
		
	/**
	 * Ranking function based on scoring result.
	 */
	protected List<Integer> rank() {
		sort(0, docIDs.size() - 1);		
		return docIDs;
	}
	
	/**
	 * Internally re-order document IDs according to their computed
	 * scores.
	 */
	private void sort(int startInd, int endInd) {
		int delta = (endInd - startInd) / 2;
		int tempID;
		int tempInd;
		int prevInd;
		double tempScore;
		
		if (delta > 5) {
			// Merge sort
			sort(startInd, startInd + delta);
			sort(startInd + delta + 1, endInd);
		}
		if (endInd > startInd) {
			// Insertion sort
			for (int i = startInd + 1; i <= endInd; ++i) {
				tempInd = i;
				prevInd = i - 1;
				while ((prevInd >= 0) && (scores.get(tempInd)
						> scores.get(prevInd))) {
					tempID = docIDs.get(tempInd);
					tempScore = scores.get(tempInd);
					docIDs.set(tempInd, docIDs.get(prevInd));
					scores.set(tempInd, scores.get(prevInd));
					docIDs.set(prevInd, tempID);
					scores.set(prevInd, tempScore);
					--tempInd;
					--prevInd;
				}
			}
		}
	}
}
