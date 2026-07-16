package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.request.JoinGameRequest;

public class JoinGameHandler {
    private final service.JoinGameService joinGameService;

    public JoinGameHandler(service.JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");

        JoinGameRequest bodyOnly = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        JoinGameRequest fullRequest = new JoinGameRequest(bodyOnly.playerColor(), bodyOnly.gameID(), authToken);

        joinGameService.joinGame(fullRequest);
        ctx.result("{}");

    }
}
