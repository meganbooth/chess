package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    // Castling Vars
    private boolean whiteKingMoved;
    private boolean whiteKingsideRookMoved;
    private boolean whiteQueensideRookMoved;

    private boolean blackKingMoved;
    private boolean blackKingsideRookMoved;
    private boolean blackQueensideRookMoved;

    private static final ChessPosition WHITE_KING_START = new ChessPosition(1,5);
    private static final ChessPosition WHITE_KINGSIDE_ROOK_START = new ChessPosition(1,8);
    private static final ChessPosition WHITE_QUEENSIDE_ROOK_START = new ChessPosition(1,1);

    private static final ChessPosition BLACK_KING_START = new ChessPosition(8,5);
    private static final ChessPosition BLACK_KINGSIDE_ROOK_START = new ChessPosition(8,8);
    private static final ChessPosition BLACK_QUEENSIDE_ROOK_START = new ChessPosition(8,1);

    private static final ChessPosition WHITE_KINGSIDE_CASTLE_END = new ChessPosition(1,7);
    private static final ChessPosition WHITE_QUEENSIDE_CASTLE_END = new ChessPosition(1,3);

    private static final ChessPosition BLACK_KINGSIDE_CASTLE_END = new ChessPosition(8,7);
    private static final ChessPosition BLACK_QUEENSIDE_CASTLE_END = new ChessPosition(8,3);

    private static final ChessPosition WHITE_KINGSIDE_PASS = new ChessPosition(1,6);
    private static final ChessPosition WHITE_QUEENSIDE_PASS = new ChessPosition(1,4);

    private static final ChessPosition BLACK_KINGSIDE_PASS = new ChessPosition(8,6);
    private static final ChessPosition BLACK_QUEENSIDE_PASS = new ChessPosition(8,4);

    // En Passant Vars
    private ChessPosition pawnDoubleMove;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();

        this.whiteKingMoved = false;
        this.blackKingMoved = false;
        this.whiteKingsideRookMoved = false;
        this.whiteQueensideRookMoved = false;
        this.blackKingsideRookMoved = false;
        this.blackQueensideRookMoved = false;
        this.pawnDoubleMove = null;
    }

    private void moveCastlingRook(ChessPosition rookStart, ChessPosition rookEnd){
        ChessPiece rook = board.getPiece(rookStart);
        board.addPiece(rookEnd, rook);
        board.addPiece(rookStart, null);
    }

    private boolean isDoublePawnMove(ChessMove move, ChessPiece piece){
        boolean isPawn = piece.getPieceType() == ChessPiece.PieceType.PAWN;
        boolean isDoubleMove = Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2;
        return isPawn && isDoubleMove;
    }

    private boolean isCastlePathSafe(ChessPosition passThrough, ChessPosition kingEnd, ChessPosition kingPosition, ChessPiece king){
        boolean safePath = false;

        // Test if the king can safely pass through the intermediate square
        ChessPiece toSave = board.getPiece(passThrough);
        board.addPiece(passThrough, king);
        board.addPiece(kingPosition, null);
        if(!isInCheck(king.getTeamColor())) { safePath = true; }
        board.addPiece(kingPosition, king);
        board.addPiece(passThrough, toSave);

        // Test if the king can safely land on the destination square
        toSave = board.getPiece(kingEnd);
        board.addPiece(kingEnd, king);
        board.addPiece(kingPosition, null);
        boolean safeLanding = safePath && !isInCheck(king.getTeamColor());
        board.addPiece(kingPosition, king);
        board.addPiece(kingEnd, toSave);
        return safeLanding;
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if(piece != null && piece.getTeamColor() == teamColor){
                    if(!validMoves(new ChessPosition(i,j)).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        // Validate that a piece exists at the given position
        if(piece != null){
            Collection<ChessMove> allMoves = piece.pieceMoves(board,startPosition);

            // Check castling eligibility for king pieces
            if(piece.getPieceType() == ChessPiece.PieceType.KING) {

                if(!whiteKingMoved && !whiteKingsideRookMoved && !isInCheck(piece.getTeamColor()) && piece.getTeamColor() == TeamColor.WHITE){
                    if(board.getPiece(WHITE_KINGSIDE_PASS) == null && board.getPiece(WHITE_KINGSIDE_CASTLE_END) == null){
                        if(isCastlePathSafe(WHITE_KINGSIDE_PASS, WHITE_KINGSIDE_CASTLE_END, startPosition, piece)){
                            moves.add(new ChessMove(startPosition, WHITE_KINGSIDE_CASTLE_END, null));
                        }
                    }
                }
                if(!whiteKingMoved && !whiteQueensideRookMoved && !isInCheck(piece.getTeamColor()) && piece.getTeamColor() == TeamColor.WHITE){
                    if(board.getPiece(WHITE_QUEENSIDE_PASS) == null && board.getPiece(WHITE_QUEENSIDE_CASTLE_END) == null && board.getPiece(new ChessPosition(1,2)) == null){
                        if(isCastlePathSafe(WHITE_QUEENSIDE_PASS, WHITE_QUEENSIDE_CASTLE_END, startPosition, piece)){
                            moves.add(new ChessMove(startPosition, WHITE_QUEENSIDE_CASTLE_END, null));
                        }
                    }
                }
                if(!blackKingMoved && !blackKingsideRookMoved && !isInCheck(piece.getTeamColor()) && piece.getTeamColor() == TeamColor.BLACK){
                    if(board.getPiece(BLACK_KINGSIDE_PASS) == null && board.getPiece(BLACK_KINGSIDE_CASTLE_END) == null){
                        if(isCastlePathSafe(BLACK_KINGSIDE_PASS, BLACK_KINGSIDE_CASTLE_END, startPosition, piece)){
                            moves.add(new ChessMove(startPosition, BLACK_KINGSIDE_CASTLE_END, null));
                        }
                    }
                }
                if(!blackKingMoved && !blackQueensideRookMoved && !isInCheck(piece.getTeamColor()) && piece.getTeamColor() == TeamColor.BLACK){
                    if(board.getPiece(BLACK_QUEENSIDE_PASS) == null && board.getPiece(BLACK_QUEENSIDE_CASTLE_END) == null && board.getPiece(new ChessPosition(8,2)) == null){
                        if(isCastlePathSafe(BLACK_QUEENSIDE_PASS, BLACK_QUEENSIDE_CASTLE_END, startPosition, piece)){
                            moves.add(new ChessMove(startPosition, BLACK_QUEENSIDE_CASTLE_END, null));
                        }
                    }
                }
            }

            // Check if en passant capture is available
            boolean isPawn = piece.getPieceType() == ChessPiece.PieceType.PAWN;
            boolean enPassantAvailable = pawnDoubleMove != null;
            boolean sameRow = enPassantAvailable && startPosition.getRow() == pawnDoubleMove.getRow();
            boolean adjacentColumn = enPassantAvailable && (startPosition.getColumn() == pawnDoubleMove.getColumn()-1 || startPosition.getColumn() == pawnDoubleMove.getColumn()+1);

            if(isPawn && enPassantAvailable && sameRow && adjacentColumn){
                int forward = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
                ChessPiece capturedPawn = board.getPiece(pawnDoubleMove);

                // Temporarily make the en passant move to test for check
                board.addPiece(new ChessPosition(pawnDoubleMove.getRow() + forward, pawnDoubleMove.getColumn()), piece);
                board.addPiece(startPosition, null);
                board.addPiece(pawnDoubleMove, null);

                if(!isInCheck(piece.getTeamColor())){
                    moves.add(new ChessMove(startPosition,new ChessPosition(pawnDoubleMove.getRow() + forward, pawnDoubleMove.getColumn()), null));
                }

                // Restore the board
                board.addPiece(startPosition, piece);
                board.addPiece(new ChessPosition(pawnDoubleMove.getRow() + forward, pawnDoubleMove.getColumn()), null);
                board.addPiece(pawnDoubleMove, capturedPawn);
            }

            // Filter moves that would leave the king in check
            for(ChessMove move : allMoves){
                ChessPiece toSave = board.getPiece(move.getEndPosition());

                board.addPiece(move.getEndPosition(),piece);
                board.addPiece(startPosition,null);

                if(!isInCheck(piece.getTeamColor())){
                    moves.add(move);
                }

                board.addPiece(startPosition,piece);
                board.addPiece(move.getEndPosition(),toSave);
            }
        }
        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        // Validate that a piece exists at the start position
        if(piece == null){
            throw new InvalidMoveException();
        }

        // Validate that it is the correct team's turn
        if(getTeamTurn() != piece.getTeamColor()){
            throw new InvalidMoveException();
        }

        // Validate that the move is legal
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(validMoves.contains(move)){

            // Update castling flags if a king or rook moves from its starting position
            if(move.getStartPosition().equals(WHITE_KING_START)) {
                whiteKingMoved = true;
            } else if(move.getStartPosition().equals(WHITE_KINGSIDE_ROOK_START)){
                whiteKingsideRookMoved = true;
            } else if(move.getStartPosition().equals(WHITE_QUEENSIDE_ROOK_START)){
                whiteQueensideRookMoved = true;
            } else if(move.getStartPosition().equals(BLACK_KING_START)){
                blackKingMoved = true;
            } else if(move.getStartPosition().equals(BLACK_KINGSIDE_ROOK_START)){
                blackKingsideRookMoved = true;
            } else if(move.getStartPosition().equals(BLACK_QUEENSIDE_ROOK_START)){
                blackQueensideRookMoved = true;
            }

            // Execute the move
            board.addPiece(move.getEndPosition(),piece);
            board.addPiece(move.getStartPosition(),null);

            // If castling, move the rook to its new position
            if(piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2) {
                if(move.getEndPosition().equals(new ChessPosition(1,7))){
                    moveCastlingRook(WHITE_KINGSIDE_ROOK_START, new ChessPosition(1,6));
                }
                if(move.getEndPosition().equals(new ChessPosition(1,3))){
                    moveCastlingRook(WHITE_QUEENSIDE_ROOK_START, new ChessPosition(1,4));
                }
                if(move.getEndPosition().equals(new ChessPosition(8,7))){
                    moveCastlingRook(BLACK_KINGSIDE_ROOK_START, new ChessPosition(8,6));
                }
                if(move.getEndPosition().equals(new ChessPosition(8,3))){
                    moveCastlingRook(BLACK_QUEENSIDE_ROOK_START, new ChessPosition(8,4));
                }
            }

            // Remove the captured pawn if this move is an en passant capture
            boolean isPawn = piece.getPieceType() == ChessPiece.PieceType.PAWN;
            boolean isDiagonalMove = Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 1 && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 1;
            boolean isEnPassant = pawnDoubleMove != null && pawnDoubleMove.getColumn() == move.getEndPosition().getColumn();

            if(isPawn && isDiagonalMove && isEnPassant){
                board.addPiece(pawnDoubleMove, null);
            }

            // Track double pawn move for en passant
            if(isDoublePawnMove(move, piece)){
                pawnDoubleMove = move.getEndPosition();
            } else {
                pawnDoubleMove = null;
            }

            // Handle pawn promotion
            if(move.getPromotionPiece() != null){
                ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotedPiece);
            }

            // Switch turns
            setTeamTurn(turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        // Find the king's position then check if any enemy piece can capture it
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    kingPosition = new ChessPosition(i,j);
                }
            }
        }

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPiece enemy = board.getPiece(new ChessPosition(i,j));
                if(enemy != null && enemy.getTeamColor() != teamColor) {
                    for (ChessMove move : enemy.pieceMoves(board, new ChessPosition(i, j))) {

                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && hasNoValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        ChessPiece piece;

        piece = board.getPiece(WHITE_KING_START);
        whiteKingMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE);

        piece = board.getPiece(WHITE_KINGSIDE_ROOK_START);
        whiteKingsideRookMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.WHITE);

        piece = board.getPiece(WHITE_QUEENSIDE_ROOK_START);
        whiteQueensideRookMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.WHITE);

        piece = board.getPiece(BLACK_KING_START);
        blackKingMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK);

        piece = board.getPiece(BLACK_KINGSIDE_ROOK_START);
        blackKingsideRookMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.BLACK);

        piece = board.getPiece(BLACK_QUEENSIDE_ROOK_START);
        blackQueensideRookMoved = !(piece != null && piece.getPieceType() == ChessPiece.PieceType.ROOK && piece.getTeamColor() == TeamColor.BLACK);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
