package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

public class TermDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7639663631649418945L;
	protected TreeMap<String, Integer> m_termDict;

	public TermDictionary() {
		 m_termDict = new TreeMap<String, Integer>();
	}
	
	public int termToID(String term) {
		if (m_termDict.containsKey(term)) {
			return m_termDict.get(term);
		}

		m_termDict.put(term, m_termDict.size() + 1);

		return m_termDict.size();
	}
	
	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeObject(m_termDict);
	}

	 
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		// this should be a write only class
		 
		m_termDict = (TreeMap<String, Integer>) o.readObject();
		
	}
	
	
}
