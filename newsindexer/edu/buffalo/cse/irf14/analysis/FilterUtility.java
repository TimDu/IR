package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FilterUtility {
	private static final String[] stopWordList =
		{ "a", "able", "about", "across", "after",
		"all", "almost", "also", "am", "among", "an", "and", "any", "are",
		"as", "at", "be", "because", "been", "but", "by", "can", "cannot",
		"could", "dear", "did", "do", "does", "either", "else", "ever",
		"every", "for", "from", "get", "got", "had", "has", "have", "he",
		"her", "hers", "him", "his", "how", "however", "i", "if", "in",
		"into", "is", "it", "its", "just", "least", "let", "like",
		"likely", "may", "me", "might", "most", "must", "my", "neither",
		"no", "nor", "not", "of", "off", "often", "on", "only", "or",
		"other", "our", "own", "rather", "said", "say", "says", "she",
		"should", "since", "so", "some", "than", "that", "the", "their",
		"them", "then", "there", "these", "they", "this", "tis", "to",
		"too", "twas", "us", "wants", "was", "we", "were", "what", "when",
		"where", "which", "while", "who", "whom", "why", "will", "with",
		"would", "yet", "you", "your" };

	private static HashSet<String> stopWordSet = null;

	/**
	 * Accent filter utility, directly perform updates on the token.
	 * @param tok token to be updated
	 * @throws TokenizerException
	 */
	public static void updateAccent(Token tok) throws TokenizerException {
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
	
	
	/**
	 * Capitalization filter utility. This might request a merge
	 * on a token if specific requirements are satisfied.
	 * 
	 * @param tok token to be updated
	 * @return {@code true} if a camel cased word appears; otherwise,
	 * return {@code false} 
	 * @throws TokenizerException 
	 */
	public static boolean
		updateCapitalization(Token tok) throws TokenizerException {
		/*
		 * All tokens should be lowercased unless: 
			*  The whole word is in caps (AIDS etc.) and the 
			whole sentence is not in caps 
			*  The word is camel cased and is not the first 
			word in a sentence.  
			*  If adjacent tokens satisfy the above rule, they 
			should be combined into a single token (San 
			Francisco, Brad Pitt, etc.) 
			
			The idea being if the capitalization is deliberate - keep it. 
	
			So unless it's dictated by grammar:
			
			- first word in a sentence
			- a word in a fully capitalized sentence
			
			You don't lowercase it.
		 */
		
		// We will perform sentence rules as follows
		// 1. Check if whole sentence is in all caps, 
		//    if so, make current token lower case, return
		// 2. Else, Check if current token is in all caps, if so return
		// 3. Else, Check if first word, if so always force lower case
		// 4. Else Check if camel casing is in effect for current word
		//    check if adjacent words are upper case
		
		// Error checking
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		if(tok.getSentenceContainer().getAllCaps())
		{
			tok.setTermText(term.toLowerCase());
		}
		else if(tok == tok.getSentenceContainer().getFirstToken())
		{
			tok.setTermText(term.toLowerCase());
		}
		// any other case make normal?
		else
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Numeric filter utility.
	 * @param tok token to be updated
	 * @return filtered term
	 * @throws TokenizerException
	 */
	public static String updateNumber(Token tok) throws TokenizerException {
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		String []eliminates = NumericMatcher.getMatches(term);

		if (eliminates != null) {
			// Start numeric elimination
			for (String e: eliminates) {
				// Eliminate 'qualified' numbers
				term = term.replaceAll(" " + e + " " + "|" + " "
				+ e + "|" + e + " ", " ");
				term = term.replace(e, "");
			}
			term = term.trim();
		}
	
		return term;
	}

	/**
	 * Special Characters filter utility.
	 * @param tok token to be updated
	 * @return filtered term
	 * @throws TokenizerException
	 */
	public static String updateSpecialChar(Token tok)
			throws TokenizerException {
		final char []specials =
				{'.', '!', '?', '\'', ' ', '-', '/'};

		if (tok == null) {
			throw new TokenizerException();
		}
		char []termBuff = tok.getTermBuffer();
		if (termBuff == null) {
			throw new TokenizerException();
		}
		String term = new String();
		List<Character> words = new LinkedList<Character>();
		for (char word: termBuff) {
			words.add(word);
		}
		Iterator<Character> iter = words.iterator();

		while(iter.hasNext()) {
			// Test whether this is or is not a number
			// or an alphabet
			char character = iter.next();
			if (!Character.
					isAlphabetic(character) &&
					!Character.isDigit(character)) {
				boolean isInvalid = true;
				// Excludes several other special characters
				for (char c: specials) {
					if (character == c) {
						isInvalid = false;
						break;
					}
				}
				if (isInvalid) {
					iter.remove();
				}
			}
		}

		// Special care on '-'
		int index = words.indexOf('-');
		
		while (index > -1) {
			if (index > 0 && index < (words.size() - 1)) {
				if (((!Character.isDigit(words.get(index - 1)) &&
						!Character
						.isAlphabetic(words.get(index - 1))) ||
						!Character.isDigit(words.get(index + 1))) &&
						!(words.get(index - 1) == ' ' &&
								Character.isAlphabetic(words
										.get(index + 1)))) {
					words.remove(index);
				}
			} else {
				if (!(words.size() > 1 &&
						Character.isAlphabetic(words.get(1)))) {
					words.remove(0);
				}
			}
			
			if (index == words.indexOf('-')) {
				break;
			} else {
				index = words.indexOf('-');
			}
		}
		
		// Special care on '/'
		index = words.indexOf('/');
		
		while (index > -1) {
			if (index > 0 && index < (words.size() - 1)) {
				if ((!Character.isDigit(words.get(index - 1)) &&
						!Character.isAlphabetic(words.get(index - 1)) ||
						!Character.isDigit(words.get(index + 1)))) {
					words.remove(index);
				}
			} else {
				words.remove(0);
			}
			
			for (; index < words.size(); ++index) {
				if (words.get(index) == '/') {
					continue;
				}
			}
			break;
		}
		
		// Apply the result on term
		for (char word: words) {
			term += word;
		}
		
		return term;
	}

	/**
	 * Stemmer filter utility.
	 * @param tok token to be updated
	 * @throws TokenizerException
	 */
	public static void updateStemmer(Token tok) throws TokenizerException {
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
				return;
			}
			stem.add(c);
		}
		
		stem.stem();
		tok.setTermText(stem.toString());
	}
	
	/**
	 * Symbol filter utility
	 * @param tok token to be updated
	 * @return filtered term
	 * @throws TokenizerException 
	 */
	public static String updateSymbol(Token tok) 
			throws TokenizerException {
		final String []endMark = {".", "!", "?"};
		final String []goodApos =
			{"shan't", "won't", "'ve", "n't", "'m", "'re",
				"'ll", "'d", "'em"};
		final String []alterApos =
			{"shall not", "will not", " have", " not",
				" am", " are", " will", " would", "them"};
		final String []badApos = {"'s", "'"};
		
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.getTermText();
		if (term == null) {
			throw new TokenizerException();
		}
		String []segs = null;
		
		// Find and eliminate end marks in current token
		boolean hasMore = true;
		while (hasMore) {
			hasMore = false;
			
			for (String mark: endMark) {
				// End marks in the middle
				while (term.contains(mark + " ")) {
					term = term.replace(mark + " ", " ");
					hasMore = true;
				}
				// End marks in the end
				while (term.endsWith(mark)) {
					term = term.substring(0, term.length() - 1);
					hasMore = true;
				}
			}
		}
		
		// Retain qualified apostrophes
		term += " ";
		for (int i = 0; i < goodApos.length; ++i) {
			term = term.replace(goodApos[i] + " ",
					alterApos[i] + " ");
		}
		
		// Remove unqualified apostrophes
		for (String mark: badApos) {
			term = term.replace(mark, "");
		}
		term = term.substring(0, term.length() - 1);
		
		// Retain qualified hyphens only
		segs = term.split("-");
		if (segs.length > 1) {
			term = new String();
			for (int i = 0; i < segs.length - 1; ++i) {
				if (segs[i].isEmpty()) continue;
				char thisLast = segs[i]
						.charAt(segs[i].length() - 1);
				char nextFirst = segs[i + 1].charAt(0);
				
				if (isNumber(thisLast) &&
						(isAlpha(nextFirst) ||
						(isNumber(nextFirst)))) {
					term += segs[i] + "-";
				} else if (isAlpha(thisLast) &&
						isNumber(nextFirst)) {
					term += segs[i] + "-";
				} else if (isUpper(thisLast) &&
						isUpper(nextFirst)) {
					term += segs[i] + "-";
				} else if (isSpace(thisLast)) {
					if (isSpace(nextFirst)){
						term += segs[i].substring(
								0, segs[i].length() - 1);
					} else {
						term += segs[i];
					}
				} else if (isSpace(nextFirst)) {
					term += segs[i];
				} else {
					term += segs[i] + " ";
				}
			}
			term += segs[segs.length - 1];
		} else {
			term = term.replace("-", "");
		}
		
		return term;
	}
	
	/**
	 * Stop words filter.
	 * @param tok token to be updated
	 * @return {@true} if there is a stop word in token;
	 * otherwise, return {@code false}
	 * @throws TokenizerException 
	 */
	public static boolean updateStopWord(Token tok) 
			throws TokenizerException {
		if (stopWordSet == null) {
			// Initialize for the first time of use
			stopWordSet = new HashSet<String>();
			for (String word : stopWordList) {
				stopWordSet.add(word);
			}
		}

		// Begin error checking
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		// End error checking
		
		return stopWordSet.contains(tok.toString());
	}
	
	/**
	 * Date filter.
	 * @param tok tokens to be updated
	 * @param stream the stream where current token is in
	 * @throws TokenizerException
	 */
	public static void updateDate(Token tok, TokenStream stream) 
			throws TokenizerException {
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		// Reorganize date information in tokens
		if (DateMatcher.containedComponent(term)) {
			String tempTerm = term;

			// Merging step
			while (DateMatcher.hasNext(tempTerm)) {
				if (stream.hasNext()) {
					Token tempTok = stream.next();
					tempTerm = tempTok.toString();
					
					if (DateMatcher.shouldMerge(tempTerm)) {
						if (tempTerm.startsWith("AD") ||
								tempTerm.startsWith("BC") ||
								tempTerm.toLowerCase()
								.startsWith("am") ||
								tempTerm.toLowerCase()
								.startsWith("pm")) {
							term = tok.toString();
							term += tempTerm.trim();
							tok.setTermText(term);
						} else {
							tok.merge(tempTok);
						}
						stream.remove();
					} else {
						// Do not merge this token,
						// go back to original token
						stream.previous();
						break;
					}
				} else {
					break;
				}
			}
			// Get updated token term
			term = tok.toString();
		}
		
		// Alternating step
		Map<String, String> map = DateMatcher.mapDates(term);

		if (map != null) {
			for (String raw: map.keySet()) {
				term = term.replace(raw, map.get(raw));
			}
			tok.setTermText(term);
		}
	}
	
	/**
	 * Tests if this is a numeric character
	 * @param c tested character
	 */
	private static boolean isNumber(char c) {
		return (c <= '9' && c >= '0');
	}
	
	/**
	 * Tests if this is a alphabetic character
	 * @param c tested character
	 */
	private static boolean isUpper(char c) {
		return (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Tests if this is a alphabetic character
	 * @param c tested character
	 */
	private static boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	/**
	 * Tests if this is a white space character
	 * @param c tested character
	 */
	private static boolean isSpace(char c) {
		return c == ' ';
	}
}
