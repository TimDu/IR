package edu.buffalo.cse.irf14.index;

import java.nio.ByteBuffer;

public class IndexerUtilityFunction {

	public IndexerUtilityFunction() {
	}
	
	static byte[] getByteArray(Integer input)
	{
		return ByteBuffer.allocate(Integer.SIZE/8).putInt(input).array();
	}
	
	static byte[] getByteArray(Long input)
	{
		return ByteBuffer.allocate(Long.SIZE/8).putLong(input).array();
	}
	
	static Integer getInteger(byte[] arr)
	{
		return ByteBuffer.wrap(arr).getInt();
	}
	
}
