package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Accents extends TokenFilter {

	public Accents(TokenStream stream) {
		super(stream);
	}

	@Override
	public void increment() throws TokenizerException {
		// TODO Auto-generated method stub
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
	    String nfdNormalizedString =
	    		Normalizer.normalize(term, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{Mn}|\\p{Me}");
	    term = pattern.matcher(nfdNormalizedString).replaceAll("");
	    tok.setTermText(term);
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
