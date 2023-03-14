package Model;

import java.io.Serializable;
import java.util.Objects;

public class BoardState implements Serializable
{
    protected PieceInterface[][] pieceSet;
    protected Move previousMove= null;

    /**
     * Builds a board from a string array
     * @param board the board formatted as at (i,j) the piece noted as a two character combination of the player (B, b, W or w) and the piece [1,6] seperated by spaces or commas in the right sequence
     */
    public BoardState(String[] board){
        pieceSet = buildBoard(board);
    }

    /**
     * Builds a board from a string array
     * @param board the board formatted as at (i,j) the piece noted as a two character combination of the player (B, b, W or w) and the piece [1,6]
     */
    public BoardState(String[][] board){
        pieceSet = buildBoard(board);
    }

    /**
     * Builds a board from a string array
     * @param board the board formatted as at (i,j) the piece noted as a two character combination of the player (B, b, W or w) and the piece [1,6] seperated by spaces or commas in the right sequence
     * @return the board
     */
    public static PieceInterface[][] buildBoard(String[] board){
        String[][] ans = new String[board.length][];
        for(byte i = 0; i < board.length; i++){
            if(board[i] == null || board[i].equals("")) ans[i] = new String[]{null, null, null, null,null, null, null, null};
            else if(board[i].contains(",")) ans[i] = board[i].split(",");
            else if(board[i].contains(" ")) ans[i] = board[i].split(" ");
            else throw new IllegalArgumentException("Missing separators");
        }
        return buildBoard(ans);
    }

    /**
     * Builds a board from a string array
     * @param board the board formatted as at (i,j) the piece noted as a two character combination of the player (B, b, W or w) and the piece [1,6]
     * @return the board
     */
    public static PieceInterface[][] buildBoard(String[][] board){
        if (board.length != 8) throw new IllegalArgumentException("board of insufficient size");
        PieceInterface[][] ans = new PieceInterface[8][8];
        byte i, j, pieceType;
        boolean player;
        for(i = 0; i < ans.length; i++){
            if(board[i] == null) ans[i] = new PieceInterface[]{null, null, null, null, null, null, null, null};
            else if(board[i].length != 8) throw new IllegalArgumentException("board of insufficient size");
            else for(j = 0; j < ans[i].length; j++){
                if(board[i][j] == null || Objects.equals(board[i][j], "")) ans[i][j] = null;
                else if(board[i][j].length()!=2) throw new IllegalArgumentException("Piece "+i+", "+j+" is wrong encoded");
                else {
                    if (board[i][j].contains("B") || board[i][j].contains("b")) player = true;
                    else if (board[i][j].contains("W") || board[i][j].contains("w")) player = false;
                    else throw new IllegalArgumentException("Piece " + i + ", " + j + " is wrong encoded");
                    try {
                        pieceType = Byte.parseByte(board[i][j].substring(0, 1));
                    } catch (NumberFormatException numberFormatException) {
                        try {
                            pieceType = Byte.parseByte(board[i][j].substring(1, 2));
                        } catch (NumberFormatException numberFormatException2) {
                            try {
                                pieceType = Byte.parseByte(board[i][j].substring(2, 3));
                            } catch (NumberFormatException numberFormatException3) {
                                throw new IllegalArgumentException("Piece " + i + ", " + j + " is wrong encoded");
                            }
                        }
                    }
                    ans[i][j] = new Piece(pieceType, player);
                }
            }
        }
        return ans;
    }
    public BoardState(){
        byte[][] templatePieceTypes = {
                {2,3,4,5,6,4,3,2},
                {1,1,1,1,1,1,1,1},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {1,1,1,1,1,1,1,1},
                {2,3,4,5,6,4,3,2}
        };
        boolean[] playerRows = {false, false, false, false, true, true, true, true};
        byte i, j;
        pieceSet = new PieceInterface[8][8];
        for(i =0; i < pieceSet.length; i++){
            for(j = 0; j < pieceSet[i].length; j++){
                if(templatePieceTypes[i][j] != 0) pieceSet[i][j] = new Piece(templatePieceTypes[i][j], playerRows[i]);
                else pieceSet[i][j] = null;
            }
        }
        this.movesSinceLastCaptureOrPawnMove = 0;
    }

    public BoardState(PieceInterface[][] initialSet)
    {
        if(!checkBoardSize(initialSet)) throw new IllegalArgumentException();
        pieceSet = initialSet;
        this.movesSinceLastCaptureOrPawnMove = 0;
    }

    public static boolean checkBoardSize(PieceInterface[][] board){
        if(board.length!= 8) return false;
        for(byte i = 0; i < board.length; i++){
            if(board[i].length!=8) return false;
        }
        return true;
    }

    public PieceInterface[][] getBoardState()
    {
        return pieceSet;
    }

    public BoardState clone()
    {
        PieceInterface[][] ansInput = new PieceInterface[pieceSet.length][];
        byte i, j;
        for(i = 0; i < ansInput.length; i++){
            ansInput[i] = new PieceInterface[pieceSet[i].length];
            for(j = 0; j < ansInput[i].length; j++){
                if(pieceSet[i][j] != null) ansInput[i][j] = pieceSet[i][j].clone();
            }
        }
        BoardState ans = new BoardState(ansInput);
        ans.movesSinceLastCaptureOrPawnMove = this.movesSinceLastCaptureOrPawnMove;
        if(previousMove != null) ans.previousMove = (Move) this.previousMove.clone();
        return ans;
    }

    public void setPieceSet(PieceInterface[][] pieceSet){
        if(!checkBoardSize(pieceSet)) throw new IllegalArgumentException();
        this.pieceSet = pieceSet;
    }

    protected int movesSinceLastCaptureOrPawnMove;
    public Boolean updateState(Move move, boolean currentPlayer, byte dice) throws IllegalMoveExemption
    {
        try {
            if(!Rules.isLegal(move, pieceSet, currentPlayer, dice, previousMove)) throw new IllegalMoveExemption();
            else previousMove = move;
        } catch (Rules.PromotionSpecialMove promotionSpecialMove) {
            byte x = promotionSpecialMove.getEffectedPosition()[0], y = promotionSpecialMove.getEffectedPosition()[1];
            pieceSet[x][y].setTypeOfPiece(dice);
            previousMove = move;
        } catch (Rules.EnPassantSpecialMove enPassantSpecialMove) {
            byte x = enPassantSpecialMove.getEffectedPosition()[0], y = enPassantSpecialMove.getEffectedPosition()[1];
            pieceSet[x][y] = null;
            previousMove = move;
            movesSinceLastCaptureOrPawnMove = 0;
        } catch (Rules.CastlingSpecialMove castlingSpecialMove) {
            executeMove(castlingSpecialMove.getPartnerMove(), currentPlayer);
            previousMove = move;
            movesSinceLastCaptureOrPawnMove--;
        }
        return executeMove(move, currentPlayer);
    }
    public Boolean executeMove(Move move, boolean player){
        movesSinceLastCaptureOrPawnMove++;
        byte x0 = move.getX0(), x1 = move.getX1(), y0 = move.getY0(), y1= move.getY1();
        if(x0 == x1 && y0 == y1) return null;
        Boolean ans = null;
        if(pieceSet[x0][y0] == null) {
           throw new IllegalArgumentException("The Move move "+move+" tries to move a piece not on the board");
        }

        if(pieceSet[x1][y1] != null && pieceSet[x1][y1].getTypeOfPiece() == 6) ans = player;
        if(pieceSet[x1][y1] != null){
            movesSinceLastCaptureOrPawnMove = 0;
        }
        if(pieceSet[x0][y0].getTypeOfPiece() == (byte) 1){
            movesSinceLastCaptureOrPawnMove = 0;
        }

        pieceSet[x1][y1] = pieceSet[x0][y0];
        pieceSet[x1][y1].moved();
        pieceSet[x0][y0] = null;
        return ans;
    }

    public Piece getPieceAt(int x, int y)
    {
        return (Piece) pieceSet[x][y];
    }

    @Override
    public boolean equals(Object other) {
        if(!other.getClass().equals(this.getClass())) return false;
        BoardState otherBoardState = (BoardState) other;
        PieceInterface[][] a = pieceSet, b = otherBoardState.getBoardState();

        if (a.length != b.length) {
            System.out.println("not of equal length");
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) {
                System.out.println("not of equal length x@" + i);
                return false;
            }
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != null && b[i][j] != null) {
                    if (a[i][j] == null && b[i][j] != null) {
                        System.out.println("not of equal value (1 is null and the other is not) x@" + i + " y@" + j);
                        return false;
                    } else if (a[i][j] != null && b[i][j] == null) {
                        System.out.println("not of equal value (1 is null and the other is not) x@" + i + " y@" + j);
                        return false;
                    } else if (a[i][j] == b[i][j] || !a[i][j].equals(b[i][j])) {
                        System.out.println("not of equal value (1 is " + a[i][j].toString() + " and the other is " + b[i][j].toString() + ") x@" + i + " y@" + j);
                        return false;
                    }
                }
            }
        }
        return true;

    }

    public Move getPreviousMove() {return previousMove;}
    public PieceInterface[][] getPieceSet()
    {
        return pieceSet;
    }

    public boolean draw(boolean currentPlayer, byte dice){
        return Rules.draw(getBoardState(), currentPlayer, previousMove, movesSinceLastCaptureOrPawnMove);
    }

    public int getMovesSinceLastCaptureOrPawnMove() {
        return movesSinceLastCaptureOrPawnMove;
    }

    @Override
    public String toString() {
        String[] additions = {"1 = Pawn", "2 = Rook", "3 = Knight", "4 = Bishop", "5 = Queen", "6 = King", "Illegal move is forfeit of turn", "With castling, promotion and en passant"};
        StringBuilder ans = new StringBuilder();
        ans.append("  a |b |c |d |e |f |g |h\n");
        for(int i = pieceSet.length-1; i >= 0; i--){
            ans.append(i+1);
            for(int j = 0; j < pieceSet[i].length; j++) {
                ans.append("|");
                if (pieceSet[i][j] == null) ans.append("  ");
                else ans.append(pieceSet[i][j].toStringShort());
            }
            ans.append("|").append(i+1).append("   ").append(additions[i]);
            ans.append("\n");
        }
        ans.append("  a |b |c |d |e |f |g |h\n");
        ans.append("Moves since last capture or pawn move = "+movesSinceLastCaptureOrPawnMove+"\n");
        ans.append("Last move = "+previousMove);
        return ans.toString();
    }
}

