package strict.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;
import strict.query.TokenVector;
import strict.utility.*;

import javax.swing.*;
import java.util.*;

public class BiasedTextNetworkMaker {

    String repoName;
    int bugID;
    double[] titleVector;
    String[] sentences;
    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
    HashMap<String, QueryToken> tokendb;
    public HashMap<String, Double> biasWeights;

    public BiasedTextNetworkMaker(String repoName, int bugID) {
        // initialization of items
        this.repoName = repoName;
        this.bugID = bugID;
        this.wgraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        this.tokendb = new HashMap<>();
        this.biasWeights = new HashMap<>();
        this.titleVector = getBiasVector(repoName, bugID);
        this.sentences = BugReportLoader.loadEmbedReport(repoName, bugID);
    }

    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> createWeightedBiasedTextNetwork() {
        // create the edges in a subset of keywords (each sentence)
        if (this.sentences.length < 2) {
            throw new RuntimeException("Sentences length < 2: " + this.sentences.length + " " + repoName + " " + bugID);
        }
        for (int n = 1; n < this.sentences.length; n++) { // Skip the first sentence (Title)
            TokenVector[] tokens = Helper.sentence2TokenVectors(this.sentences[n]);
            for (int i = 0; i < tokens.length; i++) {
                TokenVector currentToken = tokens[i];

                for (int k = i+1; k < tokens.length; k++) {
                    TokenVector comparedToken = tokens[k];

                    // now add the graph nodes
                    if (!wgraph.containsVertex(currentToken.token)) {
                        wgraph.addVertex(currentToken.token);
                        double CSBiasWeight = CosineSimilarity.calculateCS(currentToken.vector, titleVector);
                        this.biasWeights.put(currentToken.token, CSBiasWeight);
                    }
                    if (!wgraph.containsVertex(comparedToken.token) && !comparedToken.token.isEmpty()) {
                        wgraph.addVertex(comparedToken.token);
                        double CSBiasWeight = CosineSimilarity.calculateCS(comparedToken.vector, titleVector);
                        this.biasWeights.put(comparedToken.token, CSBiasWeight);
                    }

                    if(!(currentToken.token.equals(comparedToken.token))) {
                        // calculate the Cosine Similarity
                        double[] vector1 = currentToken.vector;
                        double[] vector2 = comparedToken.vector;

                        if (vector1 != null && vector2 != null) {
                            double CSScore = CosineSimilarity.calculateCS(vector1, vector2);

                            // adding edges to the graph
                            if (CSScore > StaticData.SIMILARITY_THRESHOLD) {
                                if (!wgraph.containsEdge(currentToken.token, comparedToken.token)) {
                                    DefaultWeightedEdge edge1 = wgraph.addEdge(currentToken.token, comparedToken.token);
                                    wgraph.setEdgeWeight(edge1, CSScore);

                                    DefaultWeightedEdge edge2 = wgraph.addEdge(comparedToken.token, currentToken.token);
                                    wgraph.setEdgeWeight(edge2, CSScore);
                                }
                            }
                        }
                    }
                }
            }
        }
        return wgraph;
    }

    public double[] getBiasVector(String repoName, int bugID) {
        String bugReportPath = StaticData.HOME_DIR + "/ChangeReqs-Title-Emb/" + repoName + "/" + bugID + ".txt";
        String[] embeddedTitle = ContentLoader.getAllLines(bugReportPath)[1].split(" ");
        double[] biasVector = MiscUtility.strList2DoubleList(embeddedTitle);
        return biasVector;
    }

    public HashMap<String, QueryToken> getTokenDictionary() {
        // populating token dictionary
        HashSet<String> nodes = new HashSet<>();
        nodes.addAll(wgraph.vertexSet());

        for (String vertex : nodes) {
            QueryToken qtoken = new QueryToken();
            qtoken.token = vertex;
            this.tokendb.put(vertex, qtoken);
        }
        return this.tokendb;
    }

    protected void showEdges() {
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

    public void visualizeWordGraph(HashMap<String, QueryToken> tokendb) {
        // visualize the word net
        JFrame frame = new JFrame();

        StackGraph stackgraph = new StackGraph(wgraph, 900, 700);
        frame.getContentPane().add(stackgraph);
        frame.setVisible(true);
        frame.setSize(900, 700);
        stackgraph.init();
        stackgraph.start();
    }

    public static void main(String[] args) {
        String repoName = "ecf";
        StaticData.ADD_CODE_ELEM = false;
        StaticData.ADD_TITLE = true;
        StaticData.SIMILARITY_THRESHOLD = 0.4;
        Integer bugID = 125572;

        BiasedTextNetworkMaker maker = new BiasedTextNetworkMaker(repoName, bugID);
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph = maker.createWeightedBiasedTextNetwork();

        maker.showEdges();

//      Get the vertex set from the graph
        Set<String> vertices = wgraph.vertexSet();

//      Print the number of vertices
        System.out.println("\n");
        System.out.println("Number of vertices: " + vertices.size());
    }

}
