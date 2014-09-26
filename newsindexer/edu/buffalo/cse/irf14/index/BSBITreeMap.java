package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class BSBITreeMap extends TreeMap<Integer, BSBIPriorityQueue> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9195339631321597338L;

	public void writeObject(ObjectOutputStream o) throws IOException {
		o.writeInt(size());
		for(Map.Entry<Integer,BSBIPriorityQueue> entry : entrySet()) {
		  o.writeInt((int)entry.getKey());
		  o.writeObject(entry.getValue());
		}
		// o.writeObject(propertyTwo);
	}

	public void readObject(ObjectInputStream o) throws IOException,
			ClassNotFoundException {
		// this should be a write only class
		assert (false);
	}

	
	public void readObjectNoData() throws ObjectStreamException {
		assert (false);
	}

}
