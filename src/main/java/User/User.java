package User;

import Controller.Command;

import Model.Move;

import java.io.Serializable;

public interface User extends Serializable
{
    //Actions
    public void rollDice();
    public void movePiece();
    public void setPlayerId(boolean playerId);
    public void executePlay();
    public void changePlayer();

    //Accessors
    public byte getDiceRoll();
    public boolean getPlayerFlag();
    public Move getMove();
    public void setMove(Move currentMove);

    public void executeCommand(Command command);
}
