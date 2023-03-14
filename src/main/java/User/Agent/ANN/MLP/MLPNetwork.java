package User.Agent.ANN.MLP;

import Model.*;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Method trains, tests, and performs Hyper parameter tuning for the passed Network architecture.
 * All experimental data will be logged within the HyperParameterTesting directory.
 */
public class MLPNetwork
{
    //TODO change filePaths to dynamic instead of hardcoded
    private String basePath = System.getProperty("user.dir");
    private String trainingFilePath = (basePath + File.separator + "/src/main/java/User/Agent/ANN/Data/trainingDataMLP.csv");
    private String testFilePath = (basePath + File.separator + "/src/main/java/User/Agent/ANN/Data/testDataMLP.csv");
    private String networkSaveFile = (basePath + File.separator + "/src/main/java/User/Agent/ANN/Data/MLPWeights");
    //private Logger LOG = Logger.getLogger("MLP Network Training");

    private MLPNetworkArchitecture mlpArchitecture;
    private MultiLayerNetwork mlp;
    private int inputSize;
    private int outputSize;
    //Batch sizes allow for iterations to be performed within a training epoch.
    private final int BATCHSIZE = 128;
    private final int TESTINGBATCHSIZE = 128;
    private int epoch = 25;

    /**
     * Network initialization and class field initialization of network features.
     */
    public MLPNetwork()
    {
        //LOG.info("Gather MLP architecture input details & Initialize network");
        mlpArchitecture = new MLPNetworkArchitecture();
        mlp = mlpArchitecture.accessMLP();
        inputSize = mlpArchitecture.accessInputSize();
        outputSize = mlpArchitecture.accessOutputSize();
    }

    /**
     * Train the MLP network on the gathered training data, over a specified number of epochs.
     */
    public void trainNetwork()
    {
        //LOG.info("Training data over: " + epoch + " epochs");
        try
        {
            DataSetIterator dataIterator = loadData(trainingFilePath, BATCHSIZE);

            int epochCount = 0;

            while(epochCount < epoch)
            {
                //LOG.info("Epoch number: " + epochCount);
                mlp.fit(dataIterator);
                epochCount ++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //LOG.info("Training finished");
    }

    /**
     * The trained MLP network is evaluated against a test set. Approximately 10% of training set size.
     * Accuracy is logged during the evaluation, per data batch (DataSet)
     */
    public void evaluateNetwork()
    {
        //LOG.info("Training data over: " + epoch + " epochs");
        try
        {
            DataSetIterator dataIterator = loadData(testFilePath, TESTINGBATCHSIZE);

            Evaluation evaluate = new Evaluation(outputSize);
            while(dataIterator.hasNext())
            {
                DataSet nextDataSet = dataIterator.next();
                INDArray setOutput = mlp.output(nextDataSet.getFeatures());
                evaluate.eval(nextDataSet.getLabels(), setOutput);
                //LOG.info(evaluate.stats());
                System.out.println("Accuracy: " + evaluate.accuracy());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to evaluate and label a Model to determine an advantage
     * @param model The model to be evaluated.
     * @return The label produced by the MLP network based on the passed Model.
     */
    public int evaluateSample(Model model)
    {
        double[] mlpInput = boardStateToMLPInput(model);
        int label = realTimeMLPEvalution(mlpInput);
        return label;
    }

    /**
     * Generate a bitmap representation of the current board. Determined by current player and 12 features (Piece type for each player)
     * To use as the input vector of the MLP network to evaluate a board state
     * @param model The current board from which to generate the model from
     * @return A bitmap representation of the board.
     */
    public double[] boardStateToMLPInput(Model model)
    {
        boolean currentPlayer = model.getPlayer();
        BoardState currentBoard = model.getBoardState();

        double[] mlpInput = generateMLPInput(currentPlayer, currentBoard);
        return mlpInput;
    }

    /**
     * Method to determine the inputValue (-1 or 1) dependent on current player, and formulate the board representation for each player's 6 features.
     * @param currentPlayer Boolean representing the current player (False is white, True is black)
     * @param currentBoard The current board layout
     * @return A bitmap vector representing the board layout.
     */
    public double[] generateMLPInput(boolean currentPlayer, BoardState currentBoard)
    {
        double[] whiteFeatureVector;
        double[] blackFeatureVector;

        if(currentPlayer == false)
        {
            int whiteInputValue = 1;
            int blackInputValue = -1;

            whiteFeatureVector = generatePlayerFeatureVector(false, whiteInputValue, currentBoard);
            blackFeatureVector = generatePlayerFeatureVector(true, blackInputValue, currentBoard);
        }
        else
        {
            int whiteInputValue = -1;
            int blackInputValue = 1;

            whiteFeatureVector = generatePlayerFeatureVector(true, whiteInputValue, currentBoard);
            blackFeatureVector = generatePlayerFeatureVector(false, blackInputValue, currentBoard);
        }

        return mergeFeatureVectors(whiteFeatureVector, blackFeatureVector);
    }

    /**
     * Generate a bitmap of the board, for a single player (6 features), however utilizing the knowledge that they are the player or opponent.
     * @param player The player for which we are generating the feature vector (True is black, false is white)
     * @param inputValue The value for which, when a piece is encountered, it is represented as this value in the bitmap.
     * @param currentBoard The current board layout.
     * @return The bitmap representation for a single player.
     */
    public double[] generatePlayerFeatureVector(boolean player, int inputValue, BoardState currentBoard)
    {
        int boardDimensions = currentBoard.getBoardState().length;
        double[] featureVector = new double[inputSize / 2];

        for(int i = 0; i < boardDimensions; i++)
        {
            for(int j =0; j < boardDimensions; j++)
            {
                Piece currentPiece = currentBoard.getPieceAt(i, j);
                if(currentPiece != null && player == currentPiece.getPlayer()) //Piece identified and belongs to the current player
                {
                    int index = determinePieceIndex(currentPiece, i, j);
                    featureVector[index] = inputValue;
                }
            }
        }
        return featureVector;
    }

    /**
     * Determine the index of the current piece within the one dimensional feature vector.
     * @param currentPiece The current piece to find an index for.
     * @param i The row within the board
     * @param j The column within the board
     * @return The index location of the piece within the one dimensional bitmap
     */
    public int determinePieceIndex(Piece currentPiece, int i, int j)
    {
        int rowDimension = 8;
        int boardDimension = rowDimension * rowDimension;

        byte pieceType = currentPiece.getTypeOfPiece();
        int featureIndex = (pieceType - 1) * boardDimension; //Accounting for piece-types starting at 1
        int boardIndex = (i * rowDimension) + j;

        return (featureIndex + boardIndex);
    }

    /**
     *
     * @param whiteFeatureVector
     * @param blackFeatureVector
     * @return
     */
    public double[] mergeFeatureVectors(double[] whiteFeatureVector, double[] blackFeatureVector)
    {
        double[] mlpInput = new double[inputSize];
        int inputIndex = 0;

        for(int i = 0; i < whiteFeatureVector.length; i++)
        {
            mlpInput[inputIndex] = whiteFeatureVector[i];
            inputIndex++;
        }

        for(int i=0; i< blackFeatureVector.length; i++)
        {
            mlpInput[inputIndex] = blackFeatureVector[i];
            inputIndex++;
        }
        return mlpInput;
    }

    public int realTimeMLPEvalution(double[] boardState)
    {
        double[][] batchBoardState = {boardState};
        INDArray networkInput = Nd4j.create(batchBoardState);
        INDArray output = mlp.output(networkInput);
        int label = generateLabel(output);
        return label;
    }

    /**
     *Method generates the labelled evaluation, prodcued by the MLP network based on the given input data.
     * @param output The Softmax MLP output.
     * @return The index/ label determined by the maximum value's index of the output.
     */
    public int generateLabel(INDArray output)
    {
        INDArray label = output.argMax();
        return label.getInt(0);
    }


    /**
     * Method to load from a .csv file a DataSetIterator capable of accessing data in specified file.
     * @param filePath The file from which to access the data.
     * @param batchSize The data batch sizes of which the data is subdivided into
     * @return A DataSetIterator capable of accessing data within specified files
     * @throws IOException
     * @throws InterruptedException
     */
    public RecordReaderDataSetIterator loadData(String filePath, int batchSize) throws IOException, InterruptedException {
        RecordReader recordReader = new CSVRecordReader(0, ',');
        recordReader.initialize(new FileSplit((new File(filePath))));

        RecordReaderDataSetIterator dataIterator = new RecordReaderDataSetIterator(recordReader, BATCHSIZE, inputSize, outputSize);
        return dataIterator;
    }

    /**
     * Method to save the weighting of the Neural network after training is complete.
     */
    public void saveNetwork()
    {
        try
        {
            mlp.save(new File(networkSaveFile));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to load a saved neural network state, to produce outputs based on previous training.
     */
    public void loadNetwork()
    {
        try
        {
            mlp = MultiLayerNetwork.load(new File(networkSaveFile), true);
            //System.out.println("File succesfully loaded");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * All step encapsulated method to perform and record data for the specified epoch amount.
     */
    public void epochExperimentation()
    {
        String experimentName = "epochExperiment";
        String[] heading = {"Epoch", "Accuracy", "Precision", "Recall"};
        ExperimentLogger.logToCSV(experimentName, heading);

        try
        {
            DataSetIterator dataTrainingIterator = loadData(trainingFilePath, BATCHSIZE);
            DataSetIterator dataTestingIterator = loadData(testFilePath, TESTINGBATCHSIZE);

            Evaluation evaluate = new Evaluation(outputSize);
            DataSet nextDataSet = dataTestingIterator.next();

            int epochCount = 0;

            while(epochCount < epoch)
            {
                //LOG.info("Epoch number: " + epochCount);

                //Training
                mlp.fit(dataTrainingIterator);
                epochCount ++;

                //Testing
                INDArray setOutput = mlp.output(nextDataSet.getFeatures());
                evaluate.eval(nextDataSet.getLabels(), setOutput);
                //LOG.info("Evaluation of current Epoch finished");

                //Data collection
                String[] row = generateRow(epochCount, evaluate);
                ExperimentLogger.logToCSV(experimentName, row);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * All step encapsulated method to perform and record data for various learning rates to determine optimal learning rate.
     */
    public void learningRateExperiment()
    {
        double[] learningRates = {0.0001, 0.0002, 0.0004, 0.0006, 0.0008, 0.001, 0.002, 0.006, 0.008, 0.01};

        try
        {
            DataSetIterator dataTrainingIterator = loadData(trainingFilePath, BATCHSIZE);
            DataSetIterator dataTestingIterator = loadData(testFilePath, TESTINGBATCHSIZE);

            Evaluation evaluate = new Evaluation(outputSize);
            DataSet nextDataSet = dataTestingIterator.next();

            for(double rate : learningRates)
            {
                String experimentName = "learningRateExperimentAndEpochs: " + Double.toString(rate);
                String[] heading = {"Epochs", "Accuracy", "Precision", "Recall"};
                ExperimentLogger.logToCSV(experimentName, heading);


                //LOG.info("Learning rate: " + rate);
                mlpArchitecture = new MLPNetworkArchitecture(rate);
                mlp = mlpArchitecture.accessMLP();

                int epochCount = 0;

                while(epochCount < epoch)
                {
                    //LOG.info("Epoch number: " + epochCount);
                    mlp.fit(dataTrainingIterator);
                    epochCount ++;

                    //Testing
                    INDArray setOutput = mlp.output(nextDataSet.getFeatures());
                    evaluate.eval(nextDataSet.getLabels(), setOutput);
                    //LOG.info("Evaluation of current Epoch finished");

                    //Data collection
                    String[] row = generateRow(epochCount, evaluate);
                    ExperimentLogger.logToCSV(experimentName, row);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method is in place to allow for manual architectural changes to be recorded.
     * Input manual change String for experiment file specification- ALl data will be logged there.
     */
    public void manualEvaluationOfFeatures()
    {
        try
        {
            DataSetIterator dataTrainingIterator = loadData(trainingFilePath, BATCHSIZE);

            String manaualChange = "IncreasedDataSize 1 900 000";
            String experimentName = "manualExperiments: " + manaualChange;
            String[] heading = {"Epochs", "Avg Accuracy", "Avg Precision", "Avg Recall"};
            ExperimentLogger.logToCSV(experimentName, heading);

            int epochCount = 0;

            while(epochCount < epoch)
            {
                //LOG.info("Epoch number: " + epochCount);
                mlp.fit(dataTrainingIterator);

                try
                {
                    DataSetIterator dataIterator = loadData(testFilePath, TESTINGBATCHSIZE);

                    Evaluation evaluate = new Evaluation(outputSize);
                    ArrayList<Double> accuracies = new ArrayList<Double>();
                    ArrayList<Double> precisions = new ArrayList<Double>();
                    ArrayList<Double> recalls = new ArrayList<Double>();

                    while(dataIterator.hasNext())
                    {
                        DataSet nextDataSet = dataIterator.next();
                        INDArray setOutput = mlp.output(nextDataSet.getFeatures());
                        evaluate.eval(nextDataSet.getLabels(), setOutput);

                        accuracies.add(evaluate.accuracy());
                        precisions.add(evaluate.precision());
                        recalls.add(evaluate.recall());
                    }
                    String[] row = generateAvgRow(epochCount, accuracies, precisions, recalls);
                    System.out.println(row[1]);
                    if(Double.parseDouble(row[1]) > 0.74)
                    {
                        System.out.println("weights saved");
                        saveNetwork();
                    }
                    ExperimentLogger.logToCSV(experimentName, row);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                epochCount ++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Method to generate evaluation stats row to be logged into .csv folder
     * @param epoch The current epoch in which the evaluation is carried out.
     * @param evaluate The evaluation object containing all stats
     * @return A comma separated array of strings that will be placed within a row.
     */
    public String[] generateRow(int epoch, Evaluation evaluate)
    {
        double accuracy = evaluate.accuracy();
        double precision = evaluate.precision();
        double recall = evaluate.recall();
        String[] row = {Integer.toString(epoch), Double.toString(accuracy), Double.toString(precision), Double.toString(recall)};
        return row;
    }

    /**
     * Method to generate average evaluation stats row to be logged into .csv folder due to multiple batches being evaluated.
     * @param epoch The current epoch in which the evaluation is carried out.
     * @param accuracies A collection of all batch accuracies to be averaged.
     * @param precisions A collection of all batch precisions to be averaged.
     * @param recalls A collection of all batch recalls to be averaged.
     * @return A comma separated array of strings that will be placed within a row.
     */
    public String[] generateAvgRow(int epoch, ArrayList<Double> accuracies, ArrayList<Double> precisions, ArrayList<Double> recalls)
    {
        int arrayLength = accuracies.size();
        double avgAccuracy = 0;
        double avgPrecision = 0;
        double avgRecall = 0;

        for(int i = 0; i < arrayLength; i++)
        {
            avgAccuracy += accuracies.get(i);
            avgPrecision += precisions.get(i);
            avgRecall += recalls.get(i);
        }

        String ep = Integer.toString(epoch);
        String accuracy = Double.toString(avgAccuracy / arrayLength);
        String precision = Double.toString(avgPrecision / arrayLength);
        String recall = Double.toString(avgRecall / arrayLength);
        String[] row = {ep, accuracy, precision, recall};

        return row;
    }
}
