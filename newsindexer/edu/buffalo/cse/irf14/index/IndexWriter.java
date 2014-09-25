/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {
	protected String m_indexDir;

	final protected String m_termDic = "term.dict";
	final protected String m_placeDic = "place.dict";
	final protected String m_authorDic = "author.dict";
	final protected String m_categoryDic = "cat.dict";

	final protected String m_termIndex = "term.index";
	final protected String m_placeIndex = "place.index";
	final protected String m_authorIndex = "author.index";
	final protected String m_categoryIndex = "cat.index";

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		// TODO : YOU MUST IMPLEMENT THIS
		m_indexDir = indexDir;
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		// TODO : YOU MUST IMPLEMENT THIS
		/*
		 * It is expected that you use the AnalyzerFactory and
		 * TokenFilterFactory classes while implementing these methods.
		 */

		// Things to consider
		// 1. Rule order

		// General Steps:
		// 1. Perform tokenization
		// 2. Pass through rule analysis
		// 3. Perform dictionary keeping and indexing

		// 4 types of dictionaries and indices
		// Term, Category, Place, Author
		performTermIndexLogic(d);
		performCategoryIndexLogic(d);
		performPlaceIndexLogic(d);
		performAuthorIndexLogic(d);

	}

	private TokenStream createTermStream(Document d, FieldNames type) {
		Tokenizer tknizer = new Tokenizer();
		TokenStream tstream = new TokenStream();
		try {
			String[] arr =d.getField(type); 
			for(String s: arr)
			{
				tstream.append(tknizer.consume(s));
			}
			
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tstream;
	}

	private void performTermIndexLogic(Document d) {
		// TODO: Create the actual index file here
		
		TokenStream tstream = createTermStream(d, FieldNames.CONTENT);
		if(tstream == null)
		{
			// TODO: Figure out error handling
			return;
		}
		AnalyzerFactory af = AnalyzerFactory.getInstance();
		Analyzer analyzer = af.getAnalyzerForField(FieldNames.CONTENT, tstream);
		try {
			while(analyzer.increment()) {
				
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		tstream = analyzer.getStream();
		
		while(tstream.hasNext())
		{
			Token term = tstream.next();
			// look up term
			int termID = termToID(term.toString());
			// TODO: add token to term index
			// Finer point: need to be memory conscious about index size.
			// Lookup how java deals with opening large files.
		}

	}
	
	private int termToID(String term)
	{
		// TODO: need to do dictionary logic here
		// if we can't find the term, then add it
		return 0;
	}

	private void performCategoryIndexLogic(Document d) {

	}

	private void performPlaceIndexLogic(Document d) {

	}

	private void performAuthorIndexLogic(Document d) {

	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		// TODO
	}
}
