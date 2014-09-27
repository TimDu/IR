package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.NewsDateAnalyzer;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class NewsDateAnalyzerTest {

	// Symbol, SpecialChars, Date, Number
	@Test
	public void test() throws TokenizerException {
		String test = "March-3";
		String result = "19000303";
		
		TokenStream stream = new Tokenizer().consume(test);
		NewsDateAnalyzer analyzer = new NewsDateAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
			assertEquals(result, stream.getCurrent().toString());
		}
	}
}
