package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative extends ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded {
    protected final static byte DEFAULTNumberOfThreads = 10;
    protected byte numberOfThreads;
    protected final static double DEFAULTTurnTimeMS = 5e3;
    protected double turnTimeMS;

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(Model modelReference, boolean playerId, byte numberOfThreads) {
        super(modelReference, playerId);
        this.numberOfThreads = numberOfThreads;
        turnTimeMS = DEFAULTTurnTimeMS;
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
        numberOfThreads = DEFAULTNumberOfThreads;
        turnTimeMS = DEFAULTTurnTimeMS;
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(Model modelReference, boolean playerId, byte numberOfThreads, double turnTimeMS) {
        super(modelReference, playerId);
        this.numberOfThreads = numberOfThreads;
        this.turnTimeMS = turnTimeMS;
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(Model modelReference, boolean playerId, double turnTimeMS) {
        super(modelReference, playerId);
        numberOfThreads = DEFAULTNumberOfThreads;
        this.turnTimeMS = turnTimeMS;
    }

    @Override
    public Double miniMaxLayer(Model model, boolean player, int deptToGo, Double[] killLine) {
        if(startTime+turnTimeMS < System.currentTimeMillis()) return super.miniMaxLayer(model, player, (byte) 1, killLine);
        else return super.miniMaxLayer(model, player, (byte) 0, killLine);
    }

    protected long startTime;
    private int runningThreadsCounter;
    @Override
    public void generateMove() {
        startTime = System.currentTimeMillis();
        threadList = new ArrayList<>();
        runningThreadsCounter = numberOfThreads-1;
        super.generateMove();
    }

    @Override
    protected boolean addNewTread() {
        return true;
    }

    protected List<Thread> threadList;
    @Override
    protected Thread getNewThread(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
        Thread ans = new AdditionalThread3(child, player, deptToGo, evaluation, index, killLine, false);
        threadList.add(ans);
        if(runningThreadsCounter > 0){
            activateNext();
            runningThreadsCounter--;
        }
        return ans;
    }

    @Override
    protected void waitingForThreads() {
        activateNext();
    }

    protected void activateNext(){
        Thread temp = threadList.get(0);
        threadList.remove(0);
        temp.start();
    }

    @Override
    protected Thread getNewAdditionalThreadFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i) {
        Thread ans = new AdditionalThreadFirstLayer(child, bestEvaluation, bestMoveIndex, i, false);
        threadList.add(ans);
        return ans;
    }

    protected class AdditionalThread3 extends AdditionalThread{
        public AdditionalThread3(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine, boolean start) throws ThisBranchIsIrrelevant {
            super(child, player, deptToGo, evaluation, index, killLine, start);
        }

        @Override
        public void interrupt() {
            thisBranchIsIrrelevant = true;
            super.interrupt();
        }

        @Override
        public void run() {
            if(!thisBranchIsIrrelevant) super.run();
        }
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort+" turnTimeMS = "+turnTimeMS+" numberOfThreads = "+numberOfThreads;
    }

    @Override
    public Object clone(){
        ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative ans = new ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative(null, false);
        ans.clone(this);
        return ans;
    }

    protected void clone(ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative ans){
        super.clone(ans);
        numberOfThreads = ans.numberOfThreads;
        turnTimeMS = ans.turnTimeMS;
    }

    public void setNumberOfThreads(byte numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative)) return false;
        if (!super.equals(o)) return false;
        ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative that = (ExpectiMiniMaxAlphaBetaPruningSortedLimitedMultiThreadedIterative) o;
        return numberOfThreads == that.numberOfThreads && Double.compare(that.turnTimeMS, turnTimeMS) == 0 && startTime == that.startTime && runningThreadsCounter == that.runningThreadsCounter && Objects.equals(threadList, that.threadList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberOfThreads, turnTimeMS, startTime, runningThreadsCounter, threadList);
    }

    public void setTurnTimeMS(double turnTimeMS) {
        this.turnTimeMS = turnTimeMS;
    }
}
