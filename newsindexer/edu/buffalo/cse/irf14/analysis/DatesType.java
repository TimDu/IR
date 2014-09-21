package edu.buffalo.cse.irf14.analysis;

/**
 * Token filter that handles Dates type.
 */
public class DatesType extends TokenFilter {

	public DatesType(TokenStream stream) {
		super(stream);
	}

	@Override
	public void increment() throws TokenizerException {
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	/**
	 * 
	 */
}
