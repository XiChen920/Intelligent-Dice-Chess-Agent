package Old.ReseachQuestions;


import Experiments.ResearchQuestions.ExperimentLogger;
import Model.CurrentModel;
import Model.Model;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruning;
import User.Agent.MCTS.MCTS;
import User.Agent.MCTSAgent;

@Deprecated
public class PlayoutMCTS
{
    Agent player1;
    Agent player2;
    Agent currentPlayer;
    boolean playerFlag = false;
    Model currentModel;

    int gameCount = 30;
    int currentGame = 0;

    int whiteWin = 0;
    int blackWin = 0;

    int moveTotal = 0;

    String fileName = "MCTS_vs_Baseline";
    //Heading per logged game
    String[] csvHeadings = new String[]{"Game count", "Winner", "Game duration", "Number of moves", "MCTS resource allocation"};

    public static void main(String[] args)
    {
        PlayoutMCTS experiment = new PlayoutMCTS("MCTSAgent", "Baseline");
    }

    public PlayoutMCTS(String agent_1_type, String agent_2_type)
    {
        currentGame = 0;

        ExperimentLogger.logToCSV(fileName, csvHeadings);

        while(currentGame < gameCount)
        {
            int numericResult = 0;
            //Record game starting time
            double startTime = System.currentTimeMillis();

            //Perform single game
            boolean result = playoutResult(agent_1_type, agent_2_type);

            //Record game ending time
            double endTime = System.currentTimeMillis();
            double duration = endTime - startTime;
            if(!result)
            {
                System.out.println("White win");
                whiteWin++;
                numericResult = 1;
            }
            else
            {
                System.out.println("Black win");
                blackWin++;
            }

            String[] loggingData = new String[]{String.valueOf(currentGame), String.valueOf(numericResult), String.valueOf(duration), String.valueOf(moveTotal), String.valueOf(MCTS.getResourceLimit())};
            ExperimentLogger.logToCSV(fileName, loggingData);

            System.out.println(currentGame);
            currentGame++;
        }
        System.out.println("White win count" + whiteWin + " black win count " + blackWin);
    }

    public boolean playoutResult(String agent_1_type, String agent_2_type)
    {
        currentModel =  new CurrentModel();
        moveTotal = 0;

        player1 = agentSelection(agent_1_type, false);
        player2 = agentSelection(agent_2_type, true);

        if((currentGame % 3) == 0)
        {
            double newLimit = MCTSAgent.getMCTSResourceLimit() + 1000;
            MCTSAgent.setMCTSResourceLimit(newLimit);
        }

        Boolean winner = null;
        currentPlayer = player1;

        while(winner == null)
        {
            winner = singlePlay();
            moveTotal ++;
        }
        System.out.println("Winner: " + winner);
        moveTotal = moveTotal/2;

        playerFlag = false;
        return winner;
    }

    public Boolean singlePlay()
    {
        currentPlayer.rollDice();
        currentPlayer.executePlay();

        playerFlag = !playerFlag;
        currentModel.updatePlayer(playerFlag);
        currentPlayer = changeAgent(currentPlayer);

        return currentModel.getCurrentLeader();
    }


    public Agent agentSelection(String type, boolean playerFlag)
    {
        switch(type)
        {
            case "Baseline": return new BaselineAgent(currentModel, playerFlag);
            case "ExpectiMiniMax": return new ExpectiMiniMax(currentModel, playerFlag);
            case "ExpextiMiniMaxAlphaBetaPruning": return new ExpectiMiniMaxAlphaBetaPruning(currentModel, playerFlag);
            case "MCTSAgent": return new MCTSAgent(currentModel, playerFlag);
            default:
                System.out.println("No agent selected");
                return null;
        }
    }
    public Agent changeAgent(Agent currentPlayer)
    {
        if(currentPlayer == player1)
        {
            return player2;
        }
        return player1;
    }
}
