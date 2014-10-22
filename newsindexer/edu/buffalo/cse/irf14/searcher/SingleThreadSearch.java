package edu.buffalo.cse.irf14.searcher;

import java.util.TreeMap;
import java.util.TreeSet;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.FileIndexDictionary;
import edu.buffalo.cse.irf14.index.IndexFileReader;
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
	
	private FileIndexDictionary m_fid;

	public SingleThreadSearch(String indexDir) {

		this.m_indexDir = indexDir;
 
		
		m_termReader = new IndexReader(indexDir, IndexType.TERM);
		m_placeReader = new IndexReader(indexDir, IndexType.PLACE);
		m_authorReader = new IndexReader(indexDir, IndexType.AUTHOR);
		m_categoryReader = new IndexReader(indexDir, IndexType.CATEGORY);
		try{
		m_fid = IndexFileReader.getFileDictionary(indexDir);
		}catch (Exception e)
		{
			System.out.println("Error opening file dictionary, aborting!");
			e.printStackTrace();
			assert false;
		}
	}

	public TreeSet<TermFrequencyPerFile> search(Query query) {
		// query.addClause(clause, op, startOP, isBegin)
		TreeSet<TermFrequencyPerFile> result = generalQueryRecurse(query);
		return result;
	}

	private TreeSet<TermFrequencyPerFile> generalQueryRecurse(Query input) {
		if (input == null || input.size() == 0) {
			return null;
		}
		Operator clauseOp = null;
		if (input.size() > 1) {
			clauseOp = input.getClause(1).getStartOP() == null ? input
					.getClause(1).getDefaultOP() : input.getClause(1)
					.getStartOP();
		}
		TreeSet<TermFrequencyPerFile> result = new TreeSet<TermFrequencyPerFile>();
		for (int i = 0; i < input.size(); i++) {
			Clause temp = input.getClause(i);
			TreeSet<TermFrequencyPerFile> tfpf = getClausePosting(temp);

			Operator tempOp = temp.getStartOP() == null ? temp.getDefaultOP()
					: temp.getStartOP();

			if ((i > 0) && (result.size() > 0)) {
				switch (tempOp) {
				case AND:
				case NOTOR:
					debugAssert(clauseOp.equals(Operator.AND));
					if (!temp.isQuery()
							&& temp.getComponent().toString().contains("<")) {
						result = except(result, tfpf);
					} else {
						result = intersect(result, tfpf);
					}
					break;
				case OR:
				case NOTAND:
					debugAssert(clauseOp.equals(Operator.OR));
					result = join(result, tfpf);
					break;
				case NOT:
					result = except(result, tfpf);
					if(  tempOp == Operator.NOTOR)
					{
						// shouldn't have these at this point
						System.out.println(tempOp);
						debugAssert(false);
					}
					break;
				default:
					System.out.println("error, bad operator");
				}
			} else if (i == 0) {
				result.addAll(tfpf);
			}
		}
		return result;
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

		// map = placeReader.getPostings(query);
		// System.out.println("Washington: " + map.keySet());
		TreeSet<TermFrequencyPerFile> results = null;
		if (input.size() > 1) {
			results = new TreeSet<TermFrequencyPerFile>();
			for (int i = 0; i < input.size(); i++) {
				results.addAll(getSingleTermPosting(input.getTerm(i),
						input.getIndex(i)));
			}
		} else if (input.size() == 1) {
			results = getSingleTermPosting(input.getTerm(0), input.getIndex(0));
		}
		return results;
	}

	public TreeSet<TermFrequencyPerFile> getSingleTermPosting(String input,
			IndexType it) {
		TreeMap<String, Integer> results = null;
		if (it == null) {
			System.out.println("Bad index type");
			return null;
		}
		
		switch (it) {
		case AUTHOR:
			input = getAnalyzer(input, FieldNames.AUTHOR);
			results = (TreeMap<String, Integer>)m_authorReader.getPostings(input);
			break;
		case CATEGORY:
			input = getAnalyzer(input, FieldNames.CATEGORY);
			results = (TreeMap<String, Integer>)m_categoryReader.getPostings(input);
			break;
		case PLACE:
			input = getAnalyzer(input, FieldNames.PLACE);
			results = (TreeMap<String, Integer>)m_placeReader.getPostings(input);
			break;
		case TERM:
			input = getAnalyzer(input, FieldNames.CONTENT);
			results = (TreeMap<String, Integer>)m_termReader.getPostings(input);
			break;
		}
		TreeSet<TermFrequencyPerFile> tsresults = new TreeSet<TermFrequencyPerFile>();
		
		if (results != null) {
			for(String s: results.navigableKeySet())
			{
				debugAssert(m_fid.elementToID(s) != -1);
				tsresults.add(new TermFrequencyPerFile(m_fid.elementToID(s), -1));
			}
		}
		
		return tsresults;

	}

	public TreeSet<TermFrequencyPerFile> except(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
		TreeSet<TermFrequencyPerFile> result = new TreeSet<TermFrequencyPerFile>();
		for (TermFrequencyPerFile tfpf : op1) {
			if (!op2.contains(tfpf)) {
				result.add(tfpf);
			}
		}
		return result;
	}

	public TreeSet<TermFrequencyPerFile> intersect(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
		TreeSet<TermFrequencyPerFile> result = new TreeSet<TermFrequencyPerFile>();
		for (TermFrequencyPerFile tfpf : op1) {
			if (op2.contains(tfpf)) {
				result.add(tfpf);
			}
		}
		return result;
	}

	public TreeSet<TermFrequencyPerFile> join(
			TreeSet<TermFrequencyPerFile> op1, TreeSet<TermFrequencyPerFile> op2) {
		TreeSet<TermFrequencyPerFile> result = new TreeSet<TermFrequencyPerFile>();
		result.addAll(op1);
		result.addAll(op2);
		return result;
	}

	public void debugAssert(boolean input) {
		if (!input) {
			System.out.println("debug assert failed for");
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
			}
		}
	}
	
	protected static String getAnalyzer(String string, FieldNames fn) {
		Tokenizer tknizer = new Tokenizer();
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		try {
			TokenStream stream;
			if (fn.equals(FieldNames.AUTHOR) || fn.equals(FieldNames.AUTHORORG)
					|| fn.equals(FieldNames.PLACE)) {
				stream = new Tokenizer("=").consume(string);
			} else {
				stream = tknizer.consume(string);
			}
			Analyzer analyzer = fact.getAnalyzerForField(fn, stream);

			while (analyzer.increment()) {

			}

			stream = analyzer.getStream();

			stream.reset();
			if (!stream.hasNext()) {
				return null;
			}
			String retStr =stream.next().toString();
			while(stream.hasNext())
			{
				retStr = retStr + " " + stream.next().toString();
			}
			return retStr;
		} catch (TokenizerException e) {
			e.printStackTrace();
		}
		return null;
	}

}
