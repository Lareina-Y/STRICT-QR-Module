package strict.utility;

public class CosineSimilarity {

    public CosineSimilarity() {}

    public static double calculateCS(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }


}
