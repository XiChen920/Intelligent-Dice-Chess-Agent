package Model;

import Controller.Command;

import java.io.Serializable;
import java.util.List;

public interface Model extends Serializable
{
    public void updateBoardModel(Move userMove);

    public void updateDice(byte diceRoll);

    public void updatePlayer(boolean playerId);

    public BoardState getBoardState();

    public void resetLeader();

    public Model clone();

    public boolean getPlayer();

    public byte getCurrentDiceRoll();

    public List<Move> getAllPossibleMoves();

    public Boolean getCurrentLeader();

    public void resetBoard();

    public Move getPreviousMove();

    public List<Byte> getMovablePieceTypes();

    static public Boolean equals(Model a, Model b){
        return a.getBoardState().equals(b.getBoardState()) &&
                a.getPlayer() == b.getPlayer() &&
                a.getCurrentDiceRoll() == b.getCurrentDiceRoll() &&
                a.getCurrentLeader() == b.getCurrentLeader();
    }

   public void setCurrentLeader(boolean b);
   public boolean draw();
}
