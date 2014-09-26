package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.TokenFilter;
import edu.buffalo.cse.irf14.analysis.TokenFilterFactory;
import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

public class Issue4 {

	@Test
	public void testStopWords() throws TokenizerException {
		String str = "a stopword";
		String result = "stopword";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.STOPWORD, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	@Test
	public void testStemmer() throws TokenizerException {
		String str = "tonies and oscars";
		String result = "toni and oscar";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.STOPWORD, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}

	@Test
	public void testCapitalization() throws TokenizerException {
		String str = "The city San Francisco is in California.";
		String result = "the city San Francisco is in California.";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.CAPITALIZATION, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	///PASSED TESTS///
	@Test
	public void testAccents() throws TokenizerException {
		String str = "vis-¨¤-vis pi¨¨ce de r¨¦sistance";
		String result = "vis-a-vis piece de resistance";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.ACCENT, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	@Test
	public void testSymbol() throws TokenizerException {
		String str = "it isn't f''(x) = df'/dx";
		String result = "it is not f(x) = df/dx";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.SYMBOL, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	@Test
	public void testNumber() throws TokenizerException {
		String str = "scores of 96.92% and 98/100 for the Xbox 360 version";
		String result = "scores of % and / for the Xbox version";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.NUMERIC, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	@Test
	public void testSpeicalChar() throws TokenizerException {
		String str = "call #555-5555";
		String result = "call 555-5555";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.SPECIALCHARS, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
	
	@Test
	public void testDate() throws TokenizerException {
		String str = "January 30, 1948 during the evening prayer at 5:15PM.";
		String result = "19480130 during the evening prayer at 17:15:00.";
		
		Tokenizer tkizer = new Tokenizer("\\+");
		TokenStream tstream = tkizer.consume(str);
		TokenFilterFactory factory = TokenFilterFactory.getInstance();
		TokenFilter filter = 
				factory.getFilterByType(TokenFilterType.DATE, tstream);
		while (filter.increment()) {}
		tstream = filter.getStream();
		tstream.reset();
		if (tstream.hasNext()) {
			System.out.println(tstream.next().toString());
			assertEquals(result, tstream.getCurrent().toString());
		}
	}
}
