package tests;
import Model.CurrentModel;
import User.Agent.ANN.MLP.MLPNetwork;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MLPNetworkTesting
{
    @Test
    void testBoardStateTpMLPInputAndResultingLabel()
    {
        CurrentModel testModel = new CurrentModel();
        MLPNetwork mlp = new MLPNetwork();
        mlp.loadNetwork();

        double[] mlpInput = mlp.boardStateToMLPInput(testModel);
        int label = mlp.realTimeMLPEvalution(mlpInput);

        //Draw should occur due to starting positions. All players equal.
        assertEquals(label, 0);
    }

    @Test
    void boardStateToMLPInput()
    {
        CurrentModel testModel = new CurrentModel();
        MLPNetwork mlp = new MLPNetwork();

        double[] mlpInput = mlp.boardStateToMLPInput(testModel);

        for(int i = 8; i < 16; i++)
        {
            assertEquals(mlpInput[i], 1.0);
        }

        System.out.println("----------------------------");

        for(int i = 432; i <  440; i++)
        {
            assertEquals(mlpInput[i], -1.0);
        }
    }
    @Test
    void testOutputToLabel_2()
    {
        MLPNetwork mlp = new MLPNetwork();
        double[] arrayOutput = {0.76, 0.45, 0.99};
        INDArray output = Nd4j.create(arrayOutput);
        int label = mlp.generateLabel(output);
        assertEquals(2, label);
    }

    @Test
    void testOutputToLabel_0()
    {
        MLPNetwork mlp = new MLPNetwork();
        double[] arrayOutput = {0.76, 0.45, 0.29};
        INDArray output = Nd4j.create(arrayOutput);
        int label = mlp.generateLabel(output);
        assertEquals(0, label);
    }

}
