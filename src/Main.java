import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.ContentLoader;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;
import strict.utility.SelectedBugs;
import strict.query.evaluation.RepoRankMaker;

public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");

        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

            String scoreKey = "TPBR";
            StaticData.ADD_SIMRANK_SCORE = true;
            StaticData.SIMILARITY_THRESHOLD = 0.4;

            StaticData.ADD_CODE_ELEM=false;
            StaticData.ADD_TITLE=true;
            ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueries();
            MiscUtility.showItems(queries);

            List<String> simThresholdRankList = Arrays.asList("SR", "BTR", "TPSR", "TPMSR", "TPBR");

            String resultKey = "STRICT-" + scoreKey + "-10"
                    + (StaticData.ADD_TITLE ? "-title" : "")
                    + (simThresholdRankList.contains(scoreKey) ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
            String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
                    + ".txt";

            ContentWriter.writeContent(approachQueryFile, queries);
            System.out.println("Repo:" + repoName + " | " + approachQueryFile);
        }
    }
}