package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
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

    
}
