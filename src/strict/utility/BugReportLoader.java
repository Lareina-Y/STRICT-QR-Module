package strict.utility;

import java.util.ArrayList;
import java.util.HashMap;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;

public class BugReportLoader {
	public static String loadBugReport(String repoName, int bugID) {

		String brFile = StaticData.HOME_DIR + "/Changereqs/" + repoName + "/" + bugID + ".txt";
		return ContentLoader.loadFileContent(brFile);
	}

	public static String loadBugReportTitle(String repoName, int bugID) {

		String brFile = StaticData.HOME_DIR + "/Changereqs/" + repoName + "/" + bugID + ".txt";
		return ContentLoader.getAllLinesOptList(brFile).get(0).trim();
	}

	public static String loadBugReportDesc(String repoName, int bugID) {
		String brFile = StaticData.HOME_DIR + "/Changereqs/" + repoName + "/" + bugID + ".txt";
		String desc = new String();
		ArrayList<String> brLines = ContentLoader.getAllLinesOptList(brFile);
		// skip the title, and start from index=1
		for (int i = 1; i < brLines.size(); i++) {
			desc += brLines.get(i) + "\n";
		}
		return desc.trim();
	}

	public static String[] loadEmbedReport(String repoName, int bugID) {
		String brFile = StaticData.CHANGEREQS_EMB + repoName + "/" + bugID + ".txt";
		String[] sentences = ContentLoader.loadSentenceSet(brFile);
		return sentences;
	}

	public static String[] loadEmbedReportDec(String repoName, int bugID) {
		String brFile = StaticData.CHANGEREQS_DEC_EMB + repoName + "/" + bugID + ".txt";
		String[] sentences = ContentLoader.loadSentenceSet(brFile);
		return sentences;
	}

	public static HashMap<String, QueryToken> loadTokenScoreMap(String repoName, int bugID) {
		String brFile = StaticData.HOME_DIR + "/Lareina/TokenScoreMap/ST-0.5/" + repoName + "/" + bugID + ".txt";
		String[] lines = ContentLoader.getAllLines(brFile);

		HashMap<String, QueryToken> tokenScoreMap = new HashMap<>();
		for (String line : lines) {
			String[] tokenScores = line.split("\t");
			String word = tokenScores[0];
			double[] scores = MiscUtility.strList2DoubleList(tokenScores[1].split(" "));
			QueryToken tokenMap = new QueryToken();
			tokenMap.token = word;
			tokenMap.textRankScore = scores[0];
			tokenMap.posRankScore = scores[1];
			tokenMap.simRankScore = scores[2];
			tokenMap.bTextRankScore = scores[3];
			tokenMap.positRankScore = scores[4];
			tokenScoreMap.put(word, tokenMap);
		}
		return tokenScoreMap;
	}
}
