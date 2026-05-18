package ch.hearc.ig.apiService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public class FlowManager {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public FlowManager() {
    }

    public Set<Integer> getValidatedReceipt(Connexion connexion) throws Exception {
        Set<Integer> receipt = new HashSet<>();

        String json = """
                {
                    "searchPattern": "QB_FLUX|l01|Validée|list",
                    "contentTypeIDs": "247"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://157.26.83.80:2240/api/search/advanced"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "bearer " + connexion.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Search failed: " + response.body());
        }


        return extractReceiptID(response.body());
    }

    public Set<Integer> extractReceiptID(String jsonResponse) throws Exception {
        Set<Integer> receipt = new HashSet<>();

        JsonNode array = mapper.readTree(jsonResponse);

        for (JsonNode node : array) {
            receipt.add(node.get("ObjectID").asInt());
        }

        return receipt;
    }

    public boolean integrate(Set<Integer> receipt, Connexion connexion) throws Exception {

        for (Integer i : receipt) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://157.26.83.80:2240/api/flow/validate/" + i))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "bearer " + connexion.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Integrate failed for ID " + i + ": " + response.body());
            }
        }
        return true;
    }
}
