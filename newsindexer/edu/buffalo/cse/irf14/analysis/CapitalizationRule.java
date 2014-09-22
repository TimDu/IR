package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class CapitalizationRule extends TokenFilter {
	
	Token chainReference; 
	
	public CapitalizationRule(TokenStream stream) {
		super(stream);
		chainReference = null;
	}
	@Override
	public boolean increment() throws TokenizerException {
		/*
		 * All tokens should be lowercased unless: 
			*  The whole word is in caps (AIDS etc.) and the 
			whole sentence is not in caps 
			*  The word is camel cased and is not the first 
			word in a sentence.  
			*  If adjacent tokens satisfy the above rule, they 
			should be combined into a single token (San 
			Francisco, Brad Pitt, etc.) 
			
			The idea being if the capitalization is deliberate - keep it. 

			So unless it's dictated by grammar:
			
			- first word in a sentence
			- a word in a fully capitalized sentence
			
			You don't lowercase it.
		 */
		
		// We will perform sentence rules as follows
		// 1. Check if whole sentence is in all caps, 
		//    if so, make current token lower case, return
		// 2. Else, Check if current token is in all caps, if so return
		// 3. Else, Check if first word, if so always force lower case
		// 4. Else Check if camel casing is in effect for current word
		//    check if adjacents words are upper case
		
		// Error checking
		Token tok = stream.next();
		
		if (tok == null) {
			throw new TokenizerException();
		}
		String term = tok.toString();
		if (term == null) {
			throw new TokenizerException();
		}
		
		if(tok.getSentenceContainer().getAllCaps())
		{
			tok.setTermText(term.toLowerCase());
		}
		else if(term == term.toUpperCase())
		{
			tok.setTermText(term);
		}
		else if(tok == tok.getSentenceContainer().getFirstToken())
		{
			tok.setTermText(term.toLowerCase());
		}
		// any other case make normal?
		else
		{		
			if(Character.isUpperCase(term.charAt(0)))
			{
				if(chainReference != null)
				{
					chainReference.merge(tok);
					stream.remove();
				}
				else
				{
					chainReference = tok;
				}
					
			}
			else
			{
				chainReference = null;
			}
		}
	    
		return stream.hasNext();
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return stream;
	}

}
