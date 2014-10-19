package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public abstract class IndexDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7639663631649418945L;
	protected HashMap<String, Integer> m_elementDict;
	protected HashMap<Integer, String> m_idDict;

	public IndexDictionary() {
		m_elementDict = new HashMap<String, Integer>();
		m_idDict = new HashMap<Integer, String>();
	}
	
	public int size()
	{
		return m_elementDict.size();
	}

	public boolean exists(String element) {
		return m_elementDict.containsKey(element);
	}

	public int elementToID(String element) {
		if (m_elementDict.containsKey(element)) {
			return m_elementDict.get(element);
		}
		
		m_elementDict.put(element, m_elementDict.size() + 1);
		m_idDict.put(m_elementDict.size(), element);
		return m_elementDict.size();
	}
	
	

	public String getElementfromID(int id)
	{
		if (!m_idDict.containsKey(id)) {
			return null;
		}
		
		return m_idDict.get(id);
	}

	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeObject(m_idDict);
		o.writeObject(m_elementDict);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		m_idDict = (HashMap<Integer, String>) o.readObject();
		m_elementDict = (HashMap<String, Integer>) o.readObject();

	}

}
