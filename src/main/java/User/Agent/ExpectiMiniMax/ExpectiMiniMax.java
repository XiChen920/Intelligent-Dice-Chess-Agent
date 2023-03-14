package User.Agent.ExpectiMiniMax;

import Model.Model;
import Model.Move;
import User.Agent.*;
import User.Agent.EvaluationFunction.EvaluationFunction;
import User.Agent.EvaluationFunction.SimplifiedEvaluationFunctionWithGamePhaseDetection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpectiMiniMax extends Agent{
    /**
     * The max dept this instance is allowed to go to
     */
    protected int dept;

    private static final int DEFAULTDept = 3;
    private static final EvaluationFunction DEFAULTEvaluationFunction = new SimplifiedEvaluationFunctionWithGamePhaseDetection();
    protected EvaluationFunction evaluationFunction;

    /**
     * maximizing player as by the EvaluationFunction
     */
    protected boolean maximizingPlayer;

    /**
     * maximum evaluation as by the EvaluationFunction
     */
    protected double maxEvaluation;

    /**
     * minimum evaluation as by the EvaluationFunction
     */
    protected double minEvaluation;

    /**
     * Create a ExpectiMiniMax with a custom dept
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     */
    public ExpectiMiniMax(Model modelReference, boolean playerId, int dept){
        super(modelReference, playerId);
        setDept(dept);
        setEvaluationFunction(DEFAULTEvaluationFunction);
    }

    /**
     * Standard creator using default options
     * @param modelReference the model the agent plays upon
     * @param playerId the player the agent is
     */
    public ExpectiMiniMax(Model modelReference, boolean playerId){
        super(modelReference, playerId);
        setDept(DEFAULTDept);
        setEvaluationFunction(DEFAULTEvaluationFunction);
    }

    /**
     * To chance the evaluationFunction used
     * @param evaluationFunctionIn the new evaluationFunction to use
     */
    public void setEvaluationFunction(EvaluationFunction evaluationFunctionIn){
        this.evaluationFunction = evaluationFunctionIn;
        this.maximizingPlayer = evaluationFunction.getMaximizingPlayer();
        this.maxEvaluation = evaluationFunction.getMaxEvaluation();
        this.minEvaluation = evaluationFunction.getMinEvaluation();
    }

    @Override
    public void generateMove() {
        List<Model> children = getChildrenFirstLayer();
        if(children == null) return;
        int bestMoveIndex = 0, i;

        double bestEvaluation, opponentConclusion;
        if(playerId == maximizingPlayer) bestEvaluation = minEvaluation;
        else bestEvaluation = maxEvaluation;
        for(i = 0; i < children.size(); i++){
            opponentConclusion = miniMaxLayer(children.get(i), !playerId, dept-1);
            if((playerId == maximizingPlayer && opponentConclusion > bestEvaluation) ||
                    (playerId != maximizingPlayer && opponentConclusion < bestEvaluation)){
                bestEvaluation = opponentConclusion;
                bestMoveIndex = i;
            }
        }
        AIMove = modelReference.getAllPossibleMoves().get(bestMoveIndex);
    }

    /**
     * To get the children for the first layer/node of the tree, handles win events
     * @return the children, null if winner
     */
    protected List<Model> getChildrenFirstLayer(){
        List<Model> children;
        try {
            children = getSuccessors(modelReference, playerId);
        } catch (ExpectiMiniMax.Win win) {
            AIMove = win.getWinningMove();
            return null;
        }
        if(children.size() == 0) throw new IllegalArgumentException("This player can't make a move with the current dice roll, you forgot to roll the dice or used a different method than intended (Rules.getMovablePieceTypes");
        return children;
    }

    /**
     * Represents a chance and min/max player node
     * @param model the current model (state of the parent node)
     * @param player the player that the player part belongs to
     * @param deptToGo the dept still to go from the expecti mini max tree
     * @return the approximate evaluation for the model input
     */
    public double miniMaxLayer(Model model, boolean player, int deptToGo){
        model.updatePlayer(player);

        //Termination case
        if(deptToGo == 0) return evaluation(model);
        if(model.draw()) return (maxEvaluation+minEvaluation)/2;
        if(model.getCurrentLeader() != null) {
            if (model.getCurrentLeader() == maximizingPlayer) return maxEvaluation;
            return minEvaluation;
        }

        //Chance node
        List<Byte> diceRoles = model.getMovablePieceTypes();

        //Player nodes
        double[] evaluation = new double[diceRoles.size()];
        for(byte d = 0; d < diceRoles.size(); d++) {
            model.updateDice(diceRoles.get(d));
            List<Model> children = null;
            boolean skip = false;
            if(!skip) try {
                children = getSuccessors(model, player);
            } catch (Win win) {
                if (player == maximizingPlayer) evaluation[d] += maxEvaluation;
                else evaluation[d] += minEvaluation;
                skip = true;
                //No need to investigate further
            }
            if(!skip) {
                double bestEvaluation, opponentConclusion;
                if(player == maximizingPlayer) bestEvaluation = minEvaluation;
                else bestEvaluation = maxEvaluation;
                int i;
                for (i = 0; i < children.size(); i++) {
                    opponentConclusion = miniMaxLayer(children.get(i), !player, deptToGo - 1);
                    if ((player == maximizingPlayer && opponentConclusion > bestEvaluation) || (player != maximizingPlayer && opponentConclusion < bestEvaluation)) {
                        bestEvaluation = opponentConclusion;
                    }
                }
                evaluation[d] = bestEvaluation;
            }
        }
        double ans = 0;
        for(double temp: evaluation) ans+= temp/diceRoles.size();
        return ans;
    }

    /**
     * To get all possible children of a model, after rolling the dice
     * @param model a model with an up-to-date dice and player
     * @param player the playing player
     * @return all models that may come after
     * @throws Win if one of these children ends the game
     */
    public List<Model> getSuccessors(Model model, boolean player) throws  Win{
        List<Move> edges = model.getAllPossibleMoves();
        if(edges.size() == 0) throw new IllegalArgumentException("This player ("+player+", agent "+ this +") can't make a move with the current dice roll ("+model.getCurrentDiceRoll()+"), you forgot to roll the dice or used a different method than intended (Rules.getMovablePieceTypes");
        List<Model> ans = new ArrayList<>();
        Boolean temp;
        for (Move edge : edges) {
            ans.add(model.clone());
            ans.get(ans.size() - 1).updateBoardModel(edge);
            temp = ans.get(ans.size() - 1).getCurrentLeader();
            if(temp != null && temp == player) throw new Win(edge);
        }
        return ans;
    }

    /**
     * Class to communicate that a mach is won (suggesting no evaluation or further search in that branch needed)
     */
    public static class Win extends Throwable{
        private final Move winningMove;
        Win(Move winningMove){
            this.winningMove = winningMove;
        }

        /**
         * To get the move that won the game
         * @return the winning move
         */
        Move getWinningMove(){return winningMove;}
    }

    /**
     * To evaluate the model using the evaluationFunction
     * @param model the model to evaluate
     * @return the evaluation
     */
    public double evaluation(Model model) {
        return evaluationFunction.evaluation(model);
    }

    /**
     * Sets the maximum dept of the tree
     * @param dept the new maximum dept
     */
    public void setDept(int dept){
        if(dept < 1) throw new IllegalArgumentException("Dept must be >=1");
        this.dept = dept;
    }

    /**
     * To get the dept of this agent
     * @return the dept to which this agent searches the tree
     */
    public int getDept(){
        return dept;
    }

    /**
     * To get the model this agent plays in
     * @return the model this agent plays in
     */
    public Model getModel(){
        return modelReference;
    }

    @Override
    public String toString() {
        return "ExpectiMiniMax dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference;
    }

    @Override
    public Object clone(){
        ExpectiMiniMax ans = new ExpectiMiniMax(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMax ans){
        super.clone(ans);
        setDept(ans.dept);
        setEvaluationFunction((EvaluationFunction) evaluationFunction.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMax)) return false;
        ExpectiMiniMax that = (ExpectiMiniMax) o;
        return getDept() == that.getDept() && maximizingPlayer == that.maximizingPlayer && Double.compare(that.maxEvaluation, maxEvaluation) == 0 && Double.compare(that.minEvaluation, minEvaluation) == 0 && Objects.equals(evaluationFunction, that.evaluationFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDept(), evaluationFunction, maximizingPlayer, maxEvaluation, minEvaluation);
    }
}
