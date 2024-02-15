package strict.ca.usask.cs.srlab.strict.test;

import java.util.ArrayList;
import org.junit.Test;
import scanniello.method.LucenePRSearcher;
import strict.lucenecheck.MethodResultRankMgr;
import strict.query.evaluation.QueryPerformanceCalc;
import strict.utility.ContentLoader;

public class QueryPerformanceCalcTest {

	@Test
	public void testQueryPerformanceAllRepos() {
		ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");

		MethodResultRankMgr.matchClass = false;
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

		for (int hit : hits) {

			for (String repoName : repos) {
				String resultKey = "STRICT-TPBR-10-title-0.4";
//				String resultKey = "STRICT-best-query-dec23-8pm";

				new QueryPerformanceCalc(repoName, resultKey, approachFolder, addTitle).getQueryPerformance(hit);
			}

			System.out.println(
					"Hit=" + hit + ":\t" +
					QueryPerformanceCalc.sumAP / repos.size() * 100 + "%\t" +
							QueryPerformanceCalc.sumRR / repos.size() * 100 + "%\t" +
							QueryPerformanceCalc.sumTopKAcc / repos.size() * 100 + "%"
			);

			QueryPerformanceCalc.sumTopKAcc = 0;
			QueryPerformanceCalc.sumAP = 0;
			QueryPerformanceCalc.sumRR = 0;

			QueryPerformanceCalc.masterHitkList.clear();
			QueryPerformanceCalc.masterAPList.clear();
			QueryPerformanceCalc.masterRRList.clear();
		}
	}
}
