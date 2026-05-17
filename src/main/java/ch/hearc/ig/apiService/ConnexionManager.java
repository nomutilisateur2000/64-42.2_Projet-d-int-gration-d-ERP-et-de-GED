package ch.hearc.ig.connexionService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class ConnexionManager {
    Connexion connexion = new Connexion();

    private final HttpClient client = HttpClient.newHttpClient();

    public String login(String username, String password) throws Exception {

        String json = """
        {
            "username": "%s",
            "password": "%s"
        }
        """.formatted(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.exemple.com/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed: " + response.body());
        }

        return response.body();
    }
}
