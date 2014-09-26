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
		String test = "need-test-Feb/2/2014Marked_Down";
		String result = "need test 20140202markeddown";
		String test1 = "___";
		
		TokenStream stream = new Tokenizer().consume(test);
		CategoryAnalyzer analyzer = new CategoryAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			assertEquals(result, stream.next().toString());
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
