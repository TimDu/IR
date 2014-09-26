/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo This factory class is responsible for instantiating
 *         "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {

	private static AnalyzerFactory instance = null;

	private AnalyzerFactory() {

	}

	/**
	 * Static method to return an instance of the factory class. Usually factory
	 * classes are defined as singletons, i.e. only one instance of the class
	 * exists at any instance. This is usually achieved by defining a private
	 * static instance that is initialized by the "private" constructor. On the
	 * method being called, you return the static instance. This allows you to
	 * reuse expensive objects that you may create during instantiation
	 * 
	 * @return An instance of the factory
	 */
	public static AnalyzerFactory getInstance() {
		// TODO: YOU NEED TO IMPLEMENT THIS METHOD
		if (instance == null) {
			instance = new AnalyzerFactory();
		}
		return instance;
	}

	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance for a
	 * given {@link FieldNames} field Note again that the singleton factory
	 * instance allows you to reuse {@link TokenFilter} instances if need be
	 * 
	 * @param name
	 *            : The {@link FieldNames} for which the {@link Analyzer} is
	 *            requested
	 * @param TokenStream
	 *            : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable
	 *         {@link FieldNames} null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		// TODO : YOU NEED TO IMPLEMENT THIS METHOD

		/*
		 * Need to be careful here, must be considerate of what filters we apply
		 * for the given field types. E.g Do we run accent removal on author?
		 * What about Category? The project requirements states it can be called
		 * with TERM, PLACE, AUTHOR or CATEGORY. TERM isn't a field name,
		 * probably meant content? Can we even rely on this statement as there
		 * are many field names: FILEID, CATEGORY, TITLE, AUTHOR, AUTHORORG,
		 * PLACE, NEWSDATE, CONTENT. Should ask this on discussion board.
		 */

		switch (name) {
		case TITLE:
			return getTitleAnalyzer(stream);
		case AUTHOR:
			return getAuthorAnalyzer(stream);
		case AUTHORORG:
			return getAuthorOrgAnalyzer(stream);
		case PLACE:
			return getPlaceAnalyzer(stream);
		case NEWSDATE:
			return getNewsDateAnalyzer(stream);
		case CONTENT:
			return getContentAnalyzer(stream);
		case CATEGORY:
			return getCategoryAnalyzer(stream);
		default:
			return null;
		}
	}
	
	protected Analyzer getCategoryAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getTitleAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getAuthorAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getAuthorOrgAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getPlaceAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getNewsDateAnalyzer(TokenStream stream) {
		return null;
	}

	protected Analyzer getContentAnalyzer(TokenStream stream) {
		// May need multiple passes of certain filters depending on corpus

		try {
			runAccentRule(stream);
			// cap must be run after accent
			runCapRule(stream);
			// date could be run before accent
			runDateRule(stream);
			// expand smybols
			runSymbolRule(stream);
			// should run stopword after the symbol rule
			runStopWordRule(stream);
			// remove special characters can seemingly 
			// be run in parallel with anything after date
			runSpecialCharRule(stream);
			// stemmer should be run last
			runStemmerRule(stream);

		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new NumericRule(stream);
	}
	
	protected void runAccentRule(TokenStream stream) throws TokenizerException
	{
		AccentRule acr = new AccentRule(stream);
		while (acr.increment()) {
		}
		// just to make sure my aliasing logic is correct
		assert (stream == acr.getStream());
	}
	
	 
	protected void runCapRule(TokenStream stream) throws TokenizerException
	{
		CapitalizationRule cpr = new CapitalizationRule(stream);
		while (cpr.increment()) {
		}
	}
	protected void runStopWordRule(TokenStream stream) throws TokenizerException
	{
		StopWordRule swr = new StopWordRule(stream);
		while (swr.increment()) {
		}
	}
	
	protected void runDateRule(TokenStream stream) throws TokenizerException
	{
		DateRule dr = new DateRule(stream);
		while (dr.increment()) {
		}
	}
	

	protected void runSpecialCharRule(TokenStream stream) throws TokenizerException
	{
		SpecialCharRule scr = new SpecialCharRule(stream);
		while (scr.increment()) {
		}
	}
	
	protected void runStemmerRule(TokenStream stream) throws TokenizerException
	{
		StemmerRule stmr = new StemmerRule(stream);
		while (stmr.increment()) {
		}
	}
	
	protected void runSymbolRule(TokenStream stream) throws TokenizerException
	{
		SymbolRule symr = new SymbolRule(stream);
		while (symr.increment()) {
		}
	}
}
