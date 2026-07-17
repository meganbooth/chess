package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySqlAuthDAOTest {
    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("username","password","email"));
        authDAO.createAuth(new AuthData("authToken", "username"));
        assertDoesNotThrow(() -> authDAO.clear());
    }

    @Test
    public void createAuthSuccess() throws DataAccessException{
        userDAO.createUser(new UserData("username","password","email"));
        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("authToken","username")));
    }

    @Test
    public void createAuthFail() throws DataAccessException {
        userDAO.createUser(new UserData("username","password","email"));
        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData("authToken","username")));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData(
                "authToken","username")));
    }
}
