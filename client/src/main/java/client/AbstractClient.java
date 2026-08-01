package client;

import java.util.Scanner;

public class AbstractClient implements Client {
    Scanner scanner = new Scanner(System.in);
    protected String authToken = null;
    protected String selectedColor;

    protected int gameID;
    protected boolean switchForward = false;
    protected boolean switchBackward = false;

    public AbstractClient() {
    }

    public AbstractClient(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String handleInput(String input) {
        return "";
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

    public String getColor() {
        return selectedColor;
    }

    public int getGameID() {
        return gameID;
    }
}
