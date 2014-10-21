package edu.buffalo.cse.irf14.searcher;

import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;
import edu.buffalo.cse.irf14.query.Clause;
import edu.buffalo.cse.irf14.query.Operator;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.Term;

public class SingleThreadSearch {
	String m_indexDir;
	private IndexReader m_termReader;
	private IndexReader m_placeReader;
	private IndexReader m_authorReader;
	private IndexReader m_categoryReader;

	public SingleThreadSearch(String indexDir) {

		this.m_indexDir = indexDir;
		m_termReader = new IndexReader(indexDir, IndexType.TERM);
		m_placeReader = new IndexReader(indexDir, IndexType.PLACE);
		m_authorReader = new IndexReader(indexDir, IndexType.AUTHOR);
		m_categoryReader = new IndexReader(indexDir, IndexType.CATEGORY);

	}

	public TreeSet<TermFrequencyPerFile> search(Query query) {
		Clause firstClause = query.getClause(0);
		Clause secondClause = query.getClause(1);
		// query.addClause(clause, op, startOP, isBegin)
		TreeSet<TermFrequencyPerFile> result = generalClauseRecurse(
				firstClause, secondClause);
		return result;
	}

	private TreeSet<TermFrequencyPerFile> generalClauseRecurse(
			Clause firstClause, Clause secondClause) {
		TreeSet<TermFrequencyPerFile> result = null;
		if (firstClause != null) {
			if (secondClause != null) {
				if (secondClause.getStartOP() == Operator.AND
						|| secondClause.getStartOP() == Operator.NOTAND) {
					result = andRecurse(firstClause, secondClause);
				} else if (secondClause.getStartOP() == Operator.OR) {
					result = orRecurse(firstClause, secondClause);
				} else {
					System.out.println("Got Not OR, we shouldn't get that! "
							+ secondClause.toString());
					result = null;
				}
			} else {
				result = getClausePosting(firstClause);
			}
		}
		return result;
	}

	private TreeSet<TermFrequencyPerFile> generalClauseRecurse(Query input) {
//		TreeSet<TermFrequencyPerFile> result = null;
//		if (input != null) {
//
//			if (input.get() == Operator.AND
//					|| secondClause.getStartOP() == Operator.NOTAND) {
//				result = andRecurse(firstClause, secondClause);
//			} else if (secondClause.getStartOP() == Operator.OR) {
//				result = orRecurse(firstClause, secondClause);
//			} else {
//				System.out.println("Got Not OR, we shouldn't get that! "
//						+ secondClause.toString());
//				result = null;
//			}
//
//		}
//		return result;
		return null;
	}

	public TreeSet<TermFrequencyPerFile> andRecurse(Query input) {
		return null;
	}

	public TreeSet<TermFrequencyPerFile> orRecurse(Query input) {
		return null;
	}

	public TreeSet<TermFrequencyPerFile> andRecurse(Clause input1, Clause input2) {
		if (!input2.isQuery()) {
			return intersect(getClausePosting(input1),
					getTermPosting((Term) input2.getComponent()));
		} else {
			return intersect(getClausePosting(input1), getClausePosting(input2));
		}

	}

	public TreeSet<TermFrequencyPerFile> orRecurse(Clause input, Clause input2) {
		return null;
	}

	public TreeSet<TermFrequencyPerFile> getClausePosting(Clause input) {
		if (!input.isQuery()) {
			return getTermPosting((Term) input.getComponent());
		}
		Query q = (Query) input.getComponent();
		if (q.size() > 1) {
			return generalClauseRecurse(q.getClause(0), q.getClause(1));
		}
		if (q.size() == 0) {
			System.out.println("clause contained no terms or queries!");
			return null;
		}
		return getClausePosting(q.getClause(0));
	}

	public TreeSet<TermFrequencyPerFile> getTermPosting(Term input) {
		return null;
	}

	public TreeSet<TermFrequencyPerFile> intersect(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
		return null;
	}

	public TreeSet<TermFrequencyPerFile> join(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
		return null;
	}

}
