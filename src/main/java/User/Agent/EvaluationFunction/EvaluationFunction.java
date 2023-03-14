package User.Agent.EvaluationFunction;

import Model.Model;

public interface EvaluationFunction
{
    boolean getMaximizingPlayer();
    double getMaxEvaluation();
    double getMinEvaluation();

    double evaluation(Model model);

    Object clone();
}
