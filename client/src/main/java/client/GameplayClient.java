package client;

public class GameplayClient implements Client{
    public String handleInput(String input) {
        return "gameplay received: " + input;
    }
}
