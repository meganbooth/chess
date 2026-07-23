package client;

public class PostloginClient implements Client{
    private boolean quit = false;

    public String handleInput(String input) {
        return "postlogin received: " + input;
    }

    public boolean shouldQuit() {
        return quit;
    }
}
