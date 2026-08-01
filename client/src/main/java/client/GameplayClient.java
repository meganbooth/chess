package client;

import chess.ChessBoard;
import ui.BoardDrawer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayClient extends AbstractClient implements ServerMessageObserver{
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
        drawBoard(board);
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

    public void notifyUser(ServerMessage message) {
        switch(message.serverMessageType) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                ChessBoard board = loadGameMessage.getGame().game().getBoard();
                drawBoard(board);
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getNotificationMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(errorMessage.getErrorMessage());
            }
        }
    }

    private void drawBoard(ChessBoard board) {
        if(getColor().equals("BLACK")){
            System.out.println(BoardDrawer.drawBlackPerspective(board));
        } else {
            System.out.println(BoardDrawer.drawWhitePerspective(board));
        }
    }
}