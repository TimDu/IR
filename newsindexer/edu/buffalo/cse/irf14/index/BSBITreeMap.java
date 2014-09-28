package edu.buffalo.cse.irf14.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class BSBITreeMap extends TreeMap<Integer, BSBIPriorityQueue> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9195339631321597338L;

	public int getDiskSize() {
		int size = 0;
		for (Map.Entry<Integer, BSBIPriorityQueue> entry : entrySet()) {
			size += entry.getValue().size();
		}
		// 4*size() = the number key's * 4 (as they're integers)
		// size * 4 = the number of values for each priority queue * 4 
		// as each value is an integer
		// + 4 because we're also writing the number of key's out as well
		return 4 * size() + size * 4 + 4;
	}

	public void writeObject(ObjectOutputStream o) throws IOException {
		o.writeInt(size());
		for (Map.Entry<Integer, BSBIPriorityQueue> entry : entrySet()) {
			o.writeInt((int) entry.getKey());
			o.writeObject(entry.getValue());
		}
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
