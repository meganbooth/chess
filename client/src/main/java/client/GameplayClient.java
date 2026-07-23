package client;

import java.util.Scanner;

public class GameplayClient implements Client {
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;

    Scanner scanner = new Scanner(System.in);

    public String handleInput(String input) {
        return "gameplay received: " + input;
    }

    public boolean shouldSwitchForward() {
        return switchForward;
    }
    public boolean shouldSwitchBackward() {
        return switchBackward;
    }
    public String getAuthToken() {
        return authToken;
    }
}