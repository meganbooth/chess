package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardDrawer {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(drawWhitePerspective(board));
    }

    public static String drawWhitePerspective(ChessBoard board) {
        StringBuilder boardText = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9) {
                    appendColumnLabels(j, boardText);
                } else {
                    if (j == 0 || j == 9) {
                       boardText.append(" ").append(9 - i).append(" ");
                    } else {
                        appendSquareColors(i, j, boardText);
                        appendPieceSymbols(board, i, j, boardText);
                    }
                }
                boardText.append(EscapeSequences.RESET_BG_COLOR);
                boardText.append(EscapeSequences.RESET_TEXT_COLOR);
            }
            boardText.append("\n");
        }
        return boardText.toString();
    }

    private static void appendPieceSymbols(ChessBoard board, int i, int j, StringBuilder boardText) {
        ChessPiece piece = board.getPiece(new ChessPosition((9 - i), j));
        if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            boardText.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
            appendWhitePieceSymbol(piece, boardText);
        } else if (piece != null){
            boardText.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            appendBlackPieceSymbol(boardText, piece);
        } else {
            boardText.append(EscapeSequences.EMPTY);
        }
    }

    private static void appendSquareColors(int i, int j, StringBuilder boardText) {
        if (((9 - i) + j) % 2 == 1) {
            boardText.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else {
            boardText.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        }
    }

    private static void appendColumnLabels(int j, StringBuilder boardText) {
        if (j == 0 || j == 9) {
            boardText.append("  ");
        } else {
            boardText.append(" ").append((char) ('a' + j - 1)).append("  ");
        }
    }

    private static void appendWhitePieceSymbol(ChessPiece piece, StringBuilder boardText) {
        switch (piece.getPieceType()) {
            case KING -> boardText.append(EscapeSequences.WHITE_KING);
            case QUEEN -> boardText.append(EscapeSequences.WHITE_QUEEN);
            case BISHOP -> boardText.append(EscapeSequences.WHITE_BISHOP);
            case KNIGHT -> boardText.append(EscapeSequences.WHITE_KNIGHT);
            case ROOK -> boardText.append(EscapeSequences.WHITE_ROOK);
            case PAWN -> boardText.append(EscapeSequences.WHITE_PAWN);
        }
    }

    private static void appendBlackPieceSymbol(StringBuilder boardText, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING -> boardText.append(EscapeSequences.BLACK_KING);
            case QUEEN -> boardText.append(EscapeSequences.BLACK_QUEEN);
            case BISHOP -> boardText.append(EscapeSequences.BLACK_BISHOP);
            case KNIGHT -> boardText.append(EscapeSequences.BLACK_KNIGHT);
            case ROOK -> boardText.append(EscapeSequences.BLACK_ROOK);
            case PAWN -> boardText.append(EscapeSequences.BLACK_PAWN);
        }
    }
}
