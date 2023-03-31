package strict.utility;

import strict.query.QueryToken;

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
            System.out.println("POS Rank Score: " + queryToken.posRankScore);
            System.out.println("Sim Rank Score: " + queryToken.simRankScore);
//            System.out.println("Core Rank Score: " + queryToken.coreRankScore);
            System.out.println("Total Score: " + queryToken.totalScore);
//            System.out.println("Borda Score: " + queryToken.bordaScore);
        }
    }



}
