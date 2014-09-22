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
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		Map<String, String> map = DateMatcher.mapDates(term);
		
		for (String raw: map.keySet()) {
			term.replace(raw, map.get(raw));
		}
		tok.setTermText(term);
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
