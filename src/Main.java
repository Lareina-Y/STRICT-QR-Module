import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.SelectedBugs;
import strict.query.evaluation.RepoRankMaker;

public class Main {

    public static void main(String[] args) throws IOException {

        String repoName = "eclipse.jdt.ui";
        ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

        StaticData.ADD_CODE_ELEM = false;
        StaticData.ADD_TITLE = false;
        StaticData.SIMILARITY_THRESHOLD = 0.35;
        StaticData.ADD_SIMRANK_SCORE = true;

        String scoreKey = "TPR";
        HashMap<Integer, String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueriesInHashMap();

//        ===========SimRank===========
//        String scoreKey2 = "SR";
//        HashMap<Integer, String> queries2 = new SearchQueryProvider(repoName, scoreKey2, selectedBugs).provideSearchQueriesInHashMap();

//        ===========TextRank + POSRank + SimRank===========
        String scoreKey3 = "TPSR";
        HashMap<Integer, String> queries3 = new SearchQueryProvider(repoName, scoreKey3, selectedBugs).provideSearchQueriesInHashMap();

//        ===========TextRank + POSRank - SimRank===========
        StaticData.ADD_SIMRANK_SCORE = false;
        String scoreKey4 = "TPMSR";
        HashMap<Integer, String> queries4 = new SearchQueryProvider(repoName, scoreKey4, selectedBugs).provideSearchQueriesInHashMap();

        RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queries);
        repoRankMaker.setRankFile("output/" + repoName + "/" + repoName + "_" + StaticData.SIMILARITY_THRESHOLD + ".txt");
        ArrayList<String> ranks = repoRankMaker.collectQE(queries, queries3, queries4);
        repoRankMaker.saveQE(ranks);

    }
}