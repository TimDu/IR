package edu.buffalo.cse.irf14.analysis;

/**
 * A chained analyzer specially for Category field.
 * Considered token filters:<br>
 * Symbol, Dates, Capitalization, Stemmer.
 */
public class CategoryAnalyzer implements Analyzer {
	
	private TokenStream stream;
	private Token tempTok;
	
	/**
	 * Method that feeds a stream to this analyzer
	 */
	public void setStream(TokenStream stream) {
		this.stream = stream;
		tempTok = null;
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		Token tok = stream.next();
		String term = FilterUtility.updateSymbol(tok);
		
		// Symbol filter handler
		if (term.isEmpty()) {
			// This token is empty now, cannot proceed anymore
			stream.remove();
			return stream.hasNext();
		} else {
			tok.setTermText(term);
		}
		// Date filter
		FilterUtility.updateDate(stream.getCurrent(), stream);
		// Capitalization filter
		if (FilterUtility
				.updateCapitalization(tok)) {
			if(Character.isUpperCase(tok.toString().charAt(0)))
			{
				if(tempTok != null)
				{
					tempTok.merge(tok);
					stream.remove();
				}
				else
				{
					tempTok = tok;
				}
			}
			else
			{
				tempTok = null;
			}
		}
		// Stemmer filter
		FilterUtility.updateStemmer(stream.getCurrent());
		
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
