package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TermIndexReader implements IndexReaderInterface {
	protected String m_indexDir; 
	protected TermIndexFileReader tifr;
	public TermIndexReader(String indexDir) {
		m_indexDir = indexDir;
		tifr = new TermIndexFileReader(indexDir);
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
	 * had within that file 
	 * 
	 * PRECONDITIONS: Expects that the term and postings files are opened.
	 * 
	 * 
	 * @return The total number of terms
	 */
	@Override
	public Map<String, Integer> getPostings(String term) {
		// TODO Auto-generated method stub
		int termID = 0;
		try {
			PriorityQueue<Integer> pqi = tifr.getPostings(termID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert(false);
		}
		
		
		
		
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
