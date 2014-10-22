package edu.buffalo.cse.irf14.scorer;

public class TFIDF extends ScoreModel {
	
	private double queryNorm;
	private double docNorm;
	
	public TFIDF (long totalDocNum)
	{
		this.totalDocNum = totalDocNum;
		queryNorm = 0.0;
		docNorm = 0.0;
	}
	
	@Override
	public void run() { 
		double tempScore;
		for (int i = 0; i < docIDs.size(); ++i) {
			double queryScore = 
					(Math.log10(totalDocNum) - Math.log10(docFreq))
					* (1 + Math.log10(queryTermFreq));
			double docScore = 1 + Math.log10(docTermFreqs.get(docIDs.get(i)));
					
			tempScore = scores.get(i);
			tempScore += queryScore * docScore;
			scores.set(i, tempScore);
			// Normalized factors
			queryNorm += Math.pow(queryScore, 2);
			docNorm += Math.pow(docScore, 2);
		}
	}

	@Override
	protected void normalize() {
		for (int i = 0; i < scores.size(); ++i) {
			scores.set(i, scores.get(i) 
					/ Math.sqrt(queryNorm) / Math.sqrt(docNorm));
		}
	}

}
