package User.Agent.ExpectiMiniMax;

import Model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded extends ExpectiMiniMaxAlphaBetaPruningSorted{
    public ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded(Model modelReference, boolean playerId, int dept) {
        super(modelReference, playerId, dept);
    }

    public ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded(Model modelReference, boolean playerId) {
        super(modelReference, playerId);
    }

    abstract protected boolean addNewTread();

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
        int[] bestMoveIndex = new int[]{0};
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < children.size(); i++){
            if(addNewTread()) threads.add(getNewAdditionalThreadFirstLayer(children.get(i).getModel(), bestEvaluation, bestMoveIndex, i));
            else playerPartFirstLayer(children.get(i).getModel(), bestEvaluation, bestMoveIndex, i);
        }

        waitingForThreads();

        for(Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException ignore) {
                throw new IllegalArgumentException("Lol, this should never happen");
            }
        }
        AIMove = children.get(bestMoveIndex[0]).getEdge();
    }

    protected void playerPartFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i){
        Double opponentConclusion = miniMaxLayer(child, !playerId, dept-1, bestEvaluation);
        if(opponentConclusion != null &&
                ((playerId == maximizingPlayer && opponentConclusion > bestEvaluation[0]) ||
                        (playerId != maximizingPlayer && opponentConclusion < bestEvaluation[0]))){
            bestEvaluation[0] = opponentConclusion;
            bestMoveIndex[0] = i;
        }
    }

    protected Thread getNewAdditionalThreadFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i){
        return new AdditionalThreadFirstLayer(child, bestEvaluation, bestMoveIndex, i, true);
    }

    protected class AdditionalThreadFirstLayer extends Thread {
        private final Model child;
        private final Double[] bestEvaluation;
        private final int[] bestMoveIndex;
        private final int i;

        public AdditionalThreadFirstLayer(Model child, Double[] bestEvaluation, int[] bestMoveIndex, int i, boolean start) {
            super();
            this.child = child;
            this.bestEvaluation = bestEvaluation;
            this.bestMoveIndex = bestMoveIndex;
            this.i = i;
            if(start) start();;
        }

        @Override
        public void run() {
            super.run();
            playerPartFirstLayer(child, bestEvaluation, bestMoveIndex, i);
        }
    }

    @Override
    protected Double playerPart(boolean player, int deptToGo, Double[] killLine, List<Byte> diceRoles, Model[][] children, int maxLengthChildren) {
        Double[][] evaluation = new Double[diceRoles.size()][1];
        if(player == maximizingPlayer) for(byte i = 0; i < evaluation.length; i++) evaluation[i]=new Double[]{minEvaluation};
        else for(byte i = 0; i < evaluation.length; i++) evaluation[i]=new Double[]{maxEvaluation};

        int i;
        byte j;
        List<Thread> threads = new ArrayList<>();
        for(i = 0; i < maxLengthChildren; i++){
            for(j = 0; j < children.length; j++){
                if(i< children[j].length){
                    if(children[j][i].getCurrentLeader() != null && children[j][i].getCurrentLeader() == player){
                        if(player == maximizingPlayer) evaluation[j][0] = maxEvaluation;
                        else evaluation[j][0] = minEvaluation;
                    }
                    else {
                        try {
                            if(addNewTread()) threads.add(getNewThread(children[j][i], player, deptToGo, evaluation, j, killLine));
                            else evaluateUsingRecursion(children[j][i], player, deptToGo, evaluation, j, killLine);
                        } catch (ThisBranchIsIrrelevant ignore) {
                            for(Thread thread: threads) haltTread(thread);
                            return null;
                        }
                    }
                }
            }
        }

        waitingForThreads();

        for(Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException ignore) {}
        }

        if(player == maximizingPlayer) return expectation(evaluation, maxEvaluation);
        else return expectation(evaluation, minEvaluation);
    }

    protected void waitingForThreads(){}

    protected void haltTread(Thread thread){
        thread.interrupt();
    }

    protected Thread getNewThread(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine) throws ThisBranchIsIrrelevant {
        return new AdditionalThread(child, player, deptToGo, evaluation, index, killLine, true);
    }

    protected class AdditionalThread extends Thread{
        protected final Model child;
        protected final boolean player;
        protected final int deptToGo;
        protected final Double[][] evaluation;
        protected final byte index;
        protected final Double[] killLine;
        protected boolean thisBranchIsIrrelevant = false;

        public AdditionalThread(Model child, boolean player, int deptToGo, Double[][] evaluation, byte index, Double[] killLine, boolean start) throws ThisBranchIsIrrelevant {
            super();
            this.child = child;
            this.player = player;
            this.deptToGo = deptToGo;
            this.evaluation = evaluation;
            this.index = index;
            this.killLine = killLine;
            if(start) start();
            if(thisBranchIsIrrelevant) throw new ThisBranchIsIrrelevant();
        }

        @Override
        public void run() {
            super.run();
            try {
                evaluateUsingRecursion(child, player, deptToGo, evaluation, index, killLine);
            } catch (ThisBranchIsIrrelevant e) {
                thisBranchIsIrrelevant = true;
            }
        }
    }

    @Override
    public String toString() {
        return "ExpectiMiniMaxAlphaBetaPruningSortedMultiThreaded  dept = "+dept+" evaluationFunction = "+evaluationFunction+" ID = "+playerId+" current model = "+modelReference+" pruning = "+pruning+" sort = "+sort;
    }
}
