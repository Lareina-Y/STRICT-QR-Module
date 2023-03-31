package strict.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.QueryToken;

import java.util.HashMap;
import java.util.Set;

public class SimRankProvider {

    public DirectedGraph<String, DefaultEdge> graph;
    HashMap<String, QueryToken> tokendb;
    HashMap<String, Double> oldScoreMap;
    HashMap<String, Double> newScoreMap;
    final double INITIAL_VERTEX_SCORE = StaticData.INITIAL_TERM_WEIGHT;
    final double DAMPING_FACTOR = StaticData.DAMPING_FACTOR;
    final int MAX_ITERATION = StaticData.MAX_ITERATION;

    public SimRankProvider(DirectedGraph<String, DefaultEdge> graph, HashMap<String, QueryToken> tokendb) {
        // un-weighted graph constructor
        this.graph = graph;
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
        for (String vertex : graph.vertexSet()) {
            oldScoreMap.put(vertex, this.INITIAL_VERTEX_SCORE);
            newScoreMap.put(vertex, this.INITIAL_VERTEX_SCORE);
        }
    }

    public HashMap<String, QueryToken> calculateSimRank() {
        // calculating token rank score
        double d = this.DAMPING_FACTOR;

        // initially putting 0.25 to all
        this.initializeGraphBasic();

        boolean enoughIteration = false;
        int itercount = 0;

        while (!enoughIteration) {
            int insignificant = 0;
            for (String vertex : graph.vertexSet()) {
                Set<DefaultEdge> incomings = graph.incomingEdgesOf(vertex);
                // now calculate the SR score
                double trank = (1 - d);
                double comingScore = 0;
                for (DefaultEdge edge : incomings) {
                    String source1 = graph.getEdgeSource(edge);
                    int outdegree = graph.outDegreeOf(source1);

                    // score and out degree should be affected by the edge
                    // weight
                    double score = oldScoreMap.get(source1);
                    // score=score*this.EDGE_WEIGHT_TH;

                    if (outdegree == 1)
                        comingScore += score;
                    else if (outdegree > 1)
                        comingScore += (score / outdegree);
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
            if (insignificant == graph.vertexSet().size())
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
            qtoken.simRankScore = score;
            tokendb.put(key, qtoken);
        }
    }

}
