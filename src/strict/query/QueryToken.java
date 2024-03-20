package strict.query;

public class QueryToken {
	public String token;
	public double textRankScore=0;
	public double posRankScore=0;
	public double simRankScore=0;
	public double coreRankScore=0;
	public double totalScore=0;
	public double bordaScore=0;
	public double bTextRankScore=0;
	public double bTextRankBiasWeight=0;
	public double positRankScore=0;
	public double positRankBiasWeight =0;

	public void setScoreByScoreKey (String scoreKey, double score) {
		switch (scoreKey) {
			case "TR":
				this.textRankScore = score;
				break;
			case "PR":
				this.posRankScore = score;
				break;
			case "SR":
				this.simRankScore = score;
				break;
			case "BTR":
				this.bTextRankScore = score;
				break;
			case "PTR":
				this.positRankScore = score;
				break;
			default:
				break;
		}
	}
}
