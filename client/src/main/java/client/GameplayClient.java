package client;

import chess.ChessBoard;
import ui.BoardDrawer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayClient extends AbstractClient implements ServerMessageObserver{
    public ServerFacade serverFacade = new ServerFacade(8080);
    public WebSocketFacade webSocketFacade;
    private ChessBoard currentBoard;

    private static final String HELP_TEXT = """
        Available commands:
          quit - exit the game
          help - show this menu
          redraw - redraws the board
        """;

    public GameplayClient(String authToken, String color, int gameID) throws Exception {
        super(authToken);
        this.selectedColor = color.toUpperCase();
        this.gameID = gameID;

        webSocketFacade = new WebSocketFacade("http://localhost:8080", this);
        webSocketFacade.connect(authToken, gameID);

        this.currentBoard = new ChessBoard();
        this.currentBoard.resetBoard();
        drawBoard(this.currentBoard);
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> HELP_TEXT;
            case "quit" -> {
                switchBackward = true;
                yield "You left the game.";
            }
            case "redraw" -> {
                drawBoard(currentBoard);
                yield "";
            }
            default -> "Command not recognized.\n" + HELP_TEXT;
        };
    }

    public void notifyUser(ServerMessage message) {
        switch(message.serverMessageType) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.currentBoard = loadGameMessage.getGame().game().getBoard();
                drawBoard(this.currentBoard);
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