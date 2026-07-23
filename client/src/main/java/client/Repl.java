package client;

import java.util.Scanner;

public class Repl {
    Client preloginClient = new PreloginClient();
    Client postloginClient = new PostloginClient();
    Client gameplayClient = new GameplayClient();

    public void run(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            String input = scanner.nextLine();
            String output = preloginClient.handleInput(input);
            System.out.println(output);
            if (preloginClient.shouldQuit()) break;
        }
    }
}
