package edu.buffalo.cse.irf14.analysis;

public class CapitalizationRule extends TokenFilter {
	
	Token chainReference; 
	
	public CapitalizationRule(TokenStream stream) {
		super(stream);
		chainReference = null;
	}
	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if(tok == null) return stream.hasNext();
		if (FilterUtility
				.updateCapitalization(tok)) {

			if(Character.isUpperCase(tok.toString().charAt(0)))
			{
				if(chainReference != null)
				{
					chainReference.merge(tok);
					stream.remove();
					return stream.hasNext();
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
		return stream;
	}

}
