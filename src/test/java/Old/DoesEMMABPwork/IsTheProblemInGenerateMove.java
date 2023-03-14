package Old.DoesEMMABPwork;

import Model.Model;
import Model.CurrentModel;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruning;

/**
 * Answer: no
 */
@Deprecated
public class IsTheProblemInGenerateMove extends ExpectiMiniMaxAlphaBetaPruning {
    public static void main(String[] args) {
        for(int i = 0; i < 10; i++) IsEMMBetterThanEMMABP.compareAgainstBaseLinePlayOut(new IsTheProblemInGenerateMove(new CurrentModel(), false, 2));
    }

    public IsTheProblemInGenerateMove(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
        super.pruning = false;
    }

    @Override
    protected Double miniMaxLayer(Model model, boolean player, int deptToGO, Double[] killLine) {
        double a = super.miniMaxLayer(model, player, deptToGO, killLine);
        double b = super.miniMaxLayer(model, player, deptToGO);
        if(a != b) System.out.println("No");
        return a;
    }


}
