package strict.utility;

import strict.query.QueryToken;
import strict.query.TokenVector;

import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static void printHashMap(HashMap<String, QueryToken> hashMap) {
        for (Map.Entry<String, QueryToken> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            QueryToken queryToken = entry.getValue();
            System.out.println(key + " => ");
            System.out.println("Token: " + queryToken.token);
            System.out.println("TextRank Score: " + queryToken.textRankScore);
            System.out.println("POSRank Score: " + queryToken.posRankScore);
            System.out.println("SimRank Score: " + queryToken.simRankScore);
            System.out.println("BTextRank Score: " + queryToken.bTextRankScore);
            System.out.println("BTextRank Weight: " + queryToken.bTextRankBiasWeight);
//            System.out.println("Core Rank Score: " + queryToken.coreRankScore);
            System.out.println("Total Score: " + queryToken.totalScore);
//            System.out.println("Borda Score: " + queryToken.bordaScore);
        }
    }

    public static double[] convert2Vector(String[] vectorValues) {
        double[] vector = new double[vectorValues.length];
        for (int i = 0; i < vectorValues.length; i++) {
            vector[i] = Double.parseDouble(vectorValues[i]);
        }
        return vector;
    }

    public static TokenVector[] sentence2TokenVectors(String sentence) {
        String[] tokens = sentence.split("\n");
        TokenVector[] tokenVectors = new TokenVector[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String[] tokenAndVector = tokens[i].split("\t");
            String word = tokenAndVector[0];
            double[] vector = convert2Vector(tokenAndVector[1].split(" "));
            TokenVector tokenVector = new TokenVector(word, vector);
            tokenVectors[i] = tokenVector;
        }
        return tokenVectors;
    }
}
