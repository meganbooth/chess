package client;

public class PreloginClient implements Client{
    public String handleInput(String input) {
        return "prelogin received: " + input;
    }
}
