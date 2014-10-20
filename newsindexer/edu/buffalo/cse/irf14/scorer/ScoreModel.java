package edu.buffalo.cse.irf14.scorer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic scoring model interface. This requires works
 * to be done in the fashion of {@code term at a time}.<br>
 * <b>Example steps:</b><br>
 * 1. Set up parameters (k1, b, k3).<br>
 * 2. Set up a collection of documents.<br>
 * 3. Collect term frequencies.<br>
 * 4. Set up for a term (to get document frequency).<br>
 * 5. Perform algorithm once, and repeat step 3 until all<br>
 * 	  query terms are exhausted.
 * 6. Return an ranked list.<br>
 * <b>NOTE:</b> Input documents should be non-duplicated<br>
 * After the computation steps are finished, the ranked document
 * ID list would be returned by getRankedList() method; scores could 
 * be retrieved by getScore() method.
 */
public abstract class ScoreModel {

	private DecimalFormat decimal;
	protected long totalDocNum;
	protected long docFreq;
	protected long queryTermFreq;
	protected Map<Integer, Long> docTermFreqs;
	protected List<Integer> docIDs;
	protected List<Double> scores;
	
	public ScoreModel() {
		decimal = new DecimalFormat("#.#####");
		docIDs = null;
		docTermFreqs = new HashMap<Integer, Long>(30);
		scores = new ArrayList<Double>();
	}
	
	/**
	 * Method that runs the algorithm for current query
	 * term.
	 */
	public abstract void run();
	
	/**
	 * Set document ID collection. And do some clean works
	 * before reset document collections.
	 * 
	 * @param docIDs collection of document IDs
	 */
	public void setDocuments(List<Integer> docIDs) {
		docTermFreqs.clear();
		if (!(docIDs instanceof ArrayList)) {
			this.docIDs = new ArrayList<Integer>(docIDs);
		} else {
			this.docIDs = docIDs;
		}
		scores.clear();
		for (int i = 0; i < this.docIDs.size(); ++i) {
			scores.add(0.0);
		}
	}
	
	/**
	 * Set term frequencies to all documents
	 * 
	 * @param docID document IDs
	 * @param termFreq document term frequency
	 */
	public void setDocTermFreq(int docID, long termFreq) {
		docTermFreqs.put(docID, termFreq);
	}
	
	/**
	 * Set query term frequency
	 * 
	 * @param termFreq query term frequency
	 */
	public void setQueryTermFreq(long termFreq) {
		queryTermFreq = termFreq;
	}
	
	/**
	 * Set document frequency for a term
	 * 
	 * @param docFreq document frequency
	 */
	public void setDocFreq(long docFreq) {
		this.docFreq = docFreq;
	}
	
	/**
	 * Method that returns a document's score.
	 * In this list, each score has the same index position 
	 * to the one in result document ID list. This position
	 * also indicate its ranking position if called after
	 * {@code getRankedList} method.
	 * 
	 * @param index the score index position that is the
	 * same as the one in document ID list
	 * @return document score
	 */
	public double getScore(int index) {
		return scores.get(index);
	}
	
	/**
	 * Method that returns a document's score in formatted text.
	 * Probably more frequent to be called for display purpose
	 * than {@code getScore} method. 
	 * 
	 * @param index the score index position that is the
	 * same as the one in document ID list
	 * @return text score
	 */
	public String getTextScore(int index) {
		return decimal.format(scores.get(index));
	}
	
	/**
	 * Method that collects ranking result.
	 * 
	 * @return ranked document ID list
	 */
	public List<Integer> getRankedList() {
		return rank();
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
	 * 
	 * @return list of ranked document IDs
	 */
	private List<Integer> rank() {
		sort(0, docIDs.size() - 1);
		normalize();
		
		return docIDs;
	}
	
	/**
	 * Internally normalize scores in the range of [0, 1] by the
	 * maximum score.
	 */
	private void normalize() {
		double norm = scores.get(0);
		for (int i = 0; i < scores.size(); ++i) {
			scores.set(i, scores.get(i) / norm);
		}
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
