package edu.buffalo.cse.irf14.analysis;

public class StemmerRule extends TokenFilter {

	public StemmerRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if(tok == null || !isAlpha(tok.toString())) 
		{
			return stream.hasNext();
		}
		
		 
		
		FilterUtility.updateStemmer(tok);

		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
