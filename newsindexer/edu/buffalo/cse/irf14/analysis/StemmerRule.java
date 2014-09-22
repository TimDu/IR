package edu.buffalo.cse.irf14.analysis;

import java.util.List;
import java.util.Arrays;
import java.util.Vector;

public class StemmerRule extends TokenFilter {

	public StemmerRule(TokenStream stream) {
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

		// Stem logic
		Stemmer stem = new Stemmer();

		for (char c : term.toLowerCase().toCharArray()) {
			if(!Character.isAlphabetic(c))
			{
				return stream.hasNext();
			}
			stem.add(c);
			
		}
		
		
		stem.stem();
		tok.setTermText(stem.toString());
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
