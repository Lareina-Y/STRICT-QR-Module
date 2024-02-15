package strict.query;

public class TokenVector {

    public String token;
    public double[] vector;

    public TokenVector(String token, double[] vector) {
        this.token = token;
        this.vector = vector;
    }

}
