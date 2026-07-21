package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.CreateGameService;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;

public class CreateGameHandler {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");

        CreateGameRequest bodyOnly = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameRequest fullRequest = new CreateGameRequest(bodyOnly.gameName(), authToken);

        CreateGameResult createGameResult = createGameService.createGame(fullRequest);
        ctx.result(serializer.toJson(createGameResult));
    }
}
