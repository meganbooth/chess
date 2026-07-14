package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.request.LoginRequest;
import service.result.LoginResult;

public class LoginHandler {
    private final service.LoginService loginService;

    public LoginHandler(service.LoginService loginService) {
        this.loginService = loginService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var serializer = new Gson();

        LoginRequest loginRequest = serializer.fromJson(ctx.body(),LoginRequest.class);
        LoginResult loginResult = loginService.login(loginRequest);
        String json = serializer.toJson(loginResult);

        ctx.result(json);
    }
}
