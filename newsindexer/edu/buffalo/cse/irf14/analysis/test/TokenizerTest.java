package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class TokenizerTest {

	@Test
	public void testWhiteSpaceDelim() {
		int index = 0;
		Tokenizer tokenizer = new Tokenizer();
		String input = "This is a test.";
		String []tester = {"This", "is", "a", "test."};

		try {
			TokenStream stream = tokenizer.consume(input);
			while (stream.hasNext()) {
				assertEquals(tester[index++], stream.next().toString());
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSpecialDelim() {
		int index = 0;
		Tokenizer tokenizer0 = new Tokenizer("\t");
		Tokenizer tokenizer1 = new Tokenizer("	");
		Tokenizer tokenizer2 = new Tokenizer("\\");
		Tokenizer tokenizer3 = new Tokenizer("\\\\");
		String []tester = {"This", "is", "a", "test."};
		
		try {
			String input = "This\tis\ta\ttest.";
			// Test 0
			TokenStream stream = tokenizer0.consume(input);
			while (stream.hasNext()) {
				assertEquals(tester[index++], stream.next().toString());
			}
			// Test 1
			index = 0;
			stream = tokenizer1.consume(input);
			while (stream.hasNext()) {
				assertEquals(tester[index++], stream.next().toString());
			}
			// Test 2
			index = 0;
			input = "This\\is\\a\\test.";
			stream = tokenizer2.consume(input);
			while (stream.hasNext()) {
				assertEquals(tester[index++], stream.next().toString());
			}
			// Test 3
			index = 0;
			input = "This\\\\is\\\\a\\\\test.";
			stream = tokenizer3.consume(input);
			while (stream.hasNext()) {
				assertEquals(tester[index++], stream.next().toString());
			}
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
