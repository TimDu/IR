package edu.buffalo.cse.irf14.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class TermFrequencyPerFile implements Comparable<TermFrequencyPerFile> {

	protected int m_termFreq = 0;
	protected Integer m_docID;
	protected TreeSet<Integer> m_posIndex;
	protected final int oddFreq = 40;
	protected boolean informed = false;
	

	public int size() { 
		//debugFunc();
		return 2 + m_termFreq;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TermFrequencyPerFile) {
			TermFrequencyPerFile toCompare = (TermFrequencyPerFile) o;
			return this.m_docID.equals(toCompare.m_docID);
		}
		return false;
	}
	
	@Override
	public int compareTo(TermFrequencyPerFile o1)
	{
	  // 
	  //return m_docID - o1.m_docID;
		return o1.m_docID - m_docID;	// Need ascending order in TreeSet
	}
	
	 
	@Override
	public int hashCode() {
	    return m_docID.hashCode();
	}

	public TermFrequencyPerFile(int fileID, int pos) {
		m_posIndex = new TreeSet<Integer>();
		addTerm(pos);
		m_docID = fileID;  
		//debugFunc();
	}

	public TermFrequencyPerFile(int fileID, int termFreq, TreeSet<Integer> pos) {
		assert (pos != null);
		assert (termFreq == pos.size());
		m_posIndex = new TreeSet<Integer>();
		addAllTerms(pos);
		m_docID = fileID;
		m_termFreq = termFreq; 
		//debugFunc();
	}

	public void addTerm(int pos) {
		m_posIndex.add(pos);
		if(pos < 0)
		{
			m_termFreq = 1;
			 
		}
		else{
			m_termFreq++;
		}
		//debugFunc();
		 
	}

	public void addAllTerms(TreeSet<Integer> posList) { 
		m_posIndex.addAll(posList);
		if(posList.contains(-2))
		{
			m_termFreq = 1;
			 
		}
		else
		{
			m_termFreq += posList.size();
		}
		//debugFunc();
		 
	}
	
	public void debugFunc()
	{
		if((m_posIndex.size() > oddFreq  && !informed) || 
				(m_termFreq != m_posIndex.size()
				&& (m_posIndex.size() != 1 || !m_posIndex.contains(-1))))
		{
			System.out.println("Winner!");
			informed = true;
		}
		
		if(m_termFreq != m_posIndex.size())
		{
			System.out.println("Winner!");
		}
	}

	public int getTermFrequency() {
		//debugFunc();
		return m_termFreq;
		
	}

	public int getDocID() {
		//debugFunc();
		return m_docID;
	}

	public TreeSet<Integer> getPosIndex() {
		//debugFunc();
		return m_posIndex;
	}

}
