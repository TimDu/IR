package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class FileIndexDictionary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9119887963933956944L;

	protected HashMap<String, int[]> m_elementDict;
	protected HashMap<Integer, String> m_idDict; 
	
	public FileIndexDictionary() {
		m_elementDict = new HashMap<String, int[]>();
		m_idDict = new HashMap<Integer, String>(); 
	}
	
	public int size()
	{
		return m_elementDict.size();
	}
	
	public boolean exists(Document d) {
		return m_elementDict.containsKey(d.getField(FieldNames.FILEID)[0]);
	}

	public boolean exists(String element) {
		return m_elementDict.containsKey(element);
	}

	public int elementToID(Document d) {
		String element = d.getField(FieldNames.FILEID)[0];
		if (m_elementDict.containsKey(element)) {
			return m_elementDict.get(element)[0];
		}
		
		//m_elementDict.put(element, m_elementDict.size() + 1);
		m_elementDict.put(element, new int[2]);
		m_elementDict.get(element)[0] = m_elementDict.size();
		if(d.getField(FieldNames.LENGTH).length > 0)
		{
			m_elementDict.get(element)[1] = Integer.parseInt(d.getField(FieldNames.LENGTH)[0]);
		}
		else
		{
			System.out.println("Document " + d.getField(FieldNames.FILEID)[0] + " has no body!");
			m_elementDict.get(element)[1] = 0;
		}
		
		m_idDict.put(m_elementDict.size(), element);
		return m_elementDict.size();
	}
	
	public void setFileLength(int fileId, int length)
	{
		assert(m_idDict.containsKey(fileId));
		m_elementDict.get(m_idDict.get(fileId))[1] = length;
	}
	
	public int getFileLength(int fileId)
	{
		assert(m_idDict.containsKey(fileId));
		return m_elementDict.get(m_idDict.get(fileId))[1];
	}
	
	public void setFileLength(String fileId, int length)
	{
		assert(m_elementDict.containsKey(fileId));
		m_elementDict.get(fileId)[1] = length;
	}
	
	public int getFileLength(String fileId)
	{
		assert(m_elementDict.containsKey(fileId));
		return m_elementDict.get(fileId)[1];
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
		m_elementDict = (HashMap<String, int[]>) o.readObject();

	}
	

}
