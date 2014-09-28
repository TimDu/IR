package edu.buffalo.cse.irf14.index;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public interface PerformIndexWriterLogic {
	
	
	public void performIndexLogic(Document d, FieldNames fn) throws IndexerException;
	public void finishIndexing() throws  IndexerException;
}
