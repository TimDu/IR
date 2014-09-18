package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Symbol;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class SymbolTest {

	@Test
	public void testSymbol() {
		String test = "This is a test. Will it succeed? So...Yes!";
		String result = "This is a test Will it succeed So...Yes";
		
		TokenStream testStream = null;
		TokenStream resultStream = null;
		try {
			testStream = new Tokenizer().consume(test);
			resultStream = new Tokenizer().consume(result);
		} catch (TokenizerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Symbol symb = new Symbol(testStream);
		while (testStream.hasNext()) {
			try {
				symb.increment();
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		testStream = symb.getStream();
		testStream.reset();
		resultStream.reset();
		
		while (resultStream.hasNext()) {
			assertEquals(resultStream.next().toString(),
					testStream.next().toString());
		}
		assertFalse(testStream.hasNext());
	}

}
