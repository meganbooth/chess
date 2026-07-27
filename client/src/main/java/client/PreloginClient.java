package client;

import model.result.LoginResult;
import model.result.RegisterResult;

public class PreloginClient extends AbstractClient {
    public ServerFacade facade = new ServerFacade(8080);
    private static final String HELP_TEXT = """
        Available commands:
          register - create a new account
          login - sign in to an existing account
          quit - exit the program
          help - show this menu
        """;

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> HELP_TEXT;
            case "quit" -> {
                switchBackward = true;
                yield "Thanks for playing!";
            }
            case "register" -> {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                if (username.isBlank()) {
                    yield "Error: username cannot be empty.";
                }
                System.out.print("Password: ");
                String password = scanner.nextLine();
                if (password.isBlank()) {
                    yield "Error: password cannot be empty.";
                }
                System.out.print("Email: ");
                String email = scanner.nextLine();
                if (email.isBlank()) {
                    yield "Error: email cannot be empty.";
                }
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
            default -> "Command not recognized.\n" + HELP_TEXT;
        };
    }
}
