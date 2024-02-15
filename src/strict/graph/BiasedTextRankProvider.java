package strict.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;

import java.util.HashMap;
import java.util.Set;

public class BiasedTextRankProvider {

    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
    HashMap<String, QueryToken> tokendb;
    HashMap<String, Double> oldScoreMap;
    HashMap<String, Double> newScoreMap;
    final double INITIAL_VERTEX_SCORE = StaticData.INITIAL_TERM_WEIGHT;
    final double DAMPING_FACTOR = StaticData.DAMPING_FACTOR;
    final int MAX_ITERATION = StaticData.MAX_ITERATION;

    public BiasedTextRankProvider(SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph, HashMap<String, QueryToken> tokendb) {
        // weighted graph constructor
        this.wgraph = wgraph;
        this.tokendb = tokendb;
        this.oldScoreMap = new HashMap<>();
        this.newScoreMap = new HashMap<>();
    }

    boolean checkSignificantDiff(double oldV, double newV) {
        double diff = 0;
        if (newV > oldV)
            diff = newV - oldV;
        else
            diff = oldV - newV;
        return diff > StaticData.SIGNIFICANCE_THRESHOLD ? true : false;
    }


    protected void initializeGraphBasic() {
        // initially putting 0.25 to all
        for (String vertex : wgraph.vertexSet()) {
            oldScoreMap.put(vertex, this.INITIAL_VERTEX_SCORE);
            newScoreMap.put(vertex, this.INITIAL_VERTEX_SCORE);
        }
    }

    public HashMap<String, QueryToken> calculateBTextRank() {
        // calculating token rank score
        double d = this.DAMPING_FACTOR;

        // initially putting 0.25 to all
        this.initializeGraphBasic();

        boolean enoughIteration = false;
        int itercount = 0;

        while (!enoughIteration) {
            int insignificant = 0;
            for (String vertex : wgraph.vertexSet()) { // V_i
                double biasWeight = this.tokendb.get(vertex).bTextRankBiasWeight; //biasWeight_i
                Set<DefaultWeightedEdge> incomings = wgraph.incomingEdgesOf(vertex); //  Out(V_i)
                // now calculate the TBR score
                double trank = biasWeight * (1 - d); // biasWeight_i * (1 - d)
                double comingScore = 0;
                for (DefaultWeightedEdge edge : incomings) {
                    String source1 = wgraph.getEdgeSource(edge); // V_j
                    double sumOfWeights = 0.0;
                    Set<DefaultWeightedEdge> outgoings = wgraph.outgoingEdgesOf(source1); //  Out(V_j)
                    for (DefaultWeightedEdge outEdge : outgoings) { // V_k
                        sumOfWeights += wgraph.getEdgeWeight(outEdge); // W_jk
                    }
                    double edgeWeight = wgraph.getEdgeWeight(edge); // W_ji
                    double score = oldScoreMap.get(source1); // Score(V_j)

                    if (sumOfWeights == 0) {
                        comingScore += score;
                    } else {
                        comingScore += ((edgeWeight / sumOfWeights) * score);
                    }
                }

                comingScore = comingScore * d;
                trank += comingScore;
                boolean significant = checkSignificantDiff(oldScoreMap.get(vertex).doubleValue(), trank);
                if (significant) {
                    newScoreMap.put(vertex, trank);
                } else {
                    insignificant++;
                }
            }
            // coping values to new Hash Map
            for (String key : newScoreMap.keySet()) {
                oldScoreMap.put(key, newScoreMap.get(key));
            }
            itercount++;
            if (insignificant == wgraph.vertexSet().size())
                enoughIteration = true;
            if (itercount == this.MAX_ITERATION)
                enoughIteration = true;
        }
        recordNormalizeScores();
        return this.tokendb;
    }

    protected void recordNormalizeScores() {
        // record normalized scores
        double maxRank = 0;
        for (String key : newScoreMap.keySet()) {
            double score = newScoreMap.get(key).doubleValue();
            if (score > maxRank) {
                maxRank = score;
            }
        }
        for (String key : newScoreMap.keySet()) {
            double score = newScoreMap.get(key).doubleValue();
            score = score / maxRank;
            // this.newScoreMap.put(key, score);

            QueryToken qtoken = tokendb.get(key);
            qtoken.bTextRankScore = score;
            tokendb.put(key, qtoken);
        }
    }
}
