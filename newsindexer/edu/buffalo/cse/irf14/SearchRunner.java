package edu.buffalo.cse.irf14;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
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
import edu.buffalo.cse.irf14.index.FileIndexDictionary;
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
			posting = searcher.searchNoThread(query);
			FileIndexDictionary fid = IndexFileReader.getFileDictionary(indexDir);

			int count = 0;

			for(TermFrequencyPerFile tfpf: posting)
			{
				String fileName = fid.getElementfromID(tfpf.getDocID());
				System.out.print(fileName + ", ");
				count++;
				if(count%10 == 0)
				{
					System.out.println();
				}
			}
			System.out.println();
			if(count > 0)
			{
				t1 = System.currentTimeMillis();
				// Print result 
				System.out.printf("TIME USED: %5.3f seconds.\n", (t1 - t0) / 1000.0);
				System.out.println();
			}

			if (posting.size() > 0) {
				// Rank searched list
				scoreMod = getRankedModel(query, model, posting);
				result = scoreMod.getFirstK(k);
				t1 = System.currentTimeMillis();
				// Print result
				System.out.println("QUERY: " + userQuery);
				System.out.printf("TIME USED: %5.3f seconds.\n", (t1 - t0) / 1000.0);
				System.out.println();
				
				
				for (int i = 0; i < result.size() && i < k; ++i) {
					String fileName = fid.getElementfromID(result.get(i));
					String firstCategory = fid.getCategories(fileName).get(0);
					
					String path = Paths.get(
							corpusDir, firstCategory, fileName).toString();
					Document doc = Parser.parse(path);
					String content = doc.getField(FieldNames.CONTENT)[0];
					String temp = new String();
					String []seg = content.substring(0
							, (content.length() < 200)
							? content.length() : 200).split(" ");
					if(content.length() == 0)
					{
						System.out.println("Bad Content: " + doc.getField(FieldNames.FILEID)[0]);
						continue;
					}

					content = "";
					for (int j = 0; j < seg.length; ++j) {
						content += seg[j] + " ";
						temp += seg[j];
						if (content.length() > 200) {
							break;
						}
						if (temp.length() > 50) {
							temp = new String();
							content += "\n";
						}
					}
					
					System.out.println((i + 1) + "." 
							+ doc.getField(FieldNames.TITLE)[0]);
					System.out.println("------------");
					System.out.println(content.trim() + " ...");
					System.out.println("Score: " + scoreMod.getTextScore(i));
					System.out.println();
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
		final int k = 10;
		int numQuery = 0;
		String result; 	// Output to be written
		List<Query> queryList = new LinkedList<Query>();
		List<String> queryID = new LinkedList<String>();
		TreeSet<TermFrequencyPerFile> posting;
		ScoreModel scoreMod;
		List<Integer> tempResult = null;
		
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(queryFile));
			String line = reader.readLine();
			String []elements;	// ID-Query pair in query file
			// Error Check
			if ((line == null) || !line.startsWith("numQueries=")
					|| (line.split("=").length != 2)) {
				reader.close();
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
				line = line.substring(line.indexOf(':') + 2);
				line = line.substring(0, line.length() - 1);
				queryID.add(elements[0]);
				Query q = QueryParser.parse(line, null);
				if (q != null) {
					queryList.add(q);
				}
			}
			reader.close();
			
			result = "numResults=" + queryList.size() + System.lineSeparator();
			// Perform algorithm
			for(int i = 0; i < queryList.size(); i++)
			{
				//posting = searcher.search(queryList.get(i));
				posting = searcher.searchNoThread(queryList.get(i));
				// Rank document list
				if (posting.size() > 0) {
					// Rank searched list
					scoreMod = getRankedModel(queryList.get(i)
							, ScoringModel.TFIDF, posting);
					tempResult = scoreMod.getFirstK(k);
				
					// Result
					if (tempResult != null) {
						result += queryID.get(i) + ":" + "{";
						for (int j = 0; j < tempResult.size(); ++j) {
							result += tempResult.get(j) + "#" 
									+ scoreMod.getScore(j) + ", ";
						}
						result = result.substring(0, result.length() - 2) + "}"
								+ System.lineSeparator();
					}
				}
			}
			
			writer.write(result.getBytes());
			writer.flush();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SearcherException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
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
					IndexFileReader.getFileDictionary(indexDir).size());
		} else if (model.equals(ScoringModel.OKAPI)) {
			scoreMod = new Okapi(IndexFileReader.getFileDictionary(indexDir).size()
					, IndexFileReader.getStatsFile(indexDir));
		}
		Iterator<TermFrequencyPerFile> iter = posting.iterator();
		while (iter.hasNext()) {
			TermFrequencyPerFile temp = iter.next();
			postingList.add(temp.getDocID());
		}
		scoreMod.setDocuments(postingList);
		scoreMod = new RankingManager(crawler, scoreMod, indexDir, exe).run();
		
		return scoreMod;
	}
}
