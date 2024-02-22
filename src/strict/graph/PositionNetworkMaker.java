package strict.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;
import strict.query.SearchTermProvider;
import strict.utility.BugReportLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PositionNetworkMaker {

    ArrayList<String> sentences;
    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
    HashMap<String, QueryToken> tokendb;
    final int WINDOW_SIZE = 2;
    HashMap<String, Integer> coocCountMap;

    public HashMap<String, Double> positBiasWeights;

    public PositionNetworkMaker(ArrayList<String> sentences) {
        this.sentences = sentences;
        this.wgraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        this.tokendb = new HashMap<>();
        this.positBiasWeights = new HashMap<>();
        this.coocCountMap = new HashMap<>();
    }

    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> createWeightedPositNetwork() {
        // developing the position network
        double positIndex = 1.0;
        for (String sentence : this.sentences) {
            if (sentence.isEmpty()) break;
            String[] tokens = sentence.split("\\s+");
            for (int index = 0; index < tokens.length; index++) {
                String previousToken = new String();
                String nextToken = new String();
                String currentToken = tokens[index];
                if (index > 0)
                    previousToken = tokens[index - 1];

                if (index < tokens.length - 1)
                    nextToken = tokens[index + 1];

                if (!this.positBiasWeights.containsKey(currentToken)) {
                    this.positBiasWeights.put(currentToken, 1.0 / positIndex);
                } else {
                    double currentValue = this.positBiasWeights.get(currentToken);
                    this.positBiasWeights.put(currentToken, currentValue + 1.0 / positIndex);
                }

                // TODO: select only nouns and adjectives (maybe)
                // now add the graph nodes
                if (!wgraph.containsVertex(currentToken)) {
                    wgraph.addVertex(currentToken);
                }
                if (!wgraph.containsVertex(previousToken) && !previousToken.isEmpty()) {
                    wgraph.addVertex(previousToken);
                }
                if (!wgraph.containsVertex(nextToken) && !nextToken.isEmpty()) {
                    wgraph.addVertex(nextToken);
                }

                // adding edges to the graph
                if (!previousToken.isEmpty() && !currentToken.equals(previousToken)) {
                    if (!wgraph.containsEdge(currentToken, previousToken)) {
                        wgraph.addEdge(currentToken, previousToken);
                    }
                    updateCooccCount(currentToken, previousToken);
                }
                if (!nextToken.isEmpty() && !currentToken.equals(nextToken)) {
                    if (!wgraph.containsEdge(currentToken, nextToken)) {
                        wgraph.addEdge(currentToken, nextToken);
                    }
                    updateCooccCount(currentToken, nextToken);
                }
                positIndex ++;
            }
        }

        // setting edge weight
        this.setEdgeWeight();

        // normalized weights for each candidate word
        this.normalizePositBiasWeights();

        // returning the created graph
        return wgraph;
    }

    protected void setEdgeWeight() {
        Set<DefaultWeightedEdge> edges = this.wgraph.edgeSet();
        for (DefaultWeightedEdge edge : edges) {
            String source = wgraph.getEdgeSource(edge);
            String dest = wgraph.getEdgeTarget(edge);
            String keypair = source + "-" + dest;
            if (coocCountMap.containsKey(keypair)) {
                this.wgraph.setEdgeWeight(edge, (double) coocCountMap.get(keypair));
            }
        }
    }

    protected void updateCooccCount(String source, String dest) {
        // updating the co-occurrence count
        String keypair = source + "-" + dest;
        if (this.coocCountMap.containsKey(keypair)) {
            int updated = coocCountMap.get(keypair) + 1;
            this.coocCountMap.put(keypair, updated);
        } else {
            this.coocCountMap.put(keypair, 1);
        }
    }

    protected void normalizePositBiasWeights() {
        double sum = this.positBiasWeights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        this.positBiasWeights.replaceAll((key, value) -> value / sum);
    }


    public HashMap<String, QueryToken> getTokenDictionary(boolean weighted) {
        // populating token dictionary
        HashSet<String> nodes = new HashSet<>();
        if (weighted)
            nodes.addAll(wgraph.vertexSet());
        else
            nodes.addAll(wgraph.vertexSet());
        for (String vertex : nodes) {
            QueryToken qtoken = new QueryToken();
            qtoken.token = vertex;
            this.tokendb.put(vertex, qtoken);
        }
        return this.tokendb;
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

    public void showEdges() {
        // show the graph edges
        for (String key : wgraph.vertexSet()) {
            Set<DefaultWeightedEdge> edges = wgraph.edgesOf(key);

            System.out.print("\n" + key + " " + edges.size() + " ");

            // Print the weights of the edges in the desired format
            for (DefaultWeightedEdge edge : edges) {
                String sourceVertex = wgraph.getEdgeSource(edge);
                String targetVertex = wgraph.getEdgeTarget(edge);
                double weight = wgraph.getEdgeWeight(edge);
                System.out.print("(" + sourceVertex + ", " + targetVertex + ", " + weight + "), ");
            }
        }
    }

    public void visualizeWordGraph(HashMap<String, QueryToken> sortedtokendb, String bugtitle) {
        // visualize the word net
        JFrame frame = new JFrame();
        // ArrayList<String> toptokens = collectTopTokens(sortedtokendb);
        ArrayList<String> toptokens = getImportantTokens(sortedtokendb, bugtitle);
        // StackGraph stackgraph=new StackGraph(graph, 900, 700);
        StackGraph stackgraph = new StackGraph(wgraph, 900, 700, toptokens);
        frame.getContentPane().add(stackgraph);
        frame.setVisible(true);
        frame.setSize(900, 700);
        stackgraph.init();
        stackgraph.start();
    }

    public static void main(String[] args) {
        String repoName = "eclipse.jdt.ui";
        StaticData.ADD_CODE_ELEM=false;
        StaticData.ADD_TITLE=true;

        String bugReport = BugReportLoader.loadBugReport(repoName, 27194);
        String title = BugReportLoader.loadBugReportTitle(repoName, 27194);
        SearchTermProvider provider = new SearchTermProvider(title, bugReport);
        ArrayList<String> sentences = provider.getSentences();
//        MiscUtility.showItems(sentences);

        PositionNetworkMaker maker = new PositionNetworkMaker(sentences);
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph = maker.createWeightedPositNetwork();
//        HashMap<String, QueryToken> sortedTokendb = maker.getTokenDictionary(true);
//        maker.visualizeWordGraph(sortedTokendb, title);


        maker.showEdges();

//      Get the vertex set from the graph
        Set<String> vertices = wgraph.vertexSet();

//      Print the number of vertices
        System.out.println("\n");
        System.out.println("Number of vertices: " + vertices.size());
        System.out.println("Number of positBiasWeights: " + maker.positBiasWeights.size());
    }
}
