package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MySqlGameDAOTest {
    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1234, null, null, "gameName", new ChessGame()));
        assertDoesNotThrow(() -> gameDAO.clear());
    }

    @Test
    public void createGameSuccess() throws DataAccessException{
        assertDoesNotThrow(() -> gameDAO.createGame(new GameData(1234,null,
                null,"gameName", new ChessGame())));
    }

    @Test
    public void createGameFail() throws DataAccessException {
        assertDoesNotThrow(() -> gameDAO.createGame(new GameData(1234,null,
                null,"gameName", new ChessGame())));
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(new GameData(1234,null,
                null,"gameName", new ChessGame())));
    }

    @Test
    public void getGameSuccess() throws DataAccessException{
        gameDAO.createGame(new GameData(1234,null,
                null,"gameName", new ChessGame()));
        assertNotNull(gameDAO.getGame(1234));
    }

    @Test
    public void getGameFail() throws DataAccessException{
        assertNull(gameDAO.getGame(1234));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1234,null,
                null,"gameName1", new ChessGame()));
        gameDAO.createGame(new GameData(1235,null,
                null,"gameName2", new ChessGame()));
        gameDAO.createGame(new GameData(1236,null,
                null,"gameName3", new ChessGame()));
        var result = gameDAO.listGames();
        assertEquals(3, result.size());
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        var result = gameDAO.listGames();
        assertEquals(0, result.size());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("whiteUsername", "password", "email"));
        gameDAO.createGame(new GameData(1234, null, null, "gameName", new ChessGame()));
        gameDAO.updateGame(new GameData(1234, "whiteUsername", null, "gameName", new ChessGame()));
        var result = gameDAO.getGame(1234);
        assertEquals("whiteUsername", result.whiteUsername());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        assertDoesNotThrow(() -> gameDAO.updateGame(new GameData(1234, null, null, "gameName", new ChessGame())));
        assertNull(gameDAO.getGame(1234));
    }
}
