package edu.buffalo.cse.irf14.scorer;

public class TFIDF extends ScoreModel {
	protected long m_totalDocNum;
	
	public TFIDF (long totalDocNum)
	{
		m_totalDocNum = totalDocNum;
	}
	
	@Override
	public void run() { 
		double tempScore;
		for (int i = 0; i < docIDs.size(); ++i) {
			tempScore = scores.get(i);
			tempScore += Math.log10(((double)m_totalDocNum)/((double)docFreq))*docTermFreqs.get(docIDs.get(i));
			scores.set(i, tempScore);
		}

	}

}
