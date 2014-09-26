package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
 
import java.io.Serializable;
import java.util.PriorityQueue;

public class BSBIPriorityQueue extends PriorityQueue<Integer> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8275067776179374453L;

	private void writeObject(ObjectOutputStream o) throws IOException {
		o.writeInt(size());
		Integer previous = Integer.MAX_VALUE;
		 
		for (Integer entry : this) {
			o.writeInt((int)entry);

		}
	}

	private void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		// this should be a write only class
		int numFiles = o.readInt();
		for(int i = 0; i < numFiles; i++)
		{
			add(o.readInt());
		}
		
	}

	 
}
