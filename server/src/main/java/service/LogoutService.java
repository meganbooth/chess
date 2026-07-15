package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import service.request.LogoutRequest;
import service.result.LogoutResult;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        model.AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        } else {
            authDAO.deleteAuth(authToken);
        }
        return new LogoutResult();
    }
}
