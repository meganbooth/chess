package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(userDAO.getUser(registerRequest.username()) == null){
            UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(user);
        } else {
            throw new DataAccessException("Error: already taken");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, registerRequest.username());
        authDAO.createAuth(auth);

        return new RegisterResult(registerRequest.username(), authToken);
    }
}
