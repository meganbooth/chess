package client;

import com.google.gson.Gson;
import model.request.*;
import model.result.*;

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

    private <T> T makeRequest(String httpMethod, String endpoint, Object requestBody,
                              Class<T> resultClass, String authToken) throws Exception {
        URL url = new URI(serverUrl + endpoint).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(httpMethod);
        http.setDoOutput(true);

        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }

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

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username,password,email);
        return makeRequest("POST", "/user", request, RegisterResult.class, null);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username,password);
        return makeRequest("POST", "/session", request, LoginResult.class, null );
    }

    public LogoutResult logout(String authToken) throws Exception {
        return makeRequest("DELETE", "/session", null, null, authToken);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws Exception {
        CreateGameRequest request = new CreateGameRequest(gameName, authToken);
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
    }

    public JoinGameResult joinGame(String playerColor, int gameID, String authToken) throws Exception {
        JoinGameRequest request = new JoinGameRequest(playerColor,gameID,authToken);
        return makeRequest("PUT", "/game", request, JoinGameResult.class, authToken);
    }
}
