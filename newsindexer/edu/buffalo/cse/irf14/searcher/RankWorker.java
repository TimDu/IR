package edu.buffalo.cse.irf14.searcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;

/**
 * {@code Or} relation worker thread for {@link RankingManager}.
 */
public class RankWorker implements Callable<Map<Integer, Integer>> {

	private IndexReader reader;
	private String []terms;
	
	public RankWorker(String indexDir, IndexType type
			, List<String> terms) {
		reader = new IndexReader(indexDir, type);
		this.terms = terms.toArray(new String[terms.size()]);
	}

	@Override
	public Map<Integer, Integer> call() throws Exception {
		return reader.queryOR(terms);
	}
}