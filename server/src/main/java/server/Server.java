package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handler.ClearHandler;
import handler.RegisterHandler;
import io.javalin.*;
import service.ClearService;
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
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
