package strict.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import strict.ca.usask.cs.srlab.strict.config.StaticData;

public class GloveWordVectors {
    private final Map<String, double[]> wordVectors;

    public GloveWordVectors() {
        wordVectors = new HashMap<>();
        loadWordVectors();
    }

    private void loadWordVectors() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(StaticData.WORD_VECTOR_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                String word = values[0];
                double[] vector = new double[100];
                for (int i = 0; i < 100; i++) {
                    vector[i] = Double.parseDouble(values[i + 1]);
                }
                wordVectors.put(word, vector);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] getWordVector(String word) {
        return wordVectors.get(word);
    }
}
