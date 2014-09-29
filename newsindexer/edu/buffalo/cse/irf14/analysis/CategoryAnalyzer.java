package edu.buffalo.cse.irf14.analysis;

/**
 * A chained analyzer specially for Category field.
 * Considered token filters:<br>
 * Symbol, SpecialChars, Dates, Capitalization, Stemmer.
 */
public class CategoryAnalyzer implements Analyzer {
	
	private TokenStream stream = null;
	private Token tempTok;
	
	/**
	 * Method that feeds a stream to this analyzer
	 * @param stream token stream to be analyzed
	 */
	public void setStream(TokenStream stream) {
		this.stream = stream;
		tempTok = null;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		if (stream == null) {
			throw new TokenizerException();
		}
		
		Token tok = stream.next();
		if(tok == null) return stream.hasNext();
		String term = FilterUtility.updateSymbol(tok);
		
		// Symbol filter handler
		if (term.isEmpty()) {
			// This token is empty now, cannot proceed anymore
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
		FilterUtility.updateDate(stream.getCurrent(), stream);
		// Capitalization filter
		if (FilterUtility
				.updateCapitalization(tok)) {
			if(Character.isUpperCase(tok.toString().charAt(0)))
			{
				if(tempTok != null)
				{
					tempTok.merge(tok);
					stream.remove();
					tok = tempTok;
				}
				else
				{
					tempTok = tok;
				}
			}
			else
			{
				tempTok = null;
			}
		}
		// Stemmer filter
		FilterUtility.updateStemmer(tok);
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
