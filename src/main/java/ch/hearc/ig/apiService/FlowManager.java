package ch.hearc.ig.apiService;

import ch.hearc.ig.apiService.deserializer.FileDeserializer;
import ch.hearc.ig.apiService.deserializer.ReceiptDeserializer;
import ch.hearc.ig.business.AttachementFile;
import ch.hearc.ig.business.Receipt;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.*;

public class FlowManager {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;


    public FlowManager() {
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Receipt.class, new ReceiptDeserializer());
        this.mapper.registerModule(module);
    }

    public Map<Integer, Receipt> getValidatedReceipts(Connexion connexion)
            throws IOException, InterruptedException {

        String json = """
                {
                    "searchPattern": "QB_FLUX|l01|Validée|list",
                    "contentTypeIDs": "263"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://157.26.83.80:2240/api/search/advanced"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "bearer " + connexion.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Search failed: " + response.body());
        }

        List<Receipt> receipts = mapper.readValue(response.body(), new TypeReference<>() {});

        Map<Integer, Receipt> result = new HashMap<>();
        for (Receipt r : receipts) {
            result.put(r.getId(), r);
        }
        return result;
    }

    public boolean integrate(Map<Integer, Receipt> receipts, Connexion connexion)
            throws IOException, InterruptedException {

        if (receipts.isEmpty()) return false;

        for (Integer id : receipts.keySet()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://157.26.83.80:2240/api/flow/validate/" + id))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "bearer " + connexion.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Integrate failed for ID " + id + ": " + response.body());
            }
        }
        return true;
    }

    public Receipt getAttachment(Connexion connexion, Receipt receipt)
            throws IOException, InterruptedException {

        Integer id = receipt.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://157.26.83.80:2240/api/document/" + id + "/attachment/"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "bearer " + connexion.getToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Get attachment failed for ID " + id + ": " + response.body());
        }

        ObjectMapper mapper = new ObjectMapper();
        AttachementFile file = mapper.readValue(response.body(), AttachementFile.class);

        receipt.setFile(file);
        return receipt;
    }
}
