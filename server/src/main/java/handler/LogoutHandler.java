package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.request.LogoutRequest;
import service.result.LogoutResult;

public class LogoutHandler {
    private final service.LogoutService logoutService;

    public LogoutHandler(service.LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void handle(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        logoutService.logout(new LogoutRequest(authToken));
        ctx.result("{}");
    }
}
