package client;

import dataaccess.*;
import model.result.CreateGameResult;
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

    @Test
    public void logoutSuccess() throws Exception{
        RegisterResult result = facade.register("username","password","email");
        assertDoesNotThrow(() -> facade.logout(result.authToken()));
    }

    @Test
    public void logoutFail() {
        assertThrows(Exception.class, () -> facade.logout("wrongAuthToken"));
    }

    @Test
    public void createGameSuccess() throws Exception {
        RegisterResult result = facade.register("username","password","email");
        assertDoesNotThrow(() -> facade.createGame("gameName", result.authToken()));
    }

    @Test
    public void createGameFail() {
        assertThrows(Exception.class, () -> facade.createGame("gameName","wrongAuthToken"));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        RegisterResult registerResult = facade.register("username","password","email");
        CreateGameResult createGameResult = facade.createGame("gameName",registerResult.authToken());
        assertDoesNotThrow(() -> facade.joinGame("BLACK",createGameResult.gameID(), registerResult.authToken()));
    }

    @Test
    public void joinGameFail() throws Exception {
        RegisterResult user1 = facade.register("username","password","email");
        RegisterResult user2 = facade.register("username2","password2","email2");
        CreateGameResult createGameResult = facade.createGame("gameName",user1.authToken());

        // Unauthorized
        assertThrows(Exception.class, () -> facade.joinGame("BLACK",createGameResult.gameID(), "wrongAuthToken"));

        // Game does not exist
        assertThrows(Exception.class, () -> facade.joinGame("BLACK",1234, user1.authToken()));

        // Color already taken
        facade.joinGame("BLACK",createGameResult.gameID(), user1.authToken());
        assertThrows(Exception.class, () -> facade.joinGame("BLACK",createGameResult.gameID(), user2.authToken()));
    }
}
