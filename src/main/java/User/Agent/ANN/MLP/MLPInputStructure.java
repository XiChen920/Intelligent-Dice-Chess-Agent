package User.Agent.ANN.MLP;

public class MLPInputStructure
{
    private int inputVectorSize;
    private int labelSize;

    public MLPInputStructure(int inputSize, int outputSize)
    {
        inputVectorSize = inputSize;
        labelSize = outputSize;
    }

    public double[] generateInputVector(String[] boardStringInput)
    {
        double[] vector = new double[inputVectorSize];
        String[] boardSplit = boardStringInput[0].split(" ");

        for(int index = 0; index < boardSplit.length; index++)
        {
            vector[index] = Double.parseDouble(boardSplit[index]);
        }
        return vector;
    }

    public double[] generateLabelledEvaluation(String[] boardStringInput)
    {
        int index = boardStringInput.length - 1;
        double dataLabel = Double.parseDouble(boardStringInput[index]);
        return generateLabel(dataLabel);
    }

    public double[] generateLabel(double evaluation)
    {
        double[] label = new double[labelSize];
        int changeIndex = 0;
        switch((int)evaluation)
        {
            case -1:
                changeIndex = 1;
                break;
            case 1:
                changeIndex = 2;
                break;
            default:
                changeIndex = 0;
                break;
        }
        label[changeIndex] = 1.0;
        return label;
    }
}
