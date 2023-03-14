package User.Agent.ExpectiMiniMax;

import Model.Move;
import Model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExpectiMiniMaxAlphaBetaPruningSorted extends ExpectiMiniMaxAlphaBetaPruning{
    public ExpectiMiniMaxAlphaBetaPruningSorted(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
    }

    public ExpectiMiniMaxAlphaBetaPruningSorted(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
    }

    public boolean sort = true;
    public boolean shuffle = true;

    @Override
    public void generateMove() {
        List<ModelEdgeCombination> children = getChildrenFirstLayerForSorting();
        if(children == null) return;

        if(sort){
            if(shuffle) Collections.shuffle(children);
            children.sort((o1,o2)->{
                double ans = evaluation(o2.getModel())-evaluation(o1.getModel());
                if(playerId != maximizingPlayer) ans = ans*-1;
                return (int) ans;
            });
        }

        Double[] bestEvaluation = new Double[1];
        if(playerId == maximizingPlayer) bestEvaluation[0] = minEvaluation;
        else bestEvaluation[0] = maxEvaluation;
        Double opponentConclusion;
        int bestMoveIndex = 0, i;
        for(i = 0; i < children.size(); i++){
            opponentConclusion = miniMaxLayer(children.get(i).getModel(), !playerId, dept-1, bestEvaluation);
            if(opponentConclusion != null &&
                    ((playerId == maximizingPlayer && opponentConclusion > bestEvaluation[0]) ||
                            (playerId != maximizingPlayer && opponentConclusion < bestEvaluation[0]))){
                bestEvaluation[0] = opponentConclusion;
                bestMoveIndex = i;
            }
        }
        AIMove = children.get(bestMoveIndex).getEdge();
    }

    protected List<ModelEdgeCombination> getChildrenFirstLayerForSorting(){
        List<Move> edges = modelReference.getAllPossibleMoves();
        if(edges.size() == 0) throw new IllegalArgumentException("This player can't make a move with the current dice roll, you forgot to roll the dice or used a different method than intended (Rules.getMovablePieceTypes");
        List<ModelEdgeCombination> ans = new ArrayList<>();
        Model temp;
        for(Move edge: edges){
            temp = modelReference.clone();
            temp.updateBoardModel(edge);
            if(temp.getCurrentLeader() != null && temp.getCurrentLeader() == playerId) {
                AIMove = edge;
                return null;
            }
            ans.add(new ModelEdgeCombination(edge, temp));
        }
       return ans;
    }

    protected record ModelEdgeCombination(Move edge, Model model) {

        public Move getEdge() {
            return edge;
        }

        public Model getModel() {
            return model;
        }
    }



    @Override
    public Double miniMaxLayer(Model model, boolean player, int deptToGo, Double[] killLine) {
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
        Model[][] children = new Model[diceRoles.size()][];
        Model tempModel;
        List<Model> temp;
        int maxLengthChildren = 0;
        for(byte d = 0; d < diceRoles.size(); d++){
            tempModel = model.clone();
            tempModel.updateDice(diceRoles.get(d));
            try {
                temp = getSuccessors(tempModel, player);
                if(sort){
                    if(shuffle) Collections.shuffle(temp);
                    temp.sort((o1, o2)->{
                        double ans = evaluation(o2)-evaluation(o1);
                        if(player != maximizingPlayer) ans = ans*-1;
                        return (int) ans;
                    });
                }
                children[d] = temp.toArray(new Model[0]);
            } catch (Win win) {
                tempModel.updateBoardModel(win.getWinningMove());
                children[d] = new Model[]{tempModel};
            }
            if(children[d].length > maxLengthChildren) maxLengthChildren = children[d].length;
        }

        return playerPart(player, deptToGo, killLine, diceRoles, children, maxLengthChildren);
    }

    protected Double playerPart(boolean player, int deptToGo, Double[] killLine, List<Byte> diceRoles, Model[][] children, int maxLengthChildren) {
        Double[][] evaluation = new Double[diceRoles.size()][1];
        if(player == maximizingPlayer) for(byte i = 0; i < evaluation.length; i++) evaluation[i]=new Double[]{minEvaluation};
        else for(byte i = 0; i < evaluation.length; i++) evaluation[i]=new Double[]{maxEvaluation};

        int i;
        byte j;
        for(i = 0; i < maxLengthChildren; i++){
            for(j = 0; j < children.length; j++){
                if(i< children[j].length){
                    if(children[j][i].getCurrentLeader() != null && children[j][i].getCurrentLeader() == player){
                        if(player == maximizingPlayer) evaluation[j][0] = maxEvaluation;
                        else evaluation[j][0] = minEvaluation;
                    }
                    else {
                        try {
                            evaluateUsingRecursion(children[j][i], player, deptToGo, evaluation, j, killLine);
                        } catch (ThisBranchIsIrrelevant ignore) {return null;}
                    }
                }
            }
        }
        if(player == maximizingPlayer) return expectation(evaluation, maxEvaluation);
        else return expectation(evaluation, minEvaluation);
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningSorted  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningSorted ans = new ExpectiMiniMaxAlphaBetaPruningSorted(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningSorted ans){
        super.clone(ans);
        sort = ans.sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMaxAlphaBetaPruningSorted)) return false;
        if (!super.equals(o)) return false;
        ExpectiMiniMaxAlphaBetaPruningSorted that = (ExpectiMiniMaxAlphaBetaPruningSorted) o;
        return sort == that.sort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sort);
    }
}