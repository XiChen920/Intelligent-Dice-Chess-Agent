package User.Agent.EvaluationFunction;

import Model.Model;
import Model.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * @link https://dke.maastrichtuniversity.nl/m.winands/documents/mc_star_minimax.pdf
 *
 * s = state
 * a = player
 * d = maximum dept left
 * alpha =
 * β =
 * h(s) = heuristic of s
 *
 * 1 Star1(s, a, d, alpha, β)
 * 2    if d = 0 or s ∈ T then return h(s)
 * 3    else
 * 4        O <- genOutcomeSet(s, a)
 * 5        for o ∈ O do
 * 6            alpha' <- childAlpha(o, alpha)
 * 7            β' <- childBeta (o, β)
 * 8            s' <- actionChanceEvent (s, a, o)
 * 9            v <- Star1(s', d − 1, alpha', β')
 * 10           ol <- v; ou <- v
 * 11           if v ≥ β then return pess(O)
 * 12           if v ≤ αlpha then return opti(O)
 * 13       return Vd(s, a)
 *
 * genOutcomeSet(s, a) = an array of tuples, 1 per possible child of s, with 3 attributes
 *      a lowerbound ol (initilizesed to vmin)
 *      a upperbound ou (initilized to vmax)
 *      the outcome probapility op (related to chance node in expecti mini max)
 * childAlpha(o, alpha) = max {vmin, (alpha-opti(O)+op*ou)/op}
 * childBeta (o, β) = min {vmax, (β-press(O)+op*ol)/op}
 * actionChanceEvent (s, a, o) = the sucessor of s for player a with o
 * pess(O) = current lowerbound on the chance node: sum for o ∈ O (op*ol)
 * opti(O) = current upperbound on the chance node: sum for o ∈ O (op*ou)
 * Vd(s, a) = pres(O) and opti(O) at this position are the same, this is returnd
 */
@Deprecated
public class Star1 implements EvaluationFunction {
    protected byte maximumDept;
    protected EvaluationFunction evaluationFunction;
    protected boolean alphaBetaPruning = true;
    protected boolean sort = false;
    public Star1(byte maximumDept, EvaluationFunction evaluationFunction) {
        this.maximumDept = maximumDept;
        this.evaluationFunction = evaluationFunction;
    }

    public byte getMaximumDept() {
        return maximumDept;
    }

    protected double star1(Model s, boolean a, byte d, double alpha, double beta){
        //if d = 0 or s ∈ T then return h(s)
        if(d == 0) return evaluationFunction.evaluation(s);
        if(s.getCurrentLeader() == evaluationFunction.getMaximizingPlayer()) return evaluationFunction.getMaxEvaluation();
        if(s.getCurrentLeader() == !evaluationFunction.getMaximizingPlayer()) return evaluationFunction.getMinEvaluation();

        //else
        //O <- genOutcomeSet(s, a)
        List<Byte> possibleDiceRolls = s.getMovablePieceTypes();
        double op = ((double) 1)/((double) possibleDiceRolls.size());
        Model temp, temp1;
        List<o> O = new ArrayList<o>(), tempList;
        List<Move> edgeList;
        o tempO;
        for(Byte diceRoll: possibleDiceRolls){
            temp = s.clone();
            temp.updateDice(diceRoll);
            edgeList = temp.getAllPossibleMoves();
            tempList = new ArrayList<>();
            for(Move edge: edgeList){
                temp1 = temp.clone();
                temp1.updateBoardModel(edge);
                tempO = new o(evaluationFunction.getMinEvaluation(), op/edgeList.size(), evaluationFunction.getMaxEvaluation(), temp1);
                O.add(tempO);
            }
        }

        if(sort) O.sort((o1,o2) -> {
            double ans = evaluationFunction.evaluation(o1.s) - evaluationFunction.evaluation(o2.s);
            if(a != evaluationFunction.getMaximizingPlayer()) ans = -ans;
            return (int) ans;
        });

        //for o ∈ O do
        double newAlpha, newBeta, opti, pess, v;
        double[] temp2;
        for(o o: O){
            temp2 = optiAndPess(O);
            opti = temp2[0];
            pess = temp2[1];
            //alpha' <- childAlpha(o, alpha), childAlpha(o, alpha) = max {vmin, (alpha-opti(O)+op*ou)/op}
            newAlpha = Math.max(evaluationFunction.getMinEvaluation(), (alpha - opti+o.op*o.ou)/o.op);
            //β' <- childBeta (o, β) = min {vmax, (β-press(O)+op*ol)/op}
            newBeta = Math.min(evaluationFunction.getMaxEvaluation(), (beta - pess+o.op*o.ol)/o.op);
            //v <- Star1(s', d − 1, alpha', β')
            v = star1(o.s, !a, (byte) (d-1), newAlpha, newBeta);
            //ol <- v; ou <- v
            o.ol = v;
            o.ou = v;
            if(alphaBetaPruning) {
                if (v >= beta) return optiAndPess(O)[1];
                if (v <= alpha) return optiAndPess(O)[0];
            }
        }
        temp2 = optiAndPess(O);
        if(temp2[1] != temp2[0]) throw new IllegalArgumentException("This should not be possible");
        return temp2[0];
    }

    //pess(O) = current lowerbound on the chance node: sum for o ∈ O (op*ol)
    //opti(O) = current upperbound on the chance node: sum for o ∈ O (op*ou)
    private static double[] optiAndPess(List<o> O){
        double opti = 0;
        double pess = 0;
        for(o o: O){
            opti += o.op*o.ol;
            pess += o.op*o.ou;
        }
        return new double[]{opti, pess};
    }

    @Override
    public boolean getMaximizingPlayer() {
        return false;
    }

    @Override
    public double getMaxEvaluation() {
        return evaluationFunction.getMaxEvaluation();
    }

    @Override
    public double getMinEvaluation() {
        return evaluationFunction.getMinEvaluation();
    }

    @Override
    public double evaluation(Model model) {
        double ans = star1(model, model.getPlayer(), maximumDept, evaluationFunction.getMaxEvaluation(), evaluationFunction.getMinEvaluation());
        if(model.getPlayer() == !getMaximizingPlayer()) ans = -1*ans;
        return ans;
    }

    private static class o{
        private double ol;
        private double op;
        private double ou;
        private Model s;

        public o(double ol, double op, double ou, Model s) {
            this.ol = ol;
            this.op = op;
            this.ou = ou;
            this.s = s;
        }
    }

    @Override
    public Object clone(){
        Star1 ans = new Star1(maximumDept, (EvaluationFunction) evaluationFunction.clone());
        ans.alphaBetaPruning = alphaBetaPruning;
        ans.sort = sort;
        return ans;
    }
}
