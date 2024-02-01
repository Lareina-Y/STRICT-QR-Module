import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;
import strict.utility.SelectedBugs;
import strict.query.evaluation.RepoRankMaker;

public class Main {

    public static void main(String[] args) throws IOException {

        // 'i' represent the SIMILARITY_THRESHOLD which wants to be tested
//        for (double i = 0.2; i < 0.3; i += 0.1) {
//
//            String repoName = "eclipse.pde.ui";
//            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
//
//            StaticData.ADD_CODE_ELEM = false;
//            StaticData.ADD_TITLE = false;
//            StaticData.SIMILARITY_THRESHOLD = i;
//            StaticData.ADD_SIMRANK_SCORE = true;
//
//            String scoreKey = "TPR";
//            HashMap<Integer, String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueriesInHashMap();
//
////        ===========SimRank===========
////        String scoreKey2 = "SR";
////        HashMap<Integer, String> queries2 = new SearchQueryProvider(repoName, scoreKey2, selectedBugs).provideSearchQueriesInHashMap();
//
////        ===========TextRank + POSRank + SimRank===========
//            String scoreKey3 = "TPSR";
//            HashMap<Integer, String> queries3 = new SearchQueryProvider(repoName, scoreKey3, selectedBugs).provideSearchQueriesInHashMap();
//
////        ===========TextRank + POSRank - SimRank===========
//            StaticData.ADD_SIMRANK_SCORE = false;
//            String scoreKey4 = "TPMSR";
//            HashMap<Integer, String> queries4 = new SearchQueryProvider(repoName, scoreKey4, selectedBugs).provideSearchQueriesInHashMap();
//
//            RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queries);
//            repoRankMaker.setRankFile("output/" + repoName + "/" + repoName + "_" + StaticData.SIMILARITY_THRESHOLD + ".txt");
//            ArrayList<String> ranks = repoRankMaker.collectQE(queries, queries3, queries4);
//            repoRankMaker.saveQE(ranks);

        String repoName = "eclipse.jdt.ui";
        ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

        String scoreKey = "TPSR";
        StaticData.ADD_SIMRANK_SCORE = true;
        StaticData.SIMILARITY_THRESHOLD = 0.7;

        StaticData.ADD_CODE_ELEM=false;
        StaticData.ADD_TITLE=true;
        ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueries();
        MiscUtility.showItems(queries);

        String resultKey = "STRICT-" + scoreKey + "-10"
                + (StaticData.ADD_TITLE ? "-title" : "")
                + (scoreKey.equals("TPSR") ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
        String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
                + ".txt";

        ContentWriter.writeContent(approachQueryFile, queries);
        System.out.println("Repo:" + repoName + " | " + approachQueryFile);


    }
}