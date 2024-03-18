package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;
import strict.utility.SelectedBugs;

import java.util.*;

public class SearchQueryProviderTest {

	@Test
	public void testProvideSearchQueries() {

		String repoName = "eclipse.jdt.core";
		ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

		String scoreKey = "TPR";
		List<String> scoreKeyList = Arrays.asList("TR", "SR"); // "TR", "PR", "SR", "BTR", "PTR"
		StaticData.ADD_SIMRANK_SCORE = true;
		StaticData.SIMILARITY_THRESHOLD = 0.4;

		StaticData.ADD_CODE_ELEM=false;
		StaticData.ADD_TITLE=true;
		ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs, scoreKeyList).provideSearchQueries();
		MiscUtility.showItems(queries);

		List<String> simThresholdRankList = Arrays.asList("SR", "BTR", "TPSR", "TPMSR", "TPBR");

		String resultKey = "STRICT-" + scoreKey + "-10"
				+ (StaticData.ADD_TITLE ? "-title" : "")
				+ (simThresholdRankList.contains(scoreKey) ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
		String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
				+ ".txt";

//		ContentWriter.writeContent(approachQueryFile, queries);
		System.out.println("Repo:" + repoName + " | " + approachQueryFile);
		System.out.println(queries);
	}
}
