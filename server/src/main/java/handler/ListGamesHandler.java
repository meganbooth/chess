package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.request.ListGamesRequest;
import service.request.LoginRequest;
import service.result.ListGamesResult;

public class ListGamesHandler {
    private final service.ListGamesService listGamesService;

    public ListGamesHandler(service.ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = listGamesService.listGames(listGamesRequest);

        String json = serializer.toJson(listGamesResult);
        ctx.result(json);
    }
}
