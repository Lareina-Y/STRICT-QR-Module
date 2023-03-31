package strict.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import strict.query.QueryToken;
import strict.utility.Helper;

import java.util.HashMap;

public class SimilarityRankManager {

    DirectedGraph<String, DefaultEdge> simGraph;
    HashMap<String, QueryToken> tokendb;

    public SimilarityRankManager(DirectedGraph<String, DefaultEdge> simGraph, HashMap<String, QueryToken> tokendb) {
        this.simGraph = simGraph;
        this.tokendb = tokendb;
    }

    protected HashMap<String, QueryToken> calculateSimRankScores() {
        // collect query token scores
        SimRankProvider simProvider = new SimRankProvider(this.simGraph, tokendb);
        return simProvider.calculateSimRank();
    }

    public HashMap<String, QueryToken> getSIMRank() {
        HashMap<String, QueryToken> simRankMap = new HashMap<>();
        simRankMap = calculateSimRankScores();
        ScoreFilterManager filter = new ScoreFilterManager(simRankMap, "SR");
        return filter.applyFilters();
    }
}
