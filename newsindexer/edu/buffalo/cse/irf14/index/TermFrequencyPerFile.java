package edu.buffalo.cse.irf14.index;



public class TermFrequencyPerFile implements Comparable<TermFrequencyPerFile> {
	
	protected int m_termFreq;
	protected int m_docID;
	
	public int compareTo(TermFrequencyPerFile o1)
			  {
			  // 
			  return m_docID - o1.m_docID;
			  }
			  
	public TermFrequencyPerFile(int fileID)
	{
		this(fileID, 1);
	}
	
	public TermFrequencyPerFile(int fileID, int termFreq)
	{
		m_docID = fileID;
		m_termFreq = termFreq;
	}
		
	public void incrementTermFrequency()
	{
		m_termFreq++;
	}
	
	public void incrementTermFrequency(int amount)
	{
		m_termFreq += amount;
	}
	
	public int getTermFrequency()
	{
		return m_termFreq;
	}
	
	public int getDocID()
	{
		return m_docID;
	}

}
