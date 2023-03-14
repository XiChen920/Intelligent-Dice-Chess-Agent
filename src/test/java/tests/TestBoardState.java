package tests;

import Model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestBoardState  {
    @Test
    void testBuildBoard() {
        PieceInterface[][][] toTest = {BoardState.buildBoard(new String[]{
                "w2 w3 w4 w5 w6 w4 w3 w2",
                "w1 w1 w1 w1 w1 w1 w1 w1",
                null, null, null, null,
                "b1 b1 b1 b1 b1 b1 b1 b1",
                "b2 b3 b4 b5 b6 b4 b3 b2"}), BoardState.buildBoard(new String[]{
                "w2,w3,w4,w5,w6,w4,w3,w2",
                "w1,w1,w1,w1,w1,w1,w1,w1"
                ,null, null, null, null,
                "b1,b1,b1,b1,b1,b1,b1,b1",
                "b2,b3,b4,b5,b6,b4,b3,b2"}), BoardState.buildBoard(new String[]{
                "w2,w3,w4,w5,w6,w4,w3,w2",
                "w1,w1,w1,w1,w1,w1,w1,w1",
                null, null, null, null,
                "b1,b1,b1,b1,b1,b1,b1,b1",
                "b2,b3,b4,b5,b6,b4,b3,b2"}), BoardState.buildBoard(new String[]{
                "w2,w3,w4,w5,w6,w4,w3,w2",
                "w1,w1,w1,w1,w1,w1,w1,w1",
                "", "", "", "",
                "b1,b1,b1,b1,b1,b1,b1,b1",
                "b2,b3,b4,b5,b6,b4,b3,b2"}), new BoardState().getBoardState()};
        assertArrayEquals(toTest[0], toTest[1]);
        assertArrayEquals(toTest[1], toTest[2]);
        assertArrayEquals(toTest[2], toTest[3]);
        assertArrayEquals(toTest[3], toTest[4]);
    }

    @Test
    void testClone(){
        String[][] board = new String[8][8];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(Math.random() < 0.5) board[i][j] = "b";
                else board[i][j] = "w";
                board[i][j]+=(int)(Math.random()*7);
                if(board[i][j].contains("0")) board[i][j] = null;
            }
        }
        BoardState a = new BoardState(board);
        BoardState b = a.clone();
        assertArrayEquals(a.getBoardState(), b.getBoardState());
    }

    @Test
    void testScholarsMate(){
        // source: https://en.wikipedia.org/wiki/Scholar%27s_mate
       String[][] compare  = {
                {"w2", "w3", "w4", "", "w6", "", "w3", "w2"},
                {"w1", "w1", "w1", "w1", "", "w1", "w1", "w1"},
                {"","","","","","","",""},
                {"", "", "w4", "", "w1", "", "", ""},
                {"", "", "", "", "b1", "", "", ""},
                {"", "", "b3", "", "", "b3", "", ""},
                {"b1", "b1", "b1", "b1", "", "w5", "b1", "b1"},
                {"b2", "", "b4", "b5", "b6", "b4", "", "b2"}
        };
        testGame(new String[]{"e2e4", "e7e5", "d1h5", "b8c6", "f1c4", "g8f6", "h5f7"}, compare);
    }

    /**
     * To validate that a given set of move instructions arrives at the right end state
     * @param moveInstructions in y0x0y1x1 met y in a/b/c/d/e/f/g/h
     * @param end the state to compare against
     */
    void testGame(String[] moveInstructions, String[][] end){
        boolean player = false;
        BoardState game = new BoardState();
        for(int i = 0; i < moveInstructions.length; i++){
            if(moveInstructions[i].length()!=4) throw new IllegalArgumentException("Instruction "+i+" is in incorrect format");
            Move tempMove = makeMove(moveInstructions[i]);
            try {
                game.updateState(tempMove, player, game.getBoardState()[tempMove.getX0()][tempMove.getY0()].getTypeOfPiece());
            } catch (IllegalMoveExemption e) {
                System.out.println("Illegal move at "+i+": "+moveInstructions[i]);
                Demo.display(game.getBoardState());
            }
            player = !player;
        }

        BoardState toCompare = new BoardState(end);
        for(int i = 0; i < moveInstructions.length; i++){
            if(toCompare.getBoardState()[makeMove(moveInstructions[i]).getX1()][makeMove(moveInstructions[i]).getY1()] != null) {
                toCompare.getBoardState()[makeMove(moveInstructions[i]).getX1()][makeMove(moveInstructions[i]).getY1()].moved();
            }
        }
        if(!compareBoardStates(toCompare, game)){
            Demo.display(toCompare.getBoardState());
            System.out.println();
            Demo.display(game.getBoardState());
        }

        assertArrayEquals(toCompare.getBoardState(), game.getBoardState());
    }

    boolean compareBoardStates(BoardState a, BoardState b){
        if(a.getBoardState().length != b.getBoardState().length) return false;
        for(int i = 0; i < a.getBoardState().length; i++){
            if(a.getBoardState()[i].length != b.getBoardState()[i].length) return false;
            for (int j = 0; j < a.getBoardState()[i].length; j++){
                if(a.getBoardState()[i][j] != null && b.getBoardState()[i][j] == null) {
                    System.out.println("a[i][j] == "+a.getBoardState()[i][j].toString()+", b[i][j] == null"+"@"+i+"@"+j);
                    return false;}
                if(a.getBoardState()[i][j] == null && b.getBoardState()[i][j] != null) {
                    System.out.println("a[i][j] == null, b[i][j] == "+b.getBoardState()[i][j].toString()+"@"+i+"@"+j);
                    return false;}
                if(!(a.getBoardState()[i][j] == null && b.getBoardState()[i][j] == null) && !a.getBoardState()[i][j].equals(b.getBoardState()[i][j])){
                    System.out.println("a[i][j] == "+a.getBoardState()[i][j].toString()+", b[i][j] == "+a.getBoardState()[i][j].toString()+"@"+i+"@"+j);
                    return false;}
            }
        }
        return true;
    }

    Move makeMove(String move){
        if(move.length()!=4) throw new IllegalArgumentException("Wrong length");
        String yCor = "abcdefgh";
        return new MoveHuman(
                Byte.parseByte(String.valueOf(move.charAt(1)))-1,
                Byte.parseByte(String.valueOf(move.charAt(3)))-1,
                yCor.indexOf(move.charAt(0)),
                yCor.indexOf(move.charAt(2))
        );
    }

    @Test
    void testRook(){
        boolean illegalMove = false;
        BoardState toTest = new BoardState();
        Move[] moves = new Move[5];
        moves[0] = new MoveHuman(0,2,1,2);
        moves[1] = new MoveHuman(6,4,0,0);
        moves[2] = new MoveHuman(0,0,0,1);
        moves[3] = new MoveHuman(6,4,1,1);
        moves[4] = new MoveHuman(0,0,1,0);
        byte[] dice = {3,1,2,1,2};
        boolean player = false;
        for(int i = 0; i < moves.length; i++){
            try {
                toTest.updateState(moves[i], player, dice[i]);
            } catch (IllegalMoveExemption e) {
                illegalMove = true;
            }
            player = !player;
        }
        assertFalse(illegalMove);
    }

    @Test
    void testEnPassantLeft() throws IllegalMoveExemption {
        Rules.ENPASSANT = true;
        BoardState toTest = new BoardState(new String[][]{
                {"w1","","","","", "","",""},
                null,
                {"","b1","","","","","","" },
                null, null, null, null, {"w6","b6","","","","","" ,""}
        });
        toTest.updateState(new MoveHuman(0,2,0,0), false, (byte) 1);
        toTest.updateState(new MoveHuman(2,1,1,0), true, (byte) 1);
        PieceInterface[][] toCompare = BoardState.buildBoard(new String[][]{null,{"b1","","","","", "","",""},null,null, null, null, null, {"w6","b6","","","","","" ,""}});
        toCompare[1][0].moved();
        assertArrayEquals(toCompare, toTest.getBoardState());
        Rules.ENPASSANT = false;
    }

    @Test
    void testEnPassantRight() throws IllegalMoveExemption {
        Rules.ENPASSANT = true;
        BoardState toTest = new BoardState(new String[][]{
                {"","","w1","","", "","",""},
                null,
                {"","b1","","","","","","" },
                null, null, null, null, {"w6","b6","","","","","" ,""}
        });
        toTest.updateState(new MoveHuman(0,2,2,2), false, (byte) 1);
        toTest.updateState(new MoveHuman(2,1,1,2), true, (byte) 1);
        PieceInterface[][] toCompare = BoardState.buildBoard(new String[][]{null,{"","","b1","","", "","",""},null,null, null, null, null, {"w6","b6","","","","","" ,""}});
        toCompare[1][2].moved();
        compareBoardStates(new BoardState(toCompare), toTest);
        assertArrayEquals(toCompare, toTest.getBoardState());
        Rules.ENPASSANT = false;
    }

    @Test
    void  testPromotion() throws IllegalMoveExemption {
        BoardState toTest = new BoardState(new String[][]{null,{"b1","","","","", "","",""},null,null, null, null, null, {"w6","b6","","","","","" ,""}});
        toTest.updateState(new MoveHuman(1,0,0,0), true, (byte) 5);
        PieceInterface[][] toCompare = BoardState.buildBoard(new String[][]{{"b5","","","","", "","",""},null,null,null, null, null, null, {"w6","b6","","","","","" ,""}});
        toCompare[0][0].moved();
        assertArrayEquals(toCompare, toTest.getBoardState());
    }
}
