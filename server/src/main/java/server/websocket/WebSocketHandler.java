package handler;

import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import org.eclipse.jetty.server.session.Session;
import server.websocket.ConnectionManager;
import websocket.commands.UserGameCommand;
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

    public void handle(Context ctx) throws Exception {
        // deserialize message into user game command
        //read game type
        // route to appropriate method
    }

    public void connect(Session session, UserGameCommand command) throws Exception {
        // validates authtoken and gets user name or sends error
        // validates game id or sends error
        // register session in connection manager
        // sends load game back to this session
        // determine if white, black or observer
        // boradcast a notification
    }

    public void sendMessage(Session session, ServerMessage message) throws Exception {
        // serialize to json
        // sends directly to session
    }
}
