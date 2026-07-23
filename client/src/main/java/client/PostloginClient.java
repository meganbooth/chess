package client;

import java.util.Scanner;

public class PostloginClient implements Client{
    public ServerFacade facade = new ServerFacade(8080);

    private String authToken = null;
    private boolean quit = false;
    private boolean switchClient = false;

    Scanner scanner = new Scanner(System.in);

    public String handleInput(String input) {
        return "postlogin received: " + input;
    }

    public boolean shouldQuit() {
        return quit;
    }
    public boolean shouldSwitchClient() {
        return switchClient;
    }
    public String getAuthToken() {
        return authToken;
    }
}
