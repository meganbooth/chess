package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
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
        UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);
        switch(command.commandType) {
            case CONNECT -> connect(session,command);
        }
    }

    public void connect(Session session, UserGameCommand command) throws Exception {
        AuthData authData = mySqlAuthDAO.getAuth(command.authToken);
        if(authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }

        GameData gameData = mySqlGameDAO.getGame(command.gameID);
        if(gameData == null) {
            sendMessage(session, new ErrorMessage("Error: game doesn't exist"));
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

    public void sendMessage(Session session, ServerMessage message) throws Exception {
        var serializer = new Gson();
        String json = serializer.toJson(message);
        session.getRemote().sendString(json);
    }
}
