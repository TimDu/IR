package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class BSBIPriorityQueue extends PriorityQueue<Integer> {

	public void writeObject(BufferedOutputStream o) throws IOException {		 
		o.write(IndexerUtilityFunction.getByteArray(size()));
		for (Integer entry : this) {
			o.write(IndexerUtilityFunction.getByteArray((int) entry));
		}
	}

	public void readObject(BufferedInputStream o) throws IOException,
			ClassNotFoundException {
		byte[] rInt = new byte[4];
		o.read(rInt);
		int numFiles = IndexerUtilityFunction.getInteger(rInt);
		for (int i = 0; i < numFiles; i++) {
			o.read(rInt);
			add(IndexerUtilityFunction.getInteger(rInt));
		}

	}

}
