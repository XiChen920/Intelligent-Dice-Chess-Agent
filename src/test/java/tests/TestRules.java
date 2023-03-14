package tests;

import Model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static Model.Rules.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestRules {
    //Tests for 1.pawn
    /**
     * test pawn move forward one step
     *
     * move pawn from (1,0) to (2,0)
     * unblocked on the path
     *
     * legal move
     */
    @Test
    void legalPawnMoveForwardOne() {
        BoardState testState = new BoardState();
        PieceInterface[][] testBoard = testState.getBoardState();
        MoveHuman testMove = new MoveHuman((byte) 1, (byte) 2, (byte) 0, (byte) 0);
        assertTrue(pawnCheck(testMove, testBoard));
    }

    /**
     * test pawn(never move before) move forward 2 steps
     *
     * move pawn from (1,0) to (3,0)
     * unblocked on the path
     *
     * legal move
     */
    @Test
    void legalPawnMoveForwardTwo() {
        BoardState testState = new BoardState();
        PieceInterface[][] testBoard = testState.getBoardState();
        testBoard[1][0].setMoved(false);//the pawn never moved before
        MoveHuman testMove = new MoveHuman((byte) 1, (byte)3 , (byte) 0, (byte) 0);
        assertTrue(pawnCheck(testMove, testBoard));
    }

    /**
     * test pawn(moved before) move forward 2 steps
     *
     * move pawn from (1,0) to (3,0)
     * unblocked on the path
     *
     * illegal move
     */
    @Test
    void illegalPawnMoveForwardTwo() {
        BoardState testState = new BoardState();
        PieceInterface[][] testBoard = testState.getBoardState();
        testBoard[1][0].setMoved(true);//the pawn moved before
        MoveHuman testMove = new MoveHuman((byte) 1, (byte)3 , (byte) 0, (byte) 0);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    /**
     * test move pawn one step back
     *
     * move the pawn backward
     * move white pawn from (2,0) to (1,0)
     *
     * illegal move
     */
    @Test
    void pawnMoveBackward() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"","w1","w1","w1","w1","w1","w1","w1"},
                {"w1","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 1, (byte) 0, (byte) 0);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    /**
     * move pawn one step forward but there's a piece of the same color ahead of it
     *
     * illegal move
     */
    @Test
    void pawnMoveBlockedByFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","w5","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 1, (byte) 2, (byte) 1, (byte) 1);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    /**
     * move pawn one step forward but there's an opponent piece ahead of it
     *
     * illegal move
     */
    @Test
    void pawnMoveBlockedByOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","b5","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 1, (byte) 2, (byte) 1, (byte) 1);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    /**
     * move pawn one step diagonally
     *
     * illegal move
     */
    @Test
    void pawnMoveDiagonally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 1, (byte) 2, (byte) 1, (byte) 2);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    /**
     * test a pawn eats its opponent
     *
     * legal move
     */
    @Test
    void pawnEatOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","b1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 1, (byte) 2, (byte) 1, (byte) 2);
        assertTrue(pawnCheck(testMove, testBoard));
    }

    /**
     * test a pawn eats other piece of the same color
     *
     * illegal move
     */
    @Test
    void pawnEatFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","","w1","w1","w1","w1","w1"},
                {"","","w1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 1, (byte) 2, (byte) 1, (byte) 2);
        assertFalse(pawnCheck(testMove, testBoard));
    }

    //Tests for 2.rook
    /**
     * move rook horizontally from (3,2) to (3,7)
     *
     * legal move
     */
    @Test
    void rookMoveHorizontally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 3, (byte) 2, (byte) 7);
        assertTrue(rookCheck(testMove, testBoard));
    }

    /**
     * move rook vertically from (3,2) to (5,2)
     *
     * legal move
     */
    @Test
    void rookMoveVertically() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 5, (byte) 2, (byte) 2);
        assertTrue(rookCheck(testMove, testBoard));
    }

    /**
     *rook move not in a vertical or horizontal way
     *
     * illegal move
     */
    @Test
    void illegalRookMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 4, (byte) 2, (byte) 3);
        assertFalse(rookCheck(testMove, testBoard));
    }

    /**
     *rook move path blocked by a piece of the same color
     *
     * illegal move
     */
    @Test
    void rookMoveBlockedByFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","w1","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 3, (byte) 2, (byte) 5);
        assertFalse(rookCheck(testMove, testBoard));
    }

    /**
     *rook move path blocked by a piece of the opponent
     *
     * illegal move
     */
    @Test
    void rookMoveBlockedByOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","b1","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 3, (byte) 2, (byte) 5);
        assertFalse(rookCheck(testMove, testBoard));
    }

    /**
     *rook eats opponent
     *
     * legal move
     */
    @Test
    void rookEatOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","","b1","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 3, (byte) 2, (byte) 4);
        assertTrue(rookCheck(testMove, testBoard));
    }

    /**
     *rook eats piece of the same color
     *
     * illegal move
     */
    @Test
    void rookEatFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","w2","","w1","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 3, (byte) 3, (byte) 2, (byte) 4);
        assertFalse(rookCheck(testMove, testBoard));
    }

    //Tests for 3.Knight
    /**
     *legal knight move, unblocked on the path
     *
     * legal move
     */
    @Test
    void legalKnightMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 2, (byte)3 );
        assertTrue(knightCheck(testMove, testBoard));
    }

    /**
     *legal knight move, blocked on the path
     *
     * legal move
     */
    @Test
    void legalKnightMoveBlocked() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","","b1","","","","",""},
                {"","","b1","","","","",""},
                {"","","","","","","",""},
                {"b1","","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 2, (byte)3 );
        assertTrue(knightCheck(testMove, testBoard));
    }

    /**
     *knight not obeying the moving rules
     *
     * illegal move
     */
    @Test
    void illegalKnightMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 2, (byte)4 );
        assertFalse(knightCheck(testMove, testBoard));
    }

    /**
     *knight eat a piece of opponent
     *
     * legal move
     */
    @Test
    void legalKnightEatOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","","","","","","",""},
                {"","","","b1","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 2, (byte)3 );
        assertTrue(knightCheck(testMove, testBoard));
    }

    /**
     *knight eat a piece of the same color
     *
     * illegal move
     */
    @Test
    void illegalKnightEatFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","","w1","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","","","","","","",""},
                {"","","","w1","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 2, (byte)3 );
        assertFalse(knightCheck(testMove, testBoard));
    }

    //Tests for 4.bishop
    /**
     * move bishop diagonally, unblocked on the path
     *
     * legal move
     */
    @Test
    void legalBishopMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte)2, (byte) 4, (byte) 1, (byte) 3);
        assertTrue(bishopCheck(testMove, testBoard));
    }

    /**
     * move bishop not diagonally
     *
     * illegal move
     */
    @Test
    void illegalBishopMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 2, (byte) 1, (byte) 6);
        assertFalse(bishopCheck(testMove, testBoard));
    }

    /**
     * bishop move path blocked by a piece of the same color
     *
     * illegal move
     */
    @Test
    void bishopMoveBlockedByFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","w1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 4, (byte) 1, (byte) 3);
        assertFalse(bishopCheck(testMove, testBoard));
    }

    /**
     * bishop move path blocked by a piece of the opponent
     *
     * illegal move
     */
    @Test
    void bishopMoveBlockedByOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","b1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 4, (byte) 1, (byte) 3);
        assertFalse(bishopCheck(testMove, testBoard));
    }

    /**
     * bishop eat opponent
     *
     * legal move
     */
    @Test
    void bishopEatOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","b1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 3, (byte) 1, (byte) 2);
        assertTrue(bishopCheck(testMove, testBoard));
    }

    /**
     * bishop eat piece of the same color
     *
     * illegal move
     */
    @Test
    void bishopEatFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","","w5","w6","w4","w3","w2"},
                {"w1","w1","","w1","w1","w1","w1","w1"},
                {"","w4","","","","","",""},
                {"","","w1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte) 3, (byte) 1, (byte) 2);
        assertFalse(bishopCheck(testMove, testBoard));
    }

    //  Tests for 5.queen
    /**
     * queen moves vertically, unblocked
     *
     * legal move
     */
    @Test
    void legalQueenMoveVertically() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)0 );
        assertTrue(queenCheck(testMove, testBoard));
    }

    /**
     * queen moves horizontally, unblocked
     *
     * legal move
     */
    @Test
    void legalQueenMoveHorizontally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)4 );
        assertTrue(queenCheck(testMove, testBoard));
    }

    /**
     * queen moves diagonally, unblocked
     *
     * legal move
     */
    @Test
    void legalQueenMoveDiagonally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)3 );
        assertTrue(queenCheck(testMove, testBoard));
    }

    /**
     * queen moves not vertically horizontally  diagonally
     *
     * illegal move
     */
    @Test
    void illegalQueenMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)4 , (byte) 0, (byte)1 );
        assertFalse(queenCheck(testMove, testBoard));
    }

    /**
     * queen move path blocked by opponent
     *
     * illegal move
     */
    @Test
    void queenMoveBlockedByOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"w1","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)0 );
        assertFalse(queenCheck(testMove, testBoard));
    }

    /**
     * queen move path blocked by piece of the same color
     *
     * illegal move
     */
    @Test
    void queenMoveBlockedByFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"b1","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)0 );
        assertFalse(queenCheck(testMove, testBoard));
    }

    /**
     * queen eats friend
     *
     * illegal move
     */
    @Test
    void queenEatFriend() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"w1","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)0 );
        assertFalse(queenCheck(testMove, testBoard));
    }

    /**
     * queen eats opponent
     *
     * legal move
     */
    @Test
    void queenEatOpponent() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w5","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)5 , (byte) 0, (byte)0 );
        assertTrue(queenCheck(testMove, testBoard));
    }

    //Tests for 6.king
    /**
     * king moves 1 step vertically
     *
     * legal move
     */
    @Test
    void kingLegalMoveVertically() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)3 , (byte) 0, (byte)0 );
        assertTrue(kingCheck(testMove, testBoard));
    }

    /**
     * king moves 1 step horizontally
     *
     * legal move
     */
    @Test
    void kingLegalMoveHorizontally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)1 );
        assertTrue(kingCheck(testMove, testBoard));
    }

    /**
     * king moves 1 step diagonally
     *
     * legal move
     */
    @Test
    void kingLegalMoveDiagonally() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)3 , (byte) 0, (byte)1 );
        assertTrue(kingCheck(testMove, testBoard));
    }

    /**
     * king moves not obeying moving rules
     *
     * illegal move
     */
    @Test
    void illegalKingMove() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)2 );
        assertFalse(kingCheck(testMove, testBoard));
    }

    /**
     * king move path blocked by a piece of the same color
     *
     * illegal move
     */
    @Test
    void kingMoveBlockedByFriend(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","","w1","w1","w1","w1","w1","w1"},
                {"w6","w1","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)1 );
        assertFalse(kingCheck(testMove, testBoard));
    }

    /**
     * king eats opponent
     *
     * legal move
     */
    @Test
    void kingEatOpponent(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","b1","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)1 );
        assertTrue(kingCheck(testMove, testBoard));
    }

    /**
     * king eats a piece of the same color
     *
     * illegal move
     */
    @Test
    void kingEatFriend(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"w6","w1","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        MoveHuman testMove=new MoveHuman((byte) 2, (byte)2 , (byte) 0, (byte)1 );
        assertFalse(kingCheck(testMove, testBoard));
    }

    //tests for promotion
    /**
     * Success promotion
     *
     * should throw the exception
     */
    @Test
    void successPromotionTest() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"b1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 1, (byte)0, (byte) 0, (byte)0);
        try{
            promotion(testMove,testBoard);
            fail("Not a promotion move");
        } catch (PromotionSpecialMove promotionSpecialMove) {
            //promotionSpecialMove.printStackTrace();
        }
    }

    /**
     * fail promotion test, not a pawn type enter the position
     *
     * this one should not be detected to be promotion(no exception thrown)
     *
     */
    @Test
    void failPromotionTestPieceType() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"b5","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 1, (byte)0, (byte) 0, (byte)0);

        try {
            promotion(testMove,testBoard);

        } catch (PromotionSpecialMove e) {
            e.printStackTrace();
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * fail promotion test, not an opponent pawn enter the position
     *
     * this one should not be detected to be promotion(no exception thrown)
     *
     */
    @Test
    void failPromotionTestWrongColor() {
        boolean thrown=false;

        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 1, (byte)0, (byte) 0, (byte)0);
        try {
            promotion(testMove,testBoard);
        } catch (PromotionSpecialMove e) {
            e.printStackTrace();
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * fail promotion test, not enter the position to be promoted
     *
     * this one should not be detected to be promotion(no exception thrown)
     *
     */
    @Test
    void failPromotionTestNotEnterPosition() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"","w1","w1","w1","w1","w1","w1","w1"},
                {"b1","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 2, (byte)1, (byte) 0, (byte)0);
        try {
            promotion(testMove,testBoard);

        } catch (PromotionSpecialMove e) {
            e.printStackTrace();
            thrown=true;
        }
        assertFalse(thrown);
    }

    //tests for EnPassant
    @Test
    void successEnPassant() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","w1","","","","","",""},
                {"","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 4, (byte)5, (byte) 1, (byte)0);
        Move previousMove=new MoveHuman((byte) 6, (byte)4, (byte) 0, (byte)0);
        try {
            enPassant(testMove,testBoard,previousMove);
        } catch (EnPassantSpecialMove e) {
            if(e.getEffectedPosition()[0]==previousMove.getX1() && e.getEffectedPosition()[1]==previousMove.getY1()) thrown=true;
        }
        assertTrue(thrown);
    }

    /**
     * not EnpPassant
     *
     * should not throw exception
     */
    @Test
    void failEnPassantWrongType_1() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b5","w1","","","","","",""},
                {"","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 4, (byte)5, (byte) 1, (byte)0);
        Move previousMove=new MoveHuman((byte) 6, (byte)4, (byte) 0, (byte)0);
        try {
            enPassant(testMove,testBoard,previousMove);
        } catch (EnPassantSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * not EnpPassant
     *
     * should not throw exception
     */
    @Test
    void failEnPassantWrongType_2() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","w5","","","","","",""},
                {"","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 4, (byte)5, (byte) 1, (byte)0);
        Move previousMove=new MoveHuman((byte) 6, (byte)4, (byte) 0, (byte)0);
        try {
            enPassant(testMove,testBoard,previousMove);
        } catch (EnPassantSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * not EnpPassant, wrong color
     *
     * should not throw exception
     */
    @Test
    void failEnPassantWrongColor_1() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","","","","","",""},
                {"","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 4, (byte)5, (byte) 1, (byte)0);
        Move previousMove=new MoveHuman((byte) 6, (byte)4, (byte) 0, (byte)0);
        try {
            enPassant(testMove,testBoard,previousMove);
        } catch (EnPassantSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * not EnpPassant, wrong color
     *
     * should not throw exception
     */
    @Test
    void failEnPassantWrongColor_2() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"w1","w1","","","","","",""},
                {"","","","","","","",""},
                {"","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 4, (byte)5, (byte) 1, (byte)0);
        Move previousMove=new MoveHuman((byte) 6, (byte)4, (byte) 0, (byte)0);
        try {
            enPassant(testMove,testBoard,previousMove);
        } catch (EnPassantSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    //tests for castling
    /**
     * successful castling
     *
     * should throw the exception
     */
    @Test
    void successCastlingMove() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","","w6","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 4, (byte)0);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertTrue(thrown);
    }

    /**
     * successful castling
     *
     * should throw the exception
     */
    @Test
    void successCastlingMove_2() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","","w6","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 0, (byte)4);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertTrue(thrown);
    }

    /**
     * castling blocked on the path
     *
     * not throw the exception
     */
    @Test
    void failCastlingMoveBlocked() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w1","","","w6","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 4, (byte)0);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * castling blocked on the path
     *
     * not throw the exception
     */
    @Test
    void failCastlingMoveBlocked_2() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","w1","w6","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 4, (byte)0);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * castling,king moved before
     *
     * not throw the exception
     */
    @Test
    void failCastlingKingMovedBefore() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","w6","","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        testBoard[0][3].setMoved(true);//the pawn moved before;
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 3, (byte)0);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }

    /**
     * castling,rook moved before
     *
     * not throw the exception
     */
    @Test
    void failRookMovedBefore() {
        boolean thrown=false;
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","w6","","","","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        testBoard[0][0].setMoved(true);//the pawn moved before;
        Move testMove=new MoveHuman((byte) 0, (byte)0, (byte) 3, (byte)0);
        try {
            castling(testMove,testBoard);
        } catch (CastlingSpecialMove e) {
            thrown=true;
        }
        assertFalse(thrown);
    }





    /**
     * there's promotion (by eating) possible for white
     *
     * all dice value is accepted
     */
    @Test
    void isMovePossibleWhitePromotion(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","","w6","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","w1","b1","b1","b1","b1","b1"},
                {"b2","b3","w4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }

    /**
     * there's promotion (directly 1 step forward unblocked) possible for white
     *
     * all dice value is accepted
     */
    @Test
    void isMovePossibleWhitePromotion_2(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","w6","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","w1","","b1","b1","b1","b1"},
                {"","","","","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }

    /**
     * there's promotion (by eating) possible for black
     *
     * all dice value is accepted
     */
    @Test
    void isMovePossibleBlackPromotion(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","b5","w5","w6","w4","w3","w2"},
                {"w1","w1","b1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","b6","","","",""}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }

    /**
     * there's promotion (directly 1 step forward unblocked) possible for black
     *
     * all dice value is accepted
     */
    @Test
    void isMovePossibleBlackPromotion_2(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","","w6","w4","w3","w2"},
                {"w1","w1","b1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","b6","","","","","",""}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }

    /**
     * only w1,w6 is possible for white to move
     *
     *
     */
    @Test
    void isMovePossibleWhitePawn(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w1","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }
    /**
     * only w2,w6 is possible for white to move
     *
     *
     */
    @Test
    void isMovePossibleWhiteRook(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w2","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)2);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }
    /**
     * only w3,w6 is possible for white to move
     *
     *
     */
    @Test
    void isMovePossibleWhiteKnight(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w3","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)3);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }
    /**
     * only w4,w6 is possible for white to move
     *
     * legal
     */
    @Test
    void isMovePossibleWhiteBishop(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w4","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)4);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }
    /**
     * only w5,w6 is possible for white to move
     *
     *
     */
    @Test
        void isMovePossibleWhiteQueen(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w5","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }
    /**
     * only w6 is possible for white to move
     *
     * legal
     */
    @Test
    void isMovePossibleWhiteKing(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","w6","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }

    /**
    *
    * w1,w2,w3 able to move
    *
    */
    @Test
    void isMovePossibleNormalBoard(){
            PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                    {"w2","w3","w4","w5","w6","w4","w3","w2"},
                    {"","w1","w1","w1","w1","w1","w1","w1"},
                    {"w1","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"b1","b1","b1","b1","b1","b1","b1","b1"},
                    {"b2","b3","b4","b5","b6","b4","b3","b2"}});
            ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        assertEquals(test, getMovablePieceTypes(false,testBoard, null));
    }



    /**
     *
     * starting position
     *
     */
    @Test
    void isMovePossibleStartingBoard(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)3);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     * all dice value is acceptable
     */
    @Test
    void isMovePossibleAllCanMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b2","","b4","b5","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"","b3","","","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }

    /**
     *  has to eat opponents to move
     */
    @Test
    void isMovePossibleEatOthers(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"b2","b2","b4","b5","b6","b4","b2","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)2);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     *
     * this situation black pawn cannot move
     *
     */
    @Test
    void isMovePossiblePawnNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","b1","","w5","w6","w4","w3","w2"},
                {"w1","b1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)2);
        test.add((byte)3);
        test.add((byte)4);
        test.add((byte)5);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }

    /**
     *
     * this situation black rook cannot move
     *
     */
    @Test
    void isMovePossibleRookNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"b1","","","","","","",""},
                {"b2","b1","","","","","",""},
                {"b1","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"","b3","b4","b5","b6","b4","b3",""}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)3);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     *
     * this situation black knight cannot move
     *
     */
    @Test
    void isMovePossibleKnightNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b1","b3","b4","b5","b6","b4","",""}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     *
     * this situation black bishop4 cannot move
     *
     */
    @Test
    void isMovePossibleBishopNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","b1","","","","","",""},
                {"b4","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","","b5","b6","","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)3);
        test.add((byte)5);
        test.add((byte)6);

        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     *
     * this situation black queen5 cannot move
     *
     */
    @Test
    void isMovePossibleQueenNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"b1","b1","","","","","",""},
                {"b4","b1","","","","","",""},
                {"b1","b1","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)3);
        test.add((byte)6);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }
    /**
     *
     * this situation black king6 cannot move
     *
     */
    @Test
    void isMovePossibleKingNotMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"b1","b1","b1","","","","",""},
                {"b6","b1","","","","","",""},
                {"b1","b1","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","","b4","b3","b2"}});
        ArrayList<Byte> test = new ArrayList<>();
        test.add((byte)1);
        test.add((byte)3);
        test.add((byte)5);
        assertEquals(test, getMovablePieceTypes(true,testBoard, null));
    }

    //test for finding bug,  test rookCheck
    @Test
    void rookVerticalTestRookCheck()  {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w1","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b2","b1","b1","b1","b1","b1","b1"},
                {"","","b4","b5","b6","b4","b3","b2"}});
        //move from (6,1) to (7,1)
        MoveHuman testMove=new MoveHuman((byte) 6, (byte) 7, (byte) 1, (byte) 1);
        assertTrue(rookCheck( testMove,testBoard));
    }

    @Test
    void testCatleMovement()
    {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w1","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","w3","","","","w6"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b2","b1","b1","b1","b1","b1","b1"},
                {"","","b4","b5","b6","b4","b3","b2"}});
        List<Move> options = Rules.getAllPossibleMoves(testBoard, false, (byte) 2, null);
        assertEquals(options.size(), 1);
    }

    //test for finding bug, test isLegal()
    @Test
    void rookVerticalTestIsLegal() throws PromotionSpecialMove, EnPassantSpecialMove, CastlingSpecialMove {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b2","b1","b1","b1","b1","b1","b1"},
                {"","","b4","b5","b6","b4","b3","b2"}});
        //move back from (6,1) to (7,1)
        MoveHuman testMove=new MoveHuman((byte) 6, (byte) 7, (byte) 1, (byte) 1);
        MoveHuman movePrevious =  new MoveHuman((byte) 7, (byte) 6, (byte) 1, (byte) 1);
        assertTrue(isLegal(testMove, testBoard,true,(byte)2,movePrevious));
    }

    @Test
    void castlingMoveInGetAllPossibleMoves() throws CastlingSpecialMove {
        PieceInterface[][] board = BoardState.buildBoard(new String[][]{
                {"w2","","","","w6","","","w2"}, null, null, null, null, null, null,
                {"b2","","","","b6","","","b2"}
        });

        List<Move> options = Rules.getAllPossibleMoves(board, false, (byte) 6, null);
        Move[] moves = new Move[]{
                new MoveHuman(0,0,4,0),
                new MoveHuman(0,0,4,7),
                new MoveHuman(0,0,7,4),
                new MoveHuman(0,0,0,4)
        };
        for(Move move:options) assertTrue(options.contains(move));

        options = Rules.getAllPossibleMoves(board, false, (byte) 2, null);
        for(Move move:options) assertTrue(options.contains(move));

        options = Rules.getAllPossibleMoves(board, true, (byte) 6, null);
        moves = new Move[]{
                new MoveHuman(7,7,4,0),
                new MoveHuman(7,7,4,7),
                new MoveHuman(7,7,7,4),
                new MoveHuman(7,7,0,4)
        };
        for(Move move:options) assertTrue(options.contains(move));

        options = Rules.getAllPossibleMoves(board, true, (byte) 2, null);
        for(Move move:options) assertTrue(options.contains(move));
    }

    @Test
    void testDraw(){
        PieceInterface[][] board = new BoardState().getPieceSet();
        assertFalse(Rules.draw(board, false, null, 74));
        assertTrue(Rules.draw(board, false, null, 75));
        for(int i = 0; i < board[6].length; i++){
            board[6][i] = null;
            board[7][i] = null;
        }
        assertTrue(Rules.draw(board, !board[0][0].getPlayer(), null, 0));
    }
}