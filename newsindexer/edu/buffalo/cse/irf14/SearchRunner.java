package edu.buffalo.cse.irf14;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};
	private enum Mode {QUERY, EVALUATION};
	
	private BufferedOutputStream writer;
	private String indexDir;
	private String corpusDir;
	private Mode mode;
	
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD
		this.indexDir = indexDir;
		this.corpusDir = corpusDir;
		this.mode = (mode == 'Q') ? Mode.QUERY : Mode.EVALUATION;  
		writer = new BufferedOutputStream(stream);
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		IndexReader reader;
		Query query = QueryParser.parse(userQuery, null);

		// Step 1, get relevant documents with no terms negated
		
		
		// Step 2, on the remaining document list
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
		int numQuery = 0;
		String result; 	// Output to be written
		List<Query> queryList = new LinkedList<Query>();
		List<String> queryID = new LinkedList<String>();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(queryFile));
			String line = reader.readLine();
			String []elements;	// ID-Query pair in query file
			// Error Check
			if ((line == null) || !line.startsWith("numQueries=")
					|| (line.split("=").length != 2)) {
				throw new IOException("Illegal query format!");
			} else {
				numQuery = Integer.valueOf(line.split("=")[1]);
			}
			// Read queries from file
			for (; numQuery > 0; --numQuery) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				elements = line.split(":");
				elements[1] = elements[1].substring(
						elements[1].indexOf("{") + 1
						, elements[1].indexOf("}"));
				queryID.add(elements[0]);
				queryList.add(QueryParser.parse(elements[1], null));
			}
			
			// Perform algorithm
			for(int i = 0; i < queryList.size(); i++)
			{
				//performQuerySearch(queryList.get(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
	
	/**
	 * Method that searches all document IDs based on query request
	 * 
	 * @param query
	 * @return
	 */
	private TreeSet<TermFrequencyPerFile> rawSearch(Query query) {
		
		
		return null;
	}
}
