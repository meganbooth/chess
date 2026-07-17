package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySqlUserDAOTest {
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    public void mySqlUserDAOSuccess() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData("username",
                "password","email")));
    }

    @Test
    public void mySqlUserDAOFail() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData("username",
                "password", "email")));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("username",
                "password", "email")));
    }
}
