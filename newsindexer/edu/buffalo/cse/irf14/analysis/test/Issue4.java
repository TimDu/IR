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
	public void test1() throws TokenizerException {
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
	public void test2() throws TokenizerException {
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

}
