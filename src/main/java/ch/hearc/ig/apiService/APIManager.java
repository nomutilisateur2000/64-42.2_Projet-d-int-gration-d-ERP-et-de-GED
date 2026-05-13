package ch.hearc.ig.apiService;

package ch.hearc.ig.scl.service.apiService;

import ch.hearc.ig.scl.tools.EnvProperties;
import java.io.IOException;
import java.net.*;
import java.net.http.*;

public class APIManager {

    public APIManager() {}

    public static HttpResponse<String> callAPI(String token){
        HttpResponse<String> response = null;
        // Créer un client HTTP
        HttpClient client = HttpClient.newHttpClient();
        // Construire une requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("" + token))
                .build();
        // Envoyer la requête et obtenir la réponse
        try {
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    }
}

