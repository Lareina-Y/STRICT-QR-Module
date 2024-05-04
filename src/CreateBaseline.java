import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.lucenecheck.LuceneSearcher;
import strict.lucenecheck.MethodResultRankMgr;
import strict.text.normalizer.TextNormalizer;
import strict.utility.*;

import java.io.IOException;
import java.util.ArrayList;

public class CreateBaseline {

    public static void main(String[] args) throws IOException {
        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");

        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
            System.out.println(repoName + ": " + selectedBugs.size());
            ArrayList<String> queries = new ArrayList<>();

            ArrayList<String> ranks = new ArrayList<>();
//            String rankStoredFile = StaticData.HOME_DIR + "/Baseline/query/query-whole/" + repoName + ".txt";

            String rankQueryFile = StaticData.HOME_DIR + "/Lareina/baseline/query/query-title/" + repoName + ".txt";
//            String rankQueryFile = StaticData.HOME_DIR + "/Lareina/baseline/query/query-desc/" + repoName + ".txt";
//            String rankQueryFile = StaticData.HOME_DIR + "/Lareina/baseline/query/query-whole/" + repoName + ".txt";
//            String rankQueryFile = StaticData.HOME_DIR + "/Lareina/baseline/query/query-title-top10/" + repoName + ".txt";

            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-title/" + repoName + ".txt";
//            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-desc/" + repoName + ".txt";
//            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-whole/" + repoName + ".txt";
//            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/baseline/rank/rank-title-top10/" + repoName + ".txt";

            for (int bugID : selectedBugs) {

                String context = BugReportLoader.loadBugReportTitle(repoName, bugID);
//                String context = BugReportLoader.loadBugReportDesc(repoName, bugID);
//                String context = BugReportLoader.loadBugReport(repoName, bugID);

//                String normalizedContext = new TextNormalizer(context).normalizeBaseline();
                String normalizedContext = new TextNormalizer(context).normalizeSimple();

                // For Title Top 10
//                String[] normalizedContextList = normalizedContext.split("\\s+"); //\s+
//                String top10Context = "";
//                for (int i = 0; i < normalizedContextList.length; i++) {
//                    if (i < 10) {
//                        top10Context += " " + normalizedContextList[i];
//                    }
//                }
//                normalizedContext = top10Context;
                /* --------------------------------------------------------------------------*/

                String query = bugID + "\t" + normalizedContext;
                queries.add(query);

                LuceneSearcher lsearch = new LuceneSearcher(bugID, repoName, normalizedContext);
                int qe = lsearch.getFirstGoldRank();
                ranks.add(bugID + "\t" + qe);
            }

            // clearing the keys
            MethodResultRankMgr.keyMap.clear();

//            ContentWriter.writeContent(rankQueryFile, queries);
//            ContentWriter.writeContent(rankStoredFile, ranks);
        }
    }
}
