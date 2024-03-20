import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.query.evaluation.RepoRankMaker;
import strict.utility.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParameterTuning {

    public static void main(String[] args) throws IOException {
        // Step 1: Initialization (POSRank > TextRank > SimRank > B-TextRank > PositiionRank)
        // TR, PR, SP, BTR, PTR
        double[] parameters = {4, 5, 3, 2, 1}; // Initial parameter values 5, 5, 1, 4, 0
        List<Integer> excludeIndex = Arrays.asList(); // 0:TR, 1:PR, 2:SP, 3:BTR, 4:PTR

        int itercount = 0;
        int MAXITER = 1000;
        double lr = 0.1; // weight change
        double fitVal_best = Double.NEGATIVE_INFINITY;
        double maxWeight = 100; // TODO: not sure what values is normal?
        double minWeight = 0;

        // Step 2: Parameter Refinement
        while (true && itercount < MAXITER) {
            System.out.println("Iter Count: " + itercount + ", Parameters: " + Arrays.toString(parameters));
            double fitVal_best_iter = Double.NEGATIVE_INFINITY;
            double[] bestParams = new double[parameters.length];

            for (int i = 0; i < parameters.length && !excludeIndex.contains(i); i++) {
                double[] newParamsUp = parameters.clone();
                double[] newParamsDn = parameters.clone();

                newParamsUp[i] = Math.min(parameters[i] + lr, maxWeight);
                newParamsDn[i] = Math.max(parameters[i] - lr, minWeight);

                double fitVal_up = fitness(newParamsUp);
                double fitVal_dn = fitness(newParamsDn);

                System.out.println("newParamsUp: " + Arrays.toString(newParamsUp) + ", fitVal_up: " + fitVal_up);
                System.out.println("newParamsDn: " + Arrays.toString(newParamsDn) + ", fitVal_dn: " + fitVal_dn);

                if (fitVal_up > fitVal_best_iter) {
                    fitVal_best_iter = fitVal_up;
                    bestParams = newParamsUp.clone();
                }

                if (fitVal_dn > fitVal_best_iter) {
                    fitVal_best_iter = fitVal_dn;
                    bestParams = newParamsDn.clone();
                }
            }

            if (fitVal_best_iter > fitVal_best) {
                fitVal_best = fitVal_best_iter;
                parameters = bestParams.clone();
            } else {
                break;
            }

            itercount++;
        }

        System.out.println("itercount = " + itercount);
        System.out.println("Best Parameters:");
        for (int i = 0; i < parameters.length; i++) {
            System.out.println("Parameter " + (i + 1) + ": " + parameters[i]);
        }
        System.out.println("Best Fitness Value: " + fitVal_best);
    }

    public static double fitness(double[] parameters) {

        ArrayList<String> repos = ContentLoader.getAllLinesOptList("./repos/repos.txt");
        List<String> scoreKeyList = Arrays.asList("TR", "PR", "SR", "BTR", "PTR"); // "TR", "PR", "SR", "BTR", "PTR"
        StaticData.SIMILARITY_THRESHOLD = 0.5;
        StaticData.ADD_CODE_ELEM = false;
        StaticData.ADD_TITLE = true;

        StaticData.TR_alpha = parameters[0];
        StaticData.PR_beta = parameters[1];
        StaticData.SR_gamma = parameters[2];
        StaticData.BTR_delta = parameters[3];
        StaticData.PTR_epsilon = parameters[4];

        String resultKey =
            "TR_" + StaticData.TR_alpha +
            "_PR_" + StaticData.PR_beta +
            "_SR_" + StaticData.SR_gamma +
            "_STR_" + StaticData.BTR_delta +
            "_PTR_" + StaticData.PTR_epsilon +
            "_10" + (StaticData.ADD_TITLE ? "_title" : "") +  "_" + StaticData.SIMILARITY_THRESHOLD;

        int rankSum = 0;
        for (String repoName : repos) {
            ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
            String approachQueryFile = StaticData.HOME_DIR + "/Lareina/query-parameter-tuning/" + repoName + "/" + resultKey
                    + ".txt";
            String rankStoredFile = StaticData.HOME_DIR + "/Lareina/rank-parameter-tuning/" + repoName + "/" + resultKey
                    + ".txt";

            // Generate the suggested queries
            HashMap<Integer, String> queries =
                    new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueriesMapByTokenScoreMap();
//            ArrayList<String> queries =
//                    new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueriesByTokenScoreMap();
//            ContentWriter.writeContent(approachQueryFile, queries);
            System.out.println("Log: queries saved - " + repoName + " | " + approachQueryFile);

            // Get FirstGoldRank
//            HashMap<Integer, String> queryMap = QueryLoader.loadQueries(approachQueryFile, true);
            RepoRankMaker repoRankMaker = new RepoRankMaker(repoName, queries);
            ArrayList<String> ranks = repoRankMaker.collectQE();
//            repoRankMaker.setRankFile(rankStoredFile);
//            repoRankMaker.saveQE(ranks);
            System.out.println("Log: ranks saved - " + repoName + " | " + rankStoredFile);

            // Add the sum of ranks in each repo
            rankSum += repoRankMaker.RankSum;
        }
        return 1.0 / rankSum;
    }
}
