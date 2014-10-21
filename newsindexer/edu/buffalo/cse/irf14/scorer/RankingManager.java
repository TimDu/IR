package edu.buffalo.cse.irf14.scorer;

import java.io.IOException;
import java.util.Map;

import edu.buffalo.cse.irf14.index.IndexFileReader;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.query.TermCrawler;

public class RankingManager {
	
	private TermCrawler crawler;
	private ScoreModel model;
	private String indexDir;

	public RankingManager(TermCrawler crawler
			, ScoreModel model, String indexDir) {
		this.crawler = crawler;
		this.model = model;
		this.indexDir = indexDir;
	}
	
	public ScoreModel run() {
		if (model instanceof Okapi) {
			return okapi();
		} else {
			return tdidf();
		}
	}
	
	public ScoreModel tdidf() {
		IndexReader reader;
		Map<Integer, Integer> tfd;

		for (String term: crawler.getTerms()) {
			reader = new IndexReader(indexDir, crawler.termIndex(term));
			tfd = reader.queryOR(term);
			model.setQueryTermFreq(crawler.queryFreq(term));
			model.setDocFreq(tfd.size());
			
			for (int id: model.getDocIDs()) {
				model.setDocTermFreq(id, tfd.get(id));
			}
			model.run();
		}
		return model;
	}
	
	public ScoreModel okapi() {
		IndexReader reader;
		Map<Integer, Integer> tfd;
		Okapi okapi = (Okapi) model;

		for (String term: crawler.getTerms()) {
			reader = new IndexReader(indexDir, crawler.termIndex(term));
			tfd = reader.queryOR(term);
			okapi.setQueryTermFreq(crawler.queryFreq(term));
			okapi.setDocFreq(tfd.size());
			
			for (int id: okapi.getDocIDs()) {
				okapi.setDocTermFreq(id, tfd.get(id));
				try {
					okapi.setDocLength(id
							, new IndexFileReader(indexDir).OpenFileDictionary()
							.getFileLength(id));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			okapi.run();
		}
		return okapi;
	}
}
