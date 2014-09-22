package edu.buffalo.cse.irf14.analysis;

public class StopWordRule extends TokenFilter {
	
	final String []stopWordList = {"do", "not", "this", "is", "a", "of"};
	
	public StopWordRule(TokenStream stream) {
		super(stream);
	}
	@Override
	public boolean increment() throws TokenizerException {
		
		Token tok = stream.next();
		
		// Begin error checking
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		// End error checking
		
		for (String mark: stopWordList) {
			if (term.equals(mark))
			{
				stream.remove();
				break;
			}
		}
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
