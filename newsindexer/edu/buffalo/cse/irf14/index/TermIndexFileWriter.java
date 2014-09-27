package edu.buffalo.cse.irf14.index;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TermIndexFileWriter {
	/*
	 * File Format:
	 * Bytes 0-4: Maximum number of entries in the offset array
	 * 			  Acts as number sN - the number of sections
	 * Bytes 4-sizeof(long)*sN -  An array of longs that acts as offsets into the file.
	 * 
	 * Following this are chunks of the inverted index. The start of a chunk contains 
	 * an integer representing the number of terms in the chunk. The chunk the contains
	 * sets of term ID with fileIDs. This set has the following structure:
	 * [TermID, #FileIDS, FileID1, FileID2, ...]
	 * 
	 * Thus within a chunk looks like:
	 * Bytes 0 - 4: Integer - Number of Terms
	 * Bytes 4 - N: [TermID, #FileIDS, FileID1, FileID2, ...]
	 * Bytes N - M: [TermID, #FileIDS, FileID1, FileID2, ...]
	 * and so on.
	 */
	
	protected String m_indexPath;
	protected Integer m_currentInternalIndexNumber;
	protected Long m_currentFileSize;
	protected boolean m_bCreated;
	
	public TermIndexFileWriter(String indexPath) {
		// TODO Auto-generated constructor stub
		m_indexPath = indexPath;
		m_currentInternalIndexNumber = 0;
		m_currentFileSize = 0L;
		m_bCreated = false;
	}
	
	public void createTermIndex(int numIndexes) {
		BufferedOutputStream fileOut;
		m_currentInternalIndexNumber = 0;
		m_currentFileSize = 0L;
		m_bCreated = true;
		try {
			// TODO: Create pointers to various offsets within the file
			// a monolithic file is no good if we can't efficiently
			// access it's elements.
			Path indexPath = Paths.get(m_indexPath,
					IndexGlobalVariables.termIndexFileName);
			
			// remove any previous copies
			if (indexPath.toFile().exists()) {
				indexPath.toFile().delete();
			}
			fileOut = new BufferedOutputStream(new FileOutputStream(
					indexPath.toString(), true));

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			// These will be our offsets, we need to write them in the beginning,
			// one for each index.
			appendInteger(out, numIndexes);
			
			for(int i = 0; i < numIndexes; i++)
			{
				appendLong(out, 0L);
			}
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert (false);
			e.printStackTrace();
		}
	}
	
	public void appendTermIndex(BSBITreeMap termIndexChunk) {
		BufferedOutputStream fileOut;
		assert(m_bCreated);
		try {
			// TODO: Create pointers to various offsets within the file
			// a monolithic file is no good if we can't efficiently
			// access it's elements.
			Path indexPath = Paths.get(m_indexPath,
					IndexGlobalVariables.termIndexFileName);
			
			assert(indexPath.toFile().exists());
			
			RandomAccessFile raf = new RandomAccessFile(indexPath.toFile(), "rw");
			raf.seek(0);
			assert(raf.readInt() > m_currentInternalIndexNumber);
			raf.seek(4 + 4*m_currentInternalIndexNumber);
			raf.writeLong(m_currentFileSize);
			raf.close();
			fileOut = new BufferedOutputStream(new FileOutputStream(
					indexPath.toString(), true));

			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			appendIndexChunk(out, termIndexChunk);
			
			out.close();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert (false);
			e.printStackTrace();
		}
		
		m_currentInternalIndexNumber++;
	}
	
	protected void appendIndexChunk(ObjectOutputStream out, BSBITreeMap termIndexChunk) throws IOException
	{
		m_currentFileSize += termIndexChunk.getDiskSize();
		out.writeObject(termIndexChunk);
	}
	
	protected void appendInteger(ObjectOutputStream out, int input) throws IOException
	{
		m_currentFileSize += 4;
		out.writeInt(0);
	}
	
	protected void appendLong(ObjectOutputStream out, Long input) throws IOException
	{
		
		m_currentFileSize += Long.SIZE;
		out.writeLong(input);
	}
	 

}
