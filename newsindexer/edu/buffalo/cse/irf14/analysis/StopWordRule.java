package edu.buffalo.cse.irf14.analysis;

public class StopWordRule extends TokenFilter {

	public StopWordRule(TokenStream stream) {
		super(stream);
	}
	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}

}
