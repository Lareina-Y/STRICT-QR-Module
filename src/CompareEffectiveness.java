import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.utility.BugReportLoader;
import strict.utility.ContentLoader;
import strict.utility.SelectedBugs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CompareEffectiveness {
    public static void main(String[] args) throws IOException {
        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");

        int improveCount = 0;
        int worsenedCount = 0;
        int preservedCount = 0;
        int RD = 0;
        int allFound = 0;

        int totalReports = 0;

        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

            int repoImproveCount = 0;
            int repoWorsenedCount = 0;
            int repoPreservedCount = 0;
            int repoRD = 0;
            int repoFound = 0;

            totalReports += selectedBugs.size();
            String resultKey = "TR_0.0_PR_11.0_SR_1.0_STR_1.0_PTR_0.2_10_title_0.5";
            String baselineKey = "TR_1.0_PR_1.0_SR_0.0_STR_0.0_PTR_0.0_10_title_0.5";

//            String baselineRankFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-title/" + repoName + ".txt";
//            String baselineRankFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-desc/" + repoName + ".txt";
            String baselineRankFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-whole/" + repoName + ".txt";
//            String baselineRankFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-title-top10/" + repoName + ".txt";

//            String baselineRankFile = StaticData.HOME_DIR + "/Lareina/rank-parameter-tuning/" + repoName + "/"
//                    + baselineKey + ".txt";

            String rankFile = StaticData.HOME_DIR + "/Lareina/rank-parameter-tuning/" + repoName + "/" + resultKey
                    + ".txt";
            HashMap<Integer, Integer> baselineRankMap = BugReportLoader.loadRank(baselineRankFile);
            HashMap<Integer, Integer> rankMap = BugReportLoader.loadRank(rankFile);

            for (int bugId : selectedBugs) {
                int baselineRank = baselineRankMap.get(bugId);
                int rank = rankMap.get(bugId);

                if (baselineRank == -1) {
                    baselineRank = 10000;
                }
                if (rank == -1) {
                    rank = 10000;
                }

                if (rank < baselineRank) {
                    improveCount ++;
                    repoImproveCount ++;
                } else if (rank == baselineRank) {
                    preservedCount ++;
                    repoPreservedCount ++;
                } else {
                    worsenedCount ++;
                    repoWorsenedCount ++;
                }

                if (rank != 10000 && baselineRank != 10000) {
                    RD += (rank - baselineRank);
                    repoRD += (rank - baselineRank);
                    allFound ++;
                    repoFound ++;
                 }
            }

            String repoImproved = ((double) repoImproveCount / selectedBugs.size()) * 100 + "%\t";
            String repoWorsened = ((double) repoWorsenedCount / selectedBugs.size()) * 100 + "%\t";
            String repoPreserved = ((double) repoPreservedCount / selectedBugs.size()) * 100 + "%\t";
            double repoMRD = (double) repoRD / repoFound;
            System.out.println(repoName + ": I-" + repoImproved + "W-" + repoWorsened + "P-" + repoPreserved + "MRD-" + repoMRD);
        }

        System.out.println();
        System.out.println("Total #: " + totalReports + ", " + (improveCount + worsenedCount + preservedCount));
        System.out.println("Improved #: " + improveCount + ", " + ((double) improveCount / totalReports) * 100 + "%");
        System.out.println("Worsened #: " + worsenedCount + ", " + ((double) worsenedCount / totalReports) * 100 + "%");
        System.out.println("Preserved #: " + preservedCount + ", " + ((double) preservedCount / totalReports) * 100 + "%");

        System.out.println("MRD: " + ((double) RD / allFound));
    }
}
