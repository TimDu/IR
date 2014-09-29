package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.PlaceAnalyzer;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class PlaceAnalyzerTest {

	@Test
	public void test() throws TokenizerException {
		String test = "Ala-Açailândia,-Å's";
		String result = "Ala Acailandia A";
		
		String test1 = "San Francisco 8th streat";
		
		TokenStream stream = new Tokenizer().consume(test);
		PlaceAnalyzer analyzer = new PlaceAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
			//assertEquals(result, stream.getCurrent().toString());
		}
		
		stream = new Tokenizer().consume(test1);
		analyzer = new PlaceAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {
			System.out.println(stream.next());
		}
	}

}
