package edu.buffalo.cse.irf14.analysis;

/**
 * A analyzer class specially for Document ID.
 * It does simply converts a FileID received in the form
 * of TokenStream to a unique DocID
 */
public class CategoryAnalyzer implements Analyzer {
	
	private TokenStream stream;
	
	/**
	 * Method that feeds a stream to this analyzer
	 */
	public void setStream(TokenStream stream) {
		this.stream = stream;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
