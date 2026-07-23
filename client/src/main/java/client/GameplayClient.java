package client;

public class GameplayClient implements Client {
    private boolean quit = false;

    public String handleInput(String input) {
        return "gameplay received: " + input;
    }

    public boolean shouldQuit() {
        return quit;
    }
}