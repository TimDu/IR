/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.document.Document;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {
	protected String m_indexDir;

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
		
		// Steps:
		// 1. Perform tokenization
		// 2. Pass through rule analysis
		// 3. Perform indexing
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
