package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

@ClientEndpoint
public class WebSocketFacade {
    private final Session session;
    private final ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws Exception {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");
        this.observer = observer;

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

    public void connect(String authToken, int gameID) {
        // connect
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        // makeMove
    }

    public void leave(String authToken, int gameID) {
        // leave
    }

    public void resign(String authToken, int gameID) {
        // resign
    }


}
