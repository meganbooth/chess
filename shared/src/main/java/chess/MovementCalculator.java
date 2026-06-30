package chess;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovementCalculator {

    public static class Sliders {
        public static Collection<ChessMove> calculate(ChessBoard board, ChessPiece piece, ChessPosition position, int[][] directions) {
            List<ChessMove> moves = new ArrayList<>();

            for(int[] dir : directions){
                int row = position.getRow();
                int col = position.getColumn();

                while(true){
                    row += dir[0];
                    col += dir[1];

                    if (isOutOfBounds(row,col)) {
                        break;
                    }

                    ChessPiece obstacle = getObstacle(board,row,col);
                    if (isBlocked(obstacle,piece)) {
                        break;
                    }

                    if (isCapture(obstacle, piece)) {
                        recordMoves(moves,position,row,col);
                        break;
                    }

                    recordMoves(moves, position, row, col);
                }
            }
            return moves;
        }
    }

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

    private static void recordMoves(List<ChessMove> moves, ChessPosition from, int toRow, int toCol, ChessPiece.PieceType promotion){
        moves.add(new ChessMove(from, new ChessPosition(toRow, toCol), promotion));
    }

    public static class Steppers {
        public static Collection<ChessMove> calculate(ChessBoard board, ChessPiece piece, ChessPosition position, int[][] directions) {
            List<ChessMove> moves = new ArrayList<>();

            for(int[] dir:directions) {
                int j = position.getRow() + dir[0];
                int k = position.getColumn() + dir[1];

                if(isOutOfBounds(j,k)) {continue;}

                ChessPiece obstacle = getObstacle(board,j,k);

                if(isBlocked(obstacle,piece)) {continue;}
                recordMoves(moves,position,j,k);
            }
            return moves;
        }
    }

    public static class Pawns {
        public static Collection<ChessMove> calculate(ChessBoard board, ChessPiece piece, ChessPosition position) {

            List<ChessMove> moves = new ArrayList<>();

            int forward = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
            int startRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
            int promoRow = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

            int row = position.getRow();
            int nextRow = forward + position.getRow();
            int col = position.getColumn();

            ChessPiece.PieceType[] promos = {ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.QUEEN};

            if(!isOutOfBounds(nextRow,col) && getObstacle(board,nextRow,col) == null) {
                if(nextRow == promoRow) {
                    for (ChessPiece.PieceType promotion : promos) {
                        recordMoves(moves, position, nextRow, col, promotion);
                    }
                } else {
                    recordMoves(moves, position, nextRow, col);
                }

                int twoAhead = nextRow + forward;
                if(row == startRow && !isOutOfBounds(twoAhead,col) && getObstacle(board,twoAhead,col) == null) {
                    recordMoves(moves,position,twoAhead,col);
                }
            }

            for (int dc : new int[] {1,-1}) {
                int nextCol = dc + col;
                if (!isOutOfBounds(nextRow, nextCol) && isCapture(getObstacle(board, nextRow, nextCol), piece)) {
                    if (nextRow == promoRow) {
                        for (ChessPiece.PieceType promotion : promos) {
                            recordMoves(moves, position, nextRow, nextCol, promotion);
                        }
                    } else {
                        recordMoves(moves, position, nextRow, nextCol);
                    }
                }
            }
            return moves;
        }
    }
}
