package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.request.JoinGameRequest;
import model.result.JoinGameResult;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String authToken = joinGameRequest.authToken();
        model.AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        int gameID = joinGameRequest.gameID();
        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String authUsername = auth.username();
        GameData updatedGame;
        if (joinGameRequest.playerColor() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK")) {
            throw new DataAccessException("Error: bad request");
        }
        if (joinGameRequest.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            updatedGame = new GameData(game.gameID(), authUsername, game.blackUsername(),
                    game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), authUsername,
                    game.gameName(), game.game());
        }

        gameDAO.updateGame(updatedGame);
        return new JoinGameResult();
    }
}
