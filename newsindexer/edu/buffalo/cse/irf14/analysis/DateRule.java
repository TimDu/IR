package edu.buffalo.cse.irf14.analysis;

import java.util.Map;

/**
 * Token filter that handles Dates type.
 */
public class DateRule extends TokenFilter {

	public DateRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		/*
		 * Considering that a date might have been arbitrarily
		 * assigned into separated Token by Tokenizer, it seems
		 * not easy to implement this method with its original
		 * functional logic.
		 * Hence, this filter would review its next token if
		 * the current token contains some date components
		 * (e.g. day, month, etc.). And if the next token
		 * contains only date components, then it would be
		 * merged, and its next token would be checked as well,
		 * so on and so forth. 
		 */
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		// Reorganize date information in tokens
		if (DateMatcher.matches(term)) {
			String tempTerm = term;

			while (DateMatcher.hasNext(tempTerm)) {
				if (stream.hasNext()) {
					Token tempTok = stream.next();
					tempTerm = tempTok.toString();
					
					if (DateMatcher.shouldMerge(tempTerm)) {
						if (tempTerm.startsWith("AD") ||
								tempTerm.startsWith("BC") ||
								tempTerm.toLowerCase()
								.startsWith("am") ||
								tempTerm.toLowerCase()
								.startsWith("pm")) {
							term = tok.toString();
							term += tempTerm.trim();
							tok.setTermText(term);
						} else {
							tok.merge(tempTok);
						}
						stream.remove();
					} else {
						// Do not merge this token,
						// go back to original token
						stream.previous();
						break;
					}
				} else {
					break;
				}
			}
			// Get updated token term
			term = tok.toString();//System.out.println(term);
		}
		Map<String, String> map = DateMatcher.mapDates(term);

		if (map != null) {
			for (String raw: map.keySet()) {//System.out.println(raw + "-" + map.get(raw));
				term = term.replace(raw, map.get(raw));
			}
			tok.setTermText(term);
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
