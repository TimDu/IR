/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * Factory class for instantiating a given TokenFilter
 * 
 * @author nikhillo
 *
 */
public class TokenFilterFactory {

	private static TokenFilterFactory instance = null;

	private TokenFilterFactory() {

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
	public static TokenFilterFactory getInstance() {
		if (instance == null) {
			instance = new TokenFilterFactory();
		}
		return instance;
	}

	/**
	 * Returns a fully constructed {@link TokenFilter} instance for a given
	 * {@link TokenFilterType} type
	 * 
	 * @param type
	 *            : The {@link TokenFilterType} for which the
	 *            {@link TokenFilter} is requested
	 * @param stream
	 *            : The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		// SYMBOL, DATE, NUMERIC, CAPITALIZATION, STOPWORD, STEMMER, ACCENT,
		// SPECIALCHARS

		switch (type) {
		case SYMBOL:
			return new SymbolRule(stream);
		case DATE:
			return new DateRule(stream);
		case NUMERIC:
			return new NumericRule(stream);
		case CAPITALIZATION:
			return new CapitalizationRule(stream);
		case STOPWORD:
			return new StopWordRule(stream);
		case STEMMER:
			return new StemmerRule(stream);
		case ACCENT:
			return new AccentRule(stream);
		case SPECIALCHARS:
			return new SpecialCharRule(stream);
		default:
			return null;
		}
	}
}
