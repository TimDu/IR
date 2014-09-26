package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

 

public class FileDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6566080955614884619L;
	protected TreeMap<String, Integer> m_fileDict;
	
	
	public FileDictionary()
	{
		m_fileDict = new TreeMap<String, Integer>();
	}
	
 
	
	public int fileNameToID(String FileName) {
		if (m_fileDict.containsKey(FileName)) {
			return m_fileDict.get(FileName);
		}

		m_fileDict.put(FileName, m_fileDict.size() + 1);

		return m_fileDict.size();
	}
	
	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeObject(m_fileDict);
	}

	 
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		// this should be a write only class
		 
		m_fileDict = (TreeMap<String, Integer>) o.readObject();
		
	}
}
