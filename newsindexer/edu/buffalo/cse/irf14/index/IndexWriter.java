/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

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

	protected FileIndexDictionary m_fileDict;
	protected TermIndexWriter m_tiw;
	protected CategoryIndexWriter m_ciw;
	protected AuthorIndexWriter m_aiw;
	protected PlaceIndexWriter m_piw;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		m_indexDir = indexDir;
		m_fileDict = new FileIndexDictionary();
		m_tiw = new TermIndexWriter(m_fileDict, indexDir);
		m_ciw = new CategoryIndexWriter(m_fileDict, indexDir);
		m_aiw = new AuthorIndexWriter(m_fileDict, indexDir);
		m_piw = new PlaceIndexWriter(m_fileDict, indexDir);

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

		m_tiw.performIndexLogic(d, FieldNames.CONTENT);
		if (d.getField(FieldNames.NEWSDATE) != null) {
			m_tiw.performIndexLogic(d, FieldNames.NEWSDATE);
		}
		if (d.getField(FieldNames.PLACE) != null) {
			m_piw.performIndexLogic(d, FieldNames.PLACE);
		}
		m_ciw.performIndexLogic(d, FieldNames.CATEGORY);
		if (d.getField(FieldNames.AUTHOR) != null) {
			m_aiw.performIndexLogic(d, FieldNames.AUTHOR);
		}
		if (d.getField(FieldNames.AUTHORORG) != null) {
			m_aiw.performIndexLogic(d, FieldNames.AUTHORORG);
		}
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
			Path indexPath = Paths.get(m_indexDir,
					IndexGlobalVariables.fileDicFileName);
			FileOutputStream fileOut = new FileOutputStream(
					indexPath.toString());

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_fileDict);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IndexerException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException();
		}

	}

}
