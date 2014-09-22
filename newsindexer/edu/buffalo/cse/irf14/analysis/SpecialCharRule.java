package edu.buffalo.cse.irf14.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Token filter that handles SpecialChar type.
 */
public class SpecialCharRule extends TokenFilter{
	
	final char []specials = {'.', '!', '?', '\'', ' ', '-', '/'};

	public SpecialCharRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		List<Character> words = new LinkedList<Character>();
		for (char word: term.toCharArray()) {
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
				if ((!Character.isDigit(words.get(index - 1)) ||
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
				if ((!Character.isDigit(words.get(index - 1)) ||
						!Character.isDigit(words.get(index + 1)))) {
					words.remove(index);
				}
			} else {
				words.remove(0);
			}
			
			if (index == words.indexOf('/')) {
				break;
			} else {
				index = words.indexOf('/');
			}
		}
		
		// Apply the result on term
		term = new String();
		for (char word: words) {
			term += word;
		}
		
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
