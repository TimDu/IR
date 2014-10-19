/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.util.List;
import java.util.Map;

/**
 * @author nikhillo Class that emulates reading data back from a written index
 */
public class IndexReader {
	protected String m_indexDir;

	private IndexReaderInterface iri;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory from which the index is to be read. This
	 *            will be exactly the same directory as passed on IndexWriter.
	 *            In case you make subdirectories etc., you will have to handle
	 *            it accordingly.
	 * @param type
	 *            The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		switch (type) {
		case TERM:
			iri = new IndexFileReader(indexDir,
					IndexGlobalVariables.termIndexFileName,
					IndexGlobalVariables.termDicFileName);
			break;
		case PLACE:
			iri = new IndexFileReader(indexDir,
					IndexGlobalVariables.placeIndexFileName,
					IndexGlobalVariables.placeDicFileName);
			break;
		case CATEGORY:
			iri = new IndexFileReader(indexDir,
					IndexGlobalVariables.categoryIndexFileName,
					IndexGlobalVariables.categoryDicFileName);
			break;
		case AUTHOR:
			iri = new IndexFileReader(indexDir,
					IndexGlobalVariables.authorIndexFileName,
					IndexGlobalVariables.authorDicFileName);
			break;
		}
		m_indexDir = indexDir;

	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this
	 * index. A postings list is always created against the "key" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		return iri.getTotalKeyTerms();
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with
	 * this index. A postings list is always created with the "value" dictionary
	 * 
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		return iri.getTotalValueTerms();
	}

	/**
	 * Method to get the postings for a given term. You can assume that the raw
	 * string that is used to query would be passed through the same Analyzer as
	 * the original field would have been.
	 * 
	 * @param term
	 *            : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the
	 *         number of occurrences as values if the given term was found, null
	 *         otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		return iri.getPostings(term);
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * 
	 * @param k
	 *            : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values null
	 *         for invalid k values
	 */
	public List<String> getTopK(int k) {
		if (k <= 0) {
			return null;
		}
		return iri.getTopK(k);
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * 
	 * @param terms
	 *            The ordered set of terms to AND, similar to getPostings() the
	 *            terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key and
	 *         number of occurrences as the value, the number of occurrences
	 *         would be the sum of occurrences for each participating term.
	 *         return null if the given term list returns no results BONUS ONLY
	 */
	public Map<String, Integer> query(String... terms) {
		return iri.query(terms);
	}
	
	/**
	 * Method to implement a boolean OR query on given index
	 * 
	 * @param terms set of terms connected with OR
	 * @return A map of FileID-TermFrequency pair
	 */
	public Map<String, Integer> queryOR(String...terms) {
		return iri.queryOR(terms);
	}
}
