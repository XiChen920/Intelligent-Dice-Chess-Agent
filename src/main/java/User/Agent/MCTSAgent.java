package User.Agent;

import Controller.BoardCommand;
import Controller.DiceCommand;
import Model.Model;
import User.Agent.MCTS.MCTS;

/**
 * Class seeks to provide an Agent with the use of the Monte Carlo Search Tree Algorithm, to determine the best move from a current state.
 */
public class MCTSAgent extends Agent
{
    MCTS mcts;

    /**
     * MCTS Agent constructor.
     * @param modelReference The current state of the game
     * @param playerId Identifier of black or white player for the game
     */
    public MCTSAgent(Model modelReference, boolean playerId)
    {
        super(modelReference, playerId);
        mcts = new MCTS(modelReference);
    }

    /**
     * Method executes the agent play, derived from the MCTS algorithm.
     * Communicates with the model, to reflect changes into the GUI
     */
    @Override
    public void executePlay()
    {
        generateMove();
        movePiece();
    }

    /**
     * Seeks to populate the AIMove field with the MCTS calculated move.
     */
    @Override
    public void generateMove()
    {
        changePlayer();
        AIMove = mcts.getResultMove();
    }

    /**
     * Method moves the determined piece within the current model, in order to update the state based on the MCTS calculation.
     */
    @Override
    public void movePiece()
    {
        BoardCommand boardCommand = new BoardCommand(modelReference, this);
        executeCommand(boardCommand);
    }

    /**
     * Method initializes the MCTS tree based on the dice rolled
     * Dice rolled is communicated through to the model.
     */
    @Override
    public void rollDice()
    {
        mcts = new MCTS(modelReference);
        mcts.MCTSAction();
        diceRollValue = mcts.getDiceUsed();

        DiceCommand diceCommand = new DiceCommand(modelReference, this);
        executeCommand(diceCommand);
    }

    /**
     * Modifier to alter the MCTS resource limit.
     * @param limit
     */
    public static void setMCTSResourceLimit(double limit)
    {
        MCTS.alterResourceLimit(limit);
    }

    /**
     * Accessor method to access the current MCTS resource limit
     * @return The current MCTS resource limit
     */
    public static double getMCTSResourceLimit()
    {
        return MCTS.getResourceLimit();
    }

    public MCTS getMcts(){return mcts;}
}
