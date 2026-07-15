package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final MemoryUserDAO memoryUserDAO;
    private final MemoryAuthDAO memoryAuthDAO;
    private final MemoryGameDAO memoryGameDAO;

    public Server() {
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
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
