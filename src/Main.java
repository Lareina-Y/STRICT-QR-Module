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

public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");

        for (String repoName : repos) {
//            String repoName = "eclipse.jdt.core";
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

            String scoreKey = "TSR";
            List<String> scoreKeyList = Arrays.asList("TR", "SR"); // "TR", "PR", "SR", "BTR", "PTR"
            List<String> simThresholdRankList = Arrays.asList("SR", "BTR");
            boolean addST = false;
            for (String key : scoreKeyList) {
                if (simThresholdRankList.contains(key)) {
                    addST = true;
                    break;
                }
            }
//          0.1, 0.2, 0.3, 0.4, 0.5, 0.6
            double[] thresholdList = addST ? new double[]{0.2, 0.3, 0.4, 0.5, 0.6}: new double[]{0.2};
            for (double st : thresholdList) { // threshold is from 0.2 to 0.6
                StaticData.ADD_SIMRANK_SCORE = true;
                StaticData.SIMILARITY_THRESHOLD = st;

                StaticData.ADD_CODE_ELEM=false;
                StaticData.ADD_TITLE=true;
                ArrayList<String> queries = new SearchQueryProvider(
                        repoName, scoreKey, selectedBugs, scoreKeyList).provideSearchQueries();
                MiscUtility.showItems(queries);

                String resultKey = "STRICT-" + scoreKey + "-10"
                        + (StaticData.ADD_TITLE ? "-title-Word2Vec" : "") //-expandCCWords
                        + (addST ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
                String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query-v3/" + repoName + "/two/" + resultKey
                        + ".txt";

                ContentWriter.writeContent(approachQueryFile, queries);
                System.out.println("Repo:" + repoName + " | " + approachQueryFile);
            }
        }

//        // Print the number of queries for each repo
//        for (String repoName : repos) {
//            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
//            System.out.println(repoName + "\t" + selectedBugs.size());
//        }
    }
}