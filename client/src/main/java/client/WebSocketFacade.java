package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

@ClientEndpoint
public class WebSocketFacade {
    private final Session session;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws Exception {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                var serializer = new Gson();
                ServerMessage serverMessage = serializer.fromJson(message,ServerMessage.class);
                switch(serverMessage.serverMessageType) {
                    case LOAD_GAME -> {
                        LoadGameMessage loadGameMessage = serializer.fromJson(message,LoadGameMessage.class);
                        observer.notifyUser(loadGameMessage);
                    }
                    case NOTIFICATION -> {
                        NotificationMessage notificationMessage = serializer.fromJson(message,NotificationMessage.class);
                        observer.notifyUser(notificationMessage);
                    }
                    case ERROR -> {
                        ErrorMessage errorMessage = serializer.fromJson(message,ErrorMessage.class);
                        observer.notifyUser(errorMessage);
                    }
                }
            }
        });
    }

    public void connect(String authToken, int gameID) throws IOException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID);
        var serializer =  new Gson();
        String json = serializer.toJson(userGameCommand);
        session.getBasicRemote().sendText(json);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken,gameID,move);
        var serializer =  new Gson();
        String json = serializer.toJson(makeMoveCommand);
        session.getBasicRemote().sendText(json);
    }

    public void leave(String authToken, int gameID) throws IOException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID);
        var serializer =  new Gson();
        String json = serializer.toJson(userGameCommand);
        session.getBasicRemote().sendText(json);
    }

    public void resign(String authToken, int gameID) throws IOException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID);
        var serializer = new Gson();
        String json = serializer.toJson(userGameCommand);
        session.getBasicRemote().sendText(json);
    }


}
