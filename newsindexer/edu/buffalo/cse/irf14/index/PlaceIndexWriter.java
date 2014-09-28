package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

public class PlaceIndexWriter implements PerformIndexWriterLogic {

	// Counts total number of temporary files stored on disk
	private int tempFileCount = 0;
	private String indexPath;
	private TermIndexDictionary termDict;
	private IndexDictionary docDict;
	private TermIndexFileWriter indexFileWriter;
	// Partial index list stored in memory
	private BSBITreeMap indexList;
	
	final private int MAX_MEM_ENTRY = 100000;
	
	public PlaceIndexWriter(IndexDictionary fdict, String indexPath) {
		this.indexPath = indexPath;
		termDict = new TermIndexDictionary();
		docDict = fdict;
		indexFileWriter = new TermIndexFileWriter(indexPath);
		indexList = new BSBITreeMap();
	}
	
	@Override
	public void performIndexLogic(Document d,  FieldNames fn) 
			throws TokenizerException {
		// TODO Auto-generated method stub
		Analyzer analyzer;
		TokenStream stream;
		String input = new String();

		// Concatenate field variables into one string
		for (String var: d.getField(fn)) {
			input += var + "=";
		}
		// Instantiate token stream
		stream = new Tokenizer("=").consume(input);
		
		if (fn.equals(FieldNames.AUTHOR)) {
			analyzer = AnalyzerFactory.getInstance()
					.getAnalyzerForField(FieldNames.AUTHOR, stream);
		} else if (fn.equals(FieldNames.AUTHORORG)) {
			analyzer = AnalyzerFactory.getInstance()
					.getAnalyzerForField(FieldNames.AUTHORORG, stream);
		} else {
			// Wrong usage on this class!
			return;
		}
		// Analyzing authors
		while (analyzer.increment()) {}
		stream = analyzer.getStream();
		stream.reset();
		
		// Edit term dictionary
		while (stream.hasNext()) {
			int id = termDict.AddGetElementToID(stream.next().toString());
			
			if (!indexList.containsKey(id)) {
				indexList.put(id, new BSBIPriorityQueue());
			}
			indexList.get(id).add(docDict.elementToID(
					d.getField(FieldNames.FILEID)[0]));
			
			// Edit index list
			if (indexList.size() > MAX_MEM_ENTRY) {
				createTempIndex();
			}
		}
	}

	@Override
	public void finishIndexing() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		ArrayList<BufferedInputStream> chuncks =
				new ArrayList<BufferedInputStream>();
		BufferedInputStream input;
		Path path;

		// Flush remained index on memory
		createTempIndex();
		// Initiate index file
		indexFileWriter.createTermIndex(tempFileCount);
		// Read temporary files
		for (int i = 0; i < tempFileCount; ++i) {
			path = Paths.get(indexPath, "tempIndex" + i, ".index");
			if (path.toFile().exists()) {
				input = new BufferedInputStream(
						new FileInputStream(path.toString()));
				chuncks.add(input);
			}
		}
		
		// BSBI merging
		BSBI.merge(chuncks, tempFileCount, indexFileWriter, MAX_MEM_ENTRY);
		
		// Clean up, get rid of all those temporary files.
		for (int i = 0; i < tempFileCount; i++) {
			chuncks.get(i).close();
			Path indexPath = Paths.get(
					this.indexPath, "tempIndex" + i + ".index");
			File file = indexPath.toFile();
			file.delete();
		}

		// Make sure we flush any remaining data
		indexFileWriter.appendTermIndex(indexList);
		indexList.clear();

		// Reset our internal count of temporary indexes
		tempFileCount = 0;

		// Finally, write the term data
		writeAuthorDictionary();
	}
	
	/**
	 * Write author-authorID dictionary to disk
	 * @throws IOException
	 */
	private void writeAuthorDictionary() throws IOException {
		BufferedOutputStream buffOut;
		
		try {
			Path path = Paths.get(
					indexPath, IndexGlobalVariables.authorDicFileName);
			buffOut = new BufferedOutputStream(
					new FileOutputStream(path.toString()));
			ObjectOutputStream out = new ObjectOutputStream(buffOut);
			
			// Write index to dictionary file
			out.writeObject(termDict);
			// clear step
			termDict.clear();
			buffOut.close();
			out.close();
		} catch (IOException e) {
			assert(false);
			e.printStackTrace();
		}
	}

	/**
	 * Create a temporary file and move index data in memory
	 * to this file.
	 * 
	 */
	private void createTempIndex() {
		BufferedOutputStream out;
		
		try {
			Path path = Paths.get(indexPath,
					"tempIndex" + tempFileCount, ".index");
			out = new BufferedOutputStream(
					new FileOutputStream(path.toString()));
			// Write index to temporary file
			indexList.writeObject(out);
			++tempFileCount;
			// clear step
			indexList.clear();
			out.close();
		} catch (IOException e) {
			assert(false);
			e.printStackTrace();
		}
	}
}
