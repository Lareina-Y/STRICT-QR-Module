package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import scanniello.method.LucenePRSearcher;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.lucenecheck.MethodResultRankMgr;
import strict.query.SearchQueryProvider;
import strict.query.evaluation.QueryPerformanceCalc;
import strict.query.evaluation.RepoRankMaker;
import strict.utility.ContentLoader;
import strict.utility.ContentWriter;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParameterTuningTest {

    @Test
    public void testParameters() {
        // Step 1: Initialization (POSRank > TextRank > SimRank > B-TextRank > PositiionRank)
//                  Initialization (POSRank > TextRank > B-TextRank > SimRank > PositiionRank)
        // TR, PR, SP, BTR, PTR
        double[] parameters = {0, 9, 0.4, 1, 1}; // Initial parameter values 5, 5, 1, 4, 0

        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");
        List<String> scoreKeyList = Arrays.asList("TR", "PR", "SR", "BTR", "PTR"); // "TR", "PR", "SR", "BTR", "PTR"
        StaticData.SIMILARITY_THRESHOLD = 0.5;
        StaticData.ADD_CODE_ELEM = false;
        StaticData.ADD_TITLE = true;

        StaticData.TR_alpha = parameters[0];
        StaticData.PR_beta = parameters[1];
        StaticData.SR_gamma = parameters[2];
        StaticData.BTR_delta = parameters[3];
        StaticData.PTR_epsilon = parameters[4];

        String resultKey =
                "TR_" + StaticData.TR_alpha +
                        "_PR_" + StaticData.PR_beta +
                        "_SR_" + StaticData.SR_gamma +
                        "_STR_" + StaticData.BTR_delta +
                        "_PTR_" + StaticData.PTR_epsilon +
                        "_10" + (StaticData.ADD_TITLE ? "_title" : "") +  "_" + StaticData.SIMILARITY_THRESHOLD;

        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedTestBugs(repoName);
            String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query-parameter-tuning/" + repoName + "/" + resultKey
                    + ".txt";
            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/rank-parameter-tuning/" + repoName + "/" + resultKey
                    + ".txt";

            // Generate the suggested queries
//            HashMap<Integer, String> queries =
//                    new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueriesMapByTokenScoreMap();
            ArrayList<String> queries =
                    new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueriesByTokenScoreMap();
            ContentWriter.writeContent(approachQueryFile, queries);
            System.out.println("Log: queries saved - " + repoName + " | " + approachQueryFile);

            // Get FirstGoldRank
//            HashMap<Integer, String> queryMap = QueryLoader.loadQueries(approachQueryFile, true);
//            RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, selectedBugs, queryMap);
//            ArrayList<String> ranks = repoRankMaker.collectQE();
//            repoRankMaker.setRankFile(rankStoredFile);
//            repoRankMaker.saveQE(ranks);
//            System.out.println("Log: ranks saved - " + repoName + " | " + rankStoredFile);
        }

        int[] hits = { 10, 50, 100 };
        boolean addTitle = true;

        MethodResultRankMgr.matchClass = false;
        QueryPerformanceCalc.useHQB = false;
        QueryPerformanceCalc.useLQB = false;
        QueryPerformanceCalc.useTest = true; // TODO
        QueryPerformanceCalc.useTrain = false;

        String approachFolder = "Lareina";

        /** use the baseline ***/
        QueryPerformanceCalc.useBaseline = false;

        /* use of PR scanniello */
        QueryPerformanceCalc.useScanniello = false;
        LucenePRSearcher.ALL_RESULTS = 100;

        for (int hit : hits) {
            for (String repoName : repos) {
                new QueryPerformanceCalc(repoName, resultKey, approachFolder, addTitle).getQueryPerformance(hit);
            }
            System.out.print(
                    QueryPerformanceCalc.sumAP / repos.size() * 100 + "%\t" +
                            QueryPerformanceCalc.sumRR / repos.size() + "\t" +
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
