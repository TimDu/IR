package edu.buffalo.cse.irf14.analysis;


/**
 * Token filter implementation that handles SYMBOL
 */
public class SymbolRule extends TokenFilter {
	

	/**
	 * Inherent constructor.
	 * @param stream the token stream to be filtered
	 */
	public SymbolRule(TokenStream stream) {
		super(stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if(tok == null) return stream.hasNext();
		String term = FilterUtility.updateSymbol(tok);
	
		if (term.isEmpty()) {
			stream.remove();
		} else {
			tok.setTermText(term);
		}
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}
}
