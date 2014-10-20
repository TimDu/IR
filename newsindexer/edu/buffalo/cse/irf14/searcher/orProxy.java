package edu.buffalo.cse.irf14.searcher;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;
import edu.buffalo.cse.irf14.query.Clause;
import edu.buffalo.cse.irf14.query.Operator;
import edu.buffalo.cse.irf14.query.Term;

/**
 * A class that performs document search with 
 * {@code OR} query relation
 */
public class orProxy implements Callable<TreeSet<TermFrequencyPerFile>> {

	private String indexDir;
	private List<Clause> clauses;
	private TreeSet<TermFrequencyPerFile> result;
	private ExecutorService exe;
	
	/**
	 * Constructor.
	 * @param indexDir Index directory
	 * @param query query that contains a group of terms in
	 * clauses
	 * @throws SearcherException 
	 */
	public orProxy(String indexDir, List<Clause> clauses
			, ExecutorService exe) throws SearcherException {
		for (Clause cl: clauses) {
			if ((cl.getStartOP() != null)
					&& (cl.getStartOP() != Operator.AND)
					|| cl.isQuery()) {
				throw new SearcherException();
			}
		}
		this.indexDir = indexDir;
		this.clauses = clauses;
		result = new TreeSet<TermFrequencyPerFile>();
		this.exe = exe;
	}

	@Override
	public TreeSet<TermFrequencyPerFile> call() throws Exception {
		IndexType type = null;
		List<String> terms = new LinkedList<String>();
		List<Future<TreeSet<TermFrequencyPerFile>>> futureList =
				new LinkedList<Future<TreeSet<TermFrequencyPerFile>>>();
		// Process step
		for (Clause cl: clauses) {
			Term term = (Term)cl.getComponent();
			for (int i = 0; i < term.size(); ++i) {
				if (type == null) {
					type = term.getIndex(i);
					terms.add(term.getTerm(i));
				} else if (type.equals(term.getIndex(i))) {
					terms.add(term.getTerm(i));
				} else {
					futureList.add(exe.submit(
									new andWorker(indexDir, type, terms)));
					terms.clear();
					type = term.getIndex(i);
					terms.add(term.getTerm(i));
				}
			}
		}
		futureList.add(exe.submit(new andWorker(indexDir, type, terms)));
		
		// Collect step
		for (Future<TreeSet<TermFrequencyPerFile>> future: futureList) {
			result.addAll(future.get());
		}
		
		return result;
	}
	
	
}
