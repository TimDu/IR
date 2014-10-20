/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	
	private String delimiter;
	
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		delimiter = new String(" ");
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		delimiter = delim;
		delimiter = delimiter.replace("\\", "\\\\");
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		if (str == null || str.length() <= 0) {
			throw new TokenizerException();
		}
		String []terms = str.split(delimiter);
		Token []tokens = new Token[terms.length];
		Sentence contSentence = new Sentence();
		boolean newSentence = true;
		boolean isSecondTerm = false;
		
		for (int i = 0; i < terms.length; ++i) {
			tokens[i] = new Token();
			tokens[i].setTermText(terms[i]);
			tokens[i].setSentenceContainer(contSentence);
			tokens[i].setPosition(i);
			
			if (isSecondTerm) {
				
				isSecondTerm = false;
			}
			
			if(newSentence)
			{
				contSentence.setFirstToken(tokens[i]);
				newSentence = false;
				isSecondTerm = true;
			}
			
			if(terms[i].toUpperCase() != terms[i])
			{
				contSentence.setAllCaps(false);
			}
			
			terms[i] = terms[i].trim();
			if(terms[i].endsWith(".") ||
					terms[i].endsWith("!") ||
					terms[i].endsWith("?") ||
					terms[i].contains(". ") ||
					terms[i].contains("! ") ||
					terms[i].contains("? "))
			{
				contSentence.setLastToken(tokens[i]);
				contSentence = new Sentence();
				newSentence = true;
				isSecondTerm = false;
			}
			else if(i + 1 == terms.length)
			{
				contSentence.setLastToken(tokens[i]);
				contSentence = new Sentence();
				newSentence = true;
				isSecondTerm = false;
			}
		}
		
		return new TokenStream(tokens);
	}
}
