package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

public class BSBIPriorityQueue extends TreeSet<TermFrequencyPerFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4523912505130300498L;
	
	 
	
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
        	super.ceiling(e).addAllTerms(e.m_posIndex);
        }
        return isAdded;
    }
	 
	public boolean addAll(Collection<? extends TermFrequencyPerFile> e) 
    {
		
        boolean isAdded = true; 
        for(TermFrequencyPerFile tfpf: e)
        {
	        if(!super.contains(tfpf))
	        {
	            isAdded = super.add(tfpf);
	        }
	        else
	        {
	        	super.ceiling(tfpf).addAllTerms(tfpf.m_posIndex);
	        }
        }
        return isAdded;
    }
	
	
	public void writeObject(BufferedOutputStream o) throws IOException {		 
		o.write(IndexerUtilityFunction.getByteArray(size()));
		for (TermFrequencyPerFile entry : this) {
			
			o.write(IndexerUtilityFunction.getByteArray( entry.getDocID()));
			byte[] rInt;
			rInt = IndexerUtilityFunction.getByteArray( entry.getTermFrequency());
			o.write(rInt);
			int testCase = IndexerUtilityFunction.getInteger(rInt);
			if(entry.getTermFrequency() == -2 ||  testCase == -2)
			{
				System.out.println("winner");
			}
			assert entry.getTermFrequency() == entry.getPosIndex().size();
			TreeSet<Integer> posIndex = entry.getPosIndex(); 
			
			for(Integer i : posIndex)
			{
				o.write(IndexerUtilityFunction.getByteArray(i));
			}
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
			if(freqTerm == -2)
			{
				System.out.println("winner");
			}
			TreeSet<Integer> posIndex = new TreeSet<Integer>();  
			
			for(int j = 0; j < freqTerm; j++)
			{
				o.read(rInt);
				posIndex.add(IndexerUtilityFunction.getInteger(rInt));
			}
			add(new TermFrequencyPerFile(docID, freqTerm, posIndex));
		}
	}
	
	

}
