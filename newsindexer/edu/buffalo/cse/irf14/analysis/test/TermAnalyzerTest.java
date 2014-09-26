package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.TermAnalyzer;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class TermAnalyzerTest {

	@Test
	public void test() throws TokenizerException {
		String test = "U.S. CAPACITY USE RATE 79.8 PCT IN FEBRUARY "
				+ "U.S. factories, mines and utilities "
				+ "operated at 79.8 pct of capacity in February, "
				+ "compared with a revised 79.6 pct in January and "
				+ "December, the Federal reserve Board said. "
				+ "The Fed previously said the rate was 79.7 pct "
				+ "in January and 79.5 pct in December. "
				+ "A surge in automobile assemblies in February "
				+ "and a gain in primary metals production helped "
				+ "raise manufacturing to 80.1 pct capacity from "
				+ "79.9 pct in January.";
		String result = "need test 20140202markeddown";
		
		TokenStream stream = new Tokenizer().consume(test);
		TermAnalyzer analyzer = new TermAnalyzer();
		analyzer.setStream(stream);
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		while (stream.hasNext()) {//stream.next();
			System.out.println(stream.next().toString());
			//assertEquals(result, stream.next().toString());
		}
	}
}
