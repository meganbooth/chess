package client;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    private <T> T makeRequest(String httpMethod, String endpoint, Object requestBody, Class<T> resultClass) throws Exception {
        URL url = new URI(serverUrl + endpoint).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(httpMethod);
        http.setDoOutput(true);

        if (requestBody != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String jsonBody = new Gson().toJson(requestBody);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(jsonBody.getBytes());
            }
        }

        http.connect();

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            if (resultClass != null) {
                return new Gson().fromJson(reader, resultClass);
            }
            return null;
        }
    }
}
