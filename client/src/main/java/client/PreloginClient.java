package client;

import java.util.Scanner;

public class PreloginClient implements Client{
    public ServerFacade facade = new ServerFacade(8080);
    private boolean quit = false;
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
                quit = true;
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
                    facade.register(username,password,email);
                    yield "Account successfully created";
                } catch(Exception e) {
                    yield "Error: Account not created. That username may already be taken.";
                }
            }
            case "login" -> "login";
            default -> "other";
        };
    }
    public boolean shouldQuit() {
        return quit;
    }
}
