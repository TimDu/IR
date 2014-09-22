package edu.buffalo.cse.irf14.analysis;


/**
 * Token filter implementation that handles SYMBOL
 */
public class SymbolRule extends TokenFilter {
	
	final String []endMark = {".", "!", "?"};
	final String []goodApos =
		{"shan't", "won't", "'ve", "n't", "'m", "'re",
			"'ll", "'d", "'em"};
	final String []alterApos =
		{"shall not", "will not", " have", " not",
			" am", " are", " will", " would", "them"};
	final String []badApos = {"'s", "'"};

	/**
	 * Inherent constructor.
	 * @param stream the token stream to be filtered
	 */
	public SymbolRule(TokenStream stream) {
		super(stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		String []segs = null;
		
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.getTermText();
		if (term == null) {
			throw new TokenizerException();
		}
		
		// Find and eliminate end marks in current token
		boolean hasMore = true;
		while (hasMore) {
			hasMore = false;
			
			for (String mark: endMark) {
				// End marks in the middle
				while (term.contains(mark + " ")) {
					term = term.replace(mark + " ", " ");
					hasMore = true;
				}
				// End marks in the end
				while (term.endsWith(mark)) {
					term = term.substring(0, term.length() - 1);
					hasMore = true;
				}
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
			term = term.replace(mark, "");
		}
		term = term.substring(0, term.length() - 1);
		
		// Retain qualified hyphens only
		segs = term.split("-");
		if (segs.length > 1) {
			term = new String();
			for (int i = 0; i < segs.length - 1; ++i) {
				if (segs[i].isEmpty()) continue;
				char thisLast = segs[i]
						.charAt(segs[i].length() - 1);
				char nextFirst = segs[i + 1].charAt(0);
				
				if (isNumber(thisLast) &&
						(isAlpha(nextFirst) ||
						(isNumber(nextFirst)))) {
					term += segs[i] + "-";
				} else if (isAlpha(thisLast) &&
						isNumber(nextFirst)) {
					term += segs[i] + "-";
				} else if (isUpper(thisLast) &&
						isUpper(nextFirst)) {
					term += segs[i] + "-";
				} else if (isSpace(thisLast)) {
					if (isSpace(nextFirst)){
						term += segs[i].substring(
								0, segs[i].length() - 1);
					} else {
						term += segs[i];
					}
				} else if (isSpace(nextFirst)) {
					term += segs[i];
				} else {
					term += segs[i] + " ";
				}
			}
			term += segs[segs.length - 1];
		} else {
			term = term.replace("-", "");
		}
		
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

	/**
	 * Tests if this is a numeric character
	 * @param c tested character
	 */
	private boolean isNumber(char c) {
		return (c <= '9' && c >= '0');
	}
	
	/**
	 * Tests if this is a alphabetic character
	 * @param c tested character
	 */
	private boolean isUpper(char c) {
		return (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Tests if this is a alphabetic character
	 * @param c tested character
	 */
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Tests if this is a white space character
	 * @param c tested character
	 */
	private boolean isSpace(char c) {
		return c == ' ';
	}
}
