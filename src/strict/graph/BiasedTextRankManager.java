package strict.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import strict.query.QueryToken;

import java.util.HashMap;

public class BiasedTextRankManager {

    SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> bTextGraph;
    HashMap<String, QueryToken> tokendb;

    public BiasedTextRankManager(SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> bTextGraph, HashMap<String, QueryToken> tokendb) {
        this.bTextGraph = bTextGraph;
        this.tokendb = tokendb;
    }

    protected HashMap<String, QueryToken> calculateBTextRankScores() {
        // collect query token scores
        BiasedTextRankProvider bTextProvider = new BiasedTextRankProvider(this.bTextGraph, this.tokendb);
        return bTextProvider.calculateBTextRank();
    }

    public HashMap<String, QueryToken> getBTextRank() {
        HashMap<String, QueryToken> bTextRankMap = new HashMap<>();
        bTextRankMap = calculateBTextRankScores();
        ScoreFilterManager filter = new ScoreFilterManager(bTextRankMap, "BTR");
        return filter.applyFilters();
    }

}
