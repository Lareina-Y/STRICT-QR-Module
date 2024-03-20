package strict.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import strict.query.QueryToken;
import strict.query.SearchTermProvider;
import strict.query.TokenVector;
import strict.utility.*;
import strict.ca.usask.cs.srlab.strict.config.StaticData;

import javax.swing.*;
import java.util.*;

public class SimilarityNetworkMaker {

//    ArrayList<String> sentences;
    String[] sentences;
    public DirectedGraph<String, DefaultEdge> graph;
    HashMap<String, QueryToken> tokendb;

//    public SimilarityNetworkMaker(ArrayList<String> sentences) {
//        // initialization of items
//        this.sentences = sentences;
//        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
//        this.tokendb = new HashMap<>();
//    }

    public SimilarityNetworkMaker(String repoName, int bugID) {
        // initialization of items
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        this.tokendb = new HashMap<>();
        this.sentences = BugReportLoader.loadEmbedReport(repoName, bugID);
    }

    public DirectedGraph<String, DefaultEdge> createSimilarityNetwork() {

        // create the links in a subset of keywords (each sentence)
//        for (String sentence : this.sentences) {
        for (String sentence: this.sentences) {
            TokenVector[] tokens = Helper.sentence2TokenVectors(sentence);
//            String[] tokens = sentence.split("\\W+"); // "\\s+"
            for (int i = 0; i < tokens.length; i++) {
                TokenVector currentToken = tokens[i];
                for (int k = i+1; k < tokens.length; k++) {
                    TokenVector comparedToken = tokens[k];

                    // now add the graph nodes
                    if (!graph.containsVertex(currentToken.token)) {
                        graph.addVertex(currentToken.token);
                    }
                    if (!graph.containsVertex(comparedToken.token) && !comparedToken.token.isEmpty()) {
                        graph.addVertex(comparedToken.token);
                    }

                    if(!(currentToken.token.equals(comparedToken.token))) {
                        // calculate the Cosine Similarity
//                        double[] vector1 = gloveWordVectors.getWordVector(currentToken.toLowerCase());
//                        double[] vector2 = gloveWordVectors.getWordVector(comparedToken.toLowerCase());

                        double[] vector1 = currentToken.vector;
                        double[] vector2 = comparedToken.vector;

                        if (vector1 != null && vector2 != null) {
                            double CSScore = CosineSimilarity.calculateCS(vector1, vector2);

                            // adding edges to the graph
                            if (CSScore > StaticData.SIMILARITY_THRESHOLD) {
                                if (!graph.containsEdge(currentToken.token, comparedToken.token)) {
                                    graph.addEdge(currentToken.token, comparedToken.token);
                                    graph.addEdge(comparedToken.token, currentToken.token);
                                }
                            }
                        }
                    }
                }
            }
        }

        return graph;
    }

    public HashMap<String, QueryToken> getTokenDictionary() {
        // populating token dictionary
        HashSet<String> nodes = new HashSet<>();
        nodes.addAll(graph.vertexSet());
        for (String vertex : nodes) {
            QueryToken qtoken = new QueryToken();
            qtoken.token = vertex;
            this.tokendb.put(vertex, qtoken);
        }
        return this.tokendb;
    }

    protected void showEdges() {
        // show the graph edges
        for (String key : graph.vertexSet()) {
            Set<DefaultEdge> edges = graph.edgesOf(key);
            System.out.println(key + "\t" + edges.size());
        }
    }

    protected ArrayList<String> getImportantTokens(
            HashMap<String, QueryToken> sortedtokendb, String bugtitle) {
        ArrayList<String> toptokens = new ArrayList<>();
        int count = 0;
        int intitle = 0;
        for (String key : sortedtokendb.keySet()) {
            if (bugtitle.contains(key)) {
                toptokens.add(key);
                count++;
                intitle++;
            }
            if (count == 5)
                break;
        }
        int lateradded = 0;
        if (intitle < 5) {
            for (String token : sortedtokendb.keySet()) {
                if (!bugtitle.contains(token)) {
                    toptokens.add(token);
                    lateradded++;
                    if (lateradded + intitle == 5)
                        break;
                }
            }
        }
        return toptokens;
    }

    protected ArrayList<String> collectTopTokens(
            HashMap<String, QueryToken> sortedtokendb) {
        // collecting top tokens
        ArrayList<String> toptokens = new ArrayList<>();
        int count = 0;
        for (String key : sortedtokendb.keySet()) {
            toptokens.add(key);
            count++;
            if (count == 5)
                break;
        }
        return toptokens;
    }

    public void visualizeWordGraph(HashMap<String, QueryToken> sortedtokendb) {
        // visualize the word net
        JFrame frame = new JFrame();
         ArrayList<String> toptokens = collectTopTokens(sortedtokendb);
//        ArrayList<String> toptokens = getImportantTokens(sortedtokendb, bugtitle);
        // StackGraph stackgraph=new StackGraph(graph, 900, 700);
        StackGraph stackgraph = new StackGraph(graph, 900, 700, toptokens);
        frame.getContentPane().add(stackgraph);
        frame.setVisible(true);
        frame.setSize(900, 700);
        stackgraph.init();
//        stackgraph.start();
    }

    public static void main(String[] args) {
        // main method
//        ArrayList<String> sentences = new ArrayList<>();
//        sentences.add("Closing the ECF Buddy List view cause chat room disconnect");
//        sentences.add("If you close the ECF Buddy List view, and then try and type in a chat room you were connected to the following errors are thrown:");
//        SimilarityNetworkMaker maker = new SimilarityNetworkMaker(sentences);
//        DirectedGraph<String, DefaultEdge> graph = maker.createSimilarityNetwork();
//        maker.showEdges();

        String repoName = "eclipse.jdt.ui";
        StaticData.ADD_CODE_ELEM = false;
        StaticData.ADD_TITLE = false;
        StaticData.SIMILARITY_THRESHOLD = 0.55;

        String bugReport = BugReportLoader.loadBugReport(repoName, 303705);
        String title = BugReportLoader.loadBugReportTitle(repoName, 303705);
        SearchTermProvider provider = new SearchTermProvider(repoName, 303705, title, bugReport);
        ArrayList<String> sentences = provider.getSentences();
        System.out.println("======= show sentences =======");
        MiscUtility.showItems(sentences);

        SimilarityNetworkMaker maker = new SimilarityNetworkMaker(repoName, 303705);
        DirectedGraph<String, DefaultEdge> graph = maker.createSimilarityNetwork();
//        maker.showEdges();

        // Get the vertex set from the graph
//        Set<String> vertices = graph.vertexSet();

        // Print the number of vertices
//        System.out.println("Number of vertices: " + vertices.size());


        HashMap<String, QueryToken> simRankMap = provider.getSimilarityRank();
        List<Map.Entry<String, QueryToken>> srTokendb = MyItemSorter.sortQTokensByScoreKey(simRankMap, "SR");

        LinkedHashMap<String, QueryToken> sortedTokendb = new LinkedHashMap<>();
        for (Map.Entry<String, QueryToken> entry : srTokendb) {
            String key = entry.getKey();
            QueryToken value = entry.getValue();
            sortedTokendb.put(key, value);
        }

//        Helper.printHashMap(sortedTokendb);

        maker.visualizeWordGraph(sortedTokendb);

    }

}
