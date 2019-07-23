package org.deeplearning4j;

import java.util.ArrayList;
import java.util.List;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.EmnistDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.ROCMultiClass;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import org.deeplearning4j.nn.conf.Updater;
import org.nd4j.linalg.factory.Nd4j;

public class Trainer {
    static int batchSize = 16; // how many examples to simultaneously train in the network
    static EmnistDataSetIterator.Set emnistSet = EmnistDataSetIterator.Set.BALANCED;
    static int rngSeed = 123;
    static int numRows = 28;
    static int numColumns = 28;
    static int reportingInterval = 5;

    private static Logger log = LoggerFactory.getLogger(Trainer.class);

    public static void main(String... args) throws java.io.IOException, InterruptedException {
        List l = new ArrayList<>();
        l.add(1);
        l.add(2);

        float[][] labelData = new float[10][5];
        //temp
        INDArray a = Nd4j.create(labelData);
        DataSet d = new DataSet(a,a);

        System.out.println(d);
        //
        int numLinesToSkip = 0;
        char delimiter = ',';
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);
        recordReader.initialize(new FileSplit(new ClassPathResource("msgs.csv").getFile()));
        
        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 4;     //5 values in each row of the iris.txt CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
        int numClasses = 1;     //3 classes (types of iris flowers) in the iris data set. Classes have integer values 0, 1 or 2
        int batchSize = 1000;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();
        System.out.println(testData);
        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fitLabel(true);
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set

        
        final int numInputs = 4;
        int outputNum = numClasses;
        long seed = 1;

        log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                //.updater(Updater.NESTEROVS)
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(32)
                        .build())
                .layer(new DenseLayer.Builder().nIn(32).nOut(1024)
                        .build())
                .layer(new DenseLayer.Builder().nIn(1024).nOut(2048)
                        .build())
                .layer(new DenseLayer.Builder().nIn(2048).nOut(2048)
                        .build())
                .layer(new DenseLayer.Builder().nIn(2048).nOut(512)
                        .build())
                .layer(new DenseLayer.Builder().nIn(512).nOut(64)
                        .build())
                .layer(new DenseLayer.Builder().nIn(64).nOut(16)
                        .build())
                .layer( new OutputLayer.Builder(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR)
                        .activation(Activation.SIGMOID)
                        .nIn(16).nOut(outputNum).build())
                .build();

        //run the model
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));


        for(int i=0; i<1000; i++ ) {
            model.fit(trainingData);
        }

        //System.out.println(testData.getLabels());
        //evaluate the model on the test set
        Evaluation eval = new Evaluation(numClasses);
        INDArray output = model.output(testData.getFeatures());
        //System.out.println(output);
        eval.eval(testData.getLabels(), output);
        log.info(eval.stats());
    }
}