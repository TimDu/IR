package edu.buffalo.cse.irf14.index;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public abstract class IndexReaderInterface {

	protected String m_indexDir;
	protected TermIndexDictionary m_termDict;
	protected FileIndexDictionary m_fileDict;

	public abstract int getTotalKeyTerms();

	public abstract int getTotalValueTerms();

	public abstract Map<String, Integer> getPostings(String term);

	public abstract List<String> getTopK(int k);

	public abstract Map<String, Integer> query(String... terms);

	public abstract Map<String, Integer> queryOR(String...terms);
	
	public void OpenFileDictionary()
			throws IOException,ClassNotFoundException {
		BufferedInputStream fileIn = new BufferedInputStream(
				new FileInputStream(Paths.get(m_indexDir,
						IndexGlobalVariables.fileDicFileName).toString()));
		ObjectInputStream instream = new ObjectInputStream(fileIn);
		m_fileDict = (FileIndexDictionary) instream.readObject();
		
		instream.close();
		fileIn.close();
	}
}