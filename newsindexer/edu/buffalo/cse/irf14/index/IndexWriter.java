/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.document.Document;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {
	protected String m_indexDir;

	protected IndexDictionary m_fileDict;
	protected TermIndexWriter m_tiw;
	protected CategoryIndexWriter m_ciw;
	protected PlaceIndexWriter m_piw;
	protected AuthorIndexWriter m_aiw;

	final protected String m_fileDicFileName = "file.dict";
	
	final protected String m_placeDicFileName = "place.dict";
	final protected String m_authorDicFileName = "author.dict";
	final protected String m_categoryDicFileName = "cat.dict";

	final protected String m_placeIndexFileName = "place.index";
	final protected String m_authorIndexFileName = "author.index";
	final protected String m_categoryIndexFileName = "cat.index";

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		m_indexDir = indexDir;
		m_fileDict = new IndexDictionary();
		// TODO: Make sure we're correctly setting up our path directory!!!

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
		/*
		 * It is expected that you use the AnalyzerFactory and
		 * TokenFilterFactory classes while implementing these methods.
		 */

		m_tiw.performIndexLogic(d);
		m_piw.performIndexLogic(d);
		m_ciw.performIndexLogic(d);
		m_aiw.performIndexLogic(d);

	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		try {
			m_tiw.finishIndexing();
			m_piw.finishIndexing();
			m_ciw.finishIndexing();
			m_aiw.finishIndexing();
			FileOutputStream fileOut = new FileOutputStream(m_fileDicFileName);

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_fileDict);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
