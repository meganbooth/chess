package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    Map<Integer, Set<Session>> connections = new HashMap<>();
    public void add(int gameID, Session session) {
        if(connections.containsKey(gameID)) {
            connections.get(gameID).add(session);
        } else {
            Set<Session> gameSessions = new HashSet<>();
            gameSessions.add(session);
            connections.put(gameID, gameSessions);
        }
    }

    public void remove(int gameID, Session session) {
        if(connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(int gameID, ServerMessage message, Session excludeSession) throws Exception {
        var serializer = new Gson();
        String json = serializer.toJson(message);

        if (!connections.containsKey(gameID)) {
            return;
        }

        Set<Session> gameSessions = connections.get(gameID);
        for (Session session : gameSessions) {
            if (session == excludeSession) {
                continue;
            }
            try {
                synchronized (session) {
                    session.getRemote().sendString(json);
                }
            } catch (Exception e) {
                System.out.println("Failed to send to a session: " + e.getMessage());
            }
        }
    }

    public void removeSession(Session session) {
        for (Set<Session> gameSessions : connections.values()) {
            gameSessions.remove(session);
        }
    }
}
