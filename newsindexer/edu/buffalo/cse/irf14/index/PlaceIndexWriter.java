package edu.buffalo.cse.irf14.index;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
	private TermIndexDictionary termIDDict;
	private IndexDictionary docIDDict;
	// Partial index list stored in memory
	private BSBITreeMap indexList;
	
	final private int MAX_MEM_ENTRY = 100000;
	
	public PlaceIndexWriter(IndexDictionary fdict, String indexPath) {
		this.indexPath = indexPath;
		termIDDict = new TermIndexDictionary();
		docIDDict = fdict;
		indexList = new BSBITreeMap();
	}
	
	@Override
	public void performIndexLogic(Document d,  FieldNames fn) 
			throws TokenizerException {
		// TODO Auto-generated method stub
		Analyzer analyzer;
		TokenStream stream;
		String input = new String();
		List<Integer> termIDs = new LinkedList<Integer>();

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
			int id = termIDDict.AddGetElementToID(stream.next().toString());
			
			if (!indexList.containsKey(id)) {
				indexList.put(id, new BSBIPriorityQueue());
			}
			indexList.get(id).add(docIDDict.elementToID(
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
			out.writeObject(termIDDict);
			// clear step
			termIDDict.clear();
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
