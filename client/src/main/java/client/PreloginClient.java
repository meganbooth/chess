package client;

import model.result.LoginResult;
import model.result.RegisterResult;

import java.util.Scanner;

public class PreloginClient implements Client{
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;

    Scanner scanner = new Scanner(System.in);

    public String handleInput(String input) {
        return switch (input) {
            case "help" ->
                    """
                    Available commands:
                      register - create a new account
                      login - sign in to an existing account
                      quit - exit the program
                      help - show this menu
                    """;
            case "quit" -> {
                switchBackward = true;
                yield "Thanks for playing!";
            }
            case "register" -> {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                try {
                    RegisterResult result = facade.register(username,password,email);
                    authToken = result.authToken();
                    switchForward = true;
                    yield "Account successfully created";
                } catch(Exception e) {
                    yield "Error: Account not created. That username may already be taken.";
                }
            }
            case "login" -> {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                try {
                    LoginResult result = facade.login(username,password);
                    authToken = result.authToken();
                    switchForward = true;
                    yield "Welcome";
                } catch(Exception e) {
                    yield "Error: Incorrect username or password.";
                }
            }
            default -> """
                    Command not recognized.
                    Available commands:
                      register - create a new account
                      login - sign in to an existing account
                      quit - exit the program
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
