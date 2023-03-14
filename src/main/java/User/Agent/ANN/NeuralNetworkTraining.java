package User.Agent.ANN;

import User.Agent.ANN.MLP.MLPNetwork;

/**
 * Method allows for Neural Network training and hyper-prameter tuning to be executed.
 */
public class NeuralNetworkTraining
{
    /**
     * Execution of MLP training and experimentation
     * @param args Input argyments passed into the method.
     */
    public static void main(String[] args)
    {
        MLPNetwork mlp = new MLPNetwork();
        mlp.manualEvaluationOfFeatures();
    }
}
