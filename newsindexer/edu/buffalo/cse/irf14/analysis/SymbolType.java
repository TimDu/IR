package edu.buffalo.cse.irf14.analysis;


/**
 * Token filter implementation that handles SYMBOL
 */
public class SymbolType extends TokenFilter {
	
	final String []endMark = {".", "!", "?"};
	final String []goodApos = {"'ve", "n't", "'ll"};
	final String []alterApos = {" have", " not", " will"};
	final String []badApos = {"'d", "'s", "s'", "'re", "'m", "'"};

	/**
	 * Inherent constructor.
	 * @param stream the token stream to be filtered
	 */
	public SymbolType(TokenStream stream) {
		super(stream);
	}

	@Override
	public void increment() throws TokenizerException {
		Token tok = null;
		String term = null;
		String []segs = null;
		
		tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		term = tok.getTermText();
		if (term == null) {
			throw new TokenizerException();
		}
		
		// Find and eliminate end marks in current
		// token
		for (String mark: endMark) {
			term = term.replace(mark + " ", " ");
			if (term.endsWith(mark)) {
				term = term.substring(0,
						term.length() - 1);
			}
		}
		
		// Retain qualified apostrophes
		term += " ";
		for (int i = 0; i < goodApos.length; ++i) {
			term = term.replace(goodApos[i] + " ",
					alterApos[i] + " ");
		}
		// Remove unqualified apostrophes
		for (String mark: badApos) {
			term = term.replace(mark + " ", " ");
		}
		term = term.substring(0, term.length() - 1);
		
		// Retain qualified hyphens only
		segs = term.split("-");
		if (segs.length > 1) {
			term = new String();
			for (int i = 0; i < segs.length - 1; ++i) {
				if (isNumber(segs[i].
						charAt(segs[i].length() - 1)) &&
						isAlpha(segs[i + 1].charAt(0))) {
					term += segs[i] + "-";
				} else if (isAlpha(segs[i].
						charAt(segs[i].length() - 1)) &&
						isNumber(segs[i + 1].charAt(0))) {
					term += segs[i] + "-";
				} else if (isSpace(segs[i].
						charAt(segs[i].length() - 1))) {
					if (isSpace(segs[i + 1].charAt(0))){
						term += segs[i].substring(
								0, segs[i].length() - 1);
					} else {
						term += segs[i];
					}
				} else if (isSpace(segs[i + 1].charAt(0))) {
					term += segs[i];
				} else {
					term += segs[i] + " ";
				}
			}
			term += segs[segs.length - 1];
		} else {
			term.replace("-", "");
		}
		tok.setTermText(term);
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

	/**
	 * Tests if this is a numeric character
	 * @param c tested character
	 */
	private boolean isNumber(char c) {
		if (c <= '9' && c >= '0') {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Tests if this is a alphabetic character
	 * @param c tested character
	 */
	private boolean isAlpha(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Tests if this is a white space character
	 * @param c tested character
	 */
	private boolean isSpace(char c) {
		if (c == ' ') {
			return true;
		} else {
			return false;
		}
	}
}
