package edu.buffalo.cse.irf14;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexFileReader;
import edu.buffalo.cse.irf14.index.TermFrequencyPerFile;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.TermCrawler;
import edu.buffalo.cse.irf14.scorer.Okapi;
import edu.buffalo.cse.irf14.scorer.RankingManager;
import edu.buffalo.cse.irf14.scorer.ScoreModel;
import edu.buffalo.cse.irf14.scorer.TFIDF;
import edu.buffalo.cse.irf14.searcher.EndlessSearcher;
import edu.buffalo.cse.irf14.searcher.SearcherException;

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
	private ExecutorService exe;
	private EndlessSearcher searcher;
	
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
		exe = Executors.newFixedThreadPool(20);
		searcher = new EndlessSearcher(exe, indexDir);
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		Query query = QueryParser.parse(userQuery, null);
		TreeSet<TermFrequencyPerFile> posting;
		ScoreModel scoreMod;
		List<Integer> result;
		final int k = 10;
		long t0 = System.currentTimeMillis();
		long t1;

		try {
			// Search unranked list
			posting = searcher.search(query);
			if (posting.size() > 0) {
				// Rank searched list
				scoreMod = getRankedModel(query, model, posting);
				result = scoreMod.getFirstK(k);
				t1 = System.currentTimeMillis();
				// Print result
				System.out.println("QUERY: " + userQuery);
				System.out.printf("TIME USED: %5.3f seconds.", (t1 - t0) / 1000.0);
				System.out.println("----------");
				for (int i = 0; i < k; ++i) {
					String path = Paths.get(
							corpusDir, new IndexFileReader(indexDir)
							.OpenFileDictionary().getElementfromID(
									result.get(i))).toString();
					Document doc = Parser.parse(path);
					String content = doc.getField(FieldNames.CONTENT)[0];
					content = content.substring(0, content.indexOf('.'));
					
					System.out.println((k + 1) + "." 
							+ doc.getField(FieldNames.TITLE)[0] + "\n");
					System.out.println(content + " ...");
					System.out.println("\nScore: " + scoreMod.getTextScore(i));
				}
			} else {
				t1 = System.currentTimeMillis();
				System.out.println("QUERY: " + userQuery);
				System.out.printf("TIME USED: %5.3f seconds.", (t1 - t0) / 1000.0);
				System.out.println("----------");
				System.out.println("Empty result!");
			}
			System.out.println();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} catch (SearcherException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
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
				try {
					searcher.search(queryList.get(i));
				} catch (SearcherException | InterruptedException
						| ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			// Rank document list
			
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
		exe.shutdown();
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
	
	private ScoreModel getRankedModel(Query query
			, ScoringModel model, TreeSet<TermFrequencyPerFile> posting)
					throws ClassNotFoundException, IOException {
		List<Integer> postingList =	new LinkedList<Integer>();
		
		ScoreModel scoreMod = null;
		TermCrawler crawler = new TermCrawler(query);
		if (model.equals(ScoringModel.TFIDF)) {
			scoreMod = new TFIDF(
					new IndexFileReader(indexDir)
					.OpenFileDictionary().size());
		} else if (model.equals(ScoringModel.OKAPI)) {
			scoreMod = new Okapi(new IndexFileReader(indexDir)
					.OpenFileDictionary().size()
					, new IndexFileReader(indexDir).OpenFileStats());
		}
		Iterator<TermFrequencyPerFile> iter = posting.iterator();
		while (iter.hasNext()) {
			TermFrequencyPerFile temp = iter.next();
			postingList.add(temp.getDocID());
		}
		scoreMod.setDocuments(postingList);
		scoreMod = new RankingManager(crawler, scoreMod, indexDir).run();
		
		return scoreMod;
	}
}
