import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
//            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedTrainBugs(repoName);

            String scoreKey = "TPSBTPTR";
            List<String> scoreKeyList = Arrays.asList("TR", "PR", "SR", "BTR", "PTR"); // "TR", "PR", "SR", "BTR", "PTR"
            List<String> simThresholdRankList = Arrays.asList("SR", "BTR");
            boolean addST = false;
            for (String key : scoreKeyList) {
                if (simThresholdRankList.contains(key)) {
                    addST = true;
                    break;
                }
            }
//          0.1, 0.2, 0.3, 0.4, 0.5, 0.6
            double[] thresholdList = addST ? new double[]{0.5}: new double[]{0.5};
            for (double st : thresholdList) { // threshold is from 0.2 to 0.6
                StaticData.SIMILARITY_THRESHOLD = st;

                StaticData.ADD_CODE_ELEM=false;
                StaticData.ADD_TITLE=true;

                StaticData.TR_alpha = 0;// 0.45309403507098156; // TR
                StaticData.PR_beta = 9;// 0.8374424351745824; // PR
                StaticData.SR_gamma = 0.4;// 0.3753504417175002; //SR
                StaticData.BTR_delta = 1;// STR
                StaticData.PTR_epsilon = 1;// PTR

                ArrayList<String> queries = new SearchQueryProvider(
                        repoName, selectedBugs, scoreKeyList).provideSearchQueriesByTokenScoreMap();
//                MiscUtility.showItems(queries);

//                String resultKey = "STRICT-" + scoreKey + "-10"
//                        + (StaticData.ADD_TITLE ? "-title-test" : "")
//                        + (addST ? "-" + StaticData.SIMILARITY_THRESHOLD : "");

                String resultKey =
                    "TR_" + StaticData.TR_alpha +
                    "_PR_" + StaticData.PR_beta +
                    "_SR_" + StaticData.SR_gamma +
                    "_STR_" + StaticData.BTR_delta +
                    "_PTR_" + StaticData.PTR_epsilon +
                    "_10" + (StaticData.ADD_TITLE ? "_title" : "") +  "_" + StaticData.SIMILARITY_THRESHOLD;

                String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query-parameter-tuning/" + repoName + "/" + resultKey
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