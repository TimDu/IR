package edu.buffalo.cse.irf14.index;

import java.util.List;
import java.util.Map;

public class TermIndexReader implements IndexReaderInterface {

	public TermIndexReader(String indexDir) {
		//super(indexDir, IndexType.TERM);
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#getTotalKeyTerms()
	 */
	
	@Override
	public int getTotalKeyTerms() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#getTotalValueTerms()
	 */
	
	@Override
	public int getTotalValueTerms() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Gives a mapping between a file name and the number of occurrences a term
	 * had within that file assumes
	 * 
	 * PRECONDITIONS: Expects that the term and postings files are opened.
	 * 
	 * 
	 * @return The total number of terms
	 */
	@Override
	public Map<String, Integer> getPostings(String term) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#getTopK(int)
	 */
	@Override
	public List<String> getTopK(int k) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#query(java.lang.String)
	 */
	@Override
	public Map<String, Integer> query(String... terms) {
		// TODO Auto-generated method stub
		return null;
	} 

	 

}