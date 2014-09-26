package edu.buffalo.cse.irf14.analysis;

/**
 * Token Filter that handles Accents type
 */
public class AccentRule extends TokenFilter {

	/**
	 * Inherent constructor.
	 * @param stream the token stream to be filtered
	 */
	public AccentRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		
		FilterUtility.updateAccent(tok);
	    
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
