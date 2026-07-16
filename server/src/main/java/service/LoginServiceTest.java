package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LoginRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    public void setup() throws DataAccessException{
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);

        userDAO.createUser(new UserData("username","password","email"));
    }

    @Test
    public void loginSuccess(){
        assertDoesNotThrow(() -> loginService.login(new LoginRequest("username", "password")));
    }

    @Test
    public void loginFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> loginService.login(new LoginRequest("username", "wrongPassword")));
    }
}
