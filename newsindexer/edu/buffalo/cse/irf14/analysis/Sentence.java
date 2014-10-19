package edu.buffalo.cse.irf14.analysis;

public class Sentence {
	
	private boolean allCaps;
	private boolean isSecondUpper;
	private Token firstToken;
	private Token lastToken;
	
	public Sentence()
	{
		allCaps = true;
		isSecondUpper = false;
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
	
	// Test if the initial letter in the second token
	// is upper case.
	public boolean isSecondUpper() {
		return isSecondUpper;
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
