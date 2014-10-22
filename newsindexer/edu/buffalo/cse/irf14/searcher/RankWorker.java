package edu.buffalo.cse.irf14.searcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.buffalo.cse.irf14.document.FieldNames;
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
		for (int i = 0; i < terms.size(); ++i) {
			FieldNames fN = null;
			switch(type) {
			case CATEGORY:
				fN = FieldNames.CATEGORY;
				break;
			case AUTHOR:
				fN = FieldNames.AUTHOR;
				break;
			case PLACE:
				fN = FieldNames.PLACE;
				break;
			case TERM:
				fN = FieldNames.CONTENT;
				break;
			}
			this.terms[i] = SingleThreadSearch.getAnalyzer(this.terms[i], fN);
		}
	}

	@Override
	public Map<Integer, Integer> call() throws Exception {
		return reader.queryOR(terms);
	}
}