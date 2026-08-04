package client;

import chess.*;
import ui.BoardDrawer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GameplayClient extends AbstractClient implements ServerMessageObserver{
    public ServerFacade serverFacade = new ServerFacade(8080);
    public WebSocketFacade webSocketFacade;
    private ChessGame currentGame;

    private static final String HELP_TEXT = """
        Available commands:
          quit - exit the game
          help - show this menu
          redraw - redraws the board
          move - make a move
          highlight - highlight valid moves
          resign - resign from the game
        """;

    public GameplayClient(String authToken, String color, int gameID) throws Exception {
        super(authToken);
        this.selectedColor = color.toUpperCase();
        this.gameID = gameID;

        webSocketFacade = new WebSocketFacade("http://localhost:8080", this);
        webSocketFacade.connect(authToken, gameID);

        this.currentGame = new ChessGame();
        drawBoard(currentGame.getBoard());
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> HELP_TEXT;
            case "quit" -> {
                try {
                    webSocketFacade.leave(authToken, gameID);
                    switchBackward = true;
                    yield "You left the game";
                } catch (IOException e) {
                    yield "Error: unable to quit the game";
                }
            }
            case "redraw" -> {
                drawBoard(currentGame.getBoard());
                yield "";
            }
            case "move" -> {
                ChessPosition startPosition = getStartPosition();
                ChessPosition endPosition = getEndPosition();

                ChessPiece piece = currentGame.getBoard().getPiece(startPosition);
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
            case "highlight" -> {
                ChessPosition startPosition = getStartPosition();
                Collection<ChessMove> legalMoves = currentGame.validMoves(startPosition);
                Collection<ChessPosition> legalEndPositions = new ArrayList<>();
                for (ChessMove move : legalMoves) {
                    legalEndPositions.add(move.getEndPosition());
                }
                drawHighlight(currentGame.getBoard(),startPosition,legalEndPositions);
                yield "Valid moves highlighted";
            }
            case "resign" -> {
                System.out.print("Are you sure you want to resign?");
                System.out.print("Type y to continue: ");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("y")) {
                    try {
                        webSocketFacade.resign(authToken, gameID);
                        yield "You resigned";
                    } catch (IOException e) {
                        yield "Error: could not resign";
                    }
                } else {
                    yield "Resign not confirmed";
                }
            }
            default -> "Command not recognized.\n" + HELP_TEXT;
        };
    }

    public void notifyUser(ServerMessage message) {
        switch(message.serverMessageType) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.currentGame = loadGameMessage.getGame().game();
                drawBoard(currentGame.getBoard());
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

    private void drawHighlight(ChessBoard board, ChessPosition startPosition,
                               Collection<ChessPosition> legalEndPositions) {
        if(getColor().equals("BLACK")){
            System.out.println(BoardDrawer.drawBlackPerspective(board,startPosition,legalEndPositions));
        } else {
            System.out.println(BoardDrawer.drawWhitePerspective(board,startPosition,legalEndPositions));
        }
    }

    private ChessPosition getEndPosition() {
        System.out.print("Move to: ");
        String endString = scanner.nextLine();
        return parsePosition(endString);
    }

    private ChessPosition getStartPosition() {
        System.out.print("Piece Position: ");
        String startString = scanner.nextLine();
        return parsePosition(startString);
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