package edu.buffalo.cse.irf14.analysis;

import java.util.Hashtable;

// Hashtable Implementation came from the following 
// http://javarevisited.blogspot.com/2012/01/java-hashtable-example-tutorial-code.html#ixzz3E4GcNAc8
public class StopWordRule extends TokenFilter {

	final String[] stopWordList = { "a", "able", "about", "across", "after",
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

	private Hashtable<String, String> stopWordHash;

	public StopWordRule(TokenStream stream) {
		super(stream);
		stopWordHash = new Hashtable<String, String>();
		for (String word : stopWordList) {
			stopWordHash.put(word, word);
		}

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

		// use hash map for constant Big-Oh lookups
		if (stopWordHash.containsKey(term)) {
			stream.remove();
		}

		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		return stream;
	}

}
