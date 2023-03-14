package tests;

import User.Agent.Agent;
import org.junit.jupiter.api.Test;

import Model.Model;
import Model.CurrentModel;
import Model.Move;
import User.Agent.MCTS.MCTS;
import User.Agent.MCTSAgent;

import Model.Piece;

import static org.junit.jupiter.api.Assertions.*;

public class MCTSAgentTesting
{
    private Agent mctsAgent_1;
    private Agent mctsAgent_2;
    private Model currentState = new CurrentModel();
    Agent currentPlayer;

    public void setup()
    {
        mctsAgent_1 = new MCTSAgent(currentState, false);
        mctsAgent_1.rollDice();
        mctsAgent_1.executePlay();
    }

    public void secondPlay()
    {
        currentState.updatePlayer(true);
        mctsAgent_2 = new MCTSAgent(currentState, true);

        mctsAgent_2.rollDice();
        mctsAgent_2.executePlay();
    }

    @Test
    void testMCTSSinglePlayResult()
    {

        MCTS.clearSubTree();
        setup();

        //Dice roll from starting state must be either 1, or 3
        byte diceRoll = mctsAgent_1.getDiceRoll();
        assertTrue(diceRoll == 3 || diceRoll == 1);

        //Determine correct player
        boolean playerFlag = mctsAgent_1.getPlayerFlag();
        assertFalse(playerFlag);

        //Determine that dice roll has moved correct piece
        Move movedUsed = mctsAgent_1.getMove();
        Piece movedPiece = mctsAgent_1.getBoard().getPieceAt(movedUsed.getX1(), movedUsed.getY1());
        assertEquals(diceRoll, movedPiece.getTypeOfPiece());
    }

    @Test
    void testDoublePlayResult()
    {
        MCTS.clearSubTree();
        setup();
        secondPlay();

        //Determine correct player
        boolean playerFlag = mctsAgent_2.getPlayerFlag();
        assertTrue(playerFlag);

        //Determine this playerflag is mirrored in model
        boolean modelFlag = currentState.getPlayer();
        assertEquals(playerFlag, modelFlag);

        //Determine dice roll in range
        byte diceRoll = mctsAgent_2.getDiceRoll();
        assertTrue(inRange(diceRoll));
    }

    @Test
    void fullPlayout()
    {
        MCTS.clearSubTree();
        int numberOfPlays = 5;

        mctsAgent_1 = new MCTSAgent(currentState, false);
        mctsAgent_2 = new MCTSAgent(currentState, true);
        boolean playerFlag = false;

        currentPlayer = mctsAgent_1;

        int index =0;
        while(index < numberOfPlays)
        {
            currentPlayer.rollDice();
            currentPlayer.executePlay();

            playerFlag = !playerFlag;
            currentState.updatePlayer(playerFlag);
            currentPlayer = changeAgent(currentPlayer);
            index++;
        }
        if(numberOfPlays % 2 == 0)
        {
            assertFalse(playerFlag);
        }
        else
        {
            assertTrue(playerFlag);
        }

    }

    public Agent changeAgent(Agent currentPlayer)
    {
        if(currentPlayer == mctsAgent_1)
        {
            return mctsAgent_2;
        }
        return mctsAgent_1;
    }

    public boolean inRange(int diceValue)
    {
        if(diceValue == 0 || diceValue > 6)
        {
            return false;
        }
        return true;
    }

}
