package edu.buffalo.cse.irf14.index;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class CategoryIndexWriter implements PerformIndexWriterLogic {

	IndexDictionary m_fdict;
	
	public CategoryIndexWriter(IndexDictionary fdict) {
		m_fdict = fdict;
	}
	
	@Override
	public void performIndexLogic(Document d,  FieldNames fn) throws IndexerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishIndexing() throws IndexerException {
		// TODO Auto-generated method stub

	}

}
