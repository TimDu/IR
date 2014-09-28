package edu.buffalo.cse.irf14.index;

import java.io.IOException;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public interface performIndexWriterLogic {
	
	
	public void performIndexLogic(Document d, FieldNames fn);
	public void finishIndexing() throws ClassNotFoundException,
	IOException;
}
