package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.ContentLoader;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;
import strict.utility.SelectedBugs;

import java.util.*;

public class SearchQueryProviderTest {

	@Test
	public void testProvideSearchQueries() {
		ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/tested-repos.txt");
//		String[] repos = {"ecf"};

		String scoreKey = "PR";
		List<String> scoreKeyList = Arrays.asList("PR"); // "TR", "PR", "SR", "BTR", "PTR"
		List<String> simThresholdRankList = Arrays.asList("SR", "BTR");
		boolean addST = false;
		for (String key : scoreKeyList) {
			if (simThresholdRankList.contains(key)) {
				addST = true;
				break;
			}
		}
		StaticData.SIMILARITY_THRESHOLD = 0.5;
		StaticData.ADD_CODE_ELEM = false;
		StaticData.ADD_TITLE = true;

		for (String repoName : repos) {
			ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
			ArrayList<String> queries = new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueries();

			String resultKey = "STRICT-" + scoreKey + "-10"
					+ (StaticData.ADD_TITLE ? "-title" : "")
					+ (addST ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
			String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
					+ ".txt";

			ContentWriter.writeContent(approachQueryFile, queries);
			System.out.println("Repo:" + repoName + " | " + approachQueryFile);
		}
	}
}
