/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
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
	public static void main(String[] args) {
		String ipDir = args[1];
		String indexDir = args[0];
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		long startTime = System.currentTimeMillis();
		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
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
		long stopTime = System.currentTimeMillis();
		System.out.println("Done!");
		System.out.println("Elapsed time was " + (stopTime - startTime) + " miliseconds.");
	}

}
