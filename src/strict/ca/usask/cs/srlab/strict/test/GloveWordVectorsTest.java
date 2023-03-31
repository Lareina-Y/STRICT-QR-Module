package strict.ca.usask.cs.srlab.strict.test;

import org.junit.Test;
import strict.utility.GloveWordVectors;
import strict.utility.CosineSimilarity;

import java.util.Arrays;

public class GloveWordVectorsTest {

    @Test
    public void testGetGloveWordVectors() {
        GloveWordVectors gloveWordVectors = new GloveWordVectors();
        double[] vector1 = gloveWordVectors.getWordVector("dog");
        double[] vector2 = gloveWordVectors.getWordVector("software");
        System.out.println(Arrays.toString(vector1));
        System.out.println(Arrays.toString(vector2));
        System.out.println(CosineSimilarity.calculateCS(vector1, vector2));
    }
}
