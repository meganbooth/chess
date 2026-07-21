package client;

import dataaccess.*;
import model.result.LoginResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static ClearService clearService;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);

        UserDAO userDAO = new MySqlUserDAO();
        AuthDAO authDAO = new MySqlAuthDAO();
        GameDAO gameDAO = new MySqlGameDAO();
        clearService = new ClearService(userDAO,authDAO,gameDAO);
    }

    @BeforeEach
    public void clear() throws Exception {
        clearService.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerSuccess() throws Exception{
        RegisterResult result = facade.register("username","password","email");
        assertNotNull(result);
        assertNotNull(result.authToken());
    }

    @Test
    public void registerFail() throws Exception {
        facade.register("username","password","email");
        assertThrows(Exception.class, () -> facade.register("username","password","email"));
    }

    @Test
    public void loginSuccess() throws Exception{
        facade.register("username","password","email");
        assertDoesNotThrow(() -> facade.login("username","password"));
    }

    @Test
    public void loginFail() throws Exception {
        facade.register("username","password","email");
        assertThrows(Exception.class, () -> facade.login("username","wrongPassword"));
        assertThrows(Exception.class, () -> facade.login("wrongUsername","password"));
    }
}
