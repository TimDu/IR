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
		FilterUtility.updateDate(tok, stream);
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
