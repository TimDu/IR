package edu.buffalo.cse.irf14.analysis;

/**
 * A chained analyzer specially for Author, and AuthorOrg field.
 * Considered token filters:<br>
 * Symbol, Accents, SpecialChars, Capitalization.
 */
public class AuthorAnalyzer implements Analyzer {
	
	private TokenStream stream;
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
			// Empty token, remove and return
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		
		// Accent filter
		FilterUtility.updateAccent(tok);
		
		// SpecialChars filter
		term = FilterUtility.updateSpecialChar(tok);
		if (term.isEmpty()) {
			// Empty term left, cannot proceed
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		
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
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
