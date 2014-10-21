package edu.buffalo.cse.irf14.scorer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import edu.buffalo.cse.irf14.index.IndexFileReader;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.query.TermCrawler;
import edu.buffalo.cse.irf14.searcher.RankWorker;

public class RankingManager {
	
	private TermCrawler crawler;
	private ScoreModel model;
	private String indexDir;
	private ExecutorService exe;

	public RankingManager(TermCrawler crawler
			, ScoreModel model, String indexDir, ExecutorService exe) {
		this.crawler = crawler;
		this.model = model;
		this.indexDir = indexDir;
		this.exe = exe;
	}
	
	public ScoreModel run() {
		if (model instanceof Okapi) {
			return okapi();
		} else {
			return tdidf();
		}
	}
	
	public ScoreModel tdidf() {
		List<String> tempList = new LinkedList<String>();
		List<Future<Map<Integer, Integer>>> fList =
				new LinkedList<Future<Map<Integer, Integer>>>();

		for (String term: crawler.getTerms()) {
			tempList.add(term);
			fList.add(exe.submit(new RankWorker(
					indexDir, crawler.termIndex(term)
					, tempList.subList(tempList.size() - 1
							, tempList.size()))));
		}
		for (int i = 0; i < tempList.size(); ++i) {
			Map<Integer, Integer> temp;
			try {
				temp = fList.get(i).get();
				model.setQueryTermFreq(crawler.queryFreq(tempList.get(i)));
				model.setDocFreq(temp.size());
				
				for (int id: model.getDocIDs()) {
					if (temp.containsKey(id)) {
						model.setDocTermFreq(id, temp.get(id));
					} else {
						model.setDocTermFreq(id, 0);
					}
				}
				model.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		return model;
	}
	
	public ScoreModel okapi() {
		Okapi okapi = (Okapi) model;
		List<String> tempList = new LinkedList<String>();
		List<Future<Map<Integer, Integer>>> fList =
				new LinkedList<Future<Map<Integer, Integer>>>();

		for (String term: crawler.getTerms()) {
			tempList.add(term);
			fList.add(exe.submit(new RankWorker(
					indexDir, crawler.termIndex(term)
					, tempList.subList(tempList.size() - 1
							, tempList.size()))));
		}
		for (int i = 0; i < tempList.size(); ++i) {
			Map<Integer, Integer> temp;
			
			try {
				temp = fList.get(i).get();
				okapi.setQueryTermFreq(crawler.queryFreq(tempList.get(i)));
				okapi.setDocFreq(temp.size());
				
				for (int id: okapi.getDocIDs()) {
					if (temp.containsKey(id)) {
						okapi.setDocTermFreq(id, temp.get(id));
					} else {
						okapi.setDocTermFreq(id, 0);
					}
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
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		return okapi;
	}
}
