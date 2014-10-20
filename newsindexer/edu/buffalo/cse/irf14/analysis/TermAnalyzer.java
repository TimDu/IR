package edu.buffalo.cse.irf14.analysis;

/**
 * Analyzes any field value with full types of
 * token filter.
 */
public class TermAnalyzer implements Analyzer{

	private TokenStream stream = null;
	private Token tempTok;
	
	/**
	 * Method that feeds a stream to this analyzer
	 * @param stream a token stream to be analyzed
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
			// This token has nothing left
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		// Accents filter
		FilterUtility.updateAccent(tok);
		// SpecialChars filter
		term = FilterUtility.updateSpecialChar(tok);
		if (term.isEmpty()) {
			// This token has nothing left
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		// Date filter
		FilterUtility.updateDate(tok, stream);
		// Numeric filter
		term = FilterUtility.updateNumber(tok);
		if (term.isEmpty()) {
			// This token has nothing left
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		// Capitalization filter
		if (FilterUtility
				.updateCapitalization(tok)) {

			if(Character.isUpperCase(tok.toString().charAt(0))) {
				if(tempTok != null)
				{
					tempTok.merge(tok);
					stream.remove();
				}
				else
				{
					tempTok = tok;
				}
			} else {
				tempTok = null;
			}
		}
		
		term = FilterUtility.updateSpecialCharExtended(tok);
		// Stemmer filter
		FilterUtility.updateStemmer(tok);
		// Stop words filter
		if (FilterUtility.updateStopWord(tok)) {
			stream.remove();
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	
}
