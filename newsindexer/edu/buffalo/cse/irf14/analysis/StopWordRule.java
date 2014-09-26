package edu.buffalo.cse.irf14.analysis;

public class StopWordRule extends TokenFilter {

	public StopWordRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {

		Token tok = stream.next();

		// use hash map for constant Big-Oh lookups
		if (FilterUtility.updateStopWord(tok)) {
			stream.remove();
		}

		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
