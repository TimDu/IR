package edu.buffalo.cse.irf14.analysis;

/**
 * Token filter that handles SpecialChar type.
 */
public class SpecialCharRule extends TokenFilter{

	public SpecialCharRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		String term = FilterUtility.updateSpecialChar(tok);
		
		if (term.isEmpty()) {
			stream.remove();
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
