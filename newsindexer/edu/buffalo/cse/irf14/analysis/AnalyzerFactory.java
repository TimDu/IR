/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * NOTE: Chained analyzer implementations here is not thread-safe
 * @author nikhillo This factory class is responsible for instantiating
 *         "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {

	private static AnalyzerFactory instance = null;
	private AuthorAnalyzer author;
	private CategoryAnalyzer category;
	private PlaceAnalyzer place;
	private TermAnalyzer term;
	private NewsDateAnalyzer newsDate;

	private AnalyzerFactory() {
		author = new AuthorAnalyzer();
		category = new CategoryAnalyzer();
		place = new PlaceAnalyzer();
		term = new TermAnalyzer();
		newsDate = new NewsDateAnalyzer();
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
		category.setStream(stream);
		return category;
	}

	protected Analyzer getTitleAnalyzer(TokenStream stream) {
		term.setStream(stream);
		return term;
	}

	protected Analyzer getAuthorAnalyzer(TokenStream stream) {
		author.setStream(stream);
		return author;
	}

	protected Analyzer getAuthorOrgAnalyzer(TokenStream stream) {
		author.setStream(stream);
		return author;
	}

	protected Analyzer getPlaceAnalyzer(TokenStream stream) {
		place.setStream(stream);
		return place;
	}

	protected Analyzer getNewsDateAnalyzer(TokenStream stream) {
		newsDate.setStream(stream);
		return newsDate;
	}

	protected Analyzer getContentAnalyzer(TokenStream stream) {
		term.setStream(stream);
		return term;
	}
}
