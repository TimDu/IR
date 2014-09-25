package edu.buffalo.cse.irf14.analysis;

public class NumericRule extends TokenFilter {
	
	
	public NumericRule(TokenStream stream) {
		super(stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		String []eliminates = NumericMatcher.getMatches(term);
		
		if (eliminates != null) {
			// Start numeric elimination
			for (String e: eliminates) {
				// Eliminate 'qualified' numbers
				term = term.replaceAll(" " + e + "|" + e + " ", " ");
				term = term.replace(e, "");
			}
			term = term.trim();
		}

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
		// TODO Auto-generated method stub
		return stream;
	}

}
