package edu.buffalo.cse.irf14.index;

import java.io.IOException;

import edu.buffalo.cse.irf14.document.Document;

public interface performIndexWriterLogic {
	
	
	public void performIndexLogic(Document d)  ;
	public void finishIndexing() throws ClassNotFoundException,
	IOException;
}
