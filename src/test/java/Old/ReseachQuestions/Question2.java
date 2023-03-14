package Old.ReseachQuestions;

import Experiments.ResearchQuestions.ExperimentLogger;
import Experiments.ResearchQuestions.ProgressPrinter;
import Model.Model;
import Model.CurrentModel;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruning;
import User.Agent.MCTS.MCTS;
import User.Agent.MCTSAgent;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class Question2 {
    //Question :Which AI performs the best
    //Reformulation: at what point does the ranking of the best AIs chance

    private static final String q2 = "Question 2";
    public static void main(String[] args) {
        ExperimentLogger.logToCSV(q2,new String[]{"EMMABP depth", "avg/target turn time", "EMMABP wins","avg EMMABP win turn count", "avg EMMABP lose turn count", "MCTS wins", "avg MCTS win turn count", "avg MCTS lose turn count"});
        int minDept = 1;
        int maxDept = 10;
        Boolean ans;
        Integer drawPoint = null;
        Integer turnPoint = null;
        for(int i = minDept; i <= maxDept; i++){
            ans = measure(i);
            if(ans == null) drawPoint = i;
            else if(!ans) turnPoint = i;
        }
        if(drawPoint != null) System.out.println("draw point = "+drawPoint);
        if(turnPoint != null) System.out.println("turn point = "+turnPoint);
    }

    public static double avg(List<Integer> list){
        double ans = 0;
        for(Integer integer: list){
            ans += integer;
        }
        double size = list.size();
        return ans/size;
    }

    public static Boolean measure(int dept) {
        int numberOfMeasurements = 100;

        int winCountEMMABP = 0;
        Agent agent = new EMMABP(null, false, dept);
        List<Integer> countWin = new ArrayList<>();
        List<Integer> countLose = new ArrayList<>();
        ProgressPrinter progressPrinter = new ProgressPrinter(numberOfMeasurements, Math.max(100/numberOfMeasurements, 1));
        for (int i = 0; i < numberOfMeasurements; i++) {
            int[] out = play(agent, new BaselineAgent(null, false));
            winCountEMMABP += out[0];
            if(out[0] == 1){ countWin.add(out[1]);}
            else {countLose.add(out[1]);}
            progressPrinter.print(i);
        }
        double avgTurnTime = ((EMMABP) agent).avgTurnTimeMillis();
        double avgTurnCountWinEMMABP = avg(countWin);
        double avgTurnCountLoseEMMABP = avg(countLose);
        System.out.print("\r EMMABP won " + winCountEMMABP + " times, avg win turn count = "+avgTurnCountWinEMMABP+", avg lose turn count = "+avgTurnCountLoseEMMABP);
        System.out.println(": with an avg turn time " + avgTurnTime);

        MCTS.alterResourceLimit(avgTurnTime);
        agent = new MCTSAgent(null, false);
        int winCountMCTS = 0;
        countWin = new ArrayList<>();
        countLose = new ArrayList<>();
        progressPrinter = new ProgressPrinter(numberOfMeasurements, Math.max(100/numberOfMeasurements, 1));
        for (int i = 0; i < numberOfMeasurements; i++) {
            int[] out = play(agent, new BaselineAgent(null, false));
            winCountMCTS += out[0];
            if(out[0] == 1) countWin.add(out[1]);
            else countLose.add(out[1]);
            progressPrinter.print(i);
        }
        double avgTurnCountWinMCTS = avg(countWin);
        double avgTurnCountLoseMCTS = avg(countLose);
        System.out.println("\r MCTS won " + winCountMCTS + " times, avg win turn count = "+avgTurnCountWinMCTS+", avg lose turn count = "+avgTurnCountLoseMCTS);
        ExperimentLogger.logToCSV(q2,new String[]{String.valueOf(dept), String.valueOf(avgTurnTime), String.valueOf(winCountEMMABP), String.valueOf(avgTurnCountWinEMMABP), String.valueOf(avgTurnCountLoseEMMABP), String.valueOf(winCountMCTS), String.valueOf(avgTurnCountWinMCTS), String.valueOf(avgTurnCountLoseMCTS)});
        if (winCountEMMABP > winCountMCTS) {
            System.out.println("\rEMMABP won @ dept " + dept);
            return false;
        }
        if (winCountEMMABP < winCountMCTS) {
            System.out.println("\rMCTS won @ dept " + dept);
            return true;
        }

        System.out.println("\r Draw @ dept " + dept);
        return null;
    }

    public static int[] play(Agent a1, Agent a2){
        boolean a1Id = Math.random()<0.5;
        Model arena = new CurrentModel();
        a1.setPlayerId(a1Id);
        a1.setModel(arena);
        a2.setPlayerId(!a1Id);
        a2.setModel(arena);
        boolean player = false;
        Agent[] players;
        if(a1Id) players = new Agent[]{a2, a1};
        else players = new Agent[]{a1, a2};
        int counter = 0;
        while (arena.getCurrentLeader() == null){
            byte playerIndex = 0;
            if(player) playerIndex = 1;
            arena.updatePlayer(player);
            players[playerIndex].rollDice();
            players[playerIndex].executePlay();
            player = !player;
            counter++;
        }
        if(arena.getCurrentLeader() == a1Id) return new int[]{1,counter};
        return new int[]{0, counter};
    }

    private static class EMMABP extends ExpectiMiniMaxAlphaBetaPruning{
        public EMMABP(Model modelReference, boolean playerId, int dept) {
            super(modelReference, playerId, dept);
            turnCount = 0;
            totalTurnTime = 0;
        }
        private int turnCount;
        private long totalTurnTime;

        @Override
        public void generateMove() {
            long startTime = System.currentTimeMillis();
            super.generateMove();
            totalTurnTime += System.currentTimeMillis()-startTime;
            turnCount++;
        }

        public double avgTurnTimeMillis(){
            return ((double) totalTurnTime)/((double) turnCount);
        }
    }


}
