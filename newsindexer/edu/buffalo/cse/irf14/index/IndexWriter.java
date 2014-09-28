/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.document.Document;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	
	

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		m_indexDir = indexDir;
		m_fileDict = new IndexDictionary();
		m_tiw = new TermIndexWriter(m_fileDict, indexDir);
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
		//m_piw.performIndexLogic(d);
		//m_ciw.performIndexLogic(d);
		//m_aiw.performIndexLogic(d);

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
//			m_piw.finishIndexing();
//			m_ciw.finishIndexing();
//			m_aiw.finishIndexing();
			Path indexPath = Paths.get(m_indexDir, IndexGlobalVariables.fileDicFileName);
			FileOutputStream fileOut = new FileOutputStream(indexPath.toString());

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
