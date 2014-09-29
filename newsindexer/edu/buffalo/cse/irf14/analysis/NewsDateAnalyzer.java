package edu.buffalo.cse.irf14.analysis;

/**
 * Chained filter for Place field.
 * Considered filters:<br>
 * Symbol, SpecialChars, Dates, Numbers
 */
public class NewsDateAnalyzer implements Analyzer {

	private TokenStream stream;
	
	/**
	 * Method that feeds a stream to this analyzer
	 * @param stream token stream to be analyzed
	 */
	public void setStream(TokenStream stream) {
		this.stream = stream;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		if (stream == null) {
			throw new TokenizerException();
		}
		Token tok = stream.next();
		String term = FilterUtility.updateSymbol(tok);
		// Symbol filter handler
		if (term.isEmpty()) {
			// Empty token, remove and return
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		
		// SpecialChars filter
		term = FilterUtility.updateSpecialChar(tok);
		if (term.isEmpty()) {
			// Empty term left, cannot proceed
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		
		// Date filter
		FilterUtility.updateDate(tok, stream);
		
		// Number filter
		term = FilterUtility.updateNumber(tok);
		if (term.isEmpty()) {
			// Empty term left, cannot proceed
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
