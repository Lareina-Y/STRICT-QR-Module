package strict.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import strict.query.QueryToken;

import java.util.HashMap;

public class PositionRankManager {

    SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> positGraph;
    HashMap<String, QueryToken> tokendb;

    public PositionRankManager(SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> positGraph, HashMap<String, QueryToken> tokendb) {
        this.positGraph = positGraph;
        this.tokendb = tokendb;
    }

    protected HashMap<String, QueryToken> calculatePositRankScores() {
        // collect query token scores
        PositionRankProvider positProvider = new PositionRankProvider(this.positGraph, this.tokendb);
        return positProvider.calculatePositionRank();
    }

    public HashMap<String, QueryToken> getPositionRank() {
        HashMap<String, QueryToken> positRankMap = new HashMap<>();
        positRankMap = calculatePositRankScores();
        ScoreFilterManager filter = new ScoreFilterManager(positRankMap, "PTR");
        return filter.applyFilters();
    }

}
