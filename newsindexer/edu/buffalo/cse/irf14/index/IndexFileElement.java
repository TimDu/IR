package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class IndexFileElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7272069394787379864L;
	protected int termID;
	protected BSBIPriorityQueue fileIDs;
	
	public int getTermID()
	{
		return termID;
	}
	
	public BSBIPriorityQueue getFileIDs()
	{
		return fileIDs;
	}

	public IndexFileElement() {
		fileIDs = new BSBIPriorityQueue();
	}

	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeInt((int) termID);
		o.writeObject(fileIDs);
	}

	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		termID = o.readInt();
		fileIDs = (BSBIPriorityQueue) o.readObject();
	}

 
}
