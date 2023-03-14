package Experiments.ResearchQuestions;

import Model.Model;
import Model.StandardChessModel;

import java.io.IOException;

/**
 * What  is  the  AIs’  performance  difference(s)  compared  to their  performance  in  regular  chess?
 */
public class Question3 {
    public static String experimentName = "Q3";
    public static Model startModel = new StandardChessModel();

    public static void main(String[] args) throws IOException {
        String oldQ2ExperimentName = Question2.experimentName;
        Question2.experimentName = experimentName;
        Model oldQ2StartModel = Question2.startModel;
        Question2.startModel = startModel.clone();
        Question2.main(null);
        Question2.experimentName = oldQ2ExperimentName;
        Question2.startModel = oldQ2StartModel;
    }
}
