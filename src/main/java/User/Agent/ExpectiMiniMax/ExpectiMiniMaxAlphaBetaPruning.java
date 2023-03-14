package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.util.List;
import java.util.Objects;

public class ExpectiMiniMaxAlphaBetaPruning extends ExpectiMiniMax {
    private static final int DEFAULTDept = 4;
    public boolean pruning = true;

    /**
     * Create a ExpectiMiniMax with a custom dept
     * @param modelReference the model the agent players on
     * @param playerId the player the agent is
     * @param dept the dept it is allowed to go to (every min player and every max player node count towards this number), must be >= 1
     */
    public ExpectiMiniMaxAlphaBetaPruning(Model modelReference, boolean playerId, int dept){
        super(modelReference, playerId, dept);
    }

    /**
     * Standard creator using default options
     * @param modelReference the model the agent plays upon
     * @param playerId the player the agent is
     */
    public ExpectiMiniMaxAlphaBetaPruning(Model modelReference, boolean playerId){
        super(modelReference, playerId, DEFAULTDept);
    }

    @Override
    public void generateMove() {
        List<Model> children = getChildrenFirstLayer();
        if(children == null) return;
        Double[] bestEvaluation = new Double[1];
        if(playerId == maximizingPlayer) bestEvaluation[0] = minEvaluation;
        else bestEvaluation[0] = maxEvaluation;
        Double opponentConclusion;
        int bestMoveIndex = 0, i;
        for(i = 0; i < children.size(); i++){
            opponentConclusion = miniMaxLayer(children.get(i), !playerId, dept-1, bestEvaluation);
            if(opponentConclusion != null &&
                    ((playerId == maximizingPlayer && opponentConclusion > bestEvaluation[0]) ||
                            (playerId != maximizingPlayer && opponentConclusion < bestEvaluation[0]))){
                bestEvaluation[0] = opponentConclusion;
                bestMoveIndex = i;
            }
        }
        AIMove = modelReference.getAllPossibleMoves().get(bestMoveIndex);
    }

    /**
     * Represents a chance and min/max player node
     * @param model the current model (state of the parent node)
     * @param player the player that the player part belongs to
     * @param deptToGo the dept still to go from the expecti mini max tree
     * @param killLine if player is maximizing, alpha, else beta: if this method will guarantee and equally good or better move from the perspective of this player, kill the branch of the tree
     * @return the approximate evaluation for the model input
     */
    protected Double miniMaxLayer(Model model, boolean player, int deptToGo, Double[] killLine){
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
        Double[][] evaluation = new Double[diceRoles.size()][1];
        for(byte d = 0; d < diceRoles.size(); d++) {
            model.updateDice(diceRoles.get(d));
            List<Model> children = null;
            boolean skip = false;
            if(!skip) try {
                children = getSuccessors(model, player);
            } catch (Win win) {
                if (player == maximizingPlayer) evaluation[d][0] = maxEvaluation;
                else evaluation[d][0] = minEvaluation;
                skip = true;
                //No need to investigate further
            }
            if(!skip) {
                if(player == maximizingPlayer) evaluation[d][0] = minEvaluation;
                else evaluation[d][0] = maxEvaluation;
                int i;
                for (i = 0; i < children.size(); i++) {
                    try {
                        evaluateUsingRecursion(children.get(i), player, deptToGo, evaluation, d, killLine);
                    } catch (ThisBranchIsIrrelevant ignore) {return null;}
                }
            }
        }
        if(player == maximizingPlayer) return expectation(evaluation, maxEvaluation);
        else return expectation(evaluation, minEvaluation);
    }

    protected void evaluateUsingRecursion(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
        Double opponentConclusion;
        opponentConclusion = miniMaxLayer(child, !player, deptToGo - 1, evaluation[index]);
        if (opponentConclusion != null &&((player == maximizingPlayer&& opponentConclusion > evaluation[index][0]) || (player != maximizingPlayer && opponentConclusion < evaluation[index][0]))) {
            evaluation[index][0] = opponentConclusion;
            if (pruning) {
                if (player == maximizingPlayer && expectation(evaluation, minEvaluation) > killLine[0] ||
                        player != maximizingPlayer && expectation(evaluation, maxEvaluation) < killLine[0])
                    throw new ThisBranchIsIrrelevant();
                //NOTE: if this player is maximizing, the worst it can do know so far is expectation(evaluation, minEvaluation), thus if this is higher than the best option already known by the minimizing player deciding before this player, this branch has no purpose anymore
            }
        }
    }

    protected static class ThisBranchIsIrrelevant extends Throwable{}

    /**
     * The current evaluation will develop to in the range of [expectation(evaluation, minEvaluation), expectation(evaluation, maxEvaluation)], used to compare against alpha/beta/killLine
     * @param evaluation the array with known and unknown values
     * @param valueForUnknowns the place for unknowns
     * @return the average if all unknowns where valueForUnknowns
     */
    protected double expectation(Double[][] evaluation, double valueForUnknowns){
        double ans = 0;
        for (Double[] aDouble : evaluation) {
            ans += Objects.requireNonNullElse(aDouble[0], valueForUnknowns);
        }
        return ans/evaluation.length;
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruning dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruning ans = new ExpectiMiniMaxAlphaBetaPruning(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruning ans){
        super.clone(ans);
        this.pruning = ans.pruning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMaxAlphaBetaPruning)) return false;
        if (!super.equals(o)) return false;
        ExpectiMiniMaxAlphaBetaPruning that = (ExpectiMiniMaxAlphaBetaPruning) o;
        return pruning == that.pruning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pruning);
    }
}
