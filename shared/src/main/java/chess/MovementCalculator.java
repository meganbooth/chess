package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovementCalculator {

    private static boolean isOutOfBounds(int row, int col) {
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    private static ChessPiece getObstacle(ChessBoard board, int row, int col) {
        return board.getPiece(new ChessPosition(row,col));
    }

    private static boolean isBlocked(ChessPiece obstacle, ChessPiece piece) {
        return obstacle != null && obstacle.getTeamColor() == piece.getTeamColor();
    }

    private static boolean isCapture(ChessPiece obstacle, ChessPiece piece) {
        return obstacle != null && obstacle.getTeamColor() != piece.getTeamColor();
    }

    private static void recordMoves(List<ChessMove> moves, ChessPosition from, int toRow, int toCol){
        moves.add(new ChessMove(from, new ChessPosition(toRow, toCol), null));
    }

    public static class Sliders {
        public static Collection<ChessMove> calculate(ChessBoard board, ChessPiece piece, ChessPosition position, int[][] directions){

            List<ChessMove> moves = new ArrayList<>();

            for (int[] dir : directions) {

                int j = position.getRow();
                int k = position.getColumn();

                while (true) {
                    j += dir[0];
                    k += dir[1];

                    if (isOutOfBounds(j,k)) {break;}

                    ChessPiece obstacle = getObstacle(board,j,k);

                    if (isBlocked(obstacle, piece)) {break;}
                    if (isCapture(obstacle, piece)) {recordMoves(moves,position,j,k);break;}

                    recordMoves(moves,position,j,k);
                }
            }
            return moves;
        }
    }
}
