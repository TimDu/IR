package edu.buffalo.cse.irf14.analysis;

/**
 * Chained filter for Place field in front of the report.
 * Considered filters:<br>
 * Symbol, Accents, SpecialChars, Capitalization.
 */
public class PlaceAnalyzer implements Analyzer {

	TokenStream stream;
	Token tempTok;
	
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
		
		// Accents filter
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

	/**
	 * It is assumes that there is only one place in
	 * place field, so merge all separated tokens here.
	 */
	@Override
	public TokenStream getStream() {
		String temp = null;
		Token tok = null;
		Token prevTok = null;
		
		stream.reset();
		while (stream.hasNext()) {
			tok = stream.next();
			temp = tok.toString();
			temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);
			tok.setTermText(temp);
			if (prevTok != null) {
				prevTok.merge(tok);
				stream.remove();
			} else {
				prevTok = tok;
			}
		}
		
		return stream;
	}

}
