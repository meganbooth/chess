package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final MySqlUserDAO mySqlUserDAO;
    private final MySqlAuthDAO mySqlAuthDAO;
    private final MySqlGameDAO mySqlGameDAO;

    public Server() {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.configureDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException("failed to initialize database", ex);
        }

        mySqlUserDAO = new MySqlUserDAO();
        mySqlAuthDAO = new MySqlAuthDAO();
        mySqlGameDAO = new MySqlGameDAO();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var clearService = new ClearService(mySqlUserDAO, mySqlAuthDAO, mySqlGameDAO);
        var clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", ctx -> clearHandler.handle(ctx));

        var registerService = new RegisterService(mySqlUserDAO, mySqlAuthDAO);
        var registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", ctx -> registerHandler.handle(ctx));

        var loginService = new LoginService(mySqlUserDAO, mySqlAuthDAO);
        var loginHandler = new LoginHandler(loginService);
        javalin.post("/session", ctx -> loginHandler.handle(ctx));

        var logoutService = new LogoutService(mySqlAuthDAO);
        var logoutHandler = new LogoutHandler(logoutService);
        javalin.delete("/session", ctx -> logoutHandler.handle(ctx));

        var listGamesService = new ListGamesService(mySqlAuthDAO, mySqlGameDAO);
        var listGamesHandler = new ListGamesHandler(listGamesService);
        javalin.get("/game", ctx -> listGamesHandler.handle(ctx));

        var createGameService = new CreateGameService(mySqlAuthDAO, mySqlGameDAO);
        var createGameHandler = new CreateGameHandler(createGameService);
        javalin.post("/game", ctx -> createGameHandler.handle(ctx));

        var joinGameService = new JoinGameService(mySqlAuthDAO, mySqlGameDAO);
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
