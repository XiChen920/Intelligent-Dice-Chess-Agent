package Experiments.ResearchQuestions;

import Model.Model;
import Model.Move;
import User.Agent.BaselineAgent;
import User.Agent.EvaluationFunction.EvaluationFunction;
import User.Agent.EvaluationFunction.MLPEvaluation;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative;
import User.Agent.MCTS.MCTS;
import User.Agent.MCTSAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayOut implements Serializable {
    //Values for agent performance analysis
    public final int turnCount;
    public byte result;
    public final Long[] falseAgentTurnTimes;
    public final Long[] trueAgentTurnTimes;
    public final String name;
    public final double targetTurnTime;

    //Values for a replay
    public final Move[] movesMade;
    public final Model startingModel;

    public static PlayOut getPlayOutEMMABPSIDvsMCTS(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionEMMABPSID(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionMCTS(arena, !a.getPlayerFlag(), turnTimeMS);
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "EMMABPSID vs MCTS");
        else ans = new PlayOut(a, b, "EMMABPSID vs MCTS");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutEMMABPSIDvsBaseLine(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionEMMABPSID(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionBaseLine(arena, !a.getPlayerFlag());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "EMMABPSID vs BaseLine");
        else ans = new PlayOut(a, b, "EMMABPSID vs BaseLine");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutMCTSvsBaseLine(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionMCTS(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionBaseLine(arena, !a.getPlayerFlag());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "MCTS vs BaseLine");
        else ans = new PlayOut(a, b, "MCTS vs BaseLine");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutEMMABPSIDMLPEvaluationFunctionVSMCTS(double turnTimeMS, Model arena)
    {
        ResearchQuestionAgent a = new ResearchQuestionEMMABPSID(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionMCTS(arena, !a.getPlayerFlag(), turnTimeMS);
        a.setEvaluationFunction(new MLPEvaluation());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "EMMABPSID MLP evalution vs MCTS");
        else ans = new PlayOut(a, b, "EMMABPSID MLP evalution vs MCTS");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutMCTSMLPEvaluationVSEMMABPSID(double turnTimeMS, Model arena)
    {
        ResearchQuestionAgent a = new ResearchQuestionMCTS(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionEMMABPSID(arena, !a.getPlayerFlag(), turnTimeMS);
        a.setEvaluationFunction(new MLPEvaluation());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "MCTS MLP evalution vs EMMABPSID");
        else ans = new PlayOut(a, b, "MCTS MLP evalution vs EMMABPSID");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutEMMABPSIDMLPEvaluationFunctionVSEMMABPSID(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionEMMABPSID(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionEMMABPSID(arena, !a.getPlayerFlag(), turnTimeMS);
        a.setEvaluationFunction(new MLPEvaluation());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "EMMABPSID MLP evalution vs EMMABPSID");
        else ans = new PlayOut(a, b, "EMMABPSID MLP evalution vs EMMABPSID");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutMCTSMLPEvaluationFunctionVSMCTS(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionMCTS(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionMCTS(arena, !a.getPlayerFlag(), turnTimeMS);
        a.setEvaluationFunction(new MLPEvaluation());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "MCTS MLP evalution vs MCTS");
        else ans = new PlayOut(a, b, "MCTS MLP evalution vs MCTS");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static PlayOut getPlayOutEMMABPSIDMLPEvaluationFunctionVSMCTSMLPEvaluationFunction(double turnTimeMS, Model arena){
        ResearchQuestionAgent a = new ResearchQuestionEMMABPSID(arena, Math.random()<0.5, turnTimeMS);
        ResearchQuestionAgent b = new ResearchQuestionMCTS(arena, !a.getPlayerFlag(), turnTimeMS);
        a.setEvaluationFunction(new MLPEvaluation());
        b.setEvaluationFunction(new MLPEvaluation());
        PlayOut ans;
        if(a.getPlayerFlag()) ans = new PlayOut(b, a, "EMMABPSID MLP evalution vs MCTS MLP evalution");
        else ans = new PlayOut(a, b, "EMMABPSID MLP evalution vs MCTS MLP evalution");
        if(a.getPlayerFlag()) ans.result = (byte) (ans.result*-1);
        return ans;
    }

    public static void printCVSHeader(String experimentName, PlayOut example){
        List<String> ans = new ArrayList<>();
        ans.add("turn count");
        ans.add("result");
        List<String> temp = NumbersListAnalysis.getReportHeaders();
        for(int i = 0; i < temp.size(); i++) temp.set(i, temp.get(i)+" first mentioned agent");
        ans.addAll(temp);
        temp = NumbersListAnalysis.getReportHeaders();
        for(int i = 0; i < temp.size(); i++) temp.set(i, temp.get(i)+" second mentioned agent");
        ans.addAll(temp);
        ans.add("target turn time");
        ExperimentLogger.logToCSV(experimentName, ans.toArray(new String[0]));
    }
    public static void printCVSLine(String experimentName, PlayOut playOut){
        List<String> ans = new ArrayList<>();
        ans.add(String.valueOf(playOut.turnCount));
        ans.add(String.valueOf(playOut.result));
        List<String> temp = new NumbersListAnalysis(playOut.falseAgentTurnTimes).getReport();
        ans.addAll(temp);
        temp = new NumbersListAnalysis(playOut.trueAgentTurnTimes).getReport();
        ans.addAll(temp);
        ans.add(String.valueOf(playOut.targetTurnTime));
        ExperimentLogger.logToCSV(experimentName, ans.toArray(new String[0]));
    }
    public static void printCVSHeader(String experimentName, PlayOut[] example){
        List<String> ans = new ArrayList<>();
        for(PlayOut playOut: example){
            if(playOut.name == null) throw new IllegalArgumentException("Not all elements of example are named");
            ans.add("turn count "+playOut.name);
            ans.add("result "+playOut.name);
            List<String> temp = NumbersListAnalysis.getReportHeaders();
            for(int i = 0; i < temp.size(); i++) temp.set(i, temp.get(i)+" first mentioned agent "+playOut.name);
            ans.addAll(temp);
            temp = NumbersListAnalysis.getReportHeaders();
            for(int i = 0; i < temp.size(); i++) temp.set(i, temp.get(i)+" second mentioned agent "+playOut.name);
            ans.addAll(temp);
            ans.add("target turn time "+playOut.name);
        }
        ExperimentLogger.logToCSV(experimentName, ans.toArray(new String[0]));
    }
    public static void printCVSLine(String experimentName, PlayOut[] playOuts){
        List<String> ans = new ArrayList<>();
        for(PlayOut playOut: playOuts) {
            ans.add(String.valueOf(playOut.turnCount));
            ans.add(String.valueOf(playOut.result));
            List<String> temp = new NumbersListAnalysis(playOut.falseAgentTurnTimes).getReport();
            ans.addAll(temp);
            temp = new NumbersListAnalysis(playOut.trueAgentTurnTimes).getReport();
            ans.addAll(temp);
            ans.add(String.valueOf(playOut.targetTurnTime));
        }
        ExperimentLogger.logToCSV(experimentName, ans.toArray(new String[0]));
    }

    public static String getPreferredExtension(){
        return ".PO";
    }

    public PlayOut(){
        turnCount = 0;
        falseAgentTurnTimes = null;
        trueAgentTurnTimes = null;
        name = null;
        targetTurnTime = 0;
        movesMade = null;
        startingModel = null;
    }

    public PlayOut(String name){
        turnCount = 0;
        falseAgentTurnTimes = null;
        trueAgentTurnTimes = null;
        this.name = name;
        targetTurnTime = 0;
        movesMade = null;
        startingModel = null;
    }

    protected PlayOut(ResearchQuestionAgent falseAgent, ResearchQuestionAgent trueAgent, String name) {
        if(!falseAgent.playsOnTheSameField(trueAgent)) throw new IllegalArgumentException("ResearchQuestionAgents do not play on the same field");
        if(falseAgent.getPlayerFlag()) throw new IllegalArgumentException("falseAgent is not the false Agent");
        this.targetTurnTime = Math.max(falseAgent.getTargetTurnTimeMS(), trueAgent.getTargetTurnTimeMS());
        this.startingModel = falseAgent.getModel().clone();
        int turnCount = 0;
        List<Move> movesMade = new ArrayList<>();
        while (!falseAgent.getModel().draw() && falseAgent.getModel().getCurrentLeader() == null) {
            if (falseAgent.getModel().getPlayer() == falseAgent.getPlayerFlag()) {
                falseAgent.fullTurn();
                movesMade.add(falseAgent.getMove());
            } else {
                trueAgent.fullTurn();
                movesMade.add(trueAgent.getMove());
            }
            turnCount++;
        }
        this.turnCount = turnCount;
        if(falseAgent.getModel().getCurrentLeader() == null) this.result = 0;
        else if(falseAgent.getModel().getCurrentLeader()) this.result = -1;
        else this.result = 1;
        this.falseAgentTurnTimes = falseAgent.getTurnTimes();
        this.trueAgentTurnTimes = trueAgent.getTurnTimes();
        this.movesMade = movesMade.toArray(new Move[0]);
        this.name = name;
    }

    protected interface ResearchQuestionAgent {
        Long[] getTurnTimes();

        /**
         * Update model player, roll dice and execute play
         */
        void fullTurn();

        boolean playsOnTheSameField(ResearchQuestionAgent other);

        boolean getPlayerFlag();

        Model getModel();

        Move getMove();

        double getTargetTurnTimeMS();

        void setEvaluationFunction(EvaluationFunction evaluationFunction);
    }

    protected static class ResearchQuestionBaseLine extends BaselineAgent implements ResearchQuestionAgent {
        public ResearchQuestionBaseLine(Model modelReference, boolean playerId) {
            super(modelReference, playerId);
            turnTimes = new ArrayList<>();
        }

        @Override
        public double getTargetTurnTimeMS() {
            return 0;
        }

        private final List<Long> turnTimes;
        @Override
        public void fullTurn() {
            long startTime = System.currentTimeMillis();
            rollDice();
            executePlay();
            turnTimes.add(System.currentTimeMillis()-startTime);
            modelReference.updatePlayer(!playerId);
        }

        @Override
        public Long[] getTurnTimes() {
            return turnTimes.toArray(new Long[0]);
        }

        @Override
        public boolean playsOnTheSameField(ResearchQuestionAgent other) {
            return other.getPlayerFlag() != getPlayerFlag() && other.getModel().equals(getModel());
        }

        @Override
        public void setEvaluationFunction(EvaluationFunction evaluationFunction) {}
    }

    protected static class ResearchQuestionEMMABPSID extends ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative implements ResearchQuestionAgent {
        public ResearchQuestionEMMABPSID(Model modelReference, boolean playerId, double turnTimeMS) {
            super(modelReference, playerId, (byte) 1, turnTimeMS);
            turnTimes = new ArrayList<>();
        }

        @Override
        public double getTargetTurnTimeMS() {
            return super.turnTimeMS;
        }

        private final List<Long> turnTimes;
        @Override
        public void fullTurn() {
            long startTime = System.currentTimeMillis();
            rollDice();
            executePlay();
            turnTimes.add(System.currentTimeMillis()-startTime);
            modelReference.updatePlayer(!playerId);
        }

        @Override
        public Long[] getTurnTimes() {
            return turnTimes.toArray(new Long[0]);
        }

        @Override
        public boolean playsOnTheSameField(ResearchQuestionAgent other) {
            return other.getPlayerFlag() != getPlayerFlag() && other.getModel().equals(getModel());
        }
    }

    protected static class ResearchQuestionMCTS extends MCTSAgent implements ResearchQuestionAgent {
        public ResearchQuestionMCTS(Model modelReference, boolean playerId, double turnTimeMS) {
            super(modelReference, playerId);
            MCTS.alterResourceLimit(turnTimeMS);
            turnTimes = new ArrayList<>();
            getMcts().setEvaluationFunction("SimplifiedEvaluation");
        }

        @Override
        public double getTargetTurnTimeMS() {
            return MCTS.getResourceLimit();
        }

        private final List<Long> turnTimes;
        @Override
        public void fullTurn() {
            long startTime = System.currentTimeMillis();
            rollDice();
            executePlay();
            turnTimes.add(System.currentTimeMillis()-startTime);
            modelReference.updatePlayer(!playerId);
        }

        @Override
        public Long[] getTurnTimes() {
            return turnTimes.toArray(new Long[0]);
        }

        @Override
        public boolean playsOnTheSameField(ResearchQuestionAgent other) {
            return other.getPlayerFlag() != getPlayerFlag() && other.getModel().equals(getModel());
        }

        @Override
        public void setEvaluationFunction(EvaluationFunction evaluationFunction) {
            getMcts().setEvaluationFunction("MLPEvaluation");
        }
    }
}
