package strict.samurai.splitter;

import java.util.ArrayList;
import java.util.HashMap;

import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.utility.ContentLoader;

public class SamuraiSplitter {

	static HashMap<String, Integer> wordMap = new HashMap<>();
	ArrayList<String> srcLines;
	boolean isBR=false;

	public SamuraiSplitter(ArrayList<String> srcLines) {
		if (wordMap.isEmpty()) {
			this.loadWordMap();
		}
		this.srcLines = srcLines;
	}
	
	protected void loadWordMap() {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList( StaticData.SAMURAI_DIR + "/tokendb.txt");
		for (String line : lines) {
			String[] parts = line.split(":");
			String word = parts[0];
			// minimum word length=3
			if (word.length() > 2) {
				int count = Integer.parseInt(parts[1]);
				wordMap.put(word, count);
			}
		}
	}

	protected String decomposeCamelCase(String ccToken) {
		String cam1Regex = "([a-z])([A-Z])+";
		String replacement1 = "$1\t$2";
		return ccToken.replaceAll(cam1Regex, replacement1);
	}

	protected HashMap<String, Integer> calculateReservedMap(
			ArrayList<String> lines) {
		// extracting the file content
		HashMap<String, Integer> reserveMap = new HashMap<>();
		for (String line : lines) {
			String[] words = line.split("\\p{Punct}+|\\d+|\\s+");
			for (String word : words) {
				if (word.trim().isEmpty())
					continue;
				String decomposed = decomposeCamelCase(word);

				//System.out.println(word + ":" + decomposed);

				String[] smallTokens = decomposed.split("\\s+");
				for (String smallToken : smallTokens) {
					// avoiding the empty tokens
					if (smallToken.trim().isEmpty())
						continue;

					if (word.length() >= 2) {

						if (reserveMap.containsKey(smallToken)) {
							int count = reserveMap.get(smallToken) + 1;
							reserveMap.put(smallToken, count);

						} else {
							reserveMap.put(smallToken, 1);
						}
					}
				}
				// adding cc tokens
				if (word.length() >= 2) {
					if (reserveMap.containsKey(word)) {
						int count = reserveMap.get(word) + 1;
						reserveMap.put(word, count);
					} else {
						reserveMap.put(word, 1);
					}
				}
			}
		}
		return reserveMap;
	}

	public HashMap<String, String> getSplittedTokenMap() {
		// get splitted token map
		HashMap<String, Integer> reservedMap = calculateReservedMap(this.srcLines);
		//System.out.println("====================");
		SplitTokenScore scoreCalc = new SplitTokenScore(reservedMap, wordMap);
		ArrayList<String> tokens = new ArrayList<String>(reservedMap.keySet());
		MixedCaseSplitter mcSplitter = new MixedCaseSplitter(tokens, scoreCalc);
		HashMap<String, String> splitMap = mcSplitter.getSplittedTokens();
		return splitMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String srcFile = "F:/MyWorks/Thesis Works/Data_Mining_Works/CodeRank/experiment/corpus/class/tomcat70/20.java";
		//ArrayList<String> srcLines = ContentLoader.getAllLinesOptList(srcFile);
		ArrayList<String> tokens=new ArrayList<>();
		//tokens.add("GPSstate");
		//tokens.add("ASTVisitor");
		//tokens.add("finalstate");
		tokens.add("NAMESPACE");
		tokens.add("USERLIB");
		//tokens.add("NON-NEGATIVEDECIMALTYPE");
		tokens.add("COUNTRYCODE");
		tokens.add(" ManifestResource mre = new ManifestResource(sm.getString(\"extensionValidator.web-application-manifest\"), manifest, ManifestResource.WAR);");
		
		SamuraiSplitter ssplitter = new SamuraiSplitter(tokens);
		HashMap<String, String> splitMap = ssplitter.getSplittedTokenMap();
		for (String key : splitMap.keySet()) {
			System.out.println(key + "\t" + splitMap.get(key));
		}
	}
}
