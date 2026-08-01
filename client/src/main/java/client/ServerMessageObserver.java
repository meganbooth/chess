package client;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notifyUser(ServerMessage message);
}
