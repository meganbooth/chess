package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.ClearHandler;
import handler.LoginHandler;
import handler.LogoutHandler;
import handler.RegisterHandler;
import io.javalin.*;
import service.ClearService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

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
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
