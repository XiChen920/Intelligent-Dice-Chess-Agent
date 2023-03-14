package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Build for research question 3
 */
public class StandardChessModel extends CurrentModel{
    public StandardChessModel() {
        super(null, false);
        currentBoard = new StandardChessBoardState();
    }

    @Override
    public Model clone() {
        StandardChessModel ans = new StandardChessModel();
        ans.currentBoard = currentBoard.clone();
        ans.diceRoll = diceRoll;
        ans.player = player;
        ans.currentLeader = currentLeader;
        return ans;
    }

    @Override
    public List<Byte> getMovablePieceTypes() {
        return StandardChessRules.getMovablePieceTypes();
    }

    @Override
    public List<Move> getAllPossibleMoves() {
        return StandardChessRules.getAllPossibleMoves(currentBoard.getBoardState(), getPlayer(), getPreviousMove(), (StandardChessBoardState) currentBoard, false);
    }

    protected static class StandardChessBoardState extends BoardState{
        public StandardChessBoardState() {
        }

        public StandardChessBoardState(PieceInterface[][] initialSet) {
            super(initialSet);
        }

        @Override
        public Boolean updateState(Move move, boolean currentPlayer, byte dice) throws IllegalMoveExemption {
            try {
                if(!StandardChessRules.isLegal(move, pieceSet, currentPlayer, previousMove, this, false)) throw new IllegalMoveExemption();
                else previousMove = move;
            } catch (Rules.PromotionSpecialMove promotionSpecialMove) {
                byte x = promotionSpecialMove.getEffectedPosition()[0], y = promotionSpecialMove.getEffectedPosition()[1];
                pieceSet[x][y].setTypeOfPiece((byte) 5);
                previousMove = move;
            } catch (Rules.EnPassantSpecialMove enPassantSpecialMove) {
                byte x = enPassantSpecialMove.getEffectedPosition()[0], y = enPassantSpecialMove.getEffectedPosition()[1];
                pieceSet[x][y] = null;
                previousMove = move;
                movesSinceLastCaptureOrPawnMove = 0;
            } catch (Rules.CastlingSpecialMove castlingSpecialMove) {
                executeMove(castlingSpecialMove.getPartnerMove(), currentPlayer);
                previousMove = move;
            }
            Boolean ans = executeMove(move, currentPlayer);
            if(ans != null) return ans;
            if(StandardChessRules.checkMate(!currentPlayer, move, this)) return currentPlayer;
            return null;
        }

        private Boolean updateStateIgnoringCheck(Move move, boolean currentPlayer){
            try {
                if(!StandardChessRules.isLegal(move, pieceSet, currentPlayer, previousMove, this, true)) throw new IllegalArgumentException("This should not be possible");
                else previousMove = move;
            } catch (Rules.PromotionSpecialMove promotionSpecialMove) {
                byte x = promotionSpecialMove.getEffectedPosition()[0], y = promotionSpecialMove.getEffectedPosition()[1];
                pieceSet[x][y].setTypeOfPiece((byte) 6);
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

        @Override
        public boolean draw(boolean currentPlayer, byte dice){
            return StandardChessRules.draw(getBoardState(), currentPlayer, previousMove, movesSinceLastCaptureOrPawnMove, this);
        }

        @Override
        public StandardChessBoardState clone()
        {
            PieceInterface[][] ansInput = new PieceInterface[pieceSet.length][];
            byte i, j;
            for(i = 0; i < ansInput.length; i++){
                ansInput[i] = new PieceInterface[pieceSet[i].length];
                for(j = 0; j < ansInput[i].length; j++){
                    if(pieceSet[i][j] != null) ansInput[i][j] = pieceSet[i][j].clone();
                }
            }
            StandardChessBoardState ans = new StandardChessBoardState(ansInput);
            ans.movesSinceLastCaptureOrPawnMove = this.movesSinceLastCaptureOrPawnMove;
            if(previousMove != null) ans.previousMove = (Move) this.previousMove.clone();
            return ans;
        }
    }

    protected static class StandardChessRules{
        public static boolean isLegal(Move move, PieceInterface[][] board, boolean currentPlayer, Move previousMove, StandardChessBoardState origin, boolean ignoreCheck) throws Rules.EnPassantSpecialMove, Rules.CastlingSpecialMove, Rules.PromotionSpecialMove{
            if(board[move.getX0()][move.getY0()] == null) return false;
            byte dice = board[move.getX0()][move.getY0()].getTypeOfPiece();
            Rules.EnPassantSpecialMove enPassantSpecialMove = null;
            Rules.CastlingSpecialMove castlingSpecialMove = null;
            Rules.PromotionSpecialMove promotionSpecialMove = null;
            try {
                if (!Rules.isLegal(move, board, currentPlayer, dice, previousMove)) return false;
            } catch (Rules.PromotionSpecialMove e0){
                promotionSpecialMove = e0;
            } catch (Rules.CastlingSpecialMove e1){
                castlingSpecialMove = e1;
            } catch (Rules.EnPassantSpecialMove e2){
                enPassantSpecialMove = e2;
            }
            if(ignoreCheck) {
                if(enPassantSpecialMove != null) throw enPassantSpecialMove;
                if(castlingSpecialMove != null) throw castlingSpecialMove;
                if(promotionSpecialMove != null) throw promotionSpecialMove;
                return true;
            }

            //Ignoring The rule that you may not be in check or checkmate, so far done
            StandardChessBoardState temp = new StandardChessBoardState(origin.clone().getBoardState());
            Boolean temp1 = temp.updateStateIgnoringCheck(move, currentPlayer);
            if(temp1 != null && temp1 == !currentPlayer) return false;
            if (inCheck(move, currentPlayer, temp)) return false;
            if(enPassantSpecialMove != null) throw enPassantSpecialMove;
            if(castlingSpecialMove != null) throw castlingSpecialMove;
            if(promotionSpecialMove != null) throw promotionSpecialMove;
            return true;
        }

        public static boolean inCheck(Move previousMove, boolean currentPlayer, StandardChessBoardState origin) {
            List<Move> checkOptions = getAllPossibleMoves(origin.getPieceSet(), !currentPlayer, previousMove, origin, true);
            for(Move checkOption: checkOptions){
                if(origin.pieceSet[checkOption.getX1()][checkOption.getY1()] != null &&
                        origin.pieceSet[checkOption.getX1()][checkOption.getY1()].getTypeOfPiece() == (byte) 6 &&
                        origin.pieceSet[checkOption.getX1()][checkOption.getY1()].getPlayer() == currentPlayer) {
                    return true;
                }
            }
            return false;
        }

        public static List<Move> getAllPossibleMoves(PieceInterface[][] board, boolean currentPlayer, Move previousMove, StandardChessBoardState origin, boolean ignoreCheck){
            List<Move> ans = new ArrayList<>();
            byte x0, x1, y0, y1;
            Move temp;
            for(x0 = 0; x0 < board.length; x0++){
                for (y0 = 0; y0 < board[x0].length; y0++){
                    if(board[x0][y0] != null && board[x0][y0].getPlayer()==currentPlayer) for (x1 = 0; x1 < board.length; x1++){
                        for(y1 = 0; y1 < board[x1].length; y1++){
                            temp = new MoveHuman(x0,x1, y0, y1);
                            try {
                                if (isLegal(temp, board, currentPlayer, previousMove, origin, ignoreCheck)) ans.add(temp);
                            } catch (Rules.EnPassantSpecialMove | Rules.CastlingSpecialMove | Rules.PromotionSpecialMove specialMove) {ans.add(temp);}
                        }
                    }
                }
            }
            return ans;
        }

        static public boolean checkMate(boolean currentPlayer, Move previousMove, StandardChessBoardState origin){
            return inCheck(previousMove, currentPlayer, origin) &&
                    getAllPossibleMoves(origin.pieceSet, currentPlayer, previousMove, origin, false).size() == 0;
        }

        static public List<Byte> getMovablePieceTypes() {
            List<Byte> ans = new ArrayList<>();
            ans.add((byte)1);
            return ans;
        }

        public static boolean draw(PieceInterface[][] board, boolean currentPlayer, Move previousMove, int movesSinceLastCaptureOrPawnMove, StandardChessBoardState origin){
            if(getAllPossibleMoves(board,currentPlayer,previousMove, origin, false).size() == 0) return true;
            return movesSinceLastCaptureOrPawnMove >= 75;
        }
    }
}
