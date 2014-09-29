package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
 

public class IndexFileElement {
	
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

	public void writeObject(BufferedOutputStream o) throws IOException {
		o.write(IndexerUtilityFunction.getByteArray(termID));
		fileIDs.writeObject(o);
	}

	public void readObject(BufferedInputStream o) throws IOException,
			ClassNotFoundException {
		byte[] rInt = new byte[4];
		o.read(rInt);
		 
		
		termID = IndexerUtilityFunction.getInteger(rInt);
		fileIDs.readObject(o);
	}

 
}
