package edu.buffalo.cse.irf14.searcher;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import edu.buffalo.cse.irf14.index.IndexFileReader;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;

/**
 * {@code And} relation worker thread. <b>Only
 * called by</b> {@link AndProxy} <b>class</b>
 */
class AndWorker implements Callable<TreeSet<TermFrequencyPerFile>> {

	private IndexReader reader;
	private String []terms;
	
	public AndWorker(String indexDir, IndexType type
			, List<String> terms) {
		reader = new IndexReader(indexDir, type);
		this.terms = terms.toArray(new String[terms.size()]);
	}

	/**
	 * @return map of document ID and its term frequency pair
	 */
	@Override
	public TreeSet<TermFrequencyPerFile> call() throws Exception {
		Map<String, Integer> temp = reader.query(terms);
		TreeSet<TermFrequencyPerFile> result =
				new TreeSet<TermFrequencyPerFile>();
		TermFrequencyPerFile tfpf;
		
		for (String fID: temp.keySet()) {
			tfpf  = new TermFrequencyPerFile(
							new IndexFileReader().OpenFileDictionary()
							.elementToID(fID), temp.get(fID));
			result.add(tfpf);
		}
		
		return result;
	}
}
