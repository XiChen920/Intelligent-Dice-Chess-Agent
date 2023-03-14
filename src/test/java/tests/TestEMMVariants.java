package tests;

import Experiments.ResearchQuestions.ProgressPrinter;
import Model.CurrentModel;
import Model.Model;
import Model.Move;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.EvaluationFunction.EvaluationFunction;
import User.Agent.ExpectiMiniMax.*;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * To test all 8 EMM variants (6 if testDeprecated = false), 1 test per variant including many assert statements each
 * Expect 11+ minutes of runtime with testDeprecated = false;
 */
public class TestEMMVariants {
    private static final boolean testDeprecated = false;
    private static byte dept = 3;

    @Test
    void testEMM() {
        testEMMVar(new ExpectiMiniMax(null, false));
    }

    @Test
    void testEMMABP() {
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruning(null, false));
    }

    @Test
    void testEMMABPDMT() {
        if(testDeprecated) testEMMVar(new ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(null, false));
    }

    @Test
    void testEMMABPLMT() {
        if(testDeprecated) testEMMVar(new ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded(null, false));
    }

    @Test
    void testEMMABPS() {
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruningSorted(null, false));
    }

    @Test
    void testEMMABPSMT(){
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(null, false));
    }

    @Test
    void testEMMABPSDMT() {
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(null, false));
    }

    @Test
    void testEMMABPSLMT() {
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded(null, false));
    }

    @Test
    void testEMMABPSLMTI() {
        testEMMVar(new ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(null, false));
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative){
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative.clone());
        //DUE to the iterative deepening other variants not needed
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded){
        ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded1 = (ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded) expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded.clone();
        expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded1.setTargetCPUUtilization(-100);
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded.clone());
        testEMMVar((ExpectiMiniMaxAlphaBetaPruningSorted) expectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded1.clone());
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded){
        ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded1 = (ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded) expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded.clone();
        expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded1.setTargetThreadCount(-100);
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded.clone());
        testEMMVar((ExpectiMiniMaxAlphaBetaPruningSorted) expectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded1.clone());
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningSorted expectiMiniMaxAlphaBetaPruningSorted){
        ExpectiMiniMaxAlphaBetaPruningSorted expectiMiniMaxAlphaBetaPruningSorted1 = (ExpectiMiniMaxAlphaBetaPruningSorted) expectiMiniMaxAlphaBetaPruningSorted.clone();
        expectiMiniMaxAlphaBetaPruningSorted1.sort = false;
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningSorted.clone());
        testEMMVar((ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruningSorted1.clone());
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded) {
        ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded1 = (ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded) expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded.clone();
        expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded1.setTargetThreadCount(-100);
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded.clone());
        testEMMVar((ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruningLimitedMultiThreaded1.clone());
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded) {
        ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded1 = (ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded) expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded.clone();
        expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded1.setTargetCPUUtilization(-100);
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded.clone());
        testEMMVar((ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruningDynamicMultiThreaded1.clone());
    }

    void testEMMVar(ExpectiMiniMaxAlphaBetaPruning expectiMiniMaxAlphaBetaPruning) {
        ExpectiMiniMaxAlphaBetaPruning expectiMiniMaxAlphaBetaPruning1 = (ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruning.clone();
        expectiMiniMaxAlphaBetaPruning1.pruning = false;
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruning1.clone());
        testEMMVar((ExpectiMiniMax) expectiMiniMaxAlphaBetaPruning.clone());
        for(int i = 0;  i < 100; i++) testAlphaBetaPruningConsistency((ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruning.clone());
        for(int i = 0; i < 100; i++) testAlphaBetaPruningConsistencyAgainstEMM((ExpectiMiniMaxAlphaBetaPruning) expectiMiniMaxAlphaBetaPruning.clone());
    }

    void testEMMVar(ExpectiMiniMax expectiMiniMax) {
        testIllegalDepth((ExpectiMiniMax) expectiMiniMax.clone());
        testAgentDiceRoll((ExpectiMiniMax) expectiMiniMax.clone());
        testMoveGeneration((ExpectiMiniMax) expectiMiniMax.clone());
        testAgentMoveInModel((ExpectiMiniMax) expectiMiniMax.clone());
        playOffDept3((ExpectiMiniMax) expectiMiniMax.clone());
    }

    void testAlphaBetaPruningConsistency(ExpectiMiniMaxAlphaBetaPruning agent){
        ExpectiMiniMaxAlphaBetaPruning a = (ExpectiMiniMaxAlphaBetaPruning) agent.clone();
        a.setDept(dept);
        ExpectiMiniMaxAlphaBetaPruning b = (ExpectiMiniMaxAlphaBetaPruning) a.clone();
        b.pruning = false;
        Model model = getRandomModel();
        a.setModel(model.clone());
        a.setPlayerId(model.getPlayer());
        a.generateMove();
        b.setModel(model.clone());
        b.setPlayerId(model.getPlayer());
        b.generateMove();
        assertEquals(a.getMove(), b.getMove());
    }

    void testAlphaBetaPruningConsistencyAgainstEMM(ExpectiMiniMaxAlphaBetaPruning agent){
        ExpectiMiniMaxAlphaBetaPruning a = (ExpectiMiniMaxAlphaBetaPruning) agent.clone();
        a.setDept(dept);
        ExpectiMiniMax b = new ExpectiMiniMax(a.getModel(), a.getPlayerFlag(), a.getDept());
        Model model = getRandomModel();
        a.setModel(model.clone());
        a.setPlayerId(model.getPlayer());
        a.generateMove();
        b.setModel(model.clone());
        b.setPlayerId(model.getPlayer());
        b.generateMove();
        assertEquals(a.getMove(), b.getMove());
    }

    /**
     * illegal number of layers of mini-max (<1)
     */
    void testIllegalDepth(ExpectiMiniMax expectiMiniMax){
        boolean thrown = false;
        try {
            expectiMiniMax.setDept(0);
        } catch (IllegalArgumentException e) {
            thrown=true;
        }
        assertTrue(thrown);
    }

    void testAgentDiceRoll(ExpectiMiniMax agent) {
        Model testingModel = new CurrentModel();
        agent.setModel(testingModel);
        agent.setPlayerId(testingModel.getPlayer());
        agent.rollDice();
        assertTrue(inScope(agent.getDiceRoll()));
        assertTrue(inScope(testingModel.getCurrentDiceRoll()));
    }

    public boolean inScope(int dice_value) {
        return dice_value != 0 && dice_value <= 6;
    }

    void testMoveGeneration(ExpectiMiniMax agent) {
        Model testingModel = new CurrentModel();
        agent.setModel(testingModel);
        agent.setPlayerId(testingModel.getPlayer());
        testingModel.updateDice((byte)1);
        agent.generateMove();
        Move move = agent.getMove();
        assertNotNull(move);
    }

    void testAgentMoveInModel(ExpectiMiniMax agent) {
        Model testingModel = new CurrentModel();
        agent.setModel(testingModel);
        agent.setPlayerId(testingModel.getPlayer());
        testingModel.updateDice((byte) 1);
        agent.executePlay();
        int x0 = agent.getMove().getX0();
        int x1 = agent.getMove().getX1();
        int y0 = agent.getMove().getY0();
        int y1 = agent.getMove().getY1();

        assertNull(testingModel.getBoardState().getPieceAt(x0, y0));
        assertNotNull(testingModel.getBoardState().getPieceAt(x1, y1));
    }

    void playOffDept3(ExpectiMiniMax agent){
        CurrentModel.GUI = false;
        CurrentModel.print = false;
        int maxNumOfTurns = 10;
        int dept = 3;
        Model arena = new CurrentModel();
        agent.setDept(dept);
        agent.setModel(arena);
        agent.setPlayerId(false);
        Agent[] players = {agent,new BaselineAgent(arena, true)};
        boolean playerTurn = false;
        byte playerTurnIndex;
        for(int i = 0; i < maxNumOfTurns; i++){
            if(playerTurn) playerTurnIndex = 1;
            else playerTurnIndex = 0;
            if(arena.draw()) break;
            players[playerTurnIndex].rollDice();
            if(arena.draw()) break;
            players[playerTurnIndex].executePlay();
            playerTurn = !playerTurn;
            arena.updatePlayer(playerTurn);
            if(arena.getCurrentLeader() != null) break;
        }
    }

    public Model getRandomModel(){
        return getRandomModel(new CurrentModel());
    }
    /**
     * To get a realistic random Model adjusted for reached frequency
     * @return a Model achievable by playing a game form the default model, randomly selected out of a play between two random agents adjusted for the encountered branching factor
     */
    public static Model getRandomModel(Model startingModel){
        Agent a = new BaselineAgent(startingModel.clone(), false);
        Agent b = new BaselineAgent(a.getModel(), true);
        boolean player = a.getModel().getPlayer();
        Agent playerInTurn = a;
        List<Model> options = new ArrayList<>();
        while (playerInTurn.getModel().getCurrentLeader() == null){
            if(player == a.getPlayerFlag()) playerInTurn = a;
            else playerInTurn = b;
            playerInTurn.rollDice();
            options.add(a.getModel().clone());
            if(playerInTurn.getModel().draw()){
                break;
            }
            try {
                playerInTurn.executePlay();
            } catch (Exception ignore){
                break;
            }
            player = !player;
            playerInTurn.getModel().updatePlayer(player);
        }
        return options.get((int) (options.size()*Math.random()));
    }

    protected static class ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion extends ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded{
        public ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(Model modelReference, boolean playerId, int dept) {
            super(modelReference, playerId, dept);
        }

        public ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(Model modelReference, boolean playerId) {
            super(modelReference, playerId);
        }

        @Override
        protected boolean addNewTread() {
            return false;
        }

        @Override
        public String toString() {
            return "ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort;
        }

        @Override
        protected Thread getNewAdditionalThreadFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i) {
            throw new IllegalArgumentException("Additional thread requested, this should never happen");
        }

        @Override
        protected Thread getNewThread(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
            throw new IllegalArgumentException("Additional thread requested, this should never happen");
        }
    }
}
