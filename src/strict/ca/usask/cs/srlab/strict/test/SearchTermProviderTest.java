package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchTermProvider;
import strict.utility.BugReportLoader;

import java.util.Arrays;
import java.util.List;

public class SearchTermProviderTest {
	
	@Test
	public void testTPRQuery() {
		String repoName = "eclipse.jdt.ui";
		int bugID = 303705;
		String title = BugReportLoader.loadBugReportTitle(repoName, bugID);
		String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
		StaticData.SUGGESTED_KEYWORD_COUNT = 10;
		
		SearchTermProvider provider=new SearchTermProvider(repoName, bugID, title, bugReport);
		String query = provider.provideSearchQuery("TPR");
		System.out.println(query);
	}

	@Test
	public void testTPRQueryByList() {
		List<String> scoreKeyList = Arrays.asList("TR", "PR");
		String repoName = "eclipse.jdt.ui";
		int bugID = 303705;
		String title = BugReportLoader.loadBugReportTitle(repoName, bugID);
		String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
		StaticData.SUGGESTED_KEYWORD_COUNT = 10;

		SearchTermProvider provider=new SearchTermProvider(repoName, bugID, title, bugReport);
		String query = provider.provideSearchQueryByScoreKeyList(scoreKeyList);
		System.out.println(query);
	}
}
