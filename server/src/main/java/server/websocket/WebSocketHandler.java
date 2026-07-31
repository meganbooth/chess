package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler {
    private final MySqlAuthDAO mySqlAuthDAO;
    private final MySqlGameDAO mySqlGameDAO;
    private final ConnectionManager connectionManager;

    public WebSocketHandler(MySqlAuthDAO mySqlAuthDAO, MySqlGameDAO mySqlGameDAO, ConnectionManager connectionManager) {
        this.mySqlAuthDAO = mySqlAuthDAO;
        this.mySqlGameDAO = mySqlGameDAO;
        this.connectionManager = connectionManager;
    }

    public void handle(String message, Session session) throws Exception {
        var serializer = new Gson();
        UserGameCommand userGameCommand = serializer.fromJson(message, UserGameCommand.class);
        switch(userGameCommand.commandType) {
            case CONNECT -> connect(session,userGameCommand);
            case MAKE_MOVE -> {
                MakeMoveCommand makeMoveCommand = serializer.fromJson(message, MakeMoveCommand.class);
                makeMove(session,makeMoveCommand);
            }
        }
    }

    public void connect(Session session, UserGameCommand command) throws Exception {
        AuthData authData = mySqlAuthDAO.getAuth(command.authToken);
        if (validateAuthToken(session, authData)) {
            return;
        }

        GameData gameData = mySqlGameDAO.getGame(command.gameID);
        if (validateGame(session, gameData)) {
            return;
        }

        connectionManager.add(command.gameID, session);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
        sendMessage(session, loadGameMessage);

        if(authData.username().equals(gameData.whiteUsername())) {
            connectionManager.broadcast(command.gameID,
                    new NotificationMessage(authData.username() + " connected as white"), session);
        } else if(authData.username().equals(gameData.blackUsername())){
            connectionManager.broadcast(command.gameID,
                    new NotificationMessage(authData.username() + " connected as black"), session);
        } else {
            connectionManager.broadcast(command.gameID,
                    new NotificationMessage(authData.username() + " connected as an observer"), session);
        }
    }

    public void makeMove(Session session, MakeMoveCommand command) throws Exception {
        AuthData authData = mySqlAuthDAO.getAuth(command.authToken);
        if (validateAuthToken(session, authData)) {
            return;
        }

        GameData gameData = mySqlGameDAO.getGame(command.gameID);
        if (validateGame(session, gameData)) {
            return;
        }

        boolean playerIsWhite = authData.username().equals(gameData.whiteUsername());
        boolean playerIsBlack = authData.username().equals(gameData.blackUsername());
        boolean turnIsWhite = gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE;
        boolean turnIsBlack = gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK;

        if (playerIsWhite && turnIsWhite || playerIsBlack && turnIsBlack) {
            try {
                gameData.game().makeMove(command.move);
            } catch(InvalidMoveException e) {
                sendMessage(session, new ErrorMessage("Error: invalid move"));
                return;
            }
            mySqlGameDAO.updateGame(gameData);
            connectionManager.broadcast(command.gameID, new LoadGameMessage(gameData), null);
            connectionManager.broadcast(command.gameID, new NotificationMessage(authData.username() + " moved from "
                    + command.move.getStartPosition().getRow() + "," + command.move.getStartPosition().getColumn()
                    + " to " + command.move.getEndPosition().getRow() + ","
                    + command.move.getEndPosition().getColumn()), session);

            if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("White in check"), null);
            } else if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("Black in check"), null);
            } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("White in checkmate"), null);
            } else if (gameData.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("Black in checkmate"), null);
            } else if (gameData.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("White in stalemate"), null);
            } else if (gameData.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
                connectionManager.broadcast(command.gameID, new NotificationMessage("Black in stalemate"), null);
            }

        } else if (playerIsWhite && !turnIsWhite || playerIsBlack && !turnIsBlack) {
            sendMessage(session, new ErrorMessage("Error: not your turn"));
            return;
        } else {
            sendMessage(session, new ErrorMessage("Error: you are observing"));
            return;
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws Exception {
        var serializer = new Gson();
        String json = serializer.toJson(message);
        session.getRemote().sendString(json);
    }

    private boolean validateGame(Session session, GameData gameData) throws Exception {
        if(gameData == null) {
            sendMessage(session, new ErrorMessage("Error: game doesn't exist"));
            return true;
        }
        return false;
    }

    private boolean validateAuthToken(Session session, AuthData authData) throws Exception {
        if(authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return true;
        }
        return false;
    }
}
