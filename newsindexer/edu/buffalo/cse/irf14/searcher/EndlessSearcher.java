package edu.buffalo.cse.irf14.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import edu.buffalo.cse.irf14.index.IndexFileReader;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;
import edu.buffalo.cse.irf14.query.Clause;
import edu.buffalo.cse.irf14.query.Operator;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.Term;

/**
 * The searcher class that retrieve document information based on
 * given query. <br>
 * <b>NOTE:</b> It is said in the spec: {@code 
 * A given clause would either have a Boolean operator or not, but not both.} 
 * Which implies that logically only one operator exists in a clause, e.g.'
 * {@code A AND B OR C}' without any parenthesis is not allowed.
 */
public class EndlessSearcher {
	
	private ExecutorService exe;
	private String indexDir;

	public EndlessSearcher(ExecutorService exe, String indexDir) {
		this.exe = exe;
		this.indexDir = indexDir;
	}
	
	/**
	 * Method that searches all document IDs based on query request.<br>
	 * It first categorize all clauses into group of terms connected
	 * with the same operator(AND|OR), and assign different groups
	 * to searcher proxies.<br>
	 * After the clauses in the same query level has been processed by
	 * worker threads, this method collect them and process with NOT
	 * operator in this single thread.
	 * 
	 * @param query initial query
	 * @return result posting
	 * @throws SearcherException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public TreeSet<TermFrequencyPerFile> search(Query query)
			throws SearcherException, InterruptedException, ExecutionException {
		Clause tempClause;
		// Clauses with (start operator|default operator)
		List<Clause> andAndClauses = new LinkedList<Clause>();
		List<Clause> andOrClauses = new LinkedList<Clause>();
		List<Clause> orAndClauses = new LinkedList<Clause>();
		List<Clause> orOrClauses = new LinkedList<Clause>();
		List<Clause> tempClauses;
		// Sets with the clauses of (start operator|default operator)
		List<Future<TreeSet<TermFrequencyPerFile>>> andSets =
				new LinkedList<Future<TreeSet<TermFrequencyPerFile>>>();
		List<Future<TreeSet<TermFrequencyPerFile>>> orSets =
				new LinkedList<Future<TreeSet<TermFrequencyPerFile>>>();
		// Result set from sub-query
		List<TreeSet<TermFrequencyPerFile>> subAndSet =
				new LinkedList<TreeSet<TermFrequencyPerFile>>();
		List<TreeSet<TermFrequencyPerFile>> subOrSet =
				new LinkedList<TreeSet<TermFrequencyPerFile>>();
		List<TreeSet<TermFrequencyPerFile>> subNotSet =
				new LinkedList<TreeSet<TermFrequencyPerFile>>();
		// Result set
		TreeSet<TermFrequencyPerFile> result;
		
		for (int i = 0; i < query.size(); ++i) {
			tempClause = query.getClause(i);
			
			if (tempClause.isQuery()) {
				assignAllWorks(andAndClauses, andOrClauses
						, orAndClauses, orOrClauses, andSets, orSets);
				if ((tempClause.getStartOP() == null)
						|| tempClause.getStartOP().equals(Operator.AND)) {
					subAndSet.add(search((Query) tempClause.getComponent()));
				} else if (tempClause.getStartOP().equals(Operator.OR)) {
					subOrSet.add(search((Query) tempClause.getComponent()));
				} else {
					subNotSet.add(search((Query) tempClause.getComponent()));
				}
			}
			
			// Group similar clauses
			tempClauses = termSearch(tempClause, andAndClauses
					, andOrClauses, orAndClauses, orOrClauses);
			
			// Assign work
			if (tempClauses != null) {
				// A clause is ready to get queried
				assignWork(tempClauses, andSets, orSets);
			} else {
				if ((tempClause.getStartOP() != null) 
						&& tempClause.getStartOP().equals(Operator.NOT)) {
					Term t = (Term)tempClause.getComponent();
					assert(t.size() == 1);
					try {
						subNotSet.add(notSearch(tempClause));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		assignAllWorks(andAndClauses, andOrClauses
				, orAndClauses, orOrClauses, andSets, orSets);
		
		// Wait for AND set result
		for (Future<TreeSet<TermFrequencyPerFile>> future: andSets) {
			subAndSet.add(future.get());
		}
		// Integrate All relations at current clause level
		result = intersect(subAndSet);
		
		// Wait for OR set result
		for (Future<TreeSet<TermFrequencyPerFile>> future: orSets) {
			subOrSet.add(future.get());
		}
		for (TreeSet<TermFrequencyPerFile> set: subOrSet) {
			result.addAll(set);
		}

		// Exclude NOT queries
		result.removeAll(subNotSet);
		
		return result;
	}
	
	/**
	 * Method that helps to integrate document IDs in {@code AND}
	 * relation set.
	 * 
	 * @param sets sets with {@code AND} relation
	 * @return integrated set
	 * @throws SearcherException 
	 */
	private TreeSet<TermFrequencyPerFile>
		intersect(List<TreeSet<TermFrequencyPerFile>> sets)
				throws SearcherException {
		// Null check
		if (sets.isEmpty()) {
			return null;
		}
		
		int index = -1;
		int size = 0;
		TreeSet<TermFrequencyPerFile> result;
		
		// Find the minimum sized set
		for (int i = 0; i < sets.size(); ++i) {
			if (index < 0) {
				index = i;
				size = sets.get(i).size();
			} else {
				if (sets.get(i).size() < size) {
					index = i;
					size = sets.get(i).size();
				}
			}
		}
		result = sets.get(index);
		
		// Intersection
		for (int i = 0; i < sets.size(); ++i) {
			if (i != index) {	
				Iterator<TermFrequencyPerFile> iter0 = result.iterator();
				Iterator<TermFrequencyPerFile> iter1 = sets.get(i).iterator();
				TermFrequencyPerFile v0 = null;
				TermFrequencyPerFile v1 = null;
				TreeSet<TermFrequencyPerFile> tempSet =
						new TreeSet<TermFrequencyPerFile>();
				// Find new intersection set
				while (iter0.hasNext() && iter1.hasNext()) {
					if (v0 == null) {
						v0 = iter0.next();
						v1 = iter1.next();
					} else {
						if (v0.getDocID() == v1.getDocID()) {
							tempSet.add(v0);
							v0 = iter0.next();
							v1 = iter1.next();
						} else if (v0.getDocID() > v1.getDocID()) {
							v1 = iter1.next();
						} else {
							v0 = iter0.next();
						}
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
		
		return result;
	}
	
	private TreeSet<TermFrequencyPerFile> notSearch(Clause cl)
			throws ClassNotFoundException, IOException {
		Term term = (Term)cl.getComponent();
		Map<String, Integer> temp = new IndexReader(
				indexDir, term.getIndex(0)).query(term.getTerm(0));
		TreeSet<TermFrequencyPerFile> result =
				new TreeSet<TermFrequencyPerFile>();
		TermFrequencyPerFile tfpf;
		
		for (String fID: temp.keySet()) {
			tfpf  = new TermFrequencyPerFile(
							new IndexFileReader(indexDir).OpenFileDictionary()
							.elementToID(fID), temp.get(fID));
			result.add(tfpf);
		}
		
		return result;
	}
	
	/**
	 * Method that searches for terms in a clause
	 * 
	 * @return a list of clauses that is ready for query
	 */
	private List<Clause> termSearch(Clause cl
			, List<Clause> aA, List<Clause> aO
			, List<Clause> oA, List<Clause> oO) {
		if ((cl.getStartOP() == null)
				|| cl.getStartOP().equals(Operator.AND)) {
			if (cl.getDefaultOP().equals(Operator.AND)) {
				aA.add(cl);
				return getFilledList(aO, oA, oO);
			} else {
				aO.add(cl);
				return getFilledList(oA, oO, aA);
			}
		} else if (cl.getStartOP().equals(Operator.OR)) {
			if (cl.getDefaultOP().equals(Operator.AND)) {
				oA.add(cl);
				return getFilledList(oO, aA, aO);
			} else {
				oO.add(cl);
				return getFilledList(aA, aO, oA);
			}
		}
		return null;
	}
	
	/**
	 * Method that tests list's emptiness.
	 * 
	 * @return the filled list; or {@code null} if all are empty
	 */
	private List<Clause> getFilledList(List<Clause> a, List<Clause> b
			, List<Clause> c) {
		if (a.isEmpty()) {
			if (b.isEmpty()) {
				if (c.isEmpty()) {
					return null;
				}
				return c;
			}
			return b;
		}
		return a;
	}
	
	/**
	 * Method that perform query according to the provided clauses.
	 * It is assumed that all clauses started with AND|OR relation
	 */
	private void assignWork(List<Clause> clauses
			, List<Future<TreeSet<TermFrequencyPerFile>>> aSet
			, List<Future<TreeSet<TermFrequencyPerFile>>> oSet) {
		try {
			if (!clauses.isEmpty()) {
				if (clauses.get(0).getDefaultOP().equals(Operator.AND)) {
					if ((clauses.get(0).getStartOP() == null)
							|| clauses.get(0).getStartOP()
							.equals(Operator.AND)) {
						aSet.add(exe.submit(
								new AndProxy(indexDir, clauses, exe)));
					} else if (clauses.get(0).getStartOP()
							.equals(Operator.OR)) {
						oSet.add(exe.submit(
								new AndProxy(indexDir, clauses, exe)));
					}
				} else {
					if ((clauses.get(0).getStartOP() == null)
							|| clauses.get(0).getStartOP()
							.equals(Operator.AND)) {
						aSet.add(exe.submit(
								new OrProxy(indexDir, clauses, exe)));
					} else if (clauses.get(0).getStartOP()
							.equals(Operator.OR)) {
						oSet.add(exe.submit(
								new OrProxy(indexDir, clauses, exe)));
					}
				}
			}
		} catch (SearcherException e) {
				e.printStackTrace();
		}
		clauses.clear();
	}
	
	/**
	 * Method that assigns all left works in clause list to proxies.
	 */
	private void assignAllWorks(List<Clause> aA, List<Clause> aO
			, List<Clause> oA, List<Clause> oO
			, List<Future<TreeSet<TermFrequencyPerFile>>> andSets
			, List<Future<TreeSet<TermFrequencyPerFile>>> orSets) {
		try {
			if (!aA.isEmpty()) {
				andSets.add(
						exe.submit(new AndProxy(
								indexDir, aA, exe)));
				aA.clear();
			}
			if (!aO.isEmpty()) {
				andSets.add(
						exe.submit(new OrProxy(
								indexDir, aO, exe)));
				aO.clear();;
			}
			if (!oA.isEmpty()) {
				orSets.add(
						exe.submit(new AndProxy(
								indexDir, oA, exe)));
				oA.clear();;
			}
			if (!oO.isEmpty()) {
				orSets.add(
						exe.submit(new OrProxy(
								indexDir, oO, exe)));
				oO.clear();;
			}
		} catch (SearcherException e) {
			e.printStackTrace();
		}
	}
}
