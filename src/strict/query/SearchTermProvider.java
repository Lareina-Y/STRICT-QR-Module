package strict.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import qd.model.prediction.sampling.BestQueryPredictorSampled;
import strict.graph.*;
import strict.stopwords.StopWordManager;
import strict.text.normalizer.TextNormalizer;
import strict.utility.ContentLoader;
import strict.utility.ItemSorter;
import strict.utility.MiscUtility;
import strict.utility.MyItemSorter;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.stemmer.WordNormalizer;

public class SearchTermProvider {
	int bugID;
	String repository;
	String bugtitle;
	String bugReport;
	final int MAX_TOKEN_IN_QUERY = StaticData.SUGGESTED_KEYWORD_COUNT;
	final String TECHNIQUE_NAME = "strict";
	final int predModelCount = 50;

	/****** strong options *******/
	boolean addTitle = StaticData.ADD_TITLE;
	boolean applyDynamicSize = StaticData.USE_DYNAMIC_KEYWORD_THRESHOLD;
	/**************/

	DirectedGraph<String, DefaultEdge> textGraph;
	DirectedGraph<String, DefaultEdge> posGraph;
	DirectedGraph<String, DefaultEdge> simGraph;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> bTextGraph;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> positGraph;
	HashMap<String, Double> bTextBiasWeights;
	HashMap<String, Double> positBiasWeights;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wtextGraph;
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wposGraph;
	ArrayList<String> sentences;

	public ArrayList<String> getSentences() {
		return sentences;
	}

	public SearchTermProvider(String repository, int bugID, String title, String bugReport, List<String> scoreKeyList) {
		// initialization
		this.bugID = bugID;
		this.repository = repository;
		this.bugtitle = getNormalizeTitle(title);
		this.bugReport = bugReport;
		this.sentences = getAllNormalizedSentences(repository, bugID);
		if (scoreKeyList.contains("TR")){
			this.textGraph = GraphUtility.getWordNetwork(sentences);
		}
		if (scoreKeyList.contains("PR")){
			this.posGraph = GraphUtility.getPOSNetwork(sentences);
		}
		if (scoreKeyList.contains("SR")){
			this.simGraph = GraphUtility.getSimilarityNetwork(repository, bugID);
		}
		if (scoreKeyList.contains("BTR")){
			setBiasedTextNetwork(repository, bugID);
		}
		if (scoreKeyList.contains("PTR")){
			setPositionNetwork(sentences);
		}
//		this.wtextGraph = GraphUtility.getWeightedWordNetwork(sentences);
//		this.wposGraph = GraphUtility.getWeightedPOSNetwork(sentences);
	}

	public SearchTermProvider(String repository, int bugID, String title, String bugReport) {
		// initialization
		this.bugID = bugID;
		this.repository = repository;
		this.bugtitle = getNormalizeTitle(title);
		this.bugReport = bugReport;
		this.sentences = getAllSentences();
		this.textGraph = GraphUtility.getWordNetwork(sentences);
		this.wtextGraph = GraphUtility.getWeightedWordNetwork(sentences);
		this.posGraph = GraphUtility.getPOSNetwork(sentences);
		this.wposGraph = GraphUtility.getWeightedPOSNetwork(sentences);
	}

	public SearchTermProvider(String title, String bugReport) {
		this.bugtitle = getNormalizeTitle(title);
		this.bugReport = bugReport;
		this.sentences = getAllSentences();
	}

	public SearchTermProvider(ArrayList<String> expandedCCTokens) {
		this.textGraph = GraphUtility.getWordNetwork(expandedCCTokens);
	}

	public void setBiasedTextNetwork(String repoName, int bugID) {
		BiasedTextNetworkMaker btnMaker = new BiasedTextNetworkMaker(repoName, bugID);
		this.bTextGraph = btnMaker.createWeightedBiasedTextNetwork();
		this.bTextBiasWeights = btnMaker.biasWeights;
	}

	public void setPositionNetwork(ArrayList<String> sentences) {
		PositionNetworkMaker positNMaker = new PositionNetworkMaker(sentences);
		this.positGraph = positNMaker.createWeightedPositNetwork();
		this.positBiasWeights = positNMaker.positBiasWeights;
	}

	protected HashMap<String, QueryToken> getQueryCoreRankScoresTRC() {
		KCoreScoreProvider kcsProvider = new KCoreScoreProvider(wtextGraph, StaticData.KCORE_SIZE);
		HashMap<String, Double> kcsMap = kcsProvider.provideKCoreScores();
		return convert2WKScore(kcsMap);
	}

	protected HashMap<String, QueryToken> convert2WKScore(HashMap<String, Double> kcsMap) {
		HashMap<String, QueryToken> tokendb = new HashMap<>();
		for (String key : kcsMap.keySet()) {
			double kcsScore = kcsMap.get(key);
			QueryToken qtoken = new QueryToken();
			qtoken.token = key;
			qtoken.coreRankScore = kcsScore;
			tokendb.put(key, qtoken);
		}
		return tokendb;
	}

	protected HashMap<String, QueryToken> getQueryCoreRankScoresPRC() {
		KCoreScoreProvider kcsProvider = new KCoreScoreProvider(wposGraph, StaticData.KCORE_SIZE);
		HashMap<String, Double> kcsMap = kcsProvider.provideKCoreScores();
		return convert2WKScore(kcsMap);
	}

	protected ArrayList<String> getAllSentences() {
		QTextCollector textcollector = new QTextCollector(this.bugtitle, this.bugReport);
		return textcollector.collectQuerySentences();
	}

	protected ArrayList<String> getAllNormalizedSentences(String repoName, int bugID) {
		String brFile = StaticData.HOME_DIR + "/ChangeReqs-Normalized/" + repoName + "/" + bugID + ".txt";
		return ContentLoader.getAllLinesOptList(brFile);
	}

	protected String getNormalizeTitle(String title) {
		return new TextNormalizer().normalizeSimpleDiscardSmallwithOrder(title);
	}

	protected HashMap<String, QueryToken> getTextRank() {
		HashMap<String, QueryToken> tokendb = GraphUtility.initializeTokensDB(this.textGraph);
		TextRankManager manager = new TextRankManager(this.textGraph, tokendb);
		return manager.getTextRank();
	}

	public HashMap<String, QueryToken> getBTextRank() {
		HashMap<String, QueryToken> tokendb = GraphUtility.initBTTokensDB(this.bTextGraph, this.bTextBiasWeights);
		BiasedTextRankManager manager = new BiasedTextRankManager(this.bTextGraph, tokendb);
		return manager.getBTextRank();
	}

	public HashMap<String, QueryToken> getPositRank() {
		HashMap<String, QueryToken> tokendb = GraphUtility.initPTTokensDB(this.positGraph, this.positBiasWeights);
		PositionRankManager manager = new PositionRankManager(this.positGraph, tokendb);
		return manager.getPositionRank();
	}

	protected HashMap<String, QueryToken> getPOSRank() {
		HashMap<String, QueryToken> tokendb = GraphUtility.initializeTokensDB(this.posGraph);
		POSRankManager manager = new POSRankManager(this.posGraph, tokendb);
		return manager.getPOSRank();
	}

	public HashMap<String, QueryToken> getSimilarityRank() {
		HashMap<String, QueryToken> tokendb = GraphUtility.initializeTokensDB(this.simGraph);
		SimilarityRankManager manager = new SimilarityRankManager(this.simGraph, tokendb);
		return manager.getSIMRank();
	}

	protected HashMap<String, QueryToken> getTRC() {
		return this.getQueryCoreRankScoresTRC();
	}

	protected HashMap<String, QueryToken> getPRC() {
		return this.getQueryCoreRankScoresPRC();
	}

	public String provideSearchQueryByScoreKeyList(List<String> scoreKeyList) {
		HashMap<String, Double> combineddb = new HashMap<>();

		for (String scoreKey : scoreKeyList) {
			HashMap<String, QueryToken> tempMap = new HashMap<>();
			switch (scoreKey) {
				case "TR":
					tempMap = getTextRank();
					break;
				case "BTR":
					tempMap = getBTextRank();
					break;
				case "PTR":
					tempMap = getPositRank();
					break;
				case "PR":
					tempMap = getPOSRank();
					break;
				case "SR":
					tempMap = getSimilarityRank();
					break;
				default:
					break;
			}

			List<Map.Entry<String, QueryToken>> sortedMap = MyItemSorter.sortQTokensByScoreKey(tempMap, scoreKey);
			combineddb = combinedScores(combineddb, sortedMap, scoreKey);
		}
		return getQueryFinalizedBorda(combineddb);
	}

	// TODO: Will be removed, use "provideSearchQueryByScoreKeyList" method instead
	public String provideSearchQuery(String scoreKey) {

		HashMap<String, QueryToken> textRankMap = new HashMap<>();
		HashMap<String, QueryToken> posRankMap = new HashMap<>();
		HashMap<String, QueryToken> simRankMap = new HashMap<>();
		HashMap<String, QueryToken> bTextRankMap = new HashMap<>();
		HashMap<String, QueryToken> positRankMap = new HashMap<>();

		HashMap<String, QueryToken> coreRankMapTR = new HashMap<>();
		HashMap<String, QueryToken> coreRankMapPR = new HashMap<>();

		HashMap<String, Double> combineddb = new HashMap<>();

		switch (scoreKey) {
		case "TR":
			textRankMap = getTextRank();
			combineddb = transferScores(textRankMap, "TR");
			break;
		case "BTR":
			bTextRankMap = getBTextRank();
			combineddb = transferScores(bTextRankMap, "BTR");
			break;
		case "PTR":
			positRankMap = getPositRank();
			combineddb = transferScores(positRankMap, "PTR");
			break;
		case "PR":
			posRankMap = getPOSRank();
			combineddb = transferScores(posRankMap, "PR");
			break;
		case "SR":
			simRankMap = getSimilarityRank();
			combineddb = transferScores(simRankMap, "SR");
			break;
		case "TPR":
			textRankMap = getTextRank();
			posRankMap = getPOSRank();
			combineddb = getCombinedTPRScores(textRankMap, posRankMap);
			break;
		case "TPTR":
			textRankMap = getTextRank();
			positRankMap = getPositRank();
			combineddb = getCombinedTPTRScores(textRankMap, positRankMap);
			break;
		case "TPSR":
		case "TPMSR":
			textRankMap = getTextRank();
			posRankMap = getPOSRank();
			simRankMap = getSimilarityRank();
			combineddb = getCombinedTPSRScores(textRankMap, posRankMap, simRankMap);
			break;
		case "TPBR":
			textRankMap = getTextRank();
			posRankMap = getPOSRank();
			bTextRankMap = getBTextRank();
			combineddb = getCombinedTPBRScores(textRankMap, posRankMap, bTextRankMap);
			break;
		case "PTPR":
			positRankMap = getPositRank();
			posRankMap = getPOSRank();
			combineddb = getCombinedPTPRScores(positRankMap, posRankMap);
			break;
		case "TPPTR":
			textRankMap = getTextRank();
			posRankMap = getPOSRank();
			positRankMap = getPositRank();
			combineddb = getCombinedTPPTRScores(textRankMap, posRankMap, positRankMap);
			break;
		case "TRC":
			coreRankMapTR = getQueryCoreRankScoresTRC();
			combineddb = transferScores(coreRankMapTR, "TRC");
			break;
		case "PRC":
			coreRankMapPR = getQueryCoreRankScoresPRC();
			combineddb = transferScores(coreRankMapPR, "PRC");
			break;
		case "TPRC":
			coreRankMapTR = getQueryCoreRankScoresTRC();
			coreRankMapPR = getQueryCoreRankScoresPRC();
			combineddb = addCoreRankScores(combineddb, coreRankMapPR);
			combineddb = addCoreRankScores(combineddb, coreRankMapTR);
			break;

		default:
			break;
		}

		return getQueryFinalizedBorda(combineddb);
	}

	protected double getDOI(int index, int N) {
		return (1 - (double) index / N);
	}

	protected HashMap<String, Double> getCombinedTPRScores(HashMap<String, QueryToken> textRankMap,
																													HashMap<String, QueryToken> posRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> trSorted = MyItemSorter.sortQTokensByScoreKey(textRankMap, "TR");
		List<Map.Entry<String, QueryToken>> prSorted = MyItemSorter.sortQTokensByScoreKey(posRankMap, "PR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, trSorted, "TR");
		combineddb = combinedScores(combineddb, prSorted, "PR");
		return combineddb;
	}

	protected HashMap<String, Double> getCombinedTPTRScores(HashMap<String, QueryToken> textRankMap,
																												 HashMap<String, QueryToken> positRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> trSorted = MyItemSorter.sortQTokensByScoreKey(textRankMap, "TR");
		List<Map.Entry<String, QueryToken>> ptrSorted = MyItemSorter.sortQTokensByScoreKey(positRankMap, "PTR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, trSorted, "TR");
		combineddb = combinedScores(combineddb, ptrSorted, "PTR");
		return combineddb;
	}

	protected HashMap<String, Double> getCombinedPTPRScores(HashMap<String, QueryToken> positRankMap,
																												 HashMap<String, QueryToken> posRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> ptrSorted = MyItemSorter.sortQTokensByScoreKey(positRankMap, "PTR");
		List<Map.Entry<String, QueryToken>> prSorted = MyItemSorter.sortQTokensByScoreKey(posRankMap, "PR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, ptrSorted, "PTR");
		combineddb = combinedScores(combineddb, prSorted, "PR");
		return combineddb;
	}

	protected HashMap<String, Double> getCombinedTPSRScores(HashMap<String, QueryToken> tokenRankMap,
																													HashMap<String, QueryToken> posRankMap, HashMap<String, QueryToken> simRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> trSorted = MyItemSorter.sortQTokensByScoreKey(tokenRankMap, "TR");
		List<Map.Entry<String, QueryToken>> prSorted = MyItemSorter.sortQTokensByScoreKey(posRankMap, "PR");
		List<Map.Entry<String, QueryToken>> srSorted = MyItemSorter.sortQTokensByScoreKey(simRankMap, "SR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, trSorted, "TR");
		combineddb = combinedScores(combineddb, prSorted, "PR");
		combineddb = combinedScores(combineddb, srSorted, "SR");
		return combineddb;
	}

	protected HashMap<String, Double> getCombinedTPPTRScores(HashMap<String, QueryToken> tokenRankMap,
																													HashMap<String, QueryToken> posRankMap, HashMap<String, QueryToken> positRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> trSorted = MyItemSorter.sortQTokensByScoreKey(tokenRankMap, "TR");
		List<Map.Entry<String, QueryToken>> prSorted = MyItemSorter.sortQTokensByScoreKey(posRankMap, "PR");
		List<Map.Entry<String, QueryToken>> ptrSorted = MyItemSorter.sortQTokensByScoreKey(positRankMap, "PTR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, trSorted, "TR");
		combineddb = combinedScores(combineddb, prSorted, "PR");
		combineddb = combinedScores(combineddb, ptrSorted, "PTR");
		return combineddb;
	}

	protected HashMap<String, Double> combinedScores(HashMap<String, Double> combineddb,
																List<Map.Entry<String, QueryToken>> dbSorted, String scoreKey) {
		double parameter = 1.0;
		switch (scoreKey) {
			case "TR":
				parameter = StaticData.alpha;
				break;
			case "PR":
				parameter = StaticData.beta;
				break;
			case "SR":
				parameter = StaticData.gamma;
				break;
			case "BTR":
				parameter = StaticData.delta;
				break;
			case "PTR":
				parameter = StaticData.epsilon;
				break;
		}

		for (int i = 0; i < dbSorted.size(); i++) {
			double doi = getDOI(i, dbSorted.size());
			String key = dbSorted.get(i).getKey();
			if (!combineddb.containsKey(key)) {
				if (!StaticData.ADD_SIMRANK_SCORE && scoreKey.equals("SR")) {
					combineddb.put(key, - doi * parameter);
				} else {
					combineddb.put(key, doi * parameter);
				}
			} else {
				double score = 0.0;
				if (!StaticData.ADD_SIMRANK_SCORE && scoreKey.equals("SR")) {
					score = combineddb.get(key) - doi * parameter;
				} else {
					score = combineddb.get(key) + doi * parameter;
				}
				combineddb.put(key, score);
			}
		}
		return combineddb;
	}

	protected HashMap<String, Double> getCombinedTPBRScores(HashMap<String, QueryToken> textRankMap,
																													 HashMap<String, QueryToken> posRankMap, HashMap<String, QueryToken> bTextRankMap) {
		// extracting final query terms
		List<Map.Entry<String, QueryToken>> trSorted = MyItemSorter.sortQTokensByScoreKey(textRankMap, "TR");
		List<Map.Entry<String, QueryToken>> prSorted = MyItemSorter.sortQTokensByScoreKey(posRankMap, "PR");
		List<Map.Entry<String, QueryToken>> btrSorted = MyItemSorter.sortQTokensByScoreKey(bTextRankMap, "BTR");

		HashMap<String, Double> combineddb = new HashMap<>();
		combineddb = combinedScores(combineddb, trSorted, "TR");
		combineddb = combinedScores(combineddb, prSorted, "PR");
		combineddb = combinedScores(combineddb, btrSorted, "BTR");

		return combineddb;
	}

	// added recently
	protected HashMap<String, Double> addCoreRankScores(HashMap<String, Double> combineddb,
			HashMap<String, QueryToken> kcoreMap) {
		// works for both TRC and PRC
		List<Map.Entry<String, QueryToken>> sorted = MyItemSorter.sortQTokensByScoreKey(kcoreMap, "TRC");
		HashMap<String, Double> kcoreScoreMap = new HashMap<>();
		for (int i = 0; i < sorted.size(); i++) {
			double doi = getDOI(i, sorted.size());
			kcoreScoreMap.put(sorted.get(i).getKey(), doi);
		}

		for (String key : kcoreScoreMap.keySet()) {
			if (combineddb.containsKey(key)) {
				double updated = combineddb.get(key) + kcoreScoreMap.get(key);
				combineddb.put(key, updated);
			} else {
				combineddb.put(key, kcoreScoreMap.get(key));
			}
		}
		return combineddb;
	}

	protected HashMap<String, Double> transferScores(HashMap<String, QueryToken> scoreMap, String scoreKey) {
		HashMap<String, Double> tempMap = new HashMap<>();
		for (String key : scoreMap.keySet()) {
			switch (scoreKey) {
			case "TR":
				tempMap.put(key, scoreMap.get(key).textRankScore);
				break;
			case "BTR":
				tempMap.put(key, scoreMap.get(key).bTextRankScore);
			case "PTR":
				tempMap.put(key, scoreMap.get(key).positRankScore);
			case "PR":
				tempMap.put(key, scoreMap.get(key).posRankScore);
				break;
			case "SR":
				tempMap.put(key, scoreMap.get(key).simRankScore);
				break;
			case "TRC":
			case "PRC":
				tempMap.put(key, scoreMap.get(key).coreRankScore);
				break;
			case "ALL":
				tempMap.put(key, scoreMap.get(key).totalScore);
				break;
			default:
				break;
			}
		}
		return tempMap;
	}

	// Similar to combinedScores(), but this method not include parameters
	protected HashMap<String, Double> gatherScores(List<Map.Entry<String, QueryToken>> sortedList,
			HashMap<String, Double> scoreMap) {
		for (int i = 0; i < sortedList.size(); i++) {
			double doi = getDOI(i, sortedList.size());
			String key = sortedList.get(i).getKey();
			if (scoreMap.containsKey(key)) {
				double updatedDOI = scoreMap.get(key) + doi;
				scoreMap.put(key, updatedDOI);
			} else {
				scoreMap.put(key, doi);
			}
		}
		return scoreMap;
	}

	protected String getQueryFinalizedBorda(HashMap<String, QueryToken> trMap, HashMap<String, QueryToken> prMap,
			HashMap<String, QueryToken> trcMap, HashMap<String, QueryToken> prcMap) {
		List<Map.Entry<String, QueryToken>> trList = MyItemSorter.sortQTokensByScoreKey(trMap, "TR");
		List<Map.Entry<String, QueryToken>> prList = MyItemSorter.sortQTokensByScoreKey(trMap, "PR");
		List<Map.Entry<String, QueryToken>> trcList = MyItemSorter.sortQTokensByScoreKey(trMap, "TRC");
		List<Map.Entry<String, QueryToken>> prcList = MyItemSorter.sortQTokensByScoreKey(trMap, "PRC");
		HashMap<String, Double> scoreMap = new HashMap<>();
		scoreMap = gatherScores(trList, scoreMap);
		scoreMap = gatherScores(prList, scoreMap);
		scoreMap = gatherScores(trcList, scoreMap);
		scoreMap = gatherScores(prcList, scoreMap);

		return getQueryFinalizedBorda(scoreMap);

	}

	protected String getQueryFinalizedBorda(HashMap<String, Double> combineddb) {

		List<Map.Entry<String, Double>> sorted = ItemSorter.sortHashMapDouble(combineddb);

		ArrayList<String> suggested = new ArrayList<>();
		int MAX_QUERY_SIZE = StaticData.SUGGESTED_KEYWORD_COUNT;

		if (StaticData.USE_DYNAMIC_KEYWORD_THRESHOLD) {
			MAX_QUERY_SIZE = (int) (sorted.size() * StaticData.KEYWORD_RATIO);
		}

		for (int i = 0; i < sorted.size(); i++) {
			String token = sorted.get(i).getKey();

			suggested.add(token);
			if (suggested.size() == MAX_QUERY_SIZE) {
				break;
			}
		}

		String queryStr = MiscUtility.list2Str(suggested);
		//TODO : Test in query-v3
		String expanded = new WordNormalizer().expandCCWords(queryStr);
		return new StopWordManager().getRefinedSentence(expanded);
//		return new StopWordManager().getRefinedSentence(queryStr);
	}

	public String deliverBestQuery() {

		SearchTermProvider stProvider = new SearchTermProvider(repository, bugID, bugtitle, bugReport);
		String trQuery = stProvider.provideSearchQuery("TR");
		String prQuery = stProvider.provideSearchQuery("PR");
		String tprQuery = stProvider.provideSearchQuery("TPR");
		String trcQuery = stProvider.provideSearchQuery("TRC");
		String prcQuery = stProvider.provideSearchQuery("PRC");
		String tprcQuery = stProvider.provideSearchQuery("TPRC");
		// String allQuery = stProvider.provideSearchQuery("ALL");

		HashMap<String, String> candidateQueryMap = new HashMap<>();
		candidateQueryMap.put("TR", bugID + "\t" + trQuery);
		candidateQueryMap.put("PR", bugID + "\t" + prQuery);
		candidateQueryMap.put("TPR", bugID + "\t" + tprQuery);
		candidateQueryMap.put("TRC", bugID + "\t" + trcQuery);
		candidateQueryMap.put("PRC", bugID + "\t" + prcQuery);
		candidateQueryMap.put("TPRC", bugID + "\t" + tprcQuery);
		// candidateQueryMap.put("ALL", bugID + "\t" + allQuery);

		System.out.println("Candidates:" + candidateQueryMap);

		// BestQueryPredictor bestQueryPredictor = new BestQueryPredictor(repository,
		// bugID, candidateQueryMap, entCalc);
		BestQueryPredictorSampled bestQueryPredictor = new BestQueryPredictorSampled(repository, bugID,
				candidateQueryMap, TECHNIQUE_NAME, predModelCount);
		return bestQueryPredictor.deliverBestQuery();

	}

}
