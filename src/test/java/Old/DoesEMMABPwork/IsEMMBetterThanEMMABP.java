package Old.DoesEMMABPwork;

import Experiments.ResearchQuestions.ProgressPrinter;
import Model.CurrentModel;
import Model.Model;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded;

/**
 * Answer: yes (approximately 52% win against EMMABP)
 */
@Deprecated
public class IsEMMBetterThanEMMABP {
    public static void main(String[] args) {
        CurrentModel.GUI = false;
        CurrentModel.print = false;
        int dept = 2;
        int games = 100;
        int victoriesEMM =0;
        int victoriesEMMABP = 0;
        ProgressPrinter progressPrinter = new ProgressPrinter(games, 1);
        for(int i = 0; i < games; i++){
            Model arena = new CurrentModel();
            Boolean temp = doesTheFistMentionedPlayerWin(new ExpectiMiniMax(arena, false, dept), new ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(arena, true, (byte) dept));
            if(temp != null) {
                if (temp) victoriesEMM++;
                else victoriesEMMABP++;
            }
            progressPrinter.print(i);
        }
        System.out.print("\r \n");
        double EMMWinRate = ((double)victoriesEMM)/((double) games);
        System.out.println("EMM win rate = "+EMMWinRate*100+"%");
        double EMMABPWinRate = ((double)victoriesEMMABP)/((double) games);
        System.out.println("EMMABP win rate = "+EMMABPWinRate*100+"%");
    }

    /**
     * Plays an agent against a BaseLineAgent with random player assignment on the model of the agent
     * @param a the agent to play against the BaseLineAgent, also determines model and thus starting player
     * @return if Agent a won the game
     */
    public static boolean compareAgainstBaseLinePlayOut(Agent a){
        Agent b = new BaselineAgent(a.getModel(), Math.random() < 0.5);
        a.setPlayerId(!b.getPlayerFlag());
        return a.getPlayerFlag() == IsTheProblemInPruning.playOut(a, b);
    }

    public static Boolean doesTheFistMentionedPlayerWin(Agent player1, Agent player2){
        if(!player1.getModel().equals(player2.getModel())) throw new IllegalArgumentException("Both players don't play on the same Model");
        player1.setPlayerId(Math.random() < 0.5);
        player2.setPlayerId(!player1.getPlayerFlag());
        boolean ans = player1.getPlayerFlag() == IsTheProblemInPruning.playOut(player1, player2);
        if(player1.getModel().draw()) return null;
        return ans;
    }
}
