package edu.buffalo.cse.irf14.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.index.IndexType;

public class TermCrawler {
	
	private Map<String, Integer> termFreqs;
	private Map<String, Set<IndexType>> termTypes;

	public TermCrawler(Query query) {
		termFreqs = new HashMap<String, Integer>();
		termTypes = new HashMap<String, Set<IndexType>>();
		crawl(query);
	}
	
	public Set<String> getTerms() {
		return termFreqs.keySet();
	}
	
	public Set<IndexType> termIndex(String term) {
		return termTypes.get(term);
	}
	
	public int queryFreq(String term) {
		if (termFreqs.containsKey(term)) {
			return termFreqs.get(term);
		} else {
			return 0;
		}
	}
	
	private void crawl(Query query) {
		for (int i = 0; i < query.size(); ++i) {
			Clause temp = query.getClause(i);
			if(temp.isQuery()) {
				crawl((Query)temp.getComponent());
			} else {
				Term term = (Term)temp.getComponent();
				for (int j = 0; j < term.size(); ++j) {
					if (termFreqs.containsKey(term.getTerm(j))) {
						termFreqs.put(term.getTerm(j)
								, termFreqs.get(term.getTerm(j)) + 1);
						if (!termTypes.get(term.getTerm(j))
								.contains(term.getIndex(j))) {
							Set<IndexType> typeSet =
									termTypes.get(term.getTerm(j));
							typeSet.add(term.getIndex(j));
						}
					} else {
						Set<IndexType> typeSet = new HashSet<IndexType>();
						typeSet.add(term.getIndex(j));
						termFreqs.put(term.getTerm(j), 1);
						termTypes.put(term.getTerm(j), typeSet);
					}
				}
			}
		}
	}
}
