package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.evaluation.RepoRankMaker;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

import java.util.ArrayList;
import java.util.HashMap;

public class RepoRankMakerTest {

    protected HashMap<Integer, String> loadQueries(String queryFile, String repoName, Boolean addTitle) {
        HashMap<Integer, String> tempMap;
        if (addTitle) {
            tempMap = QueryLoader.loadQuery(queryFile);
        } else {
            tempMap = QueryLoader.loadQueryWithoutTitle(queryFile);
        }

        ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedHQBBugs(repoName);

        HashMap<Integer, String> myQueryMap = new HashMap<Integer, String>();

        for (int bugID : selectedBugs) {
            if (tempMap.containsKey(bugID)) {
                myQueryMap.put(bugID, tempMap.get(bugID));
            }
        }
        return tempMap;
    }

    @Test
    public void testRepoRankMaker() {

        String repoName = "tomcat70";

        Boolean addTitle = true;

//        String resultKey = "STRICT-TPR-10-title";
//        String resultKeyWithoutTitle = "STRICT-TPR-10";
        String resultKey = "STRICT-TPBR-10-title-0.2";
        String resultKeyWithoutTitle = "STRICT-TPBR-10-0.2";

        String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
                + ".txt";
        String storedFile = StaticData.HOME_DIR + "/Lareina/rank/" + repoName
                + "/" + (addTitle? resultKey: resultKeyWithoutTitle)
                + ".txt";
        HashMap<Integer, String> queryMap = loadQueries(approachQueryFile, repoName, addTitle);

        RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queryMap);
        ArrayList<String> ranks = repoRankMaker.collectQE();
        repoRankMaker.setRankFile(storedFile);
        repoRankMaker.saveQE(ranks);
    }
}
