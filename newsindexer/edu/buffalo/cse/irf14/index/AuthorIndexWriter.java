package edu.buffalo.cse.irf14.index;

import java.io.IOException;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class AuthorIndexWriter implements PerformIndexWriterLogic {
	IndexDictionary m_fdict;

	public AuthorIndexWriter(IndexDictionary fdict) {
		m_fdict = fdict;
	}
 

	@Override
	public void finishIndexing() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void performIndexLogic(Document d, FieldNames fn) {
		// TODO Auto-generated method stub
		
	}

}
