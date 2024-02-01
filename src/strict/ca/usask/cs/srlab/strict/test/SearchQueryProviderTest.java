package strict.ca.usask.cs.srlab.strict.test;

import java.util.ArrayList;
import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.query.evaluation.RepoRankMaker;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;
import strict.utility.SelectedBugs;

public class SearchQueryProviderTest {

	@Test
	public void testProvideSearchQueries() {

		String repoName = "eclipse.jdt.ui";
		ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);

		String scoreKey = "TPR";
		StaticData.ADD_SIMRANK_SCORE = true;
		StaticData.SIMILARITY_THRESHOLD = 0.5;

		StaticData.ADD_CODE_ELEM=false;
		StaticData.ADD_TITLE=false;
		ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueries();
		MiscUtility.showItems(queries);

		String resultKey = "STRICT-" + scoreKey + "-10"
				+ (StaticData.ADD_TITLE ? "-title" : "")
				+ (scoreKey.equals("TPSR") ? "-" + StaticData.SIMILARITY_THRESHOLD : "");
		String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query/" + repoName + "/" + resultKey
				+ ".txt";

		ContentWriter.writeContent(approachQueryFile, queries);
		System.out.println("Repo:" + repoName + " | " + approachQueryFile);
	}
}
