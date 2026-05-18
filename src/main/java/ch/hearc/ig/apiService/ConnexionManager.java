package ch.hearc.ig.apiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnexionManager {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ConnexionManager() {}

    public Connexion login(String username, String password) throws Exception {

        // Format URL-encoded, pas JSON
        String formData = "grant_type=password"
                + "&username=" + username
                + "&password=" + password;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://157.26.83.80:2240/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed: " + response.body());
        }

        Connexion connexion = new Connexion();
        connexion.setToken(extractToken(response.body()));
        return connexion;
    }

    public String extractToken(String jsonResponse) throws Exception {

        JsonNode node = mapper.readTree(jsonResponse);

        return node.get("access_token").asText();
    }
}
