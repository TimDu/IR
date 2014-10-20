package edu.buffalo.cse.irf14.searcher;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;

/**
 * {@code Or} relation worker thread. <b>Only
 * called by</b> {@link OrProxy} <b>class</b>
 */
public class OrWorker implements Callable<TreeSet<TermFrequencyPerFile>> {

	private IndexReader reader;
	private String []terms;
	
	public OrWorker(String indexDir, IndexType type
			, List<String> terms) {
		reader = new IndexReader(indexDir, type);
		this.terms = terms.toArray(new String[terms.size()]);
	}

	@Override
	public TreeSet<TermFrequencyPerFile> call() throws Exception {
		Map<Integer, Integer> temp = reader.queryOR(terms);
		TreeSet<TermFrequencyPerFile> result =
				new TreeSet<TermFrequencyPerFile>();
		TermFrequencyPerFile tfpf;
		
		for (Integer dID: temp.keySet()) {
			tfpf  = new TermFrequencyPerFile(dID, temp.get(dID));
			result.add(tfpf);
		}
		
		return result;
	}
}
