package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class FileIndexDictionary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9119887963933956944L;

	class FileData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 937155326715162949L;

		public int m_fileID;
		public int m_fileLength;
		public ArrayList<String> m_fileCategory;

		public FileData(int fileID, int fileLength, String[] fileCategory) {
			m_fileID = fileID;
			m_fileLength = fileLength;
			m_fileCategory = new ArrayList<String>();
			addCategory(fileCategory);
		}

		public void addCategory(String[] fileCategory) {
			for (String s : fileCategory) {
				if (s.trim().isEmpty()) {
					continue;
				}
				m_fileCategory.add(s);
			}
		}

		private void writeObject(ObjectOutputStream o) throws IOException {
			o.writeObject(m_fileID);
			o.writeObject(m_fileLength);
			o.writeObject(m_fileCategory);
		}

		@SuppressWarnings("unchecked")
		private void readObject(ObjectInputStream o) throws IOException,
				ClassNotFoundException {
			m_fileID = (int) o.readObject();
			m_fileLength = (int) o.readObject();
			m_fileCategory = (ArrayList<String>) o.readObject();

		}
	}

	protected HashMap<String, FileData> m_elementDict;
	protected HashMap<Integer, String> m_idDict;

	public FileIndexDictionary() {
		m_elementDict = new HashMap<String, FileData>();
		m_idDict = new HashMap<Integer, String>();
	}

	public int size() {
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
			return m_elementDict.get(element).m_fileID;
		}

		// m_elementDict.put(element, m_elementDict.size() + 1);

		if (d.getField(FieldNames.LENGTH).length > 0) {
			m_elementDict.put(
					element,
					new FileData(m_elementDict.size() + 1, Integer.parseInt(d
							.getField(FieldNames.LENGTH)[0]), d
							.getField(FieldNames.CATEGORY)));
		} else {
			System.out.println("Document " + d.getField(FieldNames.FILEID)[0]
					+ " has no body!");
			m_elementDict.put(
					element,
					new FileData(m_elementDict.size() + 1, 0, d
							.getField(FieldNames.CATEGORY)));
		}

		m_idDict.put(m_elementDict.size(), element);
		return m_elementDict.size();
	}

	public int elementToID(String id) {
		if (m_elementDict.containsKey(id)) {
			return m_elementDict.get(id).m_fileID;
		}
		return -1;
	}

	public void setFileLength(int fileId, int length) {
		assert (m_idDict.containsKey(fileId));
		m_elementDict.get(m_idDict.get(fileId)).m_fileLength = length;
	}

	public int getFileLength(int fileId) {
		assert (m_idDict.containsKey(fileId));
		return m_elementDict.get(m_idDict.get(fileId)).m_fileLength;
	}

	public void setFileLength(String fileId, int length) {
		assert (m_elementDict.containsKey(fileId));
		m_elementDict.get(fileId).m_fileLength = length;
	}

	public int getFileLength(String fileId) {
		assert (m_elementDict.containsKey(fileId));
		return m_elementDict.get(fileId).m_fileLength;
	}

	public String getElementfromID(int id) {
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
		m_elementDict = (HashMap<String, FileData>) o.readObject();

	}

	public void addCategory(Document d) {
		// TODO Auto-generated method stub
		String element = d.getField(FieldNames.FILEID)[0];
		if (m_elementDict.containsKey(element)) {
			m_elementDict.get(element).addCategory(
					d.getField(FieldNames.CATEGORY));
		}
	}
	
	public ArrayList<String> getCategories(int fileId)
	{
		if(!m_idDict.containsKey(fileId))
		{
			return null;
		}
		return m_elementDict.get(m_idDict.get(fileId)).m_fileCategory;
	}
	
	public ArrayList<String> getCategories(String FileName)
	{
		if(!m_elementDict.containsKey(FileName))
		{
			return null;
		}
		return m_elementDict.get(FileName).m_fileCategory;
	}

}
