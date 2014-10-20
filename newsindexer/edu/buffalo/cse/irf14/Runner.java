/**
 * 
 */
package edu.buffalo.cse.irf14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
	}

	/**
	 * @param args
	 */

	static private String ipDir;
	static private String indexDir;

	public static void main(String[] args) {
		ipDir = args[1];
		indexDir = args[0];
		// more? idk!

		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();

		String[] files;
		File dir;

		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		boolean readonly = true;
		if (!readonly) {
			long startTime = System.currentTimeMillis();
			long stopTime = System.currentTimeMillis();
			try {
				for (String cat : catDirectories) {
					dir = new File(ipDir + File.separator + cat);
					files = dir.list();

					if (files == null)
						continue;

					for (String f : files) {
						try {
							d = Parser.parse(dir.getAbsolutePath()
									+ File.separator + f);
							writer.addDocument(d);
						} catch (ParserException e) {
							e.printStackTrace();
						}

					}

				}

				writer.close();
			} catch (IndexerException e) {

				e.printStackTrace();
			}
			stopTime = System.currentTimeMillis();
			System.out.println("Done!");
			System.out.println("Elapsed time was " + (stopTime - startTime)
					+ " miliseconds.");
		}
		readTest();
	}

	public static void readTest() {
		IndexReader termReader;
		IndexReader placeReader;
		IndexReader authorReader;
		IndexReader categoryReader;

		termReader = new IndexReader(indexDir, IndexType.TERM);
		placeReader = new IndexReader(indexDir, IndexType.PLACE);
		authorReader = new IndexReader(indexDir, IndexType.AUTHOR);
		categoryReader = new IndexReader(indexDir, IndexType.CATEGORY);

		System.out.println("term keys " + termReader.getTotalKeyTerms());
		System.out.println("term values " + termReader.getTotalValueTerms());
		Map<String, Integer> posts = termReader.getPostings("home");
 

		String query = getAnalyzer("home", FieldNames.CONTENT);
		Map<String, Integer> map = termReader.getPostings(query);
		System.out.println("Home: " + map.size());

		query = getAnalyzer("forecasts", FieldNames.CONTENT);
		map = termReader.getPostings(query);
		System.out.println("forecasts: " + map.size());

		query = getAnalyzer("Paris", FieldNames.PLACE);
		map = placeReader.getPostings(query);
		System.out.println("Paris: " + map.keySet());

		query = getAnalyzer("cocoa", FieldNames.CATEGORY);
		map = categoryReader.getPostings(query);
		System.out.println("cocoa: " + map.size());

		query = getAnalyzer("August 3", FieldNames.NEWSDATE);
		map = termReader.getPostings(query);
		System.out.println("August: " + map.keySet());

		// Author Query
		query = getAnalyzer("Patti Domm", FieldNames.AUTHOR);
		map = authorReader.getPostings(query);
		System.out.println("Patti: " + map.keySet());

		// AuthorOrg Query
		query = getAnalyzer("Reuter", FieldNames.AUTHORORG);
		map = authorReader.getPostings(query);
		System.out.println("Reuter: " + map.keySet());
		
		query = getAnalyzer("Reuters", FieldNames.AUTHORORG);
		map = authorReader.getPostings(query);
		System.out.println("Reuters: " + map.size());

	}

	private static String getAnalyzer(String string, FieldNames fn) {
		Tokenizer tknizer = new Tokenizer();
		AnalyzerFactory fact = AnalyzerFactory.getInstance();
		try {
			TokenStream stream;
			if (fn.equals(FieldNames.AUTHOR) || fn.equals(FieldNames.AUTHORORG)
					|| fn.equals(FieldNames.PLACE)) {
				stream = new Tokenizer("=").consume(string);
			} else {
				stream = tknizer.consume(string);
			}
			Analyzer analyzer = fact.getAnalyzerForField(fn, stream);

			while (analyzer.increment()) {

			}

			stream = analyzer.getStream();

			stream.reset();
			return stream.next().toString();
		} catch (TokenizerException e) {
			e.printStackTrace();
		}
		return null;
	}

}
