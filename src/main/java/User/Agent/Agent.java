package User.Agent;

import Controller.Command;
import Model.Model;
import Model.Move;
import User.User;
import User.Dice;
import Controller.DiceCommand;
import Model.BoardState;
import Controller.PlayerCommand;
import Controller.BoardCommand;

public abstract class Agent implements User{
    protected Move AIMove;
    protected Model modelReference;
    protected byte diceRollValue;
    protected boolean playerId;

    public Agent(Model modelReference, boolean playerId)
    {
        this.modelReference = modelReference;
        this.playerId = playerId;
    }

    /**
     * switch the player to current player id
     * white- false
     * black- true
     */
    public void changePlayer()
    {
        PlayerCommand AICommand = new PlayerCommand(modelReference, this);
        executeCommand(AICommand);
    }

    public void executePlay()
    {
        generateMove();
        movePiece();
    }

    public abstract void generateMove();

    /**
     * generate a dice value
     */
    public void rollDice()
    {
        Dice dice = new Dice(modelReference);
        diceRollValue = (byte) dice.getDiceRoll();

        DiceCommand diceCommand = new DiceCommand(modelReference, this);
        executeCommand(diceCommand);
    }

    /**
     * set a dice value
     * @param dice  byte 1-6
     */
    public void setDice(byte dice){
        diceRollValue = dice;
        DiceCommand diceCommand = new DiceCommand(modelReference, this);
        executeCommand(diceCommand);
    }


    public BoardState getBoard()
    {
        return modelReference.getBoardState();
    }

    public void movePiece()
    {
        PlayerCommand AICommand = new PlayerCommand(modelReference, this);
        executeCommand(AICommand);

        BoardCommand boardCommand = new BoardCommand(modelReference, this);
        executeCommand(boardCommand);
    }

    public void executeCommand(Command command)
    {
        command.execute();
    }

    public byte getDiceRoll()
    {
        return diceRollValue;
    }

    public boolean getPlayerFlag()
    {
        return playerId;
    }

    public void setPlayerId(boolean playerId)
    {
        this.playerId = playerId;
    }

    public void setMove(Move currentMove)
    {
        AIMove = currentMove;
    }

    public Move getMove()
    {
        return AIMove;
    }

    /**
     * Sets the model for the agent to play in
     * @param model the new model
     */
    public void setModel(Model model){
        this.modelReference = model;
    }

    public Model getModel(){return modelReference;}


    /**
     * Clones this into a copy of ans
     * @param ans the Agent to copy
     */
    protected void clone(Agent ans){
        if(ans.getMove() != null) this.setMove((Move) ans.getMove().clone());
        else this.setMove(null);
        if(ans.getModel() != null) this.setModel((Model) ans.modelReference.clone());
        else this.setModel(null);
        this.diceRollValue = ans.diceRollValue;
        this.setPlayerId(ans.playerId);
    }
}