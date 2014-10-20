package edu.buffalo.cse.irf14.scorer;

public class TFIDF extends ScoreModel {
	
	public TFIDF (long totalDocNum)
	{
		this.totalDocNum = totalDocNum;
	}
	
	@Override
	public void run() { 
		double tempScore;
		for (int i = 0; i < docIDs.size(); ++i) {
			tempScore = scores.get(i);
			tempScore += (Math.log10(totalDocNum) - Math.log10(docFreq))
					*docTermFreqs.get(docIDs.get(i));
			scores.set(i, tempScore);
		}
	}

}
