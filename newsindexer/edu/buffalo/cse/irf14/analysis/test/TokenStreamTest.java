package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;

public class TokenStreamTest {

	@Test
	public void testTokenStream() {
		int count = 0;
		Token t = new Token();
		TokenStream ts = new TokenStream(t);
		TokenStream ts1 = new TokenStream(t);
		ts.append(ts1);
		
		while (ts.hasNext()) {
			assertEquals(t, ts.next());
			++count;
		}
		assertEquals(2, count);
		
		ts.reset();
		while (ts.hasNext()) {
			ts.next();
			ts.remove();
		}
		assertFalse(ts.hasNext());
	}
}
