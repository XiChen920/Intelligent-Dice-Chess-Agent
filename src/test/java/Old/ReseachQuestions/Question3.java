package Old.ReseachQuestions;

import Old.DoesEMMABPwork.IsEMMBetterThanEMMABP;
import Experiments.ResearchQuestions.ExperimentLogger;
import Experiments.ResearchQuestions.ProgressPrinter;
import Model.Model;
import Model.CurrentModel;
import Model.StandardChessModel;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;

/**
 * ??how do our agents perform in regular chess
 */
@Deprecated
public class Question3 {
    public static void main(String[] args) {
        String fileName = "Question3";
        ExperimentLogger.logToCSV(fileName, new String[]{"Dept", "Games won in dice chess out of 100", "Games won in regular chess out of 100"});
        byte minDepth = 1;
        byte maxDepth = 5;
        int games = 100;
        ExpectiMiniMax agent = new ExpectiMiniMax(null, false);
        for(byte i = minDepth; i <= maxDepth; i++){
            agent.setDept(i);
            int[] ans = wonGamesInDiceChessWonGamesInRegularChessAgainstBaseLineAgent(agent, games);
            String[] log = new String[]{String.valueOf(i), String.valueOf(ans[0]), String.valueOf(ans[1])};
            ExperimentLogger.logToCSV(fileName,log);
        }
    }

    public static int[] wonGamesInDiceChessWonGamesInRegularChessAgainstBaseLineAgent(Agent agent, int gamesPerGameType){
        int[] ans = new int[2];
        int temp = 0;
        ProgressPrinter progressPrinter = new ProgressPrinter(2*gamesPerGameType, 1);
        for(int j = 0; j < gamesPerGameType; j++){
            Boolean temp2 = doesTheFistMentionedPlayerWin(agent, new BaselineAgent(null, false), new CurrentModel());
            if(temp2 != null && temp2) temp++;
            progressPrinter.print(j);
        }
        ans[0] = temp;
        temp = 0;
        for(int j = 0; j < gamesPerGameType; j++){
            Boolean temp2 = doesTheFistMentionedPlayerWin(agent, new BaselineAgent(null, false), new StandardChessModel());
            if(temp2 != null && temp2) temp++;
            progressPrinter.print(j+gamesPerGameType);
        }
        ans[1] = temp;
        return ans;
    }

    public static Boolean doesTheFistMentionedPlayerWin(Agent player1, Agent player2, Model arena){
        player1.setModel(arena);
        player2.setModel(arena);
        return IsEMMBetterThanEMMABP.doesTheFistMentionedPlayerWin(player1, player2);
    }
}
