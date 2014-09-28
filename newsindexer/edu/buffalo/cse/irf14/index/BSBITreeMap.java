package edu.buffalo.cse.irf14.index;

import java.io.BufferedOutputStream;
 
import java.io.IOException;
 
import java.util.Map;
import java.util.TreeMap;

public class BSBITreeMap extends TreeMap<Integer, BSBIPriorityQueue> {
 

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

	public void writeObject(BufferedOutputStream o) throws IOException {
		o.write(IndexerUtilityFunction.getByteArray(size()));
		for (Map.Entry<Integer, BSBIPriorityQueue> entry : entrySet()) {

			o.write(IndexerUtilityFunction.getByteArray((int) entry.getKey()));
			entry.getValue().writeObject(o);
		}
	}

}
