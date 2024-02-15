package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QTextCollector;
import strict.utility.BugReportLoader;
import strict.utility.ContentLoader;
import strict.utility.ContentWriter;
import strict.utility.SelectedBugs;

import java.util.ArrayList;

public class NormalizedContentTest {

    @Test
    // Used to save the normalized bug report description to the STRICT-Replication-Package
    public void testNormalizedContent() {
        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");

        for (String repo : repos) {
            String repoName = repo;
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

            for (int bugID : selectedBugs) {
                String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
                String bugDesc = BugReportLoader.loadBugReportDesc(repoName, bugID);
                String title = BugReportLoader.loadBugReportTitle(repoName, bugID);

                QTextCollector textcollector = new QTextCollector(title, bugReport);
                ArrayList<String> normalizedContentList = textcollector.collectQuerySentences();

                String storedFile = StaticData.HOME_DIR + "/ChangeReqs-Normalized/" + repoName + "/" + bugID + ".txt";
//                String storedTitleFile = StaticData.HOME_DIR + "/ChangeReqs-Title/" + repoName + "/" + bugID + ".txt";

                ContentWriter.writeContent(storedFile, normalizedContentList);
//                ContentWriter.writeContent(storedTitleFile, title);

                System.out.println("Saved: " + repoName + " - " + bugID);
            }
        }
    }
}
