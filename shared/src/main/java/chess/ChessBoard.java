package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] grid = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        grid = new ChessPiece[8][8];

        int backRow;
        int pawnRow;
        ChessGame.TeamColor color;

        for(int i = 1; i <= 2; i++) {

            if(i == 1) {
                backRow = 1;
                pawnRow = 2;
                color = ChessGame.TeamColor.WHITE;
            } else {
                backRow = 8;
                pawnRow = 7;
                color = ChessGame.TeamColor.BLACK;
            }

            addPiece(new ChessPosition(backRow,1), new ChessPiece(color,ChessPiece.PieceType.ROOK));
            addPiece(new ChessPosition(backRow,2), new ChessPiece(color,ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(backRow,3), new ChessPiece(color,ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(backRow,4), new ChessPiece(color,ChessPiece.PieceType.QUEEN));
            addPiece(new ChessPosition(backRow,5), new ChessPiece(color,ChessPiece.PieceType.KING));
            addPiece(new ChessPosition(backRow,6), new ChessPiece(color,ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(backRow,7), new ChessPiece(color,ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(backRow,8), new ChessPiece(color,ChessPiece.PieceType.ROOK));

            for(int j = 1; j <= 8; j++){
                addPiece(new ChessPosition(pawnRow,j), new ChessPiece(color,ChessPiece.PieceType.PAWN));
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
