package client;

public interface Client {
    String handleInput(String input);
    boolean shouldSwitchForward();
    boolean shouldSwitchBackward();
    String getAuthToken();
}
