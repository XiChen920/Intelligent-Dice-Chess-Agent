package Old.ReseachQuestions;

import Experiments.ResearchQuestions.ProgressPrinter;
import Model.Model;
import Model.CurrentModel;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded;
import User.Agent.MCTSAgent;
@Deprecated
public class Question1 {
    public static final String question = "Do  our  methods  outperform  a  regular  baseline  agent?";
    public static double[] answer(int tests, Agent[] agents){
        int[] victories = new int[agents.length];
        ProgressPrinter progressPrinter = new ProgressPrinter(tests, 1);
        for(int i = 0; i < tests; i++){
            for(int j = 0; j < agents.length; j++){
                victories[j]+=run(agents[j]);
            }
            progressPrinter.print(i);
        }
        double[] ans = new double[agents.length];
        for(int i = 0; i < victories.length; i++){
            ans[i] = ((double) victories[i])/((double) tests);
        }
        return ans;
    }

    private static int run(Agent agent){
        Model arena = new CurrentModel();
        boolean agentIdentity = Math.random()<0.5;
        Agent[] players;
        if(agentIdentity) {
            agent.setModel(arena);
            agent.setPlayerId(agentIdentity);
            players = new Agent[]{new BaselineAgent(arena, false), agent};
        } else {
            agent.setModel(arena);
            agent.setPlayerId(agentIdentity);
            players = new Agent[]{agent, new BaselineAgent(arena, true)};
        }
        boolean playerTurn = arena.getPlayer();
        byte playerTurnIndex;
        while (true){
            if(playerTurn) playerTurnIndex = 1;
            else playerTurnIndex = 0;
            players[playerTurnIndex].rollDice();
            players[playerTurnIndex].executePlay();
            playerTurn = !playerTurn;
            arena.updatePlayer(playerTurn);
            if(arena.getCurrentLeader() != null) break;
        }
        if(arena.getCurrentLeader() == agentIdentity) return 1;
        else return 0;
    }

    public static void main(String[] args) {
        Agent[] agents = {new BaselineAgent(null, false), new ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(null, false), new MCTSAgent(null, false)};
        int tests = 10;
        double[] results = answer(tests, agents);
        for(double r: results) System.out.println("\\r"+r);
        System.out.println("out of "+tests);
    }
}
