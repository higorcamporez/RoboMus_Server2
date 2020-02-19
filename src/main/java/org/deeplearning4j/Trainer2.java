package org.deeplearning4j;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.util.logging.Level;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.conf.Updater;
import org.nd4j.linalg.factory.Nd4j;
import robomus.instrument.Delay;
import robomus.instrument.Instrument;

public class Trainer2 {
    static int batchSize = 16; // how many examples to simultaneously train in the network
    static EmnistDataSetIterator.Set emnistSet = EmnistDataSetIterator.Set.BALANCED;
    static int rngSeed = 123;
    static int numRows = 28;
    static int numColumns = 28;
    static int reportingInterval = 5;

    private static Logger log = LoggerFactory.getLogger(Trainer.class);

    public static void main(String... args) throws java.io.IOException, InterruptedException {

        //lendo arquivo
        String arqName = "dados_BongoBot.csv";
        BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\"+arqName));
        String linha = "";
        int size = 985;
        double[][] inputs = new double[size][4];
        double[] outputs = new double[size];
       int i = 0;
        while ((linha = br.readLine()) != null) {

            String[] vals = linha.split(",");

            inputs[i][0] =  Double.parseDouble(vals[0]);
            inputs[i][1] =  Double.parseDouble(vals[1]);
            inputs[i][2] =  Double.parseDouble(vals[2]);
            inputs[i][3] =  Double.parseDouble(vals[3]);
            outputs[i] =  Double.parseDouble(vals[4]);
  
            i++;

        }
        
        
        int index = 0;
        double maxValueIn = 0;
        double maxValueOut = 0;
        for(i = 0; i < size; i++){
            
            for(int j = 0; j < 4; j++){
                if(inputs[i][j] > maxValueIn){
                    maxValueIn = inputs[i][j];
                }
            }
            
            if(outputs[i] > maxValueOut){
                maxValueOut = outputs[i];
            }
        }

        //normalização
        for (i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[i].length; j++) {
                inputs[i][j] = inputs[i][j]/maxValueIn;
            }
            outputs[i] = outputs[i]/maxValueOut;
        }
        //
        
        //prints

        for (double[] input : inputs) {
            for (double double1 : input) {
                System.out.print(double1+" ");
            }
            System.out.println("");
        }
        for (double output : outputs) {
            System.out.println(output);
        }

        //tranformando para INDArray
        INDArray inp = Nd4j.create(inputs);
        INDArray o = Nd4j.create(outputs, 'f').transpose();
        //System.out.println(o.shape()[0] +" " +o.shape()[1] );
        DataSet dataSet = new DataSet(inp,o);

        System.out.println(dataSet);

        dataSet.shuffle();
        SplitTestAndTrain testAndTrain = dataSet.splitTestAndTrain(0.8);  

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        final int numInputs = 2*2;
        int outputNum = 1;
        long seed = 1;
            
        //log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                //.seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                //.activation(Activation.TANH)
                //.weightInit(WeightInit.XAVIER)
                //.updater(new Sgd(0.1))
                //.updater(Updater.NESTEROVS)
                //.l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(16)
                        .activation(Activation.TANH)
                        .build())
                .layer(new DenseLayer.Builder().nIn(16).nOut(64)
                        .activation(Activation.RELU)
                        .dropOut(0.1)
                        .build())
                
                //.layer(new DenseLayer.Builder().nIn(128).nOut(256)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(2048).nOut(2048)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(2048).nOut(512)
                //        .build())
                // .layer(new DenseLayer.Builder().nIn(512).nOut(64)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(256).nOut(32)
                //       .build())
                .layer( new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY )
                        .activation(Activation.SIGMOID)
                        .nIn(64).nOut(outputNum).build())
                .build();
        
        //run the model
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        
        model.init();
        model.setListeners(new ScoreIterationListener(1000));

        for(int j=0; j<1000; j++ ) {
            model.fit(trainingData);
        }
        /*
        double input[] = {0.0,0.05,0.0,0.5};
        //INDArray inp = Nd4j.create(input);
        INDArray inp = testData.getFeatures();
        System.out.println("inp(0) "+inp.getRow(0));
        INDArray output1 = this.model.output(inp.getRow(0));
        //System.out.println(output.getDouble(0)*this.maxValue);
        double d = (output1.getDouble(0));
                
        System.out.println("Teste fit. Delay "+d);
        */
        //salvando o maxValue
        FileWriter arq;
        try {
            arq = new FileWriter("src\\main\\resources\\"+arqName+".txt");
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.printf(Double.toString(maxValueOut));
            arq.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        File file = new File("src\\main\\resources\\"+arqName+".zip");

        try {
            model.save(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(testData.getLabels());
        //evaluate the model on the test set
        //Evaluation eval = new Evaluation(1);
        RegressionEvaluation eval =  new RegressionEvaluation(1);
        INDArray output = model.output(testData.getFeatures());
        System.out.println(output);
        System.out.println(testData.getLabels());
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());

        double[] in = new double[]{0,1000,0,1000};
        INDArray ina = Nd4j.create(in);
        output = model.output(ina);
        System.out.println(output);
    }
}