package edu.buffalo.cse.irf14.searcher;

import java.util.Iterator;
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
 * A class that manages document search with 
 * {@code AND} query relation.
 */
public class andProxy implements Callable<TreeSet<TermFrequencyPerFile>> {

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
	public andProxy(String indexDir, List<Clause> clauses
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
			try {
				intersect(future.get());
			} catch (SearcherException e) {
				System.err.println("Null result in AND query starting "
						+ "with " + clauses.get(0).toString());
			}
		}
		
		return result;
	}
	
	private void intersect(TreeSet<TermFrequencyPerFile> docs)
			throws SearcherException {
		if (result.isEmpty()) {
			result.addAll(docs);
		} else {
			TreeSet<TermFrequencyPerFile> tempSet =
					new TreeSet<TermFrequencyPerFile>();
			Iterator<TermFrequencyPerFile> iter0 = result.iterator();
			Iterator<TermFrequencyPerFile> iter1 = docs.iterator();
			TermFrequencyPerFile v0 = null;
			TermFrequencyPerFile v1 = null;
			
			while (iter0.hasNext() && iter1.hasNext()) {
				if (v0 == null) {
					v0 = iter0.next();
					v1 = iter1.next();
					continue;
				}
				if (v0.getDocID() == v1.getDocID()) {
					tempSet.add(v0);
					v0 = iter0.next();
					v1 = iter1.next();
				} else if (v0.getDocID() > v1.getDocID()) {
					v1 = iter1.next();
				} else if (v0.getDocID() < v1.getDocID()) {
					v0 = iter0.next();
				}
			}
			if (v0.getDocID() == v1.getDocID()) {
				tempSet.add(v0);
				v0 = iter0.next();
				v1 = iter1.next();
			}
			
			result = tempSet;
			if (result.isEmpty()) {
				throw new SearcherException();
			}
		}
	}
}
