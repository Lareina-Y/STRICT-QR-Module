package strict.ca.usask.cs.srlab.strict.test;

import java.util.ArrayList;
import org.junit.Test;
import scanniello.method.LucenePRSearcher;
//import strict.lucenecheck.MethodResultRankMgr;
import strict.query.evaluation.QueryPerformanceCalc;
import strict.utility.ContentLoader;

public class QueryPerformanceCalcTest {

	@Test
	public void testQueryPerformanceAllRepos() {
		ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");

//		MethodResultRankMgr.matchClass = false;
		QueryPerformanceCalc.useHQB = false;
		QueryPerformanceCalc.useLQB = false;

		String approachFolder = "Lareina";
//		String approachFolder = "Proposed-STRICT";
		// String approachFolder = "Rocchio";

		/** use the baseline ***/
		QueryPerformanceCalc.useBaseline = false;

		/* use of PR scanniello */
		QueryPerformanceCalc.useScanniello = false;
		LucenePRSearcher.ALL_RESULTS = 100;

		int[] hits = { 10, 50, 100 };

		boolean addTitle = true;
		System.out.println("AddTitle: " + addTitle);

//		double[] thresholdList = {0.2, 0.3, 0.4, 0.5, 0.6}; // 0.1, 0.2, 0.3, 0.4, 0.5, 0.6 0.7
		double[] thresholdList = {0.5};

		String scoreKey = "PR";
		System.out.println(scoreKey);

		for (double st : thresholdList) {
//			String resultKey = "STRICT-" + scoreKey + "-10-title" + "-" + st;
			String resultKey = "STRICT-" + scoreKey + "-10-title";
//			String resultKey = "TR_2.5_PR_3.0_SR_0.0_STR_0.0_PTR_0.0_10_title_0.5";
//				String resultKey = "STRICT-best-query-dec23-8pm";

			for (int hit : hits) {
				for (String repoName : repos) {
					new QueryPerformanceCalc(repoName, resultKey, approachFolder, addTitle).getQueryPerformance(hit);
				}

				System.out.print(
//					"Hit=" + hit + ":\t" +
						QueryPerformanceCalc.sumAP / repos.size() * 100 + "%\t" +
								QueryPerformanceCalc.sumRR / repos.size() * 100 + "%\t" +
								QueryPerformanceCalc.sumTopKAcc / repos.size() * 100 + "%"
								+ "\t"
				);

				QueryPerformanceCalc.sumTopKAcc = 0;
				QueryPerformanceCalc.sumAP = 0;
				QueryPerformanceCalc.sumRR = 0;

				QueryPerformanceCalc.masterHitkList.clear();
				QueryPerformanceCalc.masterAPList.clear();
				QueryPerformanceCalc.masterRRList.clear();
			}
			System.out.println();
		}
	}
}
