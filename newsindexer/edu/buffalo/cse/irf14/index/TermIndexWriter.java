package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class TermIndexWriter implements performIndexWriterLogic {

	protected BSBITreeMap m_termIndex;
	protected IndexDictionary m_termDict;
	protected IndexDictionary m_fileDict;
	final protected String m_termDicFileName = "term.dict";
	protected int m_tempIndexNum = 0;
	final protected String m_termIndexFileName = "term.index";
	final protected int m_maxMappingSize;

	public TermIndexWriter(IndexDictionary fileDict) {
		m_termIndex = new BSBITreeMap();
		m_termDict = new IndexDictionary();
		m_fileDict = fileDict;
		m_maxMappingSize = 10000000;
	}

	private TokenStream createTermStream(Document d, FieldNames type) {
		Tokenizer tknizer = new Tokenizer();
		TokenStream tstream = new TokenStream();
		try {
			String[] arr = d.getField(type);
			for (String s : arr) {
				tstream.append(tknizer.consume(s));
			}

		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tstream;
	}

	private void createTempIndex() {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream("tempIndex" + m_tempIndexNum
					+ ".index");

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_termIndex);
			out.close();
			fileOut.close();
			m_tempIndexNum++;
			m_termIndex = new BSBITreeMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert (false);
			e.printStackTrace();
		}
	}

	private void appendCreateTermIndex() {
		BufferedOutputStream fileOut;
		try {
			fileOut = new BufferedOutputStream(new FileOutputStream(
					m_termIndexFileName, true));
			;

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_termIndex);
			out.close();
			fileOut.close();
			m_termIndex = new BSBITreeMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert (false);
			e.printStackTrace();
		}

	}

	private void writeTermDictionary() throws IOException {
		BufferedOutputStream fileOut;

		fileOut = new BufferedOutputStream(new FileOutputStream(
				m_termDicFileName, true));

		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(m_termDict);
		out.close();
		fileOut.close();

	}

	@Override
	public void performIndexLogic(Document d) {

		TokenStream tstream = createTermStream(d, FieldNames.CONTENT);
		if (tstream == null) {
			// TODO: Figure out error handling
			return;
		}
		AnalyzerFactory af = AnalyzerFactory.getInstance();
		Analyzer analyzer = af.getAnalyzerForField(FieldNames.CONTENT, tstream);
		try {
			while (analyzer.increment()) {

			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		tstream = analyzer.getStream();

		while (tstream.hasNext()) {
			Token term = tstream.next();
			
			// look up term or add it to dictionary
			int termID = m_termDict.elementToID(term.toString());
			
			// check if the temporary term index contains it 
			if (!m_termIndex.containsKey(termID)) {
				// if not, add it and initialize its priority queue
				m_termIndex.put(termID, new BSBIPriorityQueue());
			}
			
			// now add the fileID to the posting for the given term
			m_termIndex.get(termID).add(
					m_fileDict.elementToID(d.getField(FieldNames.FILEID)[0]));

			// if we're above the number of mappings, write to disk
			if (m_termIndex.values().size() > m_maxMappingSize) {
				// write to disk
				createTempIndex();
			}
		}

	}

	@Override
	public void finishIndexing() throws ClassNotFoundException, IOException {
		createTempIndex();
		final int numIndexes = m_tempIndexNum;
		/*
		 * Idea from our online textbook (Manning, Raghavan, Schutze) Blocked
		 * Based Sort Indexing: To do the merging, we open all block files
		 * simultaneously, and maintain small read buffers for the ten blocks we
		 * are reading and a write buffer for the final merged index we are
		 * writing. In each iteration, we select the lowest termID that has not
		 * been processed yet using a priority queue or a similar data
		 * structure. All postings lists for this termID are read and merged,
		 * and the merged list is written back to disk. Each read buffer is
		 * refilled from its file when necessary
		 */
		ArrayList<ObjectInputStream> files = new ArrayList<ObjectInputStream>();
		// open pieces of each file
		for (int i = 0; i < numIndexes; i++) {

			files.add(new ObjectInputStream(new BufferedInputStream(
					new FileInputStream("tempIndex" + i + ".index"))));
		}

		// read from each file into our new term index
		/*********************************************************
		 * ife = index file element array We use this as a priority queue that
		 * uses the current lowest term id from each temporary posting's file.
		 * As we save each postings files in ascending order, we know this
		 * should be the smallest
		 * 
		 **********************************************************/
		IndexFileElement[] ifeArr = new IndexFileElement[numIndexes];

		/*********************************************************
		 * numObjsInFile - Counts the total number of postings per file Note
		 * that when we write the temporary posting's file we store the number
		 * of postings in each file as the first integer. Thus we can read in
		 * how many postings are in a given file. This keeps track of how many
		 * postings are in any given temporary index file.
		 **********************************************************/
		Integer[] numObjsInFile = new Integer[numIndexes];

		/*********************************************************
		 * currentNumObjsReadFromFile - Counts how many of the postings we've
		 * read so far. E.g. once currentNumObjsReadFromFile[i] =
		 * numObjsInFile[i] we know there are no more objects to be read from
		 * the file at files[i].
		 *********************************************************/
		Integer[] currentNumObjsReadFromFile = new Integer[numIndexes];

		// TODO: Need to make sure that the logic regarding this all
		// files read is correct, may be difficult to fully test
		boolean allFilesRead = true;

		/*
		 * Loop until all files have been read and all postings
		 * have been merged.
		 */
		
		/*
		 * Initialize the number of objects in each file
		 */
		for (int i = 0; i < numIndexes; i++) {
			numObjsInFile[i] = files.get(i).readInt();
			currentNumObjsReadFromFile[i] = 0;
		}
		
		while (true) {
			/*
			 * This array keeps track of what indices have the current
			 * lowest element.
			 */
			ArrayList<Integer> lowestElements = new ArrayList<Integer>();
			/*
			 * This integer keeps track of what the lowest element actually is
			 */
			Integer lowestElement = Integer.MAX_VALUE;
			
			/*
			 * Loop logic:
			 * For each temporary index check if the value of ifeArr is null.
			 * This tells us whether the pseudo priority queue ifeArr
			 * used the previous posting for that index and is ready for a new
			 * one. If it's null and there are no more postings just continue
			 * the loop, i.e. go to the next index. If this happens for all
			 * indexes then allFilesRead will remain true telling us we can
			 * exit the outer while loop.
			 * 
			 * Supposing that ifeArr has at least one non-null value, lowest
			 * element will be set. Whenever lowest element is encountered
			 * we keep track of what the index we encountered the lowest element
			 * at, e.g. the lowest term ID.
			 * 
			 * We'll use this term ID after the loop to determine what
			 * postings can be merged. The indices we're saving gives us
			 * access to each term with the same lowest ID.
			 */
			for (int i = 0; i < numIndexes; i++) {
				if (ifeArr[i] == null) {
					if (currentNumObjsReadFromFile[i] < numObjsInFile[i]) {
						ifeArr[i] = (IndexFileElement) files.get(i)
								.readObject();
						currentNumObjsReadFromFile[i]++;
					} else {
						continue;
					}
				}
				allFilesRead = false;
				if (lowestElement > ifeArr[i].getTermID()) {
					lowestElement = ifeArr[i].getTermID();
					lowestElements.clear();
					lowestElements.add(i);
				} else if (lowestElement == ifeArr[i].getTermID()) {
					lowestElements.add(i);
				}
			}
			
			/*
			 * Now, for each index in the lowestElements array combine/merge
			 * all postings. Note that duplicates of fileIDs are only removed
			 * during writing. Although, duplicate file ids shouldn't really
			 * occur.
			 */
			for (Integer i : lowestElements) {
				if (!m_termIndex.containsKey(ifeArr[i].getTermID())) {
					m_termIndex.put(ifeArr[i].getTermID(),
							new BSBIPriorityQueue());
				}

				m_termIndex.get(ifeArr[i].getTermID()).addAll(
						ifeArr[i].getFileIDs());
				ifeArr[i] = null;
			}
			/* If the number of values in our in-memory term index is enough
			 * then we need to flush to disk.
			 * */ 
			if (m_termIndex.values().size() > m_maxMappingSize) {
				// write to disk
				appendCreateTermIndex();
			}
			

			if (allFilesRead) {
				break;
			}
			// reset values to initial conditions, we want true/true
			allFilesRead = true;
		}

		// Clean up, get rid of all those temporary files.
		for (int i = 0; i < numIndexes; i++) {
			File file = new File("tempIndex" + i + ".index");
			file.delete();
		}
		
		// Make sure we flush any remaining data
		appendCreateTermIndex();
		
		// Reset our internal count of temporary indexes
		m_tempIndexNum = 0;
		
		// Finally, write the term data
		writeTermDictionary();

	}
}
