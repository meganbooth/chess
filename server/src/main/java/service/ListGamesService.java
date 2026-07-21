package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.request.ListGamesRequest;
import model.result.ListGamesResult;

import java.util.Collection;

public class ListGamesService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGamesService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.authToken();
        model.AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }
}
