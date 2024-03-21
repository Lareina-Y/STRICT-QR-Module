package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.evaluation.RepoRankMaker;
import strict.utility.ContentLoader;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

import java.util.ArrayList;
import java.util.HashMap;

public class RepoRankMakerTest {

    @Test
    public void testRepoRankMaker() {

        String scoreKey = "TPR";
        String repoName = "eclipse.jdt.core";
        Boolean addTitle = false;
        boolean addST = false;
        double st = 0.2;

        String resultKey = "STRICT-" + scoreKey + "-10"
                + (addTitle ? "-title" : "")
                + (addST ? "-" + st : "");
        System.out.println(resultKey);
//        String resultKey = "STRICT-TPBR-10-title-0.2";
//        String resultKeyWithoutTitle = "STRICT-TPBR-10-0.2";

        String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
                + ".txt";
        String storedFile = StaticData.HOME_DIR + "/Lareina/rank/" + repoName + "/" + resultKey
                + ".txt";
        HashMap<Integer, String> queryMap = QueryLoader.loadQueries(approachQueryFile, addTitle);

        RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queryMap);
        ArrayList<String> ranks = repoRankMaker.collectQE();
        repoRankMaker.setRankFile(storedFile);
        repoRankMaker.saveQE(ranks);
    }

    @Test
    public void testRepos() {
        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");
        String scoreKey = "TPR";
        Boolean addTitle = true;
        boolean addST = false;
        double st = 0.5;
        String resultKey = "STRICT-" + scoreKey + "-10-title" + (addST ? "-" + st : "");
        String resultKeyWithoutTitle = "STRICT-" + scoreKey + "-10" + (addST ? "-" + st : "");
        System.out.println(resultKey + ", addTitle: " + addTitle);

        for (String repoName: repos) {
            String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
                    + ".txt";
            String storedFile = StaticData.HOME_DIR + "/Lareina/rank/" + repoName + "/" +
                    (addTitle ? resultKey : resultKeyWithoutTitle) + ".txt";

            HashMap<Integer, String> queryMap = QueryLoader.loadQueries(approachQueryFile, addTitle);

            RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queryMap);
            ArrayList<String> ranks = repoRankMaker.collectQE();
            repoRankMaker.setRankFile(storedFile);
            repoRankMaker.saveQE(ranks);
        }
    }
}
