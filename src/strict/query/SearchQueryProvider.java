package strict.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.text.normalizer.TextNormalizer;
import strict.utility.BugReportLoader;
import strict.utility.ContentWriter;
import strict.utility.MiscUtility;

public class SearchQueryProvider {

	String repoName;
	String scoreKey;
	List<String> scoreKeyList;
	ArrayList<Integer> selectedBugs;
	String queryOutputFile;

	public SearchQueryProvider(String repoName, String scoreKey, ArrayList<Integer> selectedBugs, List<String> scoreKeyList) {
		this.repoName = repoName;
		this.scoreKey = scoreKey;
		this.selectedBugs = selectedBugs;
		this.scoreKeyList = scoreKeyList;
	}

	protected void setQueryFile(String fileName) {
		this.queryOutputFile = fileName;
	}

	protected void saveQueries(ArrayList<String> queries) {
		ContentWriter.writeContent(this.queryOutputFile, queries);
	}

	protected String getCamelCaseQuery(String bugReport) {
		ArrayList<String> sentences = getCCExpandedTokens(bugReport);
		SearchTermProvider provider = new SearchTermProvider(sentences);
		return provider.provideSearchQuery("TR");
	}

	protected ArrayList<String> getCCExpandedTokens(String bugReport) {
		String[] words = bugReport.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		TextNormalizer textNormalizer = new TextNormalizer();
		ArrayList<String> codeLikeElems = textNormalizer.extractCodeItem(wordList);
		ArrayList<String> sentences = new ArrayList<>();
		for (String celem : codeLikeElems) {
			String sentence = MiscUtility.list2Str(textNormalizer.decomposeCamelCase(celem));
			sentences.add(sentence);
		}
		return sentences;
	}

	protected String getNormalizedTitle(String title) {
		return new TextNormalizer().normalizeSimpleCodeDiscardSmall(title);
	}

	public ArrayList<String> provideSearchQueries() {
		ArrayList<String> queries = new ArrayList<>();
		for (int bugID : this.selectedBugs) {
			String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
			String title = BugReportLoader.loadBugReportTitle(repoName, bugID);
			
			SearchTermProvider provider = new SearchTermProvider(repoName, bugID, title, bugReport, scoreKeyList);
//			String suggestedKeywords = provider.provideSearchQuery(scoreKey);
			String suggestedKeywords = provider.provideSearchQueryByScoreKeyList(scoreKeyList);
			String suggestedQuery = new String(suggestedKeywords);
			
			if (StaticData.ADD_CODE_ELEM) {
				String codetokens = getCamelCaseQuery(bugReport);
				suggestedQuery += "\t" + codetokens;
			}
			if (StaticData.ADD_TITLE) {
				String titletokens = getNormalizedTitle(title);
				suggestedQuery += "\t" + titletokens;
			}
			
			String queryLine = bugID + "\t" + suggestedQuery;
			queries.add(queryLine);
			System.out.println("Log: " + queryLine);
		}
		return queries;
	}

	public HashMap<Integer, String> provideSearchQueriesInHashMap() {
		HashMap<Integer, String> queries = new HashMap<>();
		for (int bugID : this.selectedBugs) {
			String bugReport = BugReportLoader.loadBugReport(repoName, bugID);
			String title = BugReportLoader.loadBugReportTitle(repoName, bugID);

			SearchTermProvider provider = new SearchTermProvider(repoName, bugID, title, bugReport, scoreKeyList);
//			String suggestedKeywords = provider.provideSearchQuery(scoreKey);
			String suggestedKeywords = provider.provideSearchQueryByScoreKeyList(scoreKeyList);
			String suggestedQuery = new String(suggestedKeywords);

			if (StaticData.ADD_CODE_ELEM) {
				String codetokens = getCamelCaseQuery(bugReport);
				suggestedQuery += "\t" + codetokens;
			}
			if (StaticData.ADD_TITLE) {
				String titletokens = getNormalizedTitle(title);
				suggestedQuery += "\t" + titletokens;
			}

			String queryLine = bugID + "\t" + suggestedQuery;
			System.out.println("Log: " + scoreKey + " " + queryLine);
			queries.put(bugID, suggestedQuery);
		}
		return queries;
	}
}
