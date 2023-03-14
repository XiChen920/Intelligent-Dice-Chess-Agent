package User.Agent.ANN.MLP;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MLPDataSetCreator
{
    private String filePath;
    private List<String[]> dataSamples;
    private CSVReader csvReader;
    private MLPInputStructure inputStructure;
    private DataSet dataSet;

    public MLPDataSetCreator(String filePath, int inputSize, int outputSize)
    {
        this.filePath = filePath;
        inputStructure = new MLPInputStructure(inputSize, outputSize);
        readDataSamples();
        dataSet = generateDataSet();
    }

    public DataSet generateDataSet()
    {
        int dataSetSize = dataSamples.size();
        int index = 0;
        double[][] inputs = new double[dataSetSize][];
        double[][] outputs = new double[dataSetSize][];

        for(String[] sampleString : dataSamples)
        {
            double[] networkInput = inputStructure.generateInputVector(sampleString);
            double[] networkLabel = inputStructure.generateLabelledEvaluation(sampleString);

            inputs[index] = networkInput;
            outputs[index] = networkLabel;

            index++;
        }
        INDArray indInput = Nd4j.create(inputs);
        INDArray indOutput = Nd4j.create(outputs);

        return new DataSet(indInput, indOutput);
    }

    public void readDataSamples()
    {
        dataSamples = new ArrayList<String[]>();

        try
        {
            //Access file
            FileReader filereader = new FileReader(filePath);
            csvReader = new CSVReader(filereader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);

            //Read data
            String[] sample;
            while((sample = csvReader.readNext()) != null)
            {
                dataSamples.add(sample);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<String[]> accessSamples()
    {
        return dataSamples;
    }

    public DataSet accessDataSet()
    {
        return dataSet;
    }
}
