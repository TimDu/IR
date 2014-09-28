package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

 
public class TermIndexReader implements IndexReaderInterface {
	protected String m_indexDir;
	protected TermIndexFileReader tifr;
	protected TermIndexDictionary m_termDict;
	protected IndexDictionary m_fileDict;

	public TermIndexReader(String indexDir) {
		m_indexDir = indexDir;
		setup();
	}

	protected void setup() {
		tifr = new TermIndexFileReader(m_indexDir);
		m_termDict = new TermIndexDictionary();
		m_fileDict = new IndexDictionary();

		try {
			OpenTermDictionary();	
			OpenFileDictionary();
		} catch (IOException e) {

			e.printStackTrace();
			assert (false);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			assert (false);
		}

	}
	
	protected void OpenFileDictionary() throws IOException, ClassNotFoundException
	{
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.fileDicFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_fileDict = (IndexDictionary) instream.readObject();

		instream.close();
		fileIn.close();
	}
	
	protected void OpenTermDictionary() throws IOException, ClassNotFoundException
	{
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.termDicFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_termDict = (TermIndexDictionary) instream.readObject();

		instream.close();
		fileIn.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#getTotalKeyTerms()
	 */

	@Override
	public int getTotalKeyTerms() {
		return m_termDict.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.irf14.index.IndexReaderInterface#getTotalValueTerms()
	 */

	@Override
	public int getTotalValueTerms() {
		return m_fileDict.size();
	}

	/**
	 * Gives a mapping between a file name and the number of occurrences a term
	 * had within that file
	 * 
	 * PRECONDITIONS: Expects that the term and postings files are opened.
	 * 
	 * 
	 * @return The total number of terms
	 */
	@Override
	public Map<String, Integer> getPostings(String term) {
		if(!m_termDict.exists(term))
		{
			return null;
		}
		
		int termID = m_termDict.elementToID(term);
		
		Map<String, Integer> retVal = new HashMap<String, Integer>();
		try {

			PriorityQueue<Integer> pqi = tifr.getPostings(termID);
			for(Integer i: pqi)
			{
				String fileName = m_fileDict.getElementfromID(i);
				if(retVal.containsKey(fileName))
				{
					retVal.put(fileName, retVal.get(fileName) + 1);
				}
				else
				{
					retVal.put(fileName, 1);
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
			assert (false);
		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#getTopK(int)
	 */
	@Override
	public List<String> getTopK(int k) {
 
		return m_termDict.getTopK(k);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.irf14.index.IndexReaderInterface#query(java.lang.String)
	 */
	@Override
	public Map<String, Integer> query(String... terms) {
		// TODO Need to implement for extra credit
		return null;
	}

}
