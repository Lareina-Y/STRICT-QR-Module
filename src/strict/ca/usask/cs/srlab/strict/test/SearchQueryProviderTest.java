package strict.ca.usask.cs.srlab.strict.test;

import java.util.ArrayList;
import org.junit.Test;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.MiscUtility;

public class SearchQueryProviderTest {

	@Test
	public void testProvideSearchQueries() {
		String repoName = "eclipse.jdt.ui";
		ArrayList<Integer> selectedBugs = new ArrayList<>();
		selectedBugs.add(303705);

		StaticData.ADD_CODE_ELEM=false;
		StaticData.ADD_TITLE=true;

		System.out.println("===========TextRank + POSRank===========");
		String scoreKey = "TPR";
		ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueries();
		MiscUtility.showItems(queries);

		System.out.println("\n===========SimRank===========");
		String scoreKey2 = "SR";
		SearchQueryProvider searchQueryProvider = new SearchQueryProvider(repoName, scoreKey2, selectedBugs);
		ArrayList<String> queries2 = searchQueryProvider.provideSearchQueries();
		MiscUtility.showItems(queries2);

		System.out.println("\n===========TextRank + POSRank + SimRank===========");
		String scoreKey3 = "TPSR";
		ArrayList<String> queries3 = new SearchQueryProvider(repoName, scoreKey3, selectedBugs).provideSearchQueries();
		MiscUtility.showItems(queries3);
	}
}
