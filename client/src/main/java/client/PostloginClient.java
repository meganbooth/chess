package client;

import model.result.LoginResult;
import model.result.RegisterResult;

import java.util.Scanner;

public class PostloginClient implements Client {
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;

    Scanner scanner = new Scanner(System.in);

    public PostloginClient(String authToken) {
        this.authToken = authToken;
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> """
                    Available commands:
                      list - list all existing games
                      create - create a new game
                      join - join an existing game
                      observe - watch a game
                      logout - exit to login menu
                      help - show this menu
                    """;
            case "logout" -> {
                try {
                    facade.logout(authToken);
                } catch (Exception e) {
                    // ignore, proceed regardless
                }
                switchBackward = true;
                yield "See you later!";
            }
            case "observe" -> "observe";
            case "join" -> "join";
            case "create" -> "create";
            case "list" -> "list";
            default -> """
                    Command not recognized.
                    Available commands:
                      list - list all existing games
                      create - create a new game
                      join - join an existing game
                      observe - watch a game
                      logout - exit to login menu
                      help - show this menu
                    """;
        };
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
