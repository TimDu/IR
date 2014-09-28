package edu.buffalo.cse.irf14.analysis;

public class StemmerRule extends TokenFilter {

	public StemmerRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();

		FilterUtility.updateStemmer(tok);

		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
