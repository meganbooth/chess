package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import service.request.LoginRequest;
import service.result.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!user.password().equals(loginRequest.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        model.AuthData auth = new model.AuthData(authToken, loginRequest.username());
        authDAO.createAuth(auth);

        return new LoginResult(loginRequest.username(), authToken);
    }
}
