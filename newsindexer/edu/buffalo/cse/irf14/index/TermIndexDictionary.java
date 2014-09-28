package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class TermIndexDictionary extends IndexDictionary implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1504994408982239030L;

	protected HashMap<Integer, Integer> m_termCount;
	protected TopListings m_termSortedCount;

	public TermIndexDictionary() {
		// TODO Auto-generated constructor stub
		m_termCount = new HashMap<Integer, Integer>();
		m_termSortedCount = null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}

	@Override
	public boolean exists(String element) {
		// TODO Auto-generated method stub
		return super.exists(element);
	}

	protected void increaseTermOccurence(int termID) {

		if (m_termCount.containsKey(termID)) {
			m_termCount.put(termID, m_termCount.get(termID) + 1);
		} else {
			m_termCount.put(termID, 1);
		}
	}

	@Override
	public int elementToID(String element) {
		if (m_elementDict.containsKey(element)) {
			return super.elementToID(element);
		}
		return -1;
	}

	public int AddGetElementToID(String element) {
		// TODO Auto-generated method stub
		int id = super.elementToID(element);
		increaseTermOccurence(id);
		return id;
	}

	@Override
	public String getElementfromID(int id) {
		// TODO Auto-generated method stub
		return super.getElementfromID(id);
	}

	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeObject(m_termCount);
		o.writeObject(m_idDict);
		o.writeObject(m_elementDict);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		m_termSortedCount = new TopListings();
		m_termCount = (HashMap<Integer, Integer>) o.readObject();
		m_idDict = (HashMap<Integer, String>) o.readObject();
		m_elementDict = (HashMap<String, Integer>) o.readObject();
		for (Map.Entry<Integer, Integer> entry : m_termCount.entrySet()) {
			m_termSortedCount.insertSorted(new TermIndexDictionaryElement(
					entry.getKey(), entry.getValue()));
		}

	}

	public List<String> getTopK(int k) {
		if (m_termSortedCount == null) {
			return null;
		}
		List<String> topTerms = new Vector<String>();
		for(int i = 0; i < k; i++)
		{
			topTerms.add(m_idDict.get(m_termSortedCount.get(i).getID()));
		}
		return topTerms;
	}
	
	/**
	 * Clear contents from memory
	 */
	public void clear() {
		m_termCount.clear();
		m_elementDict.clear();
		m_idDict.clear();
		m_termSortedCount.clear();
	}
}
