package User.Agent;

import Controller.BoardCommand;
import Controller.DiceCommand;
import Controller.Command;

import Controller.PlayerCommand;
import java.util.Random;
import Model.BoardState;
import Model.Model;
import Model.Move;
import User.Agent.Agent;

import java.util.List;

/**
 * Baseline agent that choose random legal action to perform
 */
public class BaselineAgent extends Agent
{
    public BaselineAgent(Model modelReference, boolean playerId)
    {
        super(modelReference, playerId);
    }

    /**
     * generate a random move from all possible moves for the agent
     */
    @Override
    public void generateMove()
    {
        changePlayer();
        Random randomSelection = new Random();
        List<Move> allPossibleMoves = modelReference.getAllPossibleMoves();
        AIMove = allPossibleMoves.get(randomSelection.nextInt(allPossibleMoves.size()));
    }

    /**
     * execute move
     */
    @Override
    public void movePiece()
    {
        BoardCommand boardCommand = new BoardCommand(modelReference, this);
        executeCommand(boardCommand);
    }
}
