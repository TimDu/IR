package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class CategoryIndexWriter implements PerformIndexWriterLogic {

	// Counts total number of temporary files stored on disk
	private int tempFileCount = 0;
	private String indexPath;
	private TermIndexDictionary termDict;
	private FileIndexDictionary docDict;
	private TermIndexFileWriter indexFileWriter;
	// Partial index list stored in memory
	private BSBITreeMap indexList;

	final private int MAX_MEM_ENTRY = 100000;

	public CategoryIndexWriter(FileIndexDictionary fdict, String indexPath) {
		this.indexPath = indexPath;
		termDict = new TermIndexDictionary();
		docDict = fdict;
		indexFileWriter = new TermIndexFileWriter(indexPath,
				IndexGlobalVariables.categoryIndexFileName);
		indexList = new BSBITreeMap();
	}

	private TokenStream createTermStream(Document d, FieldNames type) {
		Tokenizer tknizer = new Tokenizer();
		TokenStream tstream = new TokenStream();
		try {
			String[] arr = d.getField(type);
			for (String s : arr) {
				tstream.append(tknizer.consume(s));
			}

		} catch (TokenizerException e) {
			e.printStackTrace();
		}
		return tstream;
	}

	@Override
	public void performIndexLogic(Document d, FieldNames fn)
			throws IndexerException {
		// TODO Auto-generated method stub
		Analyzer analyzer;
		TokenStream stream = createTermStream(d, fn);

		if (fn.equals(FieldNames.CATEGORY)) {
			analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(fn,
					stream);

		} else {
			// Wrong usage on this class!
			return;
		}
		// Analyzing categories
		try {
			while (analyzer.increment()) {
			}
		} catch (TokenizerException e) {
			throw new IndexerException();
		}
		stream = analyzer.getStream();
		stream.reset();

		// Edit term dictionary
		while (stream.hasNext()) {
			int id = termDict.AddGetElementToID(stream.next().toString());

			if (!indexList.containsKey(id)) {
				indexList.put(id, new BSBIPriorityQueue());
			}
			indexList.get(id).add(
					docDict.elementToID(d));

			// Edit index list
			if (indexList.size() > MAX_MEM_ENTRY) {
				createTempIndex();
			}
		}
	}

	@Override
	public void finishIndexing() throws IndexerException {
		ArrayList<BufferedInputStream> chuncks = new ArrayList<BufferedInputStream>();
		BufferedInputStream input;
		Path path;

		// Flush remained index on memory
		createTempIndex();
		// Initiate index file
		indexFileWriter.createTermIndex(tempFileCount);
		// Read temporary files
		for (int i = 0; i < tempFileCount; ++i) {
			path = Paths.get(indexPath, "tempCategoryIndex" + i + ".index");
			if (path.toFile().exists()) {
				try {
					input = new BufferedInputStream(new FileInputStream(
							path.toString()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					throw new IndexerException();
				}
				chuncks.add(input);
			}
		}

		// BSBI merging
		try {
			indexList = BSBI.merge(chuncks, tempFileCount, indexFileWriter, MAX_MEM_ENTRY);
		} catch (ClassNotFoundException | IOException e) {
			throw new IndexerException();
		}

		// Clean up, get rid of all those temporary files.
		for (int i = 0; i < tempFileCount; i++) {
			try {
				chuncks.get(i).close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IndexerException();
			}
			Path indexPath = Paths.get(this.indexPath, "tempCategoryIndex" + i
					+ ".index");
			File file = indexPath.toFile();
			file.delete();
		}

		// Make sure we flush any remaining data
		indexFileWriter.appendTermIndex(indexList);
		indexList.clear();

		// Reset our internal count of temporary indexes
		tempFileCount = 0;

		// Finally, write the term data
		try {
			writeCategoryDictionary();
		} catch (IOException e) {
			throw new IndexerException();
		}
	}

	/**
	 * Write Category-CategoryID dictionary to disk
	 * 
	 * @throws IOException
	 */
	private void writeCategoryDictionary() throws IOException {
		BufferedOutputStream buffOut;

		try {
			Path path = Paths.get(indexPath,
					IndexGlobalVariables.categoryDicFileName);
			if(path.toFile().exists())
			{
				path.toFile().delete();
			}
			buffOut = new BufferedOutputStream(new FileOutputStream(
					path.toString()));
			ObjectOutputStream out = new ObjectOutputStream(buffOut);

			// Write index to dictionary file
			out.writeObject(termDict);
			// clear step
			termDict.clear();
			buffOut.close();
			out.close();
		} catch (IOException e) {
			assert (false);
			e.printStackTrace();
		}
	}

	/**
	 * Create a temporary file and move index data in memory to this file.
	 * 
	 */
	private void createTempIndex() {
		BufferedOutputStream out;

		try {
			Path path = Paths.get(indexPath, "tempCategoryIndex"
					+ tempFileCount + ".index");
			out = new BufferedOutputStream(
					new FileOutputStream(path.toString()));
			// Write index to temporary file
			indexList.writeObject(out);
			++tempFileCount;
			// clear step
			indexList.clear();
			out.close();
		} catch (IOException e) {
			assert (false);
			e.printStackTrace();
		}
	}

}
