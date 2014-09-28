package edu.buffalo.cse.irf14.index;

public class TermIndexDictionaryElement  {

	protected final int m_termID;
	protected int m_occurrences;

	public TermIndexDictionaryElement(int termID, int occurrences) {
		m_termID = termID;
		m_occurrences = occurrences;
	}
	
	static TermIndexDictionaryElement getSearchInstance(int termID)
	{
		return new TermIndexDictionaryElement(termID, 0);
	}

	public int getID()
	{
		return m_termID;
	}
	
	public int getoccurrences()
	{
		return m_occurrences;
	}
	 
}
