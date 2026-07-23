package client;

public class PreloginClient implements Client{
    private boolean quit = false;
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
            case "register" -> "register";
            case "login" -> "login";
            default -> "other";
        };
    }
    public boolean shouldQuit() {
        return quit;
    }
}
