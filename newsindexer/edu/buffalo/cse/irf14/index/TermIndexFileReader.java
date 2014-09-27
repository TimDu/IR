package edu.buffalo.cse.irf14.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class TermIndexFileReader {
	/*
	 * File Format: Bytes 0-4: Maximum number of entries in the offset array
	 * Acts as number sN - the number of sections Bytes 4-sizeof(long)*sN - An
	 * array of longs that acts as offsets into the file.
	 * 
	 * Following this are chunks of the inverted index. The start of a chunk
	 * contains an integer representing the number of terms in the chunk. The
	 * chunk the contains sets of term ID with fileIDs. This set has the
	 * following structure: [TermID, #FileIDS, FileID1, FileID2, ...]
	 * 
	 * Thus within a chunk looks like: Bytes 0 - 4: Integer - Number of Terms
	 * Bytes 4 - N: [TermID, #FileIDS, FileID1, FileID2, ...] Bytes N - M:
	 * [TermID, #FileIDS, FileID1, FileID2, ...] and so on.
	 */

	protected String m_indexPath;
	protected int m_maxEntries;
	protected long m_offsetValue;
	protected long m_termID;

	public TermIndexFileReader(String indexPath) {
		// TODO Auto-generated constructor stub
		m_indexPath = indexPath;
		m_maxEntries = 0;
		m_offsetValue = 0L;
	}

	public PriorityQueue<Integer> getPostings(int termID) throws IOException {
		Path indexPath = Paths.get(m_indexPath,
				IndexGlobalVariables.termIndexFileName);

		assert (indexPath.toFile().exists());
		RandomAccessFile raf = new RandomAccessFile(indexPath.toFile(), "rw");
		m_termID = termID;
		// algorithm for getting a particular posting
		// 1. Find the chunk containing the term ID
		long offset = FindChunkOffset(raf, indexPath);

		// 2. Search within the chunk containing the term ID for the posting
		return FindTermInChunk(raf, offset);
	}

	private long FindChunkOffset(RandomAccessFile raf, Path indexPath)
			throws IOException {

		raf.seek(0);
		int maxChunks = raf.readInt();
		int actualMax = 0;

		for (; actualMax < maxChunks && raf.readLong() != 0L; actualMax++) {
			// Intentionally blank, we're trying to see how many chunks
			// are actually in use.
		}
		m_maxEntries = actualMax;
		raf.seek(0);

		// Repeat until the chunk that bounds the term id is found.
		long offset = RecurseFindChunk(raf, 0, m_maxEntries);

		raf.close();

		return offset;
	}

	private long RecurseFindChunk(RandomAccessFile raf, int firstChunk,
			int lastChunk) throws IOException {

		// if the input term id is greater than the last go to the chunk
		// in between the middle and the last, e.g. (N/2 + N)/2
		//
		// if the input term id is lower then the first id go to the chunk
		// in between the first and the middle, e.g. (N + N/2)/2
		raf.seek(0);
		// go to the middle chunk and get the first and last term id's
		int middleChunk = (firstChunk + lastChunk) / 2;
		raf.seek(4 + middleChunk * (Long.SIZE / 8));
		long minOffset = raf.readLong();
		long maxOffset = raf.readLong();
		raf.seek(minOffset);
		// get the number of terms, throw away value
		raf.readInt();
		// get the first term id
		int minTermID = raf.readInt();
		raf.seek(maxOffset);
		// get the number of terms, throw away value
		raf.readInt();
		// get the first term id of the next block
		int maxTermID = raf.readInt();

		if (m_termID >= minTermID && m_termID < maxTermID) {
			return minOffset;
		} else if (m_termID < minTermID) {
			return RecurseFindChunk(raf, firstChunk, middleChunk);
		}

		return RecurseFindChunk(raf, middleChunk + 1, lastChunk);
	}

	private PriorityQueue<Integer> FindTermInChunk(RandomAccessFile raf,
			long offset) throws IOException {
		// This can be done by a linear search. Each Chunk's format is the
		// number of terms followed by an array whose elements are the ordered
		// set [TermID, #FileIDs, FileID1, FileID2, etc]. Now check if the term
		// id's match, if not, seek 4*#FileIDs bytes to get to the next term.
		// Rinse and repeat.
		raf.seek(offset);
		PriorityQueue<Integer> retVal = new PriorityQueue<Integer>();
		int numTermIDs = raf.readInt();
		int termIDread = 0;
		for (int i = 0; i < numTermIDs; i++) {
			termIDread = raf.readInt();
			int numFileIDs = raf.readInt();
			if (termIDread == m_termID) {

				for (int j = 0; j < numFileIDs; j++) {
					retVal.add(raf.readInt());
				}
			} else {
				raf.skipBytes(numFileIDs * 4);
			}
		}

		return retVal;
	}

	// TODO: need to make a separate descending list of k terms with the largest
	// number of associated file postings

}
