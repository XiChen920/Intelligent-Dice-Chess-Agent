package Old.DoesEMMABPwork;

import Model.CurrentModel;
import Model.Model;
import User.Agent.EvaluationFunction.SimplifiedEvaluationFunctionWithGamePhaseDetection;
import User.Agent.ExpectiMiniMax.ExpectiMiniMax;
import User.Agent.ExpectiMiniMax.ExpectiMiniMaxAlphaBetaPruningSorted;
import tests.TestEMMVariants;

import java.io.*;
import java.util.List;

@Deprecated
public class ExpectiMiniMaxVariationsDifferencesTools extends TestEMMVariants {
    protected static final byte dept = 3;
    protected static File file;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        getAModelCausingProblems();
        //file = new File("src/test/java/tests", "ProblemModels2.Model");
        System.out.println("is the model a problem? "+isTheModelAProblem());
        System.out.println("is the problem in miniMaxLayer? "+isTheProblemInMiniMaxLayer());
        System.out.println("is the problem in generate move? "+isTheProblemInGenerateMove());
    }

    public static boolean isTheProblemInGenerateMove() throws IOException, ClassNotFoundException {
        class TestGenerateMove extends ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion{
            public TestGenerateMove(Model modelReference, boolean playerId, int dept) {
                super(modelReference, playerId, dept);
            }

            @Override
            public Double miniMaxLayer(Model model, boolean player, int deptToGo, Double[] killLine) {
                return miniMaxLayer(model, player, deptToGo);
            }
        }
        ExpectiMiniMax a = new ExpectiMiniMax(getProblemModel().clone(), getProblemModel().getPlayer(), dept);
        TestGenerateMove b = new TestGenerateMove(getProblemModel().clone(), getProblemModel().getPlayer(), dept);
        b.sort = false;
        a.generateMove();
        b.generateMove();
        if(a.getMove() == null && b.getMove() == null) return false;
        return !a.getMove().equals(b.getMove());
    }

    public static boolean isTheProblemInMiniMaxLayer() throws IOException, ClassNotFoundException {
        List<Model> modelList = null;
        try {
            modelList = new ExpectiMiniMax(null, false).getSuccessors(getProblemModel(), getProblemModel().getPlayer());
        } catch (ExpectiMiniMax.Win e) {
            e.printStackTrace();
        }
        ExpectiMiniMax a = new ExpectiMiniMax(null, false, dept);
        ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion b = new ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(null, false, dept);
        b.sort = false;
        Double[] killLine;
        if(!getProblemModel().getPlayer()) killLine = new Double[]{new SimplifiedEvaluationFunctionWithGamePhaseDetection().getMinEvaluation()};
        else killLine = new Double[]{new SimplifiedEvaluationFunctionWithGamePhaseDetection().getMaxEvaluation()};
        for(Model model: modelList){
            double ad = a.miniMaxLayer(model.clone(), !model.getPlayer(), dept-1);
            double bd = b.miniMaxLayer(model.clone(), !model.getPlayer(), dept - 1, killLine);
            if(Math.abs(ad-bd) > 10e-10) return true;
        }
        return false;
    }


    public static boolean isTheModelAProblem() throws IOException, ClassNotFoundException {
        Model model = getProblemModel();
        ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion a = new ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(model.clone(), model.getPlayer(), dept);
        a.sort = false;
        ExpectiMiniMax b = new ExpectiMiniMax(model.clone(), model.getPlayer(), dept);
        b.generateMove();
        a.generateMove();
        return (!a.getMove().equals(b.getMove()));
    }

    public static Model getProblemModel() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        Model model = (Model) new ObjectInputStream(fileIn).readObject();
        fileIn.close();
        return model;
    }

    public static void getAModelCausingProblems() throws IOException, ClassNotFoundException {
        ExpectiMiniMax a = new ExpectiMiniMax(new CurrentModel(), false, dept);
        ExpectiMiniMaxAlphaBetaPruningSorted b = new ExpectiMiniMaxAlphaBetaPruningSortedMultiThreadedTestVersion(a.getModel(), a.getPlayerFlag(), a.getDept());
        Model model;
        b.sort = false;
        int i = 0;
        int founds = 0;
        do {
            if(i != 0) System.out.print("\r cleared: "+i+" models");
            model = getRandomModel(new CurrentModel());
            a.setModel(model.clone());
            a.setPlayerId(model.getPlayer());
            b.setModel(model.clone());
            b.setPlayerId(model.getPlayer());
            a.generateMove();
            b.generateMove();
            i++;
            if(!a.getMove().equals(b.getMove())){
                System.out.println("\rAttempt "+i+" found a model causing a problem");
                FileOutputStream outStream = new FileOutputStream(new File("src/test/java/tests", "ProblemModels"+founds+".Model"));
                ObjectOutputStream out = new ObjectOutputStream(outStream);
                out.writeObject(model);
                out.close();
            }
        } while (true);
    }
}
