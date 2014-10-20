package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class IndexFileReader extends IndexReaderInterface {
	protected String m_indexName;
	protected String m_dictName;
	protected TermIndexFileReader tifr;
	protected double m_avgDocLength = 0;

	public IndexFileReader(String indexDir, String indexName, String dictName) {
		m_indexDir = indexDir;
		m_indexName = indexName;
		m_dictName = dictName;

		setup();
	}

	protected void setup() {
		tifr = new TermIndexFileReader(m_indexDir, m_indexName);
		m_termDict = new TermIndexDictionary();
		m_fileDict = new FileIndexDictionary();

		try {
			OpenTermDictionary();
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

	protected void OpenFileStats() throws IOException, ClassNotFoundException {
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.statsFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_avgDocLength = instream.readDouble();
		System.out.println("m_avgDocLength: " + m_avgDocLength);
		instream.close();
		fileIn.close();
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
		if (!m_termDict.exists(term)) {
			return null;
		}

		int termID = m_termDict.elementToID(term);

		Map<String, Integer> retVal = new HashMap<String, Integer>();
		try {

			TreeSet<TermFrequencyPerFile> tsTerm = tifr.getPostings(termID);
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
		int index = -1;	// Index ID for the shortest posting list
		int minSize = -1;	// The size of current shortest posting
		TreeSet<TermFrequencyPerFile> currentPosting;
		TreeSet<TermFrequencyPerFile> tempPosting;
		List<Integer> termIDs = new LinkedList<Integer>();
		List<TreeSet<TermFrequencyPerFile>> postings =
				new LinkedList<TreeSet<TermFrequencyPerFile>>();
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		// Map terms to termIDs
		for (String term: terms) {
			termIDs.add(m_termDict.elementToID(term));
		}
		
		// Get posting list for each term
		for (int tID: termIDs) {
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
				Iterator<TermFrequencyPerFile> iter0 =
						currentPosting.descendingIterator();
				Iterator<TermFrequencyPerFile> iter1 =
						postings.get(i).descendingIterator();
				TermFrequencyPerFile value0;
				TermFrequencyPerFile value1;
				while (iter0.hasNext() && iter1.hasNext()) {
					value0 = iter0.next();
					value1 = iter1.next();
					if (value1.equals(value0)) {
						tempPosting.add(value1);
					}
				}
				if (!tempPosting.isEmpty()) {
					currentPosting = tempPosting;
					tempPosting = new TreeSet<TermFrequencyPerFile>();
				} else {
					// Exit intersection before no qualified document
					// is left!
					System.err.print("AND query on [ ");
					for (String term: terms) {
						System.err.print(term + " ");
					}
					System.err.println("] terminated at " + terms[i]);
					break;
				}
			}
		}
		
		// Get map result
		for (TermFrequencyPerFile tfd: currentPosting) {
			result.put(m_fileDict.getElementfromID(tfd.getDocID())
					, tfd.getTermFrequency());
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.buffalo.cse.irf14.index.IndexReaderInterface#queryOR(java.lang.String[])
	 */
	@Override
	public Map<String, Integer> queryOR(String... terms) {
		TreeSet<TermFrequencyPerFile> currentPosting =
				new TreeSet<TermFrequencyPerFile>();
		List<Integer> termIDs = new LinkedList<Integer>();
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		// Map terms to termIDs
		for (String term: terms) {
			termIDs.add(m_termDict.elementToID(term));
		}
		
		// Get posting list for each term
		for (int tID: termIDs) {
			try {
				currentPosting.addAll(tifr.getPostings(tID));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Get map result
		for (TermFrequencyPerFile tfd: currentPosting) {
			result.put(m_fileDict.getElementfromID(tfd.getDocID())
					, tfd.getTermFrequency());
		}
		
		return result;
	}
}
