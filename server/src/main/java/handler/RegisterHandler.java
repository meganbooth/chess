package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.request.RegisterRequest;
import model.result.RegisterResult;

public class RegisterHandler {
    private final service.RegisterService registerService;

    public RegisterHandler(service.RegisterService registerService) {
        this.registerService = registerService;
    }

    public void handle(Context ctx) throws DataAccessException {
        var serializer = new Gson();

        RegisterRequest registerRequest = serializer.fromJson(ctx.body(),RegisterRequest.class);
        RegisterResult registerResult = registerService.register(registerRequest);
        String json = serializer.toJson(registerResult);

        ctx.result(json);
    }
}
