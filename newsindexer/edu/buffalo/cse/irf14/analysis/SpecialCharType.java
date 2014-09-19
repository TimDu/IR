package edu.buffalo.cse.irf14.analysis;

/**
 * Token filter that handles SpecialChar type.
 */
public class SpecialCharType extends TokenFilter{
	
	final char []specials = {'.', '!', '?', '\'', ' '};

	public SpecialCharType(TokenStream stream) {
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
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
