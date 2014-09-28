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
			 
			 
			// These will be our offsets, we need to write them in the beginning,
			// one for each index.
			appendInteger(fileOut, numIndexes);
			
			for(int i = 0; i < numIndexes; i++)
			{
				appendLong(fileOut, 0L);
			}
			 
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
			
			raf.seek((Integer.SIZE/8) + (Long.SIZE /8)*m_currentInternalIndexNumber);
			raf.writeLong(m_currentFileSize);
			raf.close();
			fileOut = new BufferedOutputStream(new FileOutputStream(
					indexPath.toString(), true)); 
			
			appendIndexChunk(fileOut, termIndexChunk);
			
			 
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert (false);
			e.printStackTrace();
		}
		
		m_currentInternalIndexNumber++;
	}
	
	protected void appendIndexChunk(BufferedOutputStream out, BSBITreeMap termIndexChunk) throws IOException
	{
		m_currentFileSize += termIndexChunk.getDiskSize();
		termIndexChunk.writeObject(out);
	}
	
	protected void appendInteger(BufferedOutputStream out, int input) throws IOException
	{
		m_currentFileSize += 4;
		out.write(IndexerUtilityFunction.getByteArray(input));
	}
	
	protected void appendLong(BufferedOutputStream out, Long input) throws IOException
	{
		
		m_currentFileSize += (Long.SIZE/8);
		out.write(IndexerUtilityFunction.getByteArray(input));
	}
	 

}
