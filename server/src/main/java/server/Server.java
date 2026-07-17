package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final MemoryUserDAO memoryUserDAO;
    private final MemoryAuthDAO memoryAuthDAO;
    private final MemoryGameDAO memoryGameDAO;

    public Server() throws  DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();

        memoryUserDAO = new MemoryUserDAO();
        memoryAuthDAO = new MemoryAuthDAO();
        memoryGameDAO = new MemoryGameDAO();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        var clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", ctx -> clearHandler.handle(ctx));

        var registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
        var registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", ctx -> registerHandler.handle(ctx));

        var loginService = new LoginService(memoryUserDAO, memoryAuthDAO);
        var loginHandler = new LoginHandler(loginService);
        javalin.post("/session", ctx -> loginHandler.handle(ctx));

        var logoutService = new LogoutService(memoryAuthDAO);
        var logoutHandler = new LogoutHandler(logoutService);
        javalin.delete("/session", ctx -> logoutHandler.handle(ctx));

        var listGamesService = new ListGamesService(memoryAuthDAO, memoryGameDAO);
        var listGamesHandler = new ListGamesHandler(listGamesService);
        javalin.get("/game", ctx -> listGamesHandler.handle(ctx));

        var createGameService = new CreateGameService(memoryAuthDAO, memoryGameDAO);
        var createGameHandler = new CreateGameHandler(createGameService);
        javalin.post("/game", ctx -> createGameHandler.handle(ctx));

        var joinGameService = new JoinGameService(memoryAuthDAO, memoryGameDAO);
        var joinGameHandler = new JoinGameHandler(joinGameService);
        javalin.put("/game", ctx -> joinGameHandler.handle(ctx));

        javalin.exception(DataAccessException.class, (exception, ctx) -> {
            String message = exception.getMessage();
            if (message.equals("Error: unauthorized")) {
                ctx.status(401);
            } else if (message.equals("Error: bad request")) {
                ctx.status(400);
            } else if (message.equals("Error: already taken")) {
                ctx.status(403);
            } else {
                ctx.status(500);
            }
            var serializer = new Gson();
            ctx.result(serializer.toJson(java.util.Map.of("message", message)));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
