package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class BSBIPriorityQueue extends TreeSet<TermFrequencyPerFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4523912505130300498L;
	
	private int internalSize()
	{
		// each element contains 2 integers
		return super.size()*2;
	}
	
	public int size()
	{
		
		return super.size();
	}
	
	public boolean add(TermFrequencyPerFile e) 
    {
        boolean isAdded = true; 
        if(!super.contains(e))
        {
            isAdded = super.add(e);
        }
        else
        {
        	super.headSet(e, true).first().incrementTermFrequency(e.m_termFreq);
        }
        return isAdded;
    }
	
	
	public void writeObject(BufferedOutputStream o) throws IOException {		 
		o.write(IndexerUtilityFunction.getByteArray(size()));
		for (TermFrequencyPerFile entry : this) {
			
			o.write(IndexerUtilityFunction.getByteArray( entry.getDocID()));
			o.write(IndexerUtilityFunction.getByteArray( entry.getTermFrequency()));
		}
	}

	public void readObject(BufferedInputStream o) throws IOException,
			ClassNotFoundException {
		byte[] rInt = new byte[4];
		o.read(rInt);
		int numFiles = IndexerUtilityFunction.getInteger(rInt);
		for (int i = 0; i < numFiles; i++) {
			o.read(rInt); 
			int docID = IndexerUtilityFunction.getInteger(rInt);
			o.read(rInt); 
			int freqTerm =IndexerUtilityFunction.getInteger(rInt);
			add(new TermFrequencyPerFile(docID, freqTerm));
		}

	}

}
