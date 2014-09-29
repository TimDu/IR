package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.AuthorAnalyzer;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class AuthorAnalyzerTest {

	// Symbol, Accents, SpecialChars, Capitalization
	@Test
	public void test() throws TokenizerException {
		String test = "pièce-X.-brûlée";
		String result = "piece X. brulee";	// Not sure how this should be formatted
		String test1 = "The Test";
		
		TokenStream stream = new Tokenizer().consume(test);
		AuthorAnalyzer analyzer = new AuthorAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
			//assertEquals(result, stream.getCurrent().toString());
		}

		stream = new Tokenizer().consume(test1);
		analyzer = new AuthorAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
			//assertEquals(result, stream.getCurrent().toString());
		}
	}
}
