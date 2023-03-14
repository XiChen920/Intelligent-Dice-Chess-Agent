package tests;

import Model.BoardState;
import Model.CurrentModel;
import Model.PieceInterface;
import User.Agent.EvaluationFunction.SimplifiedEvaluationFunctionWithGamePhaseDetection;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestEvaluationFunction {


    private CurrentModel testingModel = new CurrentModel();

    /**
     * initial position is phase 0
     */
    @Test
    void testGamePhaseOpening() {
        BoardState testState = new BoardState();
        PieceInterface[][] testBoard = testState.getBoardState();

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(0,gamePhase);
    }

    /**
     * ruy lopez opening
     * phase 0
     */
    @Test
    void testGamePhaseOpening1() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","b4","","","w1","","",""},
                {"","","","","b1","","",""},
                {"","","","","","b3","",""},
                {"b1","b1","b1","b1","","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","","","b2"}});

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(0,gamePhase);
    }

    /**
     * middle game
     *
     * less piece remain
     */
    @Test
    void testGamePhaseMiddleGame() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","w5","w6","w4","w3",""},
                {"w1","w1","w1","","","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","b4","","","w1","w1","",""},
                {"","","b1","","b1","","",""},
                {"","","","","","b3","",""},
                {"b1","","b1","b1","","b1","b1","b1"},
                {"b2","","b4","b5","b6","","",""}});

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(1,gamePhase);
    }

    /**
     * middle game
     *
     * development complete
     */
    @Test
    void testGamePhaseMiddleGame1() {
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","w3","w4","w5","w6","w4","w3","w2"},
                {"","w1","","","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"b5","w1","w1","b3","","b6","","b2"},
                {"","b3","b1","","b1","","",""},
                {"","","","","b1","","",""},
                {"","","","","b1","b1","b1","b1"},
                {"","","b4","","","b4","","b2"}});

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(1,gamePhase);
    }

    /**
     * end game :less than 7 minor and major pieces
     *
     */
    @Test
    void testGamePhaseEndGame(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","","w5","w6","","",""},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","","","b5","b6","b4","","b2"}});

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(2,gamePhase);


    }

    /**
     * end game :no queen
     *
     */
    @Test
    void testGamePhaseEndGame1(){

        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","","b6","b4","b3","b2"}});

        SimplifiedEvaluationFunctionWithGamePhaseDetection evaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
        byte gamePhase = evaluationFunction.getGamePhase(testBoard);
        assertEquals(2,gamePhase);


    }

    /**
     * initial board score should be 0
     */
    @Test
    void testInitialBoardScore(){

        BoardState testState = new BoardState();
        PieceInterface[][] testBoard = testState.getBoardState();

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        double score = agent.evaluation(testingModel);
        assertEquals(0,score);

    }
    /**
     * score should be 55
     */
    @Test
    void testBoardScoreLoseBothQueen(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","","b6","b4","b3","b2"}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(0,score);

    }

    /**
     * score should be 55
     */
    @Test
    void testBoardFirstMove(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","w3","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(55,score);

    }
    /**
     * ruy lopez opening score
     */
    @Test
    void testBoardScoreAFamousGameStart(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","","w1","w1","w1"},
                {"","","w3","","","","",""},
                {"","b4","","","w1","","",""},
                {"","","","","b1","","",""},
                {"","","","","","b3","",""},
                {"b1","b1","b1","b1","","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","","","b2"}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(-15,score);

    }

    /**
     * an endgame
     */
    @Test
    void testBoardScoreAnEndGame(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w5","","","","",""},
                {"","","","","","w4","",""},
                {"","","","","","","",""},
                {"","b5","","","","","",""},
                {"","","","","","b3","",""},
                {"","","","","","","",""},
                {"","","","b6","","","",""}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(15,score);

    }
    /**
     * an endgame
     */
    @Test
    void testBoardScoreAnEndGame1(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","w5","","","","",""},
                {"","","","","","w4","",""},
                {"","","","","","","",""},
                {"","b5","","","","","",""},
                {"","","","","","","",""},
                {"","","","b1","","","",""},
                {"","","","b6","","","",""}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(165,score);

    }


    /**
     * one side only have king
     */
    @Test
    void testAnExtremeSituation(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"","","","","w6","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(-4065,score);

    }

    /**
     * similar to above testcase, should be opposite in sign
     */
    @Test
    void testAnExtremeSituation1(){
        PieceInterface[][] testBoard =BoardState.buildBoard(new String[][]{
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","b6","","",""}});

        ExpectiMiniMax agent = new ExpectiMiniMax(testingModel, false);
        BoardState testState = new BoardState(testBoard);
        CurrentModel model = new CurrentModel(testState, (byte) 0,false);

        double score = agent.evaluation(model);
        System.out.println(score);
        assertEquals(4065,score);

    }
}
