package edu.buffalo.cse.irf14.analysis;

/**
 * A chained analyzer specially for Category field.
 * Considered token filters:<br>
 * Symbol, Dates, Capitalization, Stemmer.
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
		Token tok = stream.next();
		String term = FilterUtility.updateSymbol(tok);
		
		// Symbol filter handler
		if (term.isEmpty()) {
			stream.remove();
		} else {
			tok.setTermText(term);
		}
		
		//term = 
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
