package edu.buffalo.cse.irf14.analysis;

/**
 * Token filter that handles Dates type.
 */
public class DateRule extends TokenFilter {

	public DateRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		return false;
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	/**
	 * 
	 */
}
