package User.Agent.ExpectiMiniMax;

import Model.CurrentModel;
import Model.Model;
import User.Agent.Agent;

public class DemoExpectiMiniMaxVariations {
    /**
     * Plays a gui less game between 2 agents
     * @param args no effect
     */
    public static void main(String[] args) {
        Model arena = new CurrentModel();
        int dept = 2;
        Agent[] players = new Agent[]{
                new ExpectiMiniMax(arena, false, dept),
                new ExpectiMiniMaxAlphaBetaPruningSortedDynamicMultiThreaded(arena,true,dept)
        };
        CurrentModel.print = false;
        CurrentModel.GUI = false;
        playBetween2Agents(arena, players);
    }

    /**
     * Plays a game between 2 agents without  a GUI
     * @param arena the model to play in
     * @param players the players/agents to play in the arena
     */
    static void playBetween2Agents(Model arena, Agent[] players) {
        String[] playerNames = {"White", "Black"};
        boolean playerTurn = arena.getPlayer();
        byte playerTurnIndex;
        System.out.println(arena);
        System.out.println("----------------------------------------");
        while (true){
            if(playerTurn) playerTurnIndex = 1;
            else playerTurnIndex = 0;
            players[playerTurnIndex].rollDice();
            if(arena.draw()){
                break;
            }
            players[playerTurnIndex].executePlay();
            System.out.println(arena);
            playerTurn = !playerTurn;
            arena.updatePlayer(playerTurn);
            if(arena.getCurrentLeader() != null) break;
            System.out.println("----------------------------------------");
        }
        Boolean winner = arena.getCurrentLeader();
        if(winner != null) {
            int winnerIndex;
            if (winner) winnerIndex = 1;
            else winnerIndex = 0;
            System.out.println(playerNames[winnerIndex] + " wins!");
        } else System.out.println("Draw");
    }
}
