package tests;

import Model.CurrentModel;

import Model.Model;
import Model.Move;

import User.Agent.BaselineAgent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAI
{
    private CurrentModel testingModel = new CurrentModel();

    @Test
    void testBaselineDiceRoll()
    {
        BaselineAgent baselineAgent = new BaselineAgent(testingModel, false);
        baselineAgent.rollDice();

        assertTrue(inRange(baselineAgent.getDiceRoll()));
        assertTrue(inRange(testingModel.getCurrentDiceRoll()));
    }

    @Test
    void testBaselineAIMoveGeneration()
    {
        testingModel.updateDice((byte)1);
        BaselineAgent baselineAgent = new BaselineAgent(testingModel, false);

        baselineAgent.generateMove();
        Move chosenMove = baselineAgent.getMove();

        assertTrue(chosenMove != null);
        System.out.println(chosenMove.displayMove());
    }

    @Test
    void testBaselineAIMoveInModel()
    {
        BaselineAgent baselineAgent = new BaselineAgent(testingModel, false);
        baselineAgent.rollDice();
        baselineAgent.executePlay();

        int originalX = baselineAgent.getMove().getX0();
        int originalY = baselineAgent.getMove().getY0();
        int nextX = baselineAgent.getMove().getX1();
        int nextY = baselineAgent.getMove().getY1();

        assertTrue(testingModel.getBoardState().getPieceAt(originalX, originalY) == null);
        assertTrue(testingModel.getBoardState().getPieceAt(nextX, nextY) != null);
    }

    public boolean inRange(int diceValue)
    {
        if(diceValue == 0 || diceValue > 6)
        {
            return false;
        }
        return true;
    }

    @Test
    void playOff(){
        CurrentModel.print = false;
        int maxNumOfTurns = 10000;
        Model arena = new CurrentModel();
        BaselineAgent[] players = {new BaselineAgent(arena, false),new BaselineAgent(arena, true)};
        boolean playerTurn = false;
        byte playerTurnIndex;
        for(int i = 0; i < maxNumOfTurns; i++){
            if(playerTurn) playerTurnIndex = 1;
            else playerTurnIndex = 0;
            players[playerTurnIndex].rollDice();
            players[playerTurnIndex].executePlay();
            playerTurn = !playerTurn;
            arena.updatePlayer(playerTurn);
            if(arena.getCurrentLeader() != null) break;
        }
        //This method proves that 2 baseline agents can play against another until one wins or a maximum number of turns
    }
}
