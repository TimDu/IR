package edu.buffalo.cse.irf14.analysis;

public class Sentence {
	
	private boolean allCaps;
	private Token firstToken;
	private Token lastToken;
	
	public Sentence()
	{
		allCaps = false;
	}
	
	public void setAllCaps(boolean input)
	{
		allCaps = input;
	}
	
	public boolean getAllCaps()
	{
		return allCaps;
	}
	
	public void setFirstToken(Token input)
	{
		firstToken = input;
	}
	
	public Token getFirstToken()
	{
		return firstToken;
	}
	
	public void setLastToken(Token input)
	{
		lastToken = input;
	}
	
	public Token getLastToken()
	{
		return lastToken;
	}
}
