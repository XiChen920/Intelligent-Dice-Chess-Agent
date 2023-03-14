package User.Agent.EvaluationFunction;

import Model.Model;
import User.Agent.ANN.MLP.MLPNetwork;

public class MLPEvaluation  implements  EvaluationFunction
{
    private MLPNetwork mlp;
    private double minEvaluation = -1;
    private double maxEvaluation = 1;

    /**
     * Constructor initializes the MLP network and loaded pre-trained weights.
     */
    public MLPEvaluation()
    {
        mlp = new MLPNetwork();
        mlp.loadNetwork();
    }

    /**
     * Constructor capable of accepting an already initialized MLPNetwork.
     * @param currentMLP MLP network to use in evaluation
     */
    public MLPEvaluation(MLPNetwork currentMLP)
    {
        mlp = currentMLP;
    }

    @Override
    public boolean getMaximizingPlayer()
    {
        return false;
    }

    /**
     * Get the maximum possible evaluation
     * @return Value is always 1, due to it being max label value
     */
    @Override
    public double getMaxEvaluation()
    {
        return maxEvaluation;
    }

    /**
     * Get the minimum possible evaluation.
     * @return Value is always -1, due to it being least label value.
     */
    @Override
    public double getMinEvaluation()
    {
        return minEvaluation;
    }

    /**
     * Method to return a new evaluation object. Avoiding reloading MLP network
     * @return A new MLPEvaluation object, utilizing already loaded network.
     */
    @Override
    public Object clone()
    {
        return new MLPEvaluation(mlp);
    }

    /**
     * Evaluation simplifies the MLP labelling such that:
     * -1 : Black advantage
     * 0 : Draw
     * 1: White advantage
     * @param model The current model being evaluated
     * @return The evaluation produced by the MLP.
     */
    @Override
    public double evaluation(Model model)
    {
        int label = mlp.evaluateSample(model);
        double evaluation;
        switch(label)
        {
            case 0:
                //Draw
                evaluation = 0.0;
                break;
            case 1:
                //White Advantage
                evaluation =  1.0;
                break;
            case 2:
                //Black Advantage
                evaluation = -1.0;
                break;
            default:
                evaluation = 0;
                break;
        }
        return evaluation;
    }
}
