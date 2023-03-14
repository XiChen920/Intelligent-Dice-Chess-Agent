package tests;

import Model.BoardState;
import Model.CurrentModel;
import Model.Demo;
import Model.Move;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPossibleMoves
{
    CurrentModel mdl;

    @Test
    void testPossibleMovesPawn(){
        mdl = new CurrentModel();
        mdl.updateDice((byte) 1);

        List<Move> list = mdl.getAllPossibleMoves();

        assertTrue(!list.isEmpty());
        assertEquals(list.size(),16);

        assertEquals(list.get(0).getX1(), 2);
        assertEquals(list.get(0).getY1(), 0);

    }

    @Test
    void testPossibleMovesRook(){
        mdl = new CurrentModel();
        mdl.updateDice((byte) 2);

        List<Move> list = mdl.getAllPossibleMoves();

        assertTrue(list.isEmpty());
    }

    @Test
    void testPossibleMovesKnight(){
        mdl = new CurrentModel();
        mdl.updateDice((byte) 3);

        List<Move> list = mdl.getAllPossibleMoves();

        assertTrue(!list.isEmpty());
        assertEquals(list.size(),4);

        assertEquals(list.get(0).getX1(), 2);
        assertEquals(list.get(0).getY1(), 0);
        assertEquals(list.get(list.size() - 1).getX1(), 2);
        assertEquals(list.get(list.size() - 1).getY1(), 7);
    }

    @Test
    void testPossibleMovesQueen(){
        mdl = new CurrentModel();
        mdl.updateDice((byte) 5);

        List<Move> list = mdl.getAllPossibleMoves();

        assertTrue(list.isEmpty());
    }

    //TODO Pepijn check isLegal for king check, not displaying correct results
    @Test
    void testPossibleMovesKing(){
        mdl = new CurrentModel();
        mdl.updateDice((byte) 6);

        List<Move> list = mdl.getAllPossibleMoves();

        assertTrue(list.isEmpty());
    }
}