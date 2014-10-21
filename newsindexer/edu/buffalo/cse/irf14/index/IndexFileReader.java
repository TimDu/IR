package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class IndexFileReader implements IndexReaderInterface {

	protected String m_indexDir = null;
	protected String m_indexName = null;
	protected String m_dictName = null;
	protected TermIndexDictionary m_termDict = null;
	protected FileIndexDictionary m_fileDict = null;
	protected TermIndexFileReader tifr = null;
	protected double m_avgDocLength = 0;

	/**
	 * Hacked constructor for getting file dictionary
	 */
	public IndexFileReader(String indexDir) {
		// Hacked
		m_indexDir = indexDir;
	}

	public IndexFileReader(String indexDir, String indexName, String dictName) {
		m_indexDir = indexDir;
		m_indexName = indexName;
		m_dictName = dictName;

		setup();
	}

	protected void setup() {

		m_termDict = new TermIndexDictionary();
		m_fileDict = new FileIndexDictionary();

		try {
			OpenTermDictionary();
			tifr = new TermIndexFileReader(m_indexDir, m_indexName, m_termDict);
			OpenFileDictionary();
			OpenFileStats();
		} catch (IOException e) {

			e.printStackTrace();
			assert (false);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			assert (false);
		}

	}

	public FileIndexDictionary OpenFileDictionary() throws IOException,
			ClassNotFoundException {
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.fileDicFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_fileDict = (FileIndexDictionary) instream.readObject();

		instream.close();
		fileIn.close();
		return m_fileDict;
	}

	public double OpenFileStats() throws IOException, ClassNotFoundException {
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.statsFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_avgDocLength = instream.readDouble();
		// System.out.println("m_avgDocLength: " + m_avgDocLength);
		instream.close();
		fileIn.close();
		return m_avgDocLength;
	}

	protected void OpenTermDictionary() throws IOException,
			ClassNotFoundException {

		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir, m_dictName)
						.toString()));
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
		if (m_termDict != null) {
			return m_termDict.size();
		} else {
			// Hacked
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.irf14.index.IndexReaderInterface#getTotalValueTerms()
	 */

	@Override
	public int getTotalValueTerms() {
		if (m_fileDict != null) {
			return m_fileDict.size();
		} else {
			// Hacked
			return -1;
		}
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
		if ((m_termDict == null) && !m_termDict.exists(term)) {
			return null;
		}
		Map<String, Integer> retVal = new HashMap<String, Integer>();

		try {

			TreeSet<TermFrequencyPerFile> tsTerm;
			if (term.contains(" ") && m_indexName.equals(IndexGlobalVariables.termIndexFileName)) {
				String[] termArr = term.trim().split(" ");
				ArrayList<Integer> termIDS = new ArrayList<Integer>();
				for (String s : termArr) {
					termIDS.add(m_termDict.elementToID(s));

				}

				// getPostings
				tsTerm = tifr.getPostings(termIDS);
				if(tsTerm == null)
				{
					return null;
				}

			} else {
				int termID = m_termDict.elementToID(term);
				tsTerm = tifr.getPostings(termID);
			}
			for (TermFrequencyPerFile i : tsTerm) {
				String fileName = m_fileDict.getElementfromID(i.getDocID());

				retVal.put(fileName, i.getTermFrequency());

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
		if (m_termDict != null) {
			return m_termDict.getTopK(k);
		} else {
			// Hacked
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.irf14.index.IndexReaderInterface#query(java.lang.String)
	 */
	@Override
	public Map<String, Integer> query(String... terms) {
		if (tifr == null) {
			// Hacked
			return null;
		}

		int index = -1; // Index ID for the shortest posting list
		int minSize = -1; // The size of current shortest posting
		TreeSet<TermFrequencyPerFile> currentPosting;
		TreeSet<TermFrequencyPerFile> tempPosting;
		List<Integer> termIDs = new LinkedList<Integer>();
		List<TreeSet<TermFrequencyPerFile>> postings = new LinkedList<TreeSet<TermFrequencyPerFile>>();
		Map<String, Integer> result = new HashMap<String, Integer>();

		// Map terms to termIDs
		for (String term : terms) {
			termIDs.add(m_termDict.elementToID(term));
		}

		// Get posting list for each term
		for (int tID : termIDs) {
			try {
				currentPosting = tifr.getPostings(tID);
				postings.add(currentPosting);
				if (currentPosting.size() > minSize) {
					minSize = currentPosting.size();
					index = postings.size() - 1;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Perform postings intersection starting from
		// the shortest list
		currentPosting = postings.get(index);
		tempPosting = new TreeSet<TermFrequencyPerFile>();
		for (int i = 0; i < postings.size(); ++i) {
			if (i != index) {
				Iterator<TermFrequencyPerFile> iter0 = currentPosting
						.descendingIterator();
				Iterator<TermFrequencyPerFile> iter1 = postings.get(i)
						.descendingIterator();
				TermFrequencyPerFile value0 = null;
				TermFrequencyPerFile value1 = null;
				while (iter0.hasNext() && iter1.hasNext()) {
					if (value0 == null) {
						value0 = iter0.next();
						value1 = iter1.next();
						continue;
					}
					if (value1.getDocID() == value0.getDocID()) {
						tempPosting.add(value1);
						value0 = iter0.next();
						value1 = iter1.next();
					} else if (value1.getDocID() > value0.getDocID()) {
						value0 = iter0.next();
					} else if (value1.getDocID() < value0.getDocID()) {
						value1 = iter1.next();
					}
				}
				if (value1.getDocID() == value0.getDocID()) {
					tempPosting.add(value1);
					value0 = iter0.next();
					value1 = iter1.next();
				}

				if (!tempPosting.isEmpty()) {
					currentPosting = tempPosting;
					tempPosting = new TreeSet<TermFrequencyPerFile>();
				} else {
					// Exit intersection before no qualified document
					// is left!
					System.err.print("AND query on [ ");
					for (String term : terms) {
						System.err.print(term + " ");
					}
					System.err.println("] terminated at " + terms[i]);
					break;
				}
			}
		}

		// Get map result
		for (TermFrequencyPerFile tfd : currentPosting) {
			result.put(m_fileDict.getElementfromID(tfd.getDocID()),
					tfd.getTermFrequency());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.buffalo.cse.irf14.index.IndexReaderInterface#queryOR(java.lang.String
	 * [])
	 */
	@Override
	public Map<Integer, Integer> queryOR(String... terms) {
		if (m_termDict == null) {
			// Hacked
			return null;
		}

		TreeSet<TermFrequencyPerFile> currentPosting = new TreeSet<TermFrequencyPerFile>();
		List<Integer> termIDs = new LinkedList<Integer>();
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();

		// Map terms to termIDs
		for (String term : terms) {
			termIDs.add(m_termDict.elementToID(term));
		}

		// Get posting list for each term
		for (int tID : termIDs) {
			try {
				currentPosting.addAll(tifr.getPostings(tID));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Get map result
		for (TermFrequencyPerFile tfd : currentPosting) {
			result.put(tfd.getDocID(), tfd.getTermFrequency());
		}

		return result;
	}
}
