package client;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.BoardDrawer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
            case "move" -> {
                System.out.print("Piece Position: ");
                String startString = scanner.nextLine();
                ChessPosition startPosition = parsePosition(startString);

                System.out.print("Move to: ");
                String endString = scanner.nextLine();
                ChessPosition endPosition = parsePosition(endString);

                ChessPiece piece = currentBoard.getPiece(startPosition);
                ChessPiece.PieceType promotionType = null;
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                        (endPosition.getRow() == 8 || endPosition.getRow() == 1)) {
                    System.out.print("Promote to: ");
                    String promotionString = scanner.nextLine();

                    promotionType = convertPromotion(promotionString);
                }
                ChessMove move = new ChessMove(startPosition,endPosition,promotionType);
                try {
                    webSocketFacade.makeMove(authToken, gameID, move);
                } catch (IOException e) {
                    yield "Error: move not made";
                }
                yield "Piece moved";
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

    private ChessPosition parsePosition(String positionString) {
        char colChar = positionString.charAt(0);
        char rowChar = positionString.charAt(1);

        int colPosition = colChar - 'a' + 1;
        int rowPosition = Character.getNumericValue(rowChar);

        return new ChessPosition (rowPosition,colPosition);
    }

    private static ChessPiece.PieceType convertPromotion(String promotionString) {
        ChessPiece.PieceType promotionType = null;
        switch(promotionString.toUpperCase()) {
            case "BISHOP" -> {
                promotionType = ChessPiece.PieceType.BISHOP;
            }
            case "ROOK" -> {
                promotionType = ChessPiece.PieceType.ROOK;
            }
            case "KNIGHT" -> {
                promotionType = ChessPiece.PieceType.KNIGHT;
            }
            case "QUEEN" -> {
                promotionType = ChessPiece.PieceType.QUEEN;
            }
            default -> {
                System.out.print("Promotion type not recognized");
            }
        }
        return promotionType;
    }
}