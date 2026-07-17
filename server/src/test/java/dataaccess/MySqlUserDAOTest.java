package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserSuccess() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData("username",
                "password","email")));
    }

    @Test
    public void createUserFail() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData("username",
                "password", "email")));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("username",
                "password", "email")));
    }

    @Test
    public void getUserSuccess() throws DataAccessException{
        userDAO.createUser(new UserData("username","password", "email"));
        var result = userDAO.getUser("username");
        assertNotNull(result);
    }

    @Test
    public void getUserFail() throws DataAccessException{
        var result = userDAO.getUser("username");
        assertNull(result);
    }
}
