package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
	protected String m_indexFileName;
	protected TermIndexDictionary m_termDic;

	public TermIndexFileReader(String indexPath, String indexFileName,
			TermIndexDictionary tid) {
		m_indexPath = indexPath;
		m_indexFileName = indexFileName;
		m_termDic = tid;
		m_maxEntries = 0;
		m_offsetValue = 0L;
	}
	
	public TreeSet<TermFrequencyPerFile> getPostings(List<Integer> termIDs) throws IOException
	{
		TreeSet<TermFrequencyPerFile> retVal = new TreeSet<TermFrequencyPerFile>(); 
		ArrayList<TreeSet<TermFrequencyPerFile>> retSet = new ArrayList<TreeSet<TermFrequencyPerFile>>();
		for(int i = 0; i < termIDs.size(); i++)
		{
			//retSet.
			retSet.add(getPostings(termIDs.get(i)));
		}
		
		for(TermFrequencyPerFile tfpf: retSet.get(0))
		{
			TreeSet<Integer> posIndex = tfpf.getPosIndex();
			for(Integer i: posIndex)
			{
				
				if(checkTermOrder(retSet.subList(1, retSet.size()), i + 1, tfpf.getDocID()))
				{
					retVal.add(tfpf);
				}
			}
		}
		
		
		return retVal;
	}
	
	private boolean checkTermOrder(List<TreeSet<TermFrequencyPerFile>> docArr, int pos, int fileID)
	{
		if(docArr == null || docArr.size() == 0)
		{
			return false;
		}
		
		for(TermFrequencyPerFile tfpf: docArr.get(0))
		{
			if(tfpf.getDocID() != fileID)
			{
				continue;
			}
			TreeSet<Integer> posIndex = tfpf.getPosIndex();
			for(Integer i: posIndex)
			{
				if(i == pos)
				{
					if(docArr.size() == 1)
					{
						return true;
					}
					return checkTermOrder(docArr.subList(1, docArr.size()), i + 1, fileID); 
				}
			}
		}
		
		return false;
	}

	public TreeSet<TermFrequencyPerFile> getPostings(int termID)
			throws IOException {
		Path indexPath = Paths.get(m_indexPath, m_indexFileName);

		assert (indexPath.toFile().exists());
		RandomAccessFile raf = new RandomAccessFile(indexPath.toFile(), "r");
		
		
		m_termID = termID;
		// algorithm for getting a particular posting
		// 1. Find the chunk containing the term ID
		long offset = FindChunkOffset(raf, indexPath);

		// 2. Search within the chunk containing the term ID for the posting
		TreeSet<TermFrequencyPerFile> results = FindTermInChunk(raf, offset);

		TreeSet<Integer> simTerms = m_termDic.getSimilarTerms(termID);
		if (simTerms != null) {
			for (int i : simTerms) {
				raf = new RandomAccessFile(indexPath.toFile(), "r");
				m_termID = i;
				offset = FindChunkOffset(raf, indexPath);
				results.addAll(FindTermInChunk(raf, offset));
			}
		}

		return results;
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
		long offset = RecurseFindChunk(raf, 0, m_maxEntries - 1);

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
		if (firstChunk == lastChunk) {
			return minOffset;
		}
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

	private TreeSet<TermFrequencyPerFile> FindTermInChunk(RandomAccessFile raf,
			long offset) throws IOException {
		// This can be done by a linear search. Each Chunk's format is the
		// number of terms followed by an array whose elements are the ordered
		// set [TermID, #FileIDs, FileID1, FileID2, etc]. Now check if the term
		// id's match, if not, seek 4*#FileIDs bytes to get to the next term.
		// Rinse and repeat.
		raf.seek(offset);

		TreeSet<TermFrequencyPerFile> retVal = new TreeSet<TermFrequencyPerFile>();
		int numTermIDs = raf.readInt();
		int termIDread = 0;

		for (int i = 0; i < numTermIDs; i++) {
			try {
				termIDread = raf.readInt();
			} catch (IOException e) {
				e.printStackTrace();

				throw new IOException();
			} catch (Exception e) {
				throw new IOException();
			}
			int numFileIDs = raf.readInt();
			if (numFileIDs == 6996) {
				System.out.println("Winner");
			}
			if (termIDread == m_termID) {

				for (int j = 0; j < numFileIDs; j++) {
					retVal.add(readTermFrequencyData(raf));
				}
			} else {
				// file id is paired with term frequency, two integers = 8 bytes
				for (int k = 0; k < numFileIDs; k++) {
					// skip the document id
					raf.skipBytes(4);
					// get the total number of positions this term appears in
					// that document
					int termFreqTotal = 0;
					try {
						termFreqTotal = raf.readInt();
					} catch (IOException e) {
						System.out.println(e.toString());
					}

					// skip over all those terms
					raf.skipBytes(termFreqTotal * 4);
				}
			}
		}
		raf.close();
		return retVal;
	}

	protected TermFrequencyPerFile readTermFrequencyData(RandomAccessFile raf)
			throws IOException {
		// new TermFrequencyPerFile(raf.readInt(), raf.readInt(), null)
		int docID = raf.readInt();
		int freqTerm = raf.readInt();

		TreeSet<Integer> posIndex = new TreeSet<Integer>();

		for (int j = 0; j < freqTerm; j++) {
			posIndex.add(raf.readInt());
		}

		return new TermFrequencyPerFile(docID, freqTerm, posIndex);
	}
}
