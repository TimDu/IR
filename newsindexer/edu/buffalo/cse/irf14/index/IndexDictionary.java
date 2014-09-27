package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

public class IndexDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7639663631649418945L;
	protected HashMap<String, Integer> m_elementDict;

	public IndexDictionary() {
		m_elementDict = new HashMap<String, Integer>();
	}

	public int elementToID(String term) {
		if (m_elementDict.containsKey(term)) {
			return m_elementDict.get(term);
		}

		m_elementDict.put(term, m_elementDict.size() + 1);

		return m_elementDict.size();
	}

	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeObject(m_elementDict);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		m_elementDict = (HashMap<String, Integer>) o.readObject();

	}

}
