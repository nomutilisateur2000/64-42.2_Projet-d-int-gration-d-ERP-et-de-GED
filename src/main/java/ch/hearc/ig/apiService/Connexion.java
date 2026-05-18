package ch.hearc.ig.apiService;

public class Connexion {
    private String token;

    public Connexion() {}

    public Connexion(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
