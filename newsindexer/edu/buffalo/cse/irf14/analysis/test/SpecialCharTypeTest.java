package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.SpecialCharType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class SpecialCharTypeTest {

	@Test
	public void test() {
		String test = "Ȳ Ǭ. á a̋ a 3 ? \\";
		String result = "Ȳ Ǭ. á a̋ a 3 ?";
		try {
			TokenStream stream = new Tokenizer().consume(test);
			TokenStream answer = new Tokenizer().consume(result);
			SpecialCharType sChar = new SpecialCharType(stream);
			while (stream.hasNext()) {
				sChar.increment();
			}
			stream = sChar.getStream();
			stream.reset();
			while (answer.hasNext()) {
				assertEquals(
						answer.next().toString(), stream.next().toString());
			}
			assertTrue(stream.hasNext());
			stream.next();
			assertFalse(stream.hasNext());
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
