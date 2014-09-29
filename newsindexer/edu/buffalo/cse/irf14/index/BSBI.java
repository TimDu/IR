package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BSBI {

	// Probably integrate all BSBI related variables into this one?
	
	public static BSBITreeMap merge(ArrayList<BufferedInputStream> files,
			int numIndexes, TermIndexFileWriter tif, int m_maxMappingSize) 
					throws IOException, ClassNotFoundException {
		BSBITreeMap m_termIndex = new BSBITreeMap();

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
		 * Loop until all files have been read and all postings have been
		 * merged.
		 */

		/*
		 * Initialize the number of objects in each file
		 */
		for (int i = 0; i < numIndexes; i++) {
			byte[] rInt = new byte[4];
			files.get(i).read(rInt);
			 
			numObjsInFile[i] = IndexerUtilityFunction.getInteger(rInt);
			currentNumObjsReadFromFile[i] = 0;
		}

		while (true) {
			/*
			 * This array keeps track of what indices have the current lowest
			 * element.
			 */
			ArrayList<Integer> lowestElements = new ArrayList<Integer>();
			/*
			 * This integer keeps track of what the lowest element actually is
			 */
			Integer lowestElement = Integer.MAX_VALUE;

			/*
			 * Loop logic: For each temporary index check if the value of ifeArr
			 * is null. This tells us whether the pseudo priority queue ifeArr
			 * used the previous posting for that index and is ready for a new
			 * one. If it's null and there are no more postings just continue
			 * the loop, i.e. go to the next index. If this happens for all
			 * indexes then allFilesRead will remain true telling us we can exit
			 * the outer while loop.
			 * 
			 * Supposing that ifeArr has at least one non-null value, lowest
			 * element will be set. Whenever lowest element is encountered we
			 * keep track of what the index we encountered the lowest element
			 * at, e.g. the lowest term ID.
			 * 
			 * We'll use this term ID after the loop to determine what postings
			 * can be merged. The indices we're saving gives us access to each
			 * term with the same lowest ID.
			 */
			for (int i = 0; i < numIndexes; i++) {
				if (ifeArr[i] == null) {
					if (currentNumObjsReadFromFile[i] < numObjsInFile[i]) {
						ifeArr[i] = new IndexFileElement();
						ifeArr[i].readObject(files.get(i));
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
			 * Now, for each index in the lowestElements array combine/merge all
			 * postings. Note that duplicates of fileIDs are the way we store
			 * term frequency and should not be removed.
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
			/*
			 * If the number of values in our in-memory term index is enough
			 * then we need to flush to disk.
			 */
			if (m_termIndex.values().size() > m_maxMappingSize) {
				// write to disk
				tif.appendTermIndex(m_termIndex);
				m_termIndex = new BSBITreeMap();
			}

			if (allFilesRead) {
				break;
			}
			// reset values to initial conditions, we want true/true
			allFilesRead = true;
		}
		
		return m_termIndex;
	}
}
