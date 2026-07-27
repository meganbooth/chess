package client;

import chess.ChessBoard;
import ui.BoardDrawer;

import java.util.Scanner;

public class GameplayClient implements Client {
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;
    private String selectedColor;
    private static final String HELP_TEXT = """
        Available commands:
          quit - exit the game
          help - show this menu
        """;

    Scanner scanner = new Scanner(System.in);

    public GameplayClient(String authToken, String color) {
        this.authToken = authToken;
        this.selectedColor = color.toUpperCase();

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        if(getColor().equals("BLACK")){
            System.out.println(BoardDrawer.drawBlackPerspective(board));
        } else {
            System.out.println(BoardDrawer.drawWhitePerspective(board));
        }
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> HELP_TEXT;
            case "quit" -> {
                switchBackward = true;
                yield "You left the game.";
            }
            default -> "Command not recognized.\n" + HELP_TEXT;
        };
    }

    public boolean shouldSwitchForward() {
        return switchForward;
    }
    public boolean shouldSwitchBackward() {
        return switchBackward;
    }
    public String getAuthToken() {
        return authToken;
    }
    public String getColor() {return selectedColor;}
}