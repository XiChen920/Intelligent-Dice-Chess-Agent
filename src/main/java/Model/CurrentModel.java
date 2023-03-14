package Model;

import View.AlertWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CurrentModel implements Model
{
    protected BoardState currentBoard;
    protected byte diceRoll;
    protected boolean player;
    protected Boolean currentLeader;
    public static boolean GUI = true;
    public static boolean DEBUG = false;
    public static boolean print = false;

    public CurrentModel(BoardState currentBoard, byte diceRoll, boolean player)
    {
        this.currentBoard = currentBoard;
        this.diceRoll = diceRoll;
        this.player = player;
    }

    public CurrentModel()
    {
        currentBoard = new BoardState();
        diceRoll = 0;
        player = false;
    }

    public CurrentModel(BoardState currentBoard, boolean player)
    {
        this.currentBoard = currentBoard;
        this.player = player;
    }

    //Illegal moves result in the loss of the players current turn.
    public void updateBoardModel(Move userMove)
    {
        try
        {
            currentLeader = currentBoard.updateState(userMove, player, diceRoll);
            if(DEBUG)
            {
                System.out.println("Successful Move for player " + getPlayer());
                System.out.println("----------");
            }

        }
        catch(IllegalMoveExemption illegalMoveExemption)
        {
            if(DEBUG)
            {
                System.out.println("Illegal move detected for player: " + getPlayer()+" , move was "+userMove);
            }
            if(GUI) 
            {
                illegalMovePopUp();
            } else throw new IllegalArgumentException("Illegal move detected for player: " + getPlayer()+" , move was "+userMove);
        }
    }

    public static void illegalMovePopUp(){
        AlertWindow alert = new AlertWindow();
        alert.display("Illegal move", "Your last move was an illegal move. \n You have forfeited this round...");
    }

    public void updateDice(byte diceRoll)
    {
        this.diceRoll = diceRoll;
        if(DEBUG)
        {
            System.out.println("New dice roll is: " + diceRoll);
        }
    }

    public void updatePlayer(boolean playerId)
    {
        this.player = playerId;
    }

    public void resetBoard()
    {
        currentBoard = new BoardState();
    }

    @Override
    public Move getPreviousMove() {
        return currentBoard.getPreviousMove();
    }

    @Override
    public List<Byte> getMovablePieceTypes() {
        return Rules.getMovablePieceTypes(player, currentBoard.getBoardState(), getPreviousMove());
    }

    public void resetLeader(){currentLeader = null;}

    public boolean getPlayer()
    {
        return player;
    }

    public BoardState getBoardState()
    {
        return currentBoard;
    }

    public Model clone()
    {
        return new CurrentModel(currentBoard.clone(), diceRoll, player);
    }

    public byte getCurrentDiceRoll()
    {
        return diceRoll;
    }

    public Boolean getCurrentLeader()
    {
        return currentLeader;
    }

    public List<Move> getAllPossibleMoves(){
        return Rules.getAllPossibleMoves(currentBoard.getBoardState(), player, diceRoll, currentBoard.getPreviousMove());
    }

    public void setCurrentLeader(boolean b){
        currentLeader = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentModel)) return false;
        CurrentModel model = (CurrentModel) o;
        return diceRoll == model.diceRoll && getPlayer() == model.getPlayer() && Objects.equals(currentBoard, model.currentBoard) && Objects.equals(getCurrentLeader(), model.getCurrentLeader());
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentBoard, diceRoll, getPlayer(), getCurrentLeader());
    }

    @Override
    public boolean draw() {
        return currentBoard.draw(player, diceRoll);
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder().append(currentBoard.toString()).append("\n");
        ans.append("dice roll = ").append(diceRoll).append("\n");
        ans.append("player = ").append(player).append("\n");
        ans.append("win state = ").append(currentLeader);
        return ans.toString();

    }
}
