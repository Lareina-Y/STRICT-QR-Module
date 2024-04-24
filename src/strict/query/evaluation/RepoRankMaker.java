package strict.query.evaluation;

import java.util.ArrayList;
import java.util.HashMap;

import scanniello.method.LucenePRSearcher;
import strict.lucenecheck.LuceneSearcher;
import strict.lucenecheck.MethodResultRankMgr;
import strict.utility.ContentWriter;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

public class RepoRankMaker {

	String repoName;
	HashMap<Integer, String> suggestedQueryMap;
	ArrayList<Integer> selectedBugs;
	String rankFile;
	String queryFile;

	public double MRR; // For Fitness function
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
		this.MRR = 0;
	}

	public RepoRankMaker(String repoName, ArrayList<Integer> selectedBugs, HashMap<Integer, String> queryMap) {
		this.repoName = repoName;
 		this.selectedBugs = selectedBugs;
		this.suggestedQueryMap = queryMap;
		this.MRR = 0;
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
				int qe = lsearch.getFirstGoldRank();
				ranks.add(bugID + "\t" + qe);
			}
		}

		// clearing the keys
		MethodResultRankMgr.keyMap.clear();

		return ranks;
	}
	public ArrayList<String> collectQEMRR() {
		ArrayList<String> ranks = new ArrayList<>();
		double sumRR = 0;
		if (this.suggestedQueryMap.isEmpty()) {
			this.suggestedQueryMap = QueryLoader.loadQuery(this.queryFile);
		}

		for (int bugID : this.selectedBugs) {
			if (suggestedQueryMap.containsKey(bugID)) {
				String suggested = suggestedQueryMap.get(bugID);
				LuceneSearcher lsearch = new LuceneSearcher(bugID, repoName, suggested.toLowerCase());
//				int qe = lsearch.getFirstGoldRank();
				LuceneSearcher.TOPK_RESULTS = 10;
//				int indice = searcher.getFirstGoldRank();
				ArrayList<Integer> indices= lsearch.getGoldFileIndices();
				if (!indices.isEmpty()) {
					double rr = getRR(indices);
					if (rr > 0) {
						sumRR += rr;
					}
				}
//				ranks.add(bugID + "\t" + qe);
//				if(qe != -1) {
//					sumRR += 1.0 / qe;
//				}
			}
		}

		this.MRR = sumRR / selectedBugs.size();

		// clearing the keys
		MethodResultRankMgr.keyMap.clear();

		return ranks;
	}

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

				int qeTPR = lsearchTPR.getFirstGoldRank();
//				int qeSR = lsearchSR.getFirstGoldRank();
				int qeTPSR = lsearchTPSR.getFirstGoldRank();
				int qeTPMSR = lsearchTPMSR.getFirstGoldRank();

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
//		System.out.println("Repo:" + repoName);
	}

	protected double getRR(ArrayList<Integer> foundIndices) {
		if (foundIndices.isEmpty())
			return 0;
		double min = 10000;
		for (int index : foundIndices) {
			if (index > 0 && index < min) {
				min = index;
			}
		}
		return 1.0 / min;
	}

}
