package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Accents;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class AccentsTest {

	@Test
	public void testAccents() {
		String test = "Ȳ Ǭ á a̋ à ȁ";
		String result = "Y O a a a a";
		try {
			TokenStream stream = new Tokenizer().consume(test);
			TokenStream answer = new Tokenizer().consume(result);
			Accents accents = new Accents(stream);
			while (stream.hasNext()) {
				accents.increment();
			}
			stream = accents.getStream();
			stream.reset();
			while (answer.hasNext()) {
				assertEquals(
						answer.next().toString(), stream.next().toString());
			}
			assertFalse(stream.hasNext());
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
