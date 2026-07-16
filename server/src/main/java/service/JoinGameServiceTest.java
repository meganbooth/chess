package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinGameServiceTest {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        joinGameService = new JoinGameService(authDAO,gameDAO);

        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        GameData game = new GameData(1234, null,
                "blackUsername", "game1", new ChessGame());
        gameDAO.createGame(game);
    }

    @Test
    public void joinGameSuccess() {
        assertDoesNotThrow(() -> joinGameService.joinGame(new JoinGameRequest("WHITE",
                1234,"authToken")));
    }

    @Test
    public void joinGameFail() {
        assertThrows(DataAccessException.class, () -> joinGameService.joinGame(new JoinGameRequest(
                "playerColor",1234,"wrongAuthToken")));
    }
}
