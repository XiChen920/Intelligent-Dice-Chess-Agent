package Model;

import java.util.ArrayList;
import java.util.List;

public class Rules {
    private static final byte[] directions = {1,-1}; // pawn position of white may only move up, of black down (white direction = directions[0])
    public static boolean ENPASSANT = false;
    /**
     * check if the current move is legal, true if legal
     * @param move the current move need to be checked
     * @param board the current board, the 2-dimension array
     * @param previousMove the move played before (by the opponent), null if there is none or no legal
     * @return true if the move is legal
     * @throws EnPassantSpecialMove if it is detected to be an EnPassant move, the exception is thrown for further operation
     * @throws CastlingSpecialMove if it is detected to be a castling move, the exception is thrown for further operation
     * @throws PromotionSpecialMove if it is detected to be a promotion move, the exception is thrown for further operation
     */
    public static boolean isLegal(Move move, PieceInterface[][] board, boolean currentPlayer, byte dice, Move previousMove) throws EnPassantSpecialMove, CastlingSpecialMove, PromotionSpecialMove{
        if(!BoardState.checkBoardSize(board)) throw new IllegalArgumentException();
        byte x0 = move.getX0(), y0 = move.getY0();
        if(board[x0][y0] == null || board[x0][y0].getPlayer() != currentPlayer) return false;
        castling(move, board);
        promotion(move, board);
        if(board[x0][y0].getTypeOfPiece() != dice) return false; //piece check
        if(ENPASSANT) enPassant(move, board, previousMove);
        switch (board[move.getX0()][move.getY0()].getTypeOfPiece()) {
            case 1: return pawnCheck(move, board);
            case 2: return rookCheck(move, board);
            case 3: return knightCheck(move, board);
            case 4: return bishopCheck(move, board);
            case 5: return queenCheck(move, board);
            case 6: return kingCheck(move, board);
            default: return false;
        }
    }


    /**
     * Checks if a move would be legal for a pawn
     * @param move the move to be checked
     * @param board current board
     * @return true if legal, false if not
     */
    static public boolean pawnCheck(Move move, PieceInterface[][] board){
        if (!checkPath(move, board)) return false;
        byte x0 = move.getX0(), x1 = move.getX1(), y0 = move.getY0(), y1 = move.getY1();
        byte direction;
        if(board[x0][y0].getPlayer()) direction = directions[1]; else direction = directions[0];
        if(x1-x0 == direction && y0 == y1 && endPointMoveCheck(move, board) && board[x1][y1] == null) return true;
        if(x1-x0 == direction*2 && y0 == y1 && !board[x0][y0].isMoved() && checkPath(move, board) && board[x1][y1] == null) return true;
        return x1 - x0 == direction && Math.abs(y1 - y0) == 1 && board[x1][y1] != null && board[x0][y0].getPlayer() != board[x1][y1].getPlayer();
    }

    /**
     * Checks if a move would be legal for a rook
     * @param move the move to be checked
     * @param board current board
     * @return true if legal, false if not
     */
    static public boolean rookCheck(Move move, PieceInterface[][] board){
        byte dX = (byte) (move.getX1() - move.getX0()), dY = (byte) (move.getY1() - move.getY0());
        if (dX == 0 && dY == 0) return false;
        if (dX != 0 && dY != 0) return false;
        return checkPath(move, board);
    }

    /**
     * Checks if a move would be legal for a knight
     * @param move the move to be checked
     * @return true if legal, false if not
     */
    static public boolean knightCheck(Move move, PieceInterface[][] board){
        if (!endPointMoveCheck(move, board)) return false;
        byte dX = (byte) Math.abs(move.getX0()-move.getX1());
        byte dY = (byte) Math.abs(move.getY0()- move.getY1());
        return ((dX == 1 && dY == 2) || (dX == 2 && dY == 1));
    }

    /**
     * Checks if a move would be legal for a bishop
     * @param move the move to be checked
     * @param board the board the move is played upon
     * @return true if legal, false if not
     */
    static public boolean bishopCheck(Move move, PieceInterface[][] board){
        byte dX = (byte) Math.abs(move.getX0()-move.getX1());
        byte dY = (byte) Math.abs(move.getY0()- move.getY1());
        return (dX == dY && dX != 0 && checkPath(move, board));
    }

    /**
     * Checks if a move would be legal for a queen
     * @param move the move to be checked
     * @param board the board the move is played upon
     * @return true if legal, false if not
     */
    static public boolean queenCheck(Move move, PieceInterface[][] board){
        byte dX = (byte) Math.abs(move.getX0()-move.getX1());
        byte dY = (byte) Math.abs(move.getY0()- move.getY1());
        return (dX == dY || (dX == 0 && dY != 0) || (dY == 0 && dX != 0)) && checkPath(move, board);
    }

    /**
     * Checks if a move would be legal for a king
     * Does not check for Castling
     * @param move the move to be checked
     * @param board the board the move is played upon
     * @return true if legal, false if not
     */
    static public boolean kingCheck(Move move, PieceInterface[][] board){
        return (Math.abs(move.getX0()-move.getX1()) <= 1 && Math.abs(move.getY0()- move.getY1()) <= 1 && !(Math.abs(move.getX0()-move.getX1()) == 0 && Math.abs(move.getY0()- move.getY1()) == 0))
                && checkPath(move, board);
    }

    /**
     * Checks if the path between the start and the end is free
     * excluding start and end
     * assumes the path is a straight vertical, horizontal or diagonal line
     * @param move the move to check
     * @param board the board the move would be played upon
     * @return if the path is free
     */
    static public boolean checkPath(Move move, PieceInterface[][] board){
        if(!endPointMoveCheck(move, board)) return false;
        byte x0 = move.getX0(), x1 = move.getX1(), y0 = move.getY0(), y1 = move.getY1();
        byte dX = (byte) (x1-x0), dY = (byte) (y1-y0);
        if(dX != 0) dX = (byte) (dX/Math.abs(dX));
        if(dY != 0) dY = (byte) (dY/Math.abs(dY));
        x0+=dX;
        y0+=dY;
        while ((x0 != x1 || dX == 0) && (y0 != y1 || dY == 0)){
            if(board[x0][y0]!=null) {
                return false;
            }
            x0+=dX;
            y0+=dY;
        }
        return true;
    }

    /**
     * Checks if the move is legal in terms of only the endpoint in relation to the start point
     * @param move the move to check
     * @param board the board the move is played upon
     * @return true if under the conditions the move is legal
     */
    static public boolean endPointMoveCheck(Move move, PieceInterface[][] board){
        byte x0 = move.getX0(), x1 = move.getX1(), y0 = move.getY0(), y1 = move.getY1();
        if(x1 <0 || x1 >7|| y1<0 ||y1>7) return false;
        return (board[x1][y1] == null || (board[x1][y1].getPlayer() != board[x0][y0].getPlayer())) && board[x0][y0] != null;
    }

    /**
     * Checks if a move is an En Passant move, if yes provides the position of the captured piece by EnPassantSpecialMove
     * @param move the move to be checked
     * @param board the board on witch the move is played
     * @throws EnPassantSpecialMove including the position where the piece captured is present
     */
    static public void enPassant(Move move, PieceInterface[][] board, Move previousMove) throws EnPassantSpecialMove{
        if (previousMove == null) return;
        byte x0 = move.getX0(), x1 = move.getX1(), y0 = move.getY0(), y1 = move.getY1();
        if(board[x0][y0].getTypeOfPiece()!= 1) return;
        boolean player = board[x0][y0].getPlayer();
        byte direction;
        if(player) direction = directions[1]; else direction = directions[0];
        byte dX = (byte) (x1-x0), dY = (byte) Math.abs(y1-y0);
        if (!(dX == direction && dY==1)) return;
        if (board[x0][y1] != null && board[x0][y1].getTypeOfPiece() == 1 && board[x0][y1].getPlayer()!= player && x0 == previousMove.getX1() && y1 == previousMove.getY1()) throw new EnPassantSpecialMove(x0,y1);
    }

    /**
     * Checks if a move is a castling move (signaled by the involved king capturing the involved rook or otherwise around) and provides if yes the partner move by CastlingSpecialMove
     * @param move the move to be checked
     * @param board the board on witch the move is played
     * @throws CastlingSpecialMove is thrown if yes, includes the partner move
     */
    static public void castling(Move move, PieceInterface[][] board) throws CastlingSpecialMove{
        byte y0 = move.getY0(), y1 = move.getY1(), x = move.getX0(), pieceType = board[x][y0].getTypeOfPiece(), dY = (byte) (y1-y0),yR0, yK0, yR1, yK1;
        if(x!= move.getX1()) return;
        if(pieceType == 2){
            yR0 = y0;
            yK0 = y1;
        }
        else if(pieceType == 6){
            yK0 = y0;
            yR0 = y1;
        }
        else return;
        byte direction =(byte) Math.max(Math.min(yR0-yK0,1), -1);
        yK1 = (byte) (yK0+2*direction);
        yR1 = (byte) (yK1 - direction);

        if(x > 7 || x < 0 || yK0 > 7 || yK0 < 0 || yR0 > 7 || yR0 < 0) return;
        if(board[x][yK0] == null || board[x][yK0].getTypeOfPiece() != 6 || board[x][yK0].isMoved()) return;
        if(board[x][yR0] == null || board[x][yR0].getTypeOfPiece() != 2 || board[x][yR0].isMoved()) return;
        //NOTE: king and rook fulfill the required conditions of castling with regard to positions and if they have moved
        if(!checkPath(new MoveHuman(x, x, yR0, yR1), board)) return;
        if(!checkPath(new MoveHuman(x, x, yK0, yK1), board)) return;
        //NOTE: in very strict rule interpretation the king moves first, thus the rook can never block an attack
        if(pieceType == 6) throw new CastlingSpecialMove(new MoveHuman(x, x, yR0, yR1));
        else throw new CastlingSpecialMove(new MoveHuman(x, x, yK0, yK1));
    }

    /**
     * Checks if a move is a promotion move, if yes provides the start position of the piece by PromotionSpecialMove
     * @param move the move to check
     * @param board the board the move is played upon
     * @throws PromotionSpecialMove is thrown if yes, including the start position of the piece
     */
    static public void promotion(Move move, PieceInterface[][] board) throws PromotionSpecialMove {
        if(board[move.getX0()][move.getY0()] == null || board[move.getX0()][move.getY0()].getTypeOfPiece() != 1) return;
        if(!pawnCheck(move, board)) return;
        if(!(move.getX1()==0 || move.getX1()==7)) return;
        throw new PromotionSpecialMove(move.getX0(), move.getY0());
    }

    /**
     * To get the piece-types that can be moved
     * because we should not roll a number of an immobile piece type
     *
     * @param player black (true) / white (false)
     * @param board the board to check
     * @return the list of piece types that can be moved
     */
    static public List<Byte> getMovablePieceTypes(boolean player, PieceInterface[][] board, Move previousMove) {
        boolean[] tracker = {false, false, false, false, false, false};
        byte x0, x1, y0, y1, i, count = 0;
        Move temp;
        for(x0 = 0; x0 < board.length; x0++){
            for (y0 = 0; y0 < board[x0].length; y0++){
                if(board[x0][y0] != null && !tracker[board[x0][y0].getTypeOfPiece()-1] && board[x0][y0].getPlayer() == player) {
                    for (x1 = 0; x1 < board.length; x1++){
                        for(y1 = 0; y1 < board[x1].length; y1++){
                            try {
                                if(isLegal(new MoveHuman(x0, x1, y0, y1), board, player, board[x0][y0].getTypeOfPiece(), previousMove)){
                                    tracker[board[x0][y0].getTypeOfPiece()-1] = true;
                                    count++;
                                    break;
                                }
                            } catch (EnPassantSpecialMove ignore) {
                                tracker[board[x0][y0].getTypeOfPiece()-1] = true;
                                count++;
                                break;
                            } catch (PromotionSpecialMove ignore){
                                count = 6;
                                tracker = new boolean[]{true, true, true, true, true, true};
                                break;
                            } catch (CastlingSpecialMove castlingSpecialMove){
                                tracker[board[x0][y0].getTypeOfPiece()-1] = true;
                                count++;

                                if(!tracker[board[castlingSpecialMove.getPartnerMove().getX0()][castlingSpecialMove.getPartnerMove().getY0()].getTypeOfPiece()-1]) {
                                    tracker[board[castlingSpecialMove.getPartnerMove().getX0()][castlingSpecialMove.getPartnerMove().getY0()].getTypeOfPiece()-1] = true;
                                    count++;
                                }
                                break;
                            }
                        }
                        if(count == 6) break;
                        if(tracker[board[x0][y0].getTypeOfPiece()-1]) break;
                    }
                }
                if(count == 6) break;
            }
            if(count == 6) break;
        }
        List<Byte> ans = new ArrayList<>();
        for (i = 0; i < tracker.length; i++) {
            if (tracker[i]) ans.add((byte) (i+1));
        }
        return ans;
    }

    /**
     * Throwable to communicate that the move is a legal en passant move
     */
    public static class EnPassantSpecialMove extends Throwable{
        private final byte[] effectedPosition;
        private EnPassantSpecialMove(byte xEffected, byte yEffected){
            effectedPosition = new byte[]{xEffected,yEffected};
        }

        /**
         * To get the position from the object
         * @return the position of the other captured pawn as (x,y) = (return[0], return[1]) and captured piece = PieceInterface[x][y]
         */
        public byte[] getEffectedPosition(){
            return effectedPosition;
        }
    }

    /**
     * To communicate that the move was a legal castling move
     */
    public static class CastlingSpecialMove extends Throwable{
        private final Move partnerMove;
        private CastlingSpecialMove(Move partnerMove){
            this.partnerMove = partnerMove;
        }
        /**
            To get the other move associated with this move
         */
        public Move getPartnerMove() {
            return partnerMove;
        }
    }

    /**
     * To communicate that the move was a legal promotion move
     */
    public static class PromotionSpecialMove extends Throwable{
        private final byte[] effectedPosition;
        private PromotionSpecialMove(byte xEffected, byte yEffected){
            effectedPosition = new byte[]{xEffected,yEffected};
        }
        /**
         * To get the position from the object
         * @return the position of the piece to be promoted as (x,y) = (return[0], return[1]) and piece = PieceInterface[x][y]
         */
        public byte[] getEffectedPosition(){
            return effectedPosition;
        }
    }

    /**
     * To get all possible moves for a given dice roll and board state
     * @param board the current game board
     * @param currentPlayer the player on turn
     * @param dice the dice roll
     * @param previousMove the last move made before this one
     * @return all moves that can be made a cording to Rules.isLegal
     */
    public static List<Move> getAllPossibleMoves(PieceInterface[][] board, boolean currentPlayer, byte dice, Move previousMove){
        List<Move> ans = new ArrayList<>();
        byte x0, x1, y0, y1;
        Move temp;
        for(x0 = 0; x0 < board.length; x0++){
            for (y0 = 0; y0 < board[x0].length; y0++){
                if(board[x0][y0] != null && board[x0][y0].getPlayer()==currentPlayer) for (x1 = 0; x1 < board.length; x1++){
                    for(y1 = 0; y1 < board[x1].length; y1++){
                        temp = new MoveHuman(x0,x1, y0, y1);
                        try {
                            if (isLegal(temp, board, currentPlayer, dice, previousMove)) ans.add(temp);
                        } catch (EnPassantSpecialMove | CastlingSpecialMove | PromotionSpecialMove specialMove) {ans.add(temp);}
                    }
                }
            }
        }
        return ans;
    }

    public static String getRulesSummary(){
        String ans =  "Dice Chess\n"+
                "This game is a variant of classic Chess, which adds a random factor to the strategy. The following list describes all differences of the rules:\n"+
                "1. There is no check or checkmate, it is allowed to move the king to a square attacked by opponent's piece. The goal is to capture opponent's king.\n"+
                "2. A die is rolled for every move. The die is displayed below the game board and the number determines which piece can be used to make the move. 1 - pawn, 2 - knight, 3 - bishop, 4 - rook, 5 - queen, 6 - king. Use getMovablePieceTypes() to automatically detects which pieces can be moved at the actual situation and void rolling a number of an immobile one.\n"+
                "3. If a pawn is to be promoted (would advance to the last row), the player can move it even if the die does not show 1. However, he can only promote it to the piece chosen by the die roll - for example, if 3 is rolled, the pawn can be promoted to a bishop only. If 1 is rolled, the pawn can be promoted to any piece.\n";
        if(!ENPASSANT) ans+= "4. this game is played without en passant\n";
        return ans+"Source: https://brainking.com/en/GameRules?tp=95";
    }

    public static String getRules(){
        return "Setup\n" +
                "1.  The game is played on a bord consistent of eight-by-eight squares\n" +
                "2.  White starts the game\n" +
                "3.  The first row of each player (the row farthest on his/her/their side) contains form left to right at the start of the game: rook, knight, bishop, queen, king, bishop, knight, rook\n" +
                "4.  The second row of each player (the only row adjacent to their first row) is at the start of the game filled with 8 pawns\n" +
                "\n" +
                "A turn\n" +
                "1. The first thing that happens on a turn, is the roll of the dice, determining the type of piece that can be moved, only a movable type of piece should be rolled\n" +
                "2. If a piece is at the endpoint of a move, that piece is captured (removed from the game board)\n" +
                "3. A player may not capture its own pieces\n" +
                "4. The path a piece travels may not contain any piece (except if the moving piece is a knight)\n" +
                "5. A player may regardless of the dice roll execute a legal promotion move\n" +
                "6. Pawns:\n" +
                "    a. The first move of a pawn in a game may be 2 squares forward form the perspective of the playing player\n" +
                "    b. Otherwise a pawn may only move 1 square forward without capturing a piece or capture a piece diagonally forward from the perspective of the playing player\n" +
                "    c. Promotion: if a pawn reaches the opponents side of the game board, it becomes the piece type that was rolled\n" +
                "7. Rook: a rook may move horizontal, vertical, or initiate a castling move\n" +
                "8. Knight: a knight's move always consists of two horizontal or vertical squares and one diagonal\n" +
                "9. Bishop: a bishop may only move diagonally\n" +
                "10. Queen: a queen may move diagonally, horizontally, or vertically\n" +
                "11. King: a king may move like a queen (diagonally, horizontally, or vertically), but only one square, or it may initiate a castling move\n" +
                "12. Castling:\n" +
                "    a. If between a king and a rook of the same player there are no pieces\n" +
                "    b. Neither of them has moved this game\n" +
                "    c. And the king is not attacked (during the move) (if the king would stop, it would be attacked) during the move to come\n" +
                "    d. Than the king may move two squares towards the rook, and the rook may move to the position between the kings old and new position\n" +
                "13. The player that captures an opponent’s king is the winner, if the opponent has two kings (promotion), still a win\n" +
                "14. If one of the following statements is true, the game has ended with a draw:\n" +
                "    a. A player has no legal dice roll (no piece owned by this player can move)\n" +
                "    b. It has bin at least seventy-five moves since the last time a piece was captured, or a pawn has moved moved";
    }

    public static boolean draw(PieceInterface[][] board, boolean currentPlayer, Move previousMove, int movesSinceLastCaptureOrPawnMove){
        if(getMovablePieceTypes(currentPlayer, board, previousMove).size() == 0) return true;
        return movesSinceLastCaptureOrPawnMove >= 75;
    }
}