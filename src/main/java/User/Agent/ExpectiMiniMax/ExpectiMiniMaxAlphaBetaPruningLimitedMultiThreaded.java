package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded extends ExpectiMiniMaxAlphaBetaPruning {
    private static final int DEFAULTMaxThreadCount = 10;

    /**
     * The approximate targeted thread count
     */
    protected int targetThreadCount;

    /**
     * Create a ExpectiMiniMax with a custom dept and target thread count
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     * @param targetThreadCount the max number of threads to approximately use
     */
    public ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded(Model modelReference, boolean playerId, int dept, int targetThreadCount) {
        super(modelReference, playerId, dept);
        if(targetThreadCount < 1) throw new IllegalArgumentException("maxThreadCount must be >= 1");
        this.targetThreadCount = targetThreadCount -1;
    }

    /**
     * Create a ExpectiMiniMax with a custom dept
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     */
    public ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
        targetThreadCount = DEFAULTMaxThreadCount;
    }

    /**
     * Standard creator using default options
     * @param modelReference the model the agent plays upon
     * @param playerId the player the agent is
     */
    public ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
        targetThreadCount = DEFAULTMaxThreadCount;
    }

    /**
     * The threads associated with this method, besides the main thread
     */
    protected ThreadGroup threadGroup;

    @Override
    public void generateMove() {
        List<Model> children = getChildrenFirstLayer();
        if(children == null) return;
        Double[] bestEvaluation = new Double[1];
        if(playerId == maximizingPlayer) bestEvaluation[0] = minEvaluation;
        else bestEvaluation[0] = maxEvaluation;
        Double opponentConclusion;
        int bestMoveIndex = 0, i;
        threadGroup = new ThreadGroup("threadGroup");
        List<EMMABPLMTThread> threads = new ArrayList<>();
        for(i = 0; i < children.size(); i++){
            if(threadGroup.activeCount() < targetThreadCount){
                threads.add(new EMMABPLMTThread(threadGroup, Integer.toString(dept-1),children.get(i), !playerId, dept - 1, bestEvaluation, (byte) 0, i));
            } else {
                opponentConclusion = miniMaxLayer(children.get(i), !playerId, dept - 1, bestEvaluation);
                if (opponentConclusion != null &&
                        ((playerId == maximizingPlayer && opponentConclusion > bestEvaluation[0]) ||
                                (playerId != maximizingPlayer && opponentConclusion < bestEvaluation[0]))) {
                    bestEvaluation[0] = opponentConclusion;
                    bestMoveIndex = i;
                }
            }
        }
        for(EMMABPLMTThread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            opponentConclusion = thread.getEvaluation();
            if (opponentConclusion != null && ((playerId == maximizingPlayer && opponentConclusion > bestEvaluation[0]) || (playerId != maximizingPlayer && opponentConclusion < bestEvaluation[0]))) {
                bestEvaluation[0] = opponentConclusion;
                bestMoveIndex = thread.getIndex();
            }
        }

        AIMove = modelReference.getAllPossibleMoves().get(bestMoveIndex);
    }

    /**
     * To know if according to the specifics there is room for a new thread
     * @return if there is room
     */
    protected boolean roomForANewThread(){
        return threadGroup.activeCount() < targetThreadCount;
    }

    /**
     * Represents a chance and min/max player node
     * @param model the current model (state of the parent node)
     * @param player the player that the player part belongs to
     * @param deptToGO the dept still to go from the expecti mini max tree
     * @param killLine if player is maximizing, alpha, else beta: if this method will guarantee and equally good or better move from the perspective of this player, kill the branch of the tree, must be an array length 1 (enables sharing for threads)
     * @return the approximate evaluation for the model input
     */
    protected Double miniMaxLayer(Model model, boolean player, int deptToGO, Double[] killLine){
        model.updatePlayer(player);

        //Termination case
        if(deptToGO == 0) return evaluation(model);

        //Chance nodes
        List<Byte> diceRoles = model.getMovablePieceTypes();

        //Player nodes
        Double[][] evaluation = new Double[diceRoles.size()][1];
        List<EMMABPLMTThread> threads = new ArrayList<>();
        for(byte d = 0; d < diceRoles.size(); d++) {
            model.updateDice(diceRoles.get(d));
            List<Model> children = null;
            boolean skip = false;
            try {
                children = getSuccessors(model, player);
            } catch (ExpectiMiniMax.Win win) {
                if (player == maximizingPlayer) evaluation[d][0] = maxEvaluation;
                else evaluation[d][0] = minEvaluation;
                skip = true;
                //No need to investigate further
            }
            if(!skip) {
                if(player == maximizingPlayer) evaluation[d][0] = minEvaluation;
                else evaluation[d][0] = maxEvaluation;
                Double opponentConclusion;
                int i;
                for (i = 0; i < children.size(); i++) {
                    if(roomForANewThread()){
                        threads.add(new EMMABPLMTThread(threadGroup, Integer.toString(dept-1),children.get(i), !player, deptToGO - 1, evaluation[d], d, i));
                    } else {
                        opponentConclusion = miniMaxLayer(children.get(i), !player, deptToGO - 1, evaluation[d]);
                        if (alphaBetaPruningThreadCompatibleStoppingCondition(player, killLine, evaluation, d, opponentConclusion))
                            return null;
                    }
                }
            }
        }
        Double opponentConclusion;
        for(EMMABPLMTThread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            opponentConclusion = thread.getEvaluation();
            byte d = thread.getDiceRollIndex();
            if (alphaBetaPruningThreadCompatibleStoppingCondition(player, killLine, evaluation, d, opponentConclusion))
                return null;
        }
        if(player == maximizingPlayer) return expectation(evaluation, maxEvaluation);
        else return expectation(evaluation, minEvaluation);
    }

    private boolean alphaBetaPruningThreadCompatibleStoppingCondition(boolean player, Double[] killLine, Double[][] evaluation, byte d, Double opponentConclusion) {
        if (opponentConclusion != null && ((player == maximizingPlayer && opponentConclusion > evaluation[d][0]) || (player != maximizingPlayer && opponentConclusion < evaluation[d][0]))) {
            evaluation[d][0] = opponentConclusion;
            if (player == maximizingPlayer && expectation(evaluation, minEvaluation) > killLine[0])
                return true;
            return player != maximizingPlayer && expectation(evaluation, maxEvaluation) < killLine[0];
            //NOTE: if this player is maximizing, the worst it can do know so far is expectation(evaluation, minEvaluation), thus if this is higher than the best option already known by the minimizing player deciding before this player, this branch has no purpose anymore
        }
        return false;
    }

    /**
     * The current evaluation will develop to in the range of [expectation(evaluation, minEvaluation), expectation(evaluation, maxEvaluation)], used to compare against alpha/beta/killLine
     * @param evaluation the array with known and unknown values, must be an array of arrays length 1
     * @param valueForUnknowns the place for unknowns
     * @return the average if all unknowns where valueForUnknowns
     */
    protected double expectation(Double[][] evaluation, double valueForUnknowns){
        double ans = 0;
        for (Double[] doubles : evaluation) {
            if (doubles[0] == null) {
                ans += valueForUnknowns;
            } else {
                ans += doubles[0];
            }
        }
        return ans/evaluation.length;
    }

    private class EMMABPLMTThread extends Thread{
        private Double evaluation;
        private final Model model;
        private final boolean player;
        private final int deptToGo;
        private final Double[] killLine;
        private final byte diceRollIndex;
        private final int index;
        public EMMABPLMTThread(ThreadGroup threadGroup, String name, Model model, boolean player, int deptToGO, Double[] killLine, byte diceRollIndex, int index){
            super(threadGroup, name);
            this.model = model;
            this.player = player;
            this.deptToGo = deptToGO;
            this.killLine = killLine;
            this.diceRollIndex = diceRollIndex;
            this.index = index;
            start();
        }

        @Override
        public void run() {
            evaluation = miniMaxLayer(model, player, deptToGo, killLine);
        }

        public Double getEvaluation(){
            return evaluation;
        }

        public byte getDiceRollIndex() {
            return diceRollIndex;
        }

        public int getIndex() {
            return index;
        }
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" targetThreadCount = "+targetThreadCount;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningLimitedMultiThreaded ans = new ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningDynamicMultiThreaded ans){
        super.clone(ans);
        targetThreadCount = ans.targetThreadCount;
    }

    public void setTargetThreadCount(int targetThreadCount) {
        this.targetThreadCount = targetThreadCount;
    }
}