package strict.utility;

import java.util.ArrayList;
import java.util.HashMap;
import strict.ca.usask.cs.srlab.strict.config.StaticData;

public class QueryLoader {

	protected static String extractQuery(String line) {
		String temp = new String();
		String[] parts = line.split("\\s+");
		for (int i = 1; i < parts.length; i++) {
			temp += parts[i] + " ";
			if (i == StaticData.MAX_QUERY_LEN)
				break;
		}
		return temp.trim();
	}

	public static HashMap<Integer, String> loadQuery(String queryFile) {
		ArrayList<String> qlines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> queryMap = new HashMap<>();
		for (String line : qlines) {
			int bugID = Integer.parseInt(line.split("\\s+")[0]);
			String query = extractQuery(line);
			queryMap.put(bugID, query);
		}
		return queryMap;
	}

	public static HashMap<Integer, String> loadQueryIncludeTab(String queryFile) {
		ArrayList<String> qlines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> queryMap = new HashMap<>();
		for (String line : qlines) {
			String[] lineParts = line.split("\t", 2);
			int bugID = Integer.parseInt(lineParts[0]);
			String query = lineParts[1];
			queryMap.put(bugID, query);
		}
		return queryMap;
	}

	public static HashMap<Integer, String> loadQueryWithoutTitle(String queryFile) {
		ArrayList<String> qlines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> queryMap = new HashMap<>();
		for (String line : qlines) {
			int bugID = Integer.parseInt(line.split("\t")[0]);
			String query = line.split("\t")[1];
			queryMap.put(bugID, query);
		}

		return queryMap;
	}

	public static HashMap<Integer, String> loadQueries(String queryFile, Boolean addTitle) {
		HashMap<Integer, String> tempMap;
		if (addTitle) {
			tempMap = QueryLoader.loadQuery(queryFile);
		} else {
			tempMap = QueryLoader.loadQueryWithoutTitle(queryFile);
		}
		return tempMap;
	}

	public static HashMap<Integer, Integer> loadQueryQE(String queryEffFile) {
		ArrayList<String> qlines = ContentLoader.getAllLinesOptList(queryEffFile);
		HashMap<Integer, Integer> queryMap = new HashMap<>();
		for (String line : qlines) {
			int bugID = Integer.parseInt(line.split("\\s+")[0]);
			int qe = Integer.parseInt(line.trim().split("\\s+")[1]);
			queryMap.put(bugID, qe);
		}
		return queryMap;
	}

}
