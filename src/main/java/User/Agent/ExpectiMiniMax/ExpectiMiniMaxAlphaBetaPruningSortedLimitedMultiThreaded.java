package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.util.Objects;

public class ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded extends ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded{
    private int targetThreadCount;
    private final static int DEFAULTTargetThreadCount = 10;

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded(Model modelReference, boolean playerId, int dept, int targetThreadCount) {
        super(modelReference, playerId, dept);
        this.targetThreadCount = targetThreadCount;
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
        targetThreadCount = DEFAULTTargetThreadCount;
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
        targetThreadCount = DEFAULTTargetThreadCount;
    }

    private int threadCount;

    @Override
    protected boolean addNewTread() {
        return threadCount < targetThreadCount;
    }

    @Override
    public void generateMove() {
       threadCount = 1;
       super.generateMove();
    }

    @Override
    protected Thread getNewThread(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
        return new AdditionalThread2(child, player, deptToGo, evaluation, index, killLine);
    }

    @Override
    protected Thread getNewAdditionalThreadFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i) {
        return new AdditionalThreadFirstLayer2(child, bestEvaluation, bestMoveIndex, i);
    }

    protected class AdditionalThreadFirstLayer2 extends AdditionalThreadFirstLayer{
        public AdditionalThreadFirstLayer2(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i) {
            super(child, bestEvaluation, bestMoveIndex, i, true);
        }

        @Override
        public void run() {
            threadCount++;
            super.run();
            threadCount--;
        }
    }

    protected class AdditionalThread2 extends AdditionalThread{
        public AdditionalThread2(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
            super(child, player, deptToGo, evaluation, index, killLine, true);
        }

        @Override
        public void run() {
            threadCount++;
            super.run();
            threadCount--;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            threadCount--;
        }
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort+" targetThreadCount = "+targetThreadCount;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded ans = new ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded ans){
        super.clone(ans);
        targetThreadCount = ans.targetThreadCount;
        threadCount = ans.threadCount;
    }

    public void setTargetThreadCount(int targetThreadCount) {
        this.targetThreadCount = targetThreadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded)) return false;
        if (!super.equals(o)) return false;
        ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded that = (ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreaded) o;
        return targetThreadCount == that.targetThreadCount && threadCount == that.threadCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetThreadCount, threadCount);
    }
}
