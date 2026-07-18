package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void clearSuccess() {
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
}
