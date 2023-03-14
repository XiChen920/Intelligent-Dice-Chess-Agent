package tests;

import Model.CurrentModel;
import Model.Model;
import Model.BoardState;
import User.Agent.EvaluationFunction.MLPEvaluation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MLPEvaluationTesting
{
    @Test
    public void testDrawEvaluation()
    {
        //Setup board and model
        String[][] board = {
                {"w2","w3","w4","w5","w6","w4","w3","w2"},
                {"w1","w1","w1","w1","w1","w1","w1","w1"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"b1","b1","b1","b1","b1","b1","b1","b1"},
                {"b2","b3","b4","b5","b6","b4","b3","b2"}};
        BoardState boardLayout = new BoardState(board);
        CurrentModel testModel = new CurrentModel(boardLayout, (byte)1, false);

        //Evaluation
        MLPEvaluation mlpEvaluation = new MLPEvaluation();
        double label = mlpEvaluation.evaluation(testModel);

        assertEquals(label, 0);
    }

    @Test
    public void testWhiteAdvantageEvaluation()
    {
        //Setup board and model
        String[][] board = {{"w2","w3","w4","w5","w6","w4","w3","w2"},
        {"w1","w1","w1","w1","w1","w1","w1","w1"},
        {"","","","","","","",""},
        {"","","","","","","",""},
        {"","","","","","","",""},
        {"","","","","","","w4",""},
        {"","","","","","","",""},
        {"b2","b3","b4","b5","b6","b4","b3","b2"}};
        BoardState boardLayout = new BoardState(board);
        CurrentModel testModel = new CurrentModel(boardLayout, (byte)2, false);

        //Evaluation
        MLPEvaluation mlpEvaluation = new MLPEvaluation();
        double label = mlpEvaluation.evaluation(testModel);

        assertEquals(label, 1);
    }

    @Test
    public void testBlackAdvantageEvaluation()
    {
        //Setup board and model
        String[][] board = {{"","","","","w6","","",""},
                {"","","b3","","b4","b3","b1","b1"},
                {"","","","","","","",""},
                {"","b4","","","w1","","",""},
                {"","","b1","","b1","","",""},
                {"","","","","","b3","",""},
                {"b1","b1","","","","b1","","b1"},
                {"b2","","","","b6","b4","","b2"}};
        BoardState boardLayout = new BoardState(board);
        CurrentModel testModel = new CurrentModel(boardLayout, (byte)1, true);

        //Evaluation
        MLPEvaluation mlpEvaluation = new MLPEvaluation();
        double label = mlpEvaluation.evaluation(testModel);
        System.out.println("Label: " + label);

        assertEquals(label, -1);
    }
}
