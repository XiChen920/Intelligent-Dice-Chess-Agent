package tests;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import User.HumanUser;

public class TestController
{
    private CurrentModel testModel;
    private BoardState testBoard;
    private HumanUser testHumanUser;
    private boolean playerId = false;

    @BeforeEach
    void setup()
    {
        //Setup
        testBoard = new BoardState();
        testModel = new CurrentModel(testBoard, (byte)1, playerId);
        testHumanUser = new HumanUser(testModel, playerId);
    }

    @Test
    void testDiceCommand()
    {
        testHumanUser.rollDice();
        byte currentUserDiceRoll = testHumanUser.getDiceRoll();
        byte currentModelDiceRoll = testModel.getCurrentDiceRoll();

        System.out.println("Model dice: " + currentModelDiceRoll + "| User dice: " + currentUserDiceRoll);
        assertEquals(currentModelDiceRoll, currentUserDiceRoll);
    }

    @Test
    void testPlayer()
    {
        assertTrue(testModel.getPlayer() == playerId);
    }

    //TODO Ensure test passes as current issue regarding illegalMove check

    void testBoardCommand()
    {
        testHumanUser.generateMove((byte) 1, (byte) 3, (byte) 0, (byte) 0);
        testHumanUser.movePiece();

        Move currentMove = testHumanUser.getMove();
        System.out.println(currentMove.displayMove());

        BoardState currentBoardState = testModel.getBoardState();

        Piece prevousPiece = currentBoardState.getPieceAt(currentMove.getX0(), currentMove.getY0());
        System.out.println(prevousPiece);

        Piece currentPiece = currentBoardState.getPieceAt(currentMove.getX1(), currentMove.getY1());
        System.out.println(currentPiece);

        assertSame(prevousPiece,  null);
        assertNotNull(currentPiece);
    }
}
