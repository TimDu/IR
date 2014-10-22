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
		// query.addClause(clause, op, startOP, isBegin)
		TreeSet<TermFrequencyPerFile> result = generalQueryRecurse(query);
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

	private TreeSet<TermFrequencyPerFile> generalQueryRecurse(Query input) {
		if (input == null || input.size() == 0) {
			return null;
		}
		Operator clauseOp = input.getClause(0).getStartOP() == null ? input
				.getClause(0).getDefaultOP() : input.getClause(0).getStartOP();
		TreeSet<TermFrequencyPerFile> result = new TreeSet<TermFrequencyPerFile>();
		for (int i = 0; i < input.size(); i++) {
			Clause temp = input.getClause(i);
			TreeSet<TermFrequencyPerFile> tfpf = getClausePosting(temp);
			
			Operator tempOp = temp.getStartOP() == null ? temp.getDefaultOP()
					: temp.getStartOP();
//			if(tempOp == Operator.NOT || 
//					tempOp == Operator.NOTAND ||
//					tempOp == Operator.NOTOR)
//			{
//				debugAssert (input.size() == 2);
//			}
			
			if (result.size() != 0) {
				switch (tempOp) {
				case AND:
					debugAssert (clauseOp.equals(Operator.AND));
					result = intersect(result, tfpf);
					break;
				case OR:
					debugAssert (clauseOp.equals(Operator.OR));
					result = join(result, tfpf);
					break;
				case NOTAND:
				case NOTOR:
				case NOT:
					// shouldn't have these at this point
					debugAssert (false);
					break;
				default:
					System.out.println("error, bad operator");
				}
			} else {
				result.addAll(tfpf);
			}
		}
		return null;
	}
  
	public TreeSet<TermFrequencyPerFile> getClausePosting(Clause input) {
		if (!input.isQuery()) {
			return getTermPosting((Term) input.getComponent());
		}
		Query q = (Query) input.getComponent();
		if (q.size() > 1) {
			return generalQueryRecurse(q);
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
	
	public TreeSet<TermFrequencyPerFile> except(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
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
	
	public void debugAssert(boolean input)
	{
		if(!input)
		{
			System.out.println("debug assert failed for");
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			    System.out.println(ste);
			}
		}
	}

}
