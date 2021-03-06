package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.CategoryAnalyzer;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class CategoryAnalyzerTest {

	// Test Symbol, Dates, Capitalization, Stemmer chain
	@Test
	public void test() throws TokenizerException {
		String test = "Need-test-Feb/2/2014";
		String result = "need test 20140202";
		String test1 = "___";
		
		TokenStream stream = new Tokenizer().consume(test);
		CategoryAnalyzer analyzer = new CategoryAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next().toString());
			assertEquals(result, stream.getCurrent().toString());
		}
		
		stream = new Tokenizer().consume(test1);
		analyzer = new CategoryAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		assertNull(stream.next());
	}

}
