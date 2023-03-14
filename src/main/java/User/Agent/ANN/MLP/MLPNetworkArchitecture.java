package User.Agent.ANN.MLP;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Multi-Layer Perceptron network architecture class allowing network initialization and access to designed network.
 */
public class MLPNetworkArchitecture
{
    private int seed = 112;
    private final int INPUT = 768;
    private final int LABEL = 3;
    private double LEARNINGRATE = 0.001;
    private final double L2 = 0.0002;

    private MultiLayerNetwork mlp;

    /**
     * Constructs architecture and configures network, such that training and testing can be carried out.
     */
    public MLPNetworkArchitecture()
    {
        MultiLayerConfiguration config = mlpConfiguration();
        mlp =networkInitialization(config);
    }

    /**
     * Constructs architecture and configures network, such that training and testing can be carried out.
     * @param learningRate Specifies the learning rate to be included into the architecture
     */
    public MLPNetworkArchitecture(double learningRate)
    {
        LEARNINGRATE = learningRate;
        MultiLayerConfiguration config = mlpConfiguration();
        mlp =networkInitialization(config);
    }

    /**
     * MLP network architecture - Layer specifications, optimization algorithms, and output defined.
     * @return The MLP configuration
     */
    public MultiLayerConfiguration mlpConfiguration()
    {
        MultiLayerConfiguration conf;
        conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(LEARNINGRATE, 0.90, 0.99, 1 * Math.pow(10,-0.8)))
                .l2(L2)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(INPUT)
                        .nOut(1048)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(1048)
                        .nOut(500)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(500)
                        .nOut(50)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(50)
                        .nOut(3)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(LABEL)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();
        return conf;
    }

    /**
     * Method initializes the network based on the passed configuration.
     * @param conf Configuration network is based on.
     * @return The activated MLP network
     */
    public MultiLayerNetwork networkInitialization(MultiLayerConfiguration conf)
    {
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        return network;
    }

    /**
     * Access the generated MLP network
     * @return The current MLP network
     */
    public MultiLayerNetwork accessMLP()
    {
        return mlp;
    }

    /**
     * Determine the input layer vector size
     * @return An integer specifying the number of neurons in the input layer.
     */
    public int accessInputSize()
    {
        return INPUT;
    }

    /**
     * Determine the output layer vector size (Softmax used, so each nearon represents a classification)
     * @return An integer specifying the number of neurons in the output layer.
     */
    public int accessOutputSize()
    {
        return  LABEL;
    }
}
