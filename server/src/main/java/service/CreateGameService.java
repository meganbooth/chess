package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String authToken = createGameRequest.authToken();
        if (createGameRequest.gameName() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if(authDAO.getAuth(authToken) != null){
            int gameID = gameDAO.getNextGameID();
            GameData game = new GameData(gameID, null, null,
                    createGameRequest.gameName(), new ChessGame());
            gameDAO.createGame(game);
            return new CreateGameResult(gameID);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
