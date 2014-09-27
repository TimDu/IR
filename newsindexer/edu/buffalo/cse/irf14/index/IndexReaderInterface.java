package edu.buffalo.cse.irf14.index;

import java.util.List;
import java.util.Map;

public interface IndexReaderInterface {

	public abstract int getTotalKeyTerms();

	public abstract int getTotalValueTerms();

	public abstract Map<String, Integer> getPostings(String term);

	public abstract List<String> getTopK(int k);

	public abstract Map<String, Integer> query(String... terms);

}