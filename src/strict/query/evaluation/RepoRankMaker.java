package strict.query.evaluation;

import java.util.ArrayList;
import java.util.HashMap;

//import strict.lucenecheck.MethodResultRankMgr;
import strict.lucenecheck.ClassResultRankMgr;
import strict.lucenecheck.LuceneSearcher;
import strict.utility.ContentWriter;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

public class RepoRankMaker {

	String repoName;
	HashMap<Integer, String> suggestedQueryMap;
	ArrayList<Integer> selectedBugs;
	String rankFile;
	String queryFile;

	public int RankSum; // For Fitness function
	static int tooGood = 0;
	static boolean matchClasses = false;

	public RepoRankMaker(String repoName) {
		this.repoName = repoName;
		this.suggestedQueryMap = new HashMap<>();
		this.selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
	}

	public void setRankFile(String rankFile) {
		this.rankFile = rankFile;
	}

	public void setQueryFile(String queryFile) {
		this.queryFile = queryFile;
	}

	public RepoRankMaker(String repoName, HashMap<Integer, String> queryMap) {
		this.repoName = repoName;
		this.suggestedQueryMap = queryMap;
		this.selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
		this.RankSum = 0;
	}

	public ArrayList<String> collectQE() {
		ArrayList<String> ranks = new ArrayList<>();

		if (this.suggestedQueryMap.isEmpty()) {
			this.suggestedQueryMap = QueryLoader.loadQuery(this.queryFile);
		}

		for (int bugID : this.selectedBugs) {
			if (suggestedQueryMap.containsKey(bugID)) {
				String suggested = suggestedQueryMap.get(bugID);
				LuceneSearcher lsearch = new LuceneSearcher(bugID, repoName, suggested.toLowerCase());
//				int qe = lsearch.getFirstGoldRank();
				int qe = lsearch.getFirstGoldRankClass();
				ranks.add(bugID + "\t" + qe);
				if(qe == -1) {
					this.RankSum += 10000; // TODO: skip or not?
				} else {
					this.RankSum += qe;
				}
			}
		}

		// clearing the keys
//		MethodResultRankMgr.keyMap.clear();
		ClassResultRankMgr.keyMap.clear();

		return ranks;
	}

	// TODO: Will be removed, No longer used
	public ArrayList<String> collectQE(HashMap<Integer, String> suggestedTPRQueryMap,  HashMap<Integer, String> suggestedTPSRQueryMap, HashMap<Integer, String> suggestedTPMSRQueryMap) {
		ArrayList<String> ranks = new ArrayList<>();

		for (int bugID : this.selectedBugs) {
			if (suggestedTPRQueryMap.containsKey(bugID) && suggestedTPSRQueryMap.containsKey(bugID) && suggestedTPMSRQueryMap.containsKey(bugID)) {
				String suggestedTPR = suggestedTPRQueryMap.get(bugID);
//				String suggestedSR = suggestedSRQueryMap.get(bugID);
				String suggestedTPSR = suggestedTPSRQueryMap.get(bugID);
				String suggestedTPMSR = suggestedTPMSRQueryMap.get(bugID);

				LuceneSearcher lsearchTPR = new LuceneSearcher(bugID, repoName, suggestedTPR.toLowerCase());
//				LuceneSearcher lsearchSR = new LuceneSearcher(bugID, repoName, suggestedSR.toLowerCase());
				LuceneSearcher lsearchTPSR = new LuceneSearcher(bugID, repoName, suggestedTPSR.toLowerCase());
				LuceneSearcher lsearchTPMSR = new LuceneSearcher(bugID, repoName, suggestedTPMSR.toLowerCase());

				int qeTPR = lsearchTPR.getFirstGoldRankClass();
//				int qeSR = lsearchSR.getFirstGoldRank();
				int qeTPSR = lsearchTPSR.getFirstGoldRankClass();
				int qeTPMSR = lsearchTPMSR.getFirstGoldRankClass();

				ranks.add(bugID + "\t" + qeTPR + "\t" + qeTPSR + "\t" + qeTPMSR);
				System.out.println("Save: " + bugID + "\t" + qeTPR + "\t" + qeTPSR + "\t" + qeTPMSR);
			}
		}
		return ranks;
	}

	public void saveQE(ArrayList<String> ranks) {
//		ContentWriter.writeContent(this.rankFile, "StaticData.ADD_TITLE: " + StaticData.ADD_TITLE + " | ");
//		ContentWriter.appendContent(this.rankFile, "SIMILARITY_THRESHOLD: " + StaticData.SIMILARITY_THRESHOLD + " | ");
//		ContentWriter.appendContent(this.rankFile, "System: " + repoName + "\n");
//		ContentWriter.appendContent(this.rankFile, "#Id\tTextRank + POSRank\tTextRank + POSRank + SimRank\tTextRank + POSRank - SimRank");
//		ContentWriter.appendContent(this.rankFile, ranks);
//		System.out.println("Repo Saved: " + repoName + "_" + StaticData.SIMILARITY_THRESHOLD);

		ContentWriter.writeContent(this.rankFile, ranks);
		System.out.println("> Rank Saved: " + repoName);
	}

}
