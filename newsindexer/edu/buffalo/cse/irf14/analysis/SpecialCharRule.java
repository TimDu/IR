package edu.buffalo.cse.irf14.analysis;

/**
 * Token filter that handles SpecialChar type.
 */
public class SpecialCharRule extends TokenFilter{
	
	final char []specials = {'.', '!', '?', '\'', ' '};

	public SpecialCharRule(TokenStream stream) {
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

		for (int i = 0; i < term.length(); ++i) {
			// Test whether this is or is not a number
			// or an alphabet
			if (!Character.
					isAlphabetic(term.charAt(i)) ||
					!Character.isDigit(term.charAt(i))) {
				boolean isInvalid = true;
				// Excludes several other special characters
				for (char c: specials) {
					if (term.charAt(i) == c) {
						isInvalid = false;
						break;
					}
				}
				if (isInvalid) {
					StringBuilder destroyer = new StringBuilder(term);
					destroyer.deleteCharAt(i);
					term = destroyer.toString();
				}
			}
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
