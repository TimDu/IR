package edu.buffalo.cse.irf14.index;

import java.util.List;
import java.util.Map;

public class TermIndexReader extends IndexReader {

	public TermIndexReader(String indexDir) {
		super(indexDir, IndexType.TERM);
	}

	@Override
	public int getTotalKeyTerms() {
		// TODO Auto-generated method stub
		return super.getTotalKeyTerms();
	}

	@Override
	public int getTotalValueTerms() {
		// TODO Auto-generated method stub
		return super.getTotalValueTerms();
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

		return super.getPostings(term);
	}

	@Override
	public List<String> getTopK(int k) {
		// TODO Auto-generated method stub
		return super.getTopK(k);
	}

	@Override
	public Map<String, Integer> query(String... terms) {
		// TODO Auto-generated method stub
		return super.query(terms);
	}

}
