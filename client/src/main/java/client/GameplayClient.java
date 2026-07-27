package client;

import chess.ChessBoard;
import ui.BoardDrawer;

public class GameplayClient extends AbstractClient {
    public ServerFacade facade = new ServerFacade(8080);
    private static final String HELP_TEXT = """
        Available commands:
          quit - exit the game
          help - show this menu
        """;

    public GameplayClient(String authToken, String color) {
        super(authToken);
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
}