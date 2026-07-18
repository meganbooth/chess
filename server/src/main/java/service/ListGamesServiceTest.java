package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.ListGamesRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesServiceTest {
    private ListGamesService listGamesService;

    @BeforeEach
    public void setup() throws DataAccessException {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        listGamesService = new ListGamesService(authDAO, gameDAO);

        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        GameData game = new GameData(1234, "whiteUsername",
                "blackUsername", "game1", new ChessGame());
        gameDAO.createGame(game);
    }

    @Test
    public void listGamesSuccess() {
        assertDoesNotThrow(() -> listGamesService.listGames(new ListGamesRequest("authToken")));
    }

    @Test
    public void listGamesFail() {
        assertThrows(DataAccessException.class, () -> listGamesService.listGames(
                new ListGamesRequest("wrongAuthToken")));
    }
}
