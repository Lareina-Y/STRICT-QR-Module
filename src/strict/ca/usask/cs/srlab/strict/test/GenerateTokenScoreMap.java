package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;
import strict.query.SearchTermProvider;
import strict.utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GenerateTokenScoreMap {
    @Test
    public void testGenerateTokenScoreMap() {
        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");
        List<String> scoreKeyList = Arrays.asList("TR", "PR", "SR", "BTR", "PTR"); // "TR", "PR", "SR", "BTR", "PTR"
        StaticData.SIMILARITY_THRESHOLD = 0.5;

        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

            for (int bugID : selectedBugs) {
                String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
                String title = BugReportLoader.loadBugReportTitle(repoName, bugID);

                SearchTermProvider provider = new SearchTermProvider(repoName, bugID, title, bugReport, scoreKeyList);
                HashMap<String, QueryToken> tokenScoreMap = provider.provideTokenScoreMapByScoreKeyList(scoreKeyList);

                String approachQueryFile = StaticData.HOME_DIR + "/Lareina/TokenScoreMap/ST-"
                        + StaticData.SIMILARITY_THRESHOLD + "/" + repoName + "/" + bugID
                        + ".txt";
                ContentWriter.writeTokenScoreMap(approachQueryFile, tokenScoreMap);
                System.out.println("Log:" + repoName + " | " + bugID);
            }

        }

    }
}
