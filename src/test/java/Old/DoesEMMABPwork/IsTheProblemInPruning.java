package Old.DoesEMMABPwork;

import Model.Model;
import Model.CurrentModel;
import User.Agent.Agent;
import User.Agent.BaselineAgent;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruning;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningSorted;

/**
 * No
 * Still a problem remaining
 */
@Deprecated
public class IsTheProblemInPruning {
    public static void main(String[] args) {
        CurrentModel.GUI = false;
        CurrentModel.print = false;
        Model arena = new CurrentModel();
        for(int i = 0; i < 1000000000; i++){
            playOut(new CombinedAgent(arena, false,2), new BaselineAgent(arena, true));
        }
        System.out.println("Rounds = "+rounds);
        System.out.println("Differences EMM vs EMMABP = "+differencesEMMvsEMMABP);
        System.out.println("Differences EMM vs EMMABP2 = "+differencesEMMvsEMMABP2);
        System.out.println("Differences EMM vs EMMABPS = "+differencesEMMvsEMMABPS);
        if(differencesEMMvsEMMABP == differencesEMMvsEMMABP2) System.out.println("No");
        else System.out.println("Yes");
        System.out.print("Is there still a problem: ");
        if(differencesEMMvsEMMABP != 0) System.out.println("yes");
        else System.out.println("no");
        if(rounds < 1000) main(null);
    }

    public static Boolean playOut(Agent a, Agent b){
        if(a.getPlayerFlag() == b.getPlayerFlag()) throw new IllegalArgumentException("Players play for the same side");
        if(!a.getModel().equals(b.getModel())) throw new IllegalArgumentException("Agents not playing in the same Model");
        boolean player = a.getModel().getPlayer();
        Agent playerInTurn = a;
        while (playerInTurn.getModel().getCurrentLeader() == null){
            if(player == a.getPlayerFlag()) playerInTurn = a;
            else playerInTurn = b;
            playerInTurn.rollDice();
            if(playerInTurn.getModel().draw()){
                break;
            }
            playerInTurn.executePlay();
            player = !player;
            playerInTurn.getModel().updatePlayer(player);
            if(playerInTurn == a) rounds++;
        }
        return playerInTurn.getModel().getCurrentLeader();
    }
    private static int differencesEMMvsEMMABP = 0;
    private static int differencesEMMvsEMMABP2 = 0;
    private static int differencesEMMvsEMMABPS = 0;
    private static int rounds = 0;

    private static class CombinedAgent extends Agent {
        private final Agent EMM;
        private final Agent EMMABP;
        private final Agent EMMABP2;
        private final Agent EMMABPS;
        public CombinedAgent(Model model, boolean player, int dept){
            super(model, player);
            EMM = new ExpectiMiniMax(model, player, dept);
            EMMABP = new ExpectiMiniMaxAlphaBetaPruning(model, player, dept);
            ExpectiMiniMaxAlphaBetaPruning EMMABPTemp = new ExpectiMiniMaxAlphaBetaPruning(model, player, dept);
            EMMABPTemp.pruning = false;
            EMMABP2 = EMMABPTemp;
            ExpectiMiniMaxAlphaBetaPruningSorted EMMABPSTemp = new ExpectiMiniMaxAlphaBetaPruningSorted(model,player,dept);
            EMMABPSTemp.sort = false;
            EMMABPSTemp.pruning = false;
            EMMABPS = EMMABPSTemp;
        }
        @Override
        public void generateMove() {
            EMM.generateMove();
            EMMABP.generateMove();
            EMMABP2.generateMove();
            EMMABPS.generateMove();
            AIMove = EMM.getMove();
            if(!EMM.getMove().equals(EMMABP.getMove())) differencesEMMvsEMMABP++;
            if(!EMM.getMove().equals(EMMABP2.getMove())) differencesEMMvsEMMABP2++;
            if(!EMM.getMove().equals(EMMABPS.getMove())) differencesEMMvsEMMABPS++;
        }
    }
}
