package edu.buffalo.cse.irf14.analysis;

/**
 * Numeric Rule filter. As is hinted in recitation slides,
 * this filter is expected to be placed after Date filter.
 */
public class NumericRule extends TokenFilter {
	
	
	public NumericRule(TokenStream stream) {
		super(stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		String term = FilterUtility.updateNumber(tok);

		if (term.isEmpty()) {
			// If nothing left in this token, remove it
			stream.remove();
		} else {
			// Otherwise, update term content
			tok.setTermText(term);
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
