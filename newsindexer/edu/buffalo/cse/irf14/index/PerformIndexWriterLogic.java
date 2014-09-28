package edu.buffalo.cse.irf14.index;

import java.io.IOException;

import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public interface PerformIndexWriterLogic {
	
	
	public void performIndexLogic(
			Document d, FieldNames fn) throws TokenizerException;
	public void finishIndexing() throws ClassNotFoundException,
	IOException;
}
