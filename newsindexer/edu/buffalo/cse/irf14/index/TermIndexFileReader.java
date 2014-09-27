package edu.buffalo.cse.irf14.index;

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
	protected int m_maxEntires;
	protected long m_offsetValue;

	public TermIndexFileReader(String indexPath) {
		// TODO Auto-generated constructor stub
		m_indexPath = indexPath;
		m_maxEntires = 0;
		m_offsetValue = 0L;
	}

	public PriorityQueue<Integer> getPostings(int termID) {
		Path indexPath = Paths.get(m_indexPath,
				IndexGlobalVariables.termIndexFileName);

		assert (indexPath.toFile().exists());

		// algorithm for getting a particular posting
		// 1. Find the chunk containing the term ID
		long offset = FindChunkOffset(indexPath);

		// 2. Search within the chunk containing the term ID for the posting
		return FindTermInChunk(offset);
	}
	
	private long FindChunkOffset(Path indexPath)
	{
		// go to the middle chunk and get the first and last term id's
		// if the input term id is greater than the last go to the chunk
		// in between the middle and the last, e.g. (N/2 + N)/2
		//
		// if the input term id is lower then the first id go to the chunk
		// in between the first and the middle, e.g. (N + N/2)/2
		
		// Repeat until the chunk that bounds the term id is found.
		return 0;
	}
	
	private PriorityQueue<Integer> FindTermInChunk(long offset)
	{
		// This can be done by a linear search. Each Chunk's format is the
		// number of terms followed by an array whose elements are the ordered
		// set [TermID, #FileIDs, FileID1, FileID2, etc]. Now check if the term
		// id's match, if not, seek 4*#FileIDs bytes to get to the next term.
		// Rinse and repeat.
		return null;
	}

	// TODO: need to make a separate descending list of k terms with the largest
	// number of associated file postings

}
