package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LogoutRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutServiceTest {
    private LogoutService logoutService;

    @BeforeEach
    public void setup() throws DataAccessException {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);

        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
    }

    @Test
    public void logoutSuccess() {
        assertDoesNotThrow(() -> logoutService.logout(new LogoutRequest("authToken")));
    }

    @Test
    public void logoutFail() {
        assertThrows(DataAccessException.class, () -> logoutService.logout(
                new LogoutRequest("wrongAuthToken")));
    }
}
