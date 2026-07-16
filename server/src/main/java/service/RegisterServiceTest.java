package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterServiceTest {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess(){
        assertDoesNotThrow(() -> registerService.register(new RegisterRequest(
                "username", "password", "email")));
    }

    @Test
    public void registerFail() throws DataAccessException {
        registerService.register(new RegisterRequest("username", "password", "email"));
        assertThrows(DataAccessException.class, () -> registerService.register(new RegisterRequest(
                "username", "password", "email")));
    }
}
