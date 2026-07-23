package client;

import java.util.Scanner;

public class Repl {
    public void run(){
        String input;
        String output;
        Scanner scanner = new Scanner(System.in);
        Client currentClient = new PreloginClient();
        while(true){
            input = scanner.nextLine();
            output = currentClient.handleInput(input);
            System.out.println(output);

            if(currentClient.shouldSwitchForward()) {
                if(currentClient instanceof PreloginClient) {
                    currentClient = new PostloginClient(currentClient.getAuthToken());
                } else {
                    currentClient = new GameplayClient();
                }
            }

            if(currentClient.shouldSwitchBackward()) {
                if(currentClient instanceof GameplayClient) {
                    currentClient = new PostloginClient(currentClient.getAuthToken());
                } else if (currentClient instanceof PostloginClient){
                    currentClient = new PreloginClient();
                } else {
                    break;
                }
            }
        }
    }
}
