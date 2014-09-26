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
		previous = this.element();
		o.writeInt((int)previous);
		for (Integer entry : this) {
			// de-duplication of any possible values
			if(entry == previous)
			{
				// TODO: Determine if this really occurs at all
				assert(false);
				continue;
			}
			previous = entry;
			o.writeInt((int)entry);

		}
		// o.writeObject(propertyTwo);
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
