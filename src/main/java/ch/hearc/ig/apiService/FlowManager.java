package ch.hearc.ig.apiService;

import ch.hearc.ig.business.Receipt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlowManager {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public FlowManager() {
    }

    public Map<Integer, Receipt> getValidatedReceipt(Connexion connexion) throws Exception {
        Map<Integer, Receipt> receipt = new HashMap<>();

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


        return extractReceiptMetaData(response.body());
    }

    public Map<Integer, Receipt> extractReceiptMetaData(String jsonResponse) throws Exception {

        Map<Integer, Receipt> receipts = new HashMap<>();

        JsonNode rootArray = mapper.readTree(jsonResponse);

        for (JsonNode node : rootArray) {

            Receipt receipt = new Receipt();

            Integer id = node.get("ObjectID").asInt();
            receipt.setId(id);

            JsonNode fields = node.get("Fields");

            for (JsonNode field : fields) {

                String code = field.get("Code").asText();
                String value = field.get("Value").asText();

                switch (code) {

                    case "QB_Total":
                        if (!value.isEmpty()) {
                            receipt.setAmount(Double.parseDouble(value));
                        }
                        break;

                    case "QB_DESC":
                        receipt.setDescription(value);
                        break;

                    case "QB_TYPE":
                        receipt.setReceiptType(value);
                        break;

                    case "QB_EME":
                        receipt.setReceiptIssuer(value);
                        break;

                    case "QB_Validateur":
                        receipt.setValidator(value);
                        break;

                    case "QB_Créateur":
                        receipt.setRequestCreator(value);
                        break;

                    case "QB_DATE_DEMANDE":
                        receipt.setRequestDate(value);
                        break;

                    case "QB_DATE_DEP":
                        receipt.setDateReceipt(parseDate(value));
                        break;
                }
            }

            receipts.put(id, receipt);
        }

        return receipts;
    }

    private Date parseDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            return sdf.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean integrate(Map<Integer, Receipt> receipt, Connexion connexion) throws Exception {
        if (receipt.isEmpty()) {
            return false;
        }

        for (Integer i : receipt.keySet()) {
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
