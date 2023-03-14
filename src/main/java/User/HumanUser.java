package User;

import Controller.*;
import Model.Model;
import Model.Move;
import Model.MoveHuman;

import Controller.BoardCommand;

public class HumanUser implements User
{
    private Move userMove;
    private Model modelReference;
    private boolean playerId;
    private byte diceRollValue;

    public HumanUser(Model modelReference, boolean playerId)
    {
        this.modelReference = modelReference;
        this.playerId = playerId;
    }

    public void executePlay()
    {
        movePiece();
    }

    public void changePlayer()
    {
        PlayerCommand playerCommand = new PlayerCommand(modelReference, this);
        executeCommand(playerCommand);
    }

    public void rollDice()
    {
        Dice dice = new Dice(modelReference);
        diceRollValue = (byte) dice.getDiceRoll();

        DiceCommand diceCommand = new DiceCommand(modelReference, this);
        executeCommand(diceCommand);
    }

    public void movePiece()
    {
        BoardCommand boardCommand = new BoardCommand(modelReference, this);
        executeCommand(boardCommand);
    }

    public void generateMove(byte x0, byte x1, byte y0, byte y1)
    {
        userMove = new MoveHuman(x0, x1, y0, y1);
    }

    public byte getDiceRoll()
    {
        return diceRollValue;
    }

    public Move getMove()
    {
        return userMove;
    }

    public void setMove(Move currentMove)
    {
        userMove = currentMove;
    }

    public boolean getPlayerFlag()
    {
        return playerId;
    }

    public void setPlayerId(boolean playerId)
    {
        this.playerId = playerId;
        PlayerCommand playerCommand = new PlayerCommand(modelReference, this);
        executeCommand(playerCommand);
    }

    public void executeCommand(Command command)
    {
        command.execute();
    }
}
