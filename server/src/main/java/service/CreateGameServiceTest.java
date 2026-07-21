package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.request.CreateGameRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameServiceTest {
    private CreateGameService createGameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        createGameService = new CreateGameService(authDAO, gameDAO);

        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
    }

    @Test
    public void createGameSuccess() {
        assertDoesNotThrow(() -> createGameService.createGame(new CreateGameRequest(
                "game1","authToken")));
    }

    @Test
    public void createGameFail() {
        assertThrows(DataAccessException.class, () -> createGameService.createGame(
                new CreateGameRequest("gameName","wrongAuthToken")));
    }
}
