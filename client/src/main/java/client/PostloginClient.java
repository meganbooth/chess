package client;

public class PostloginClient implements Client{
    public String handleInput(String input) {
        return "postlogin received: " + input;
    }
}
