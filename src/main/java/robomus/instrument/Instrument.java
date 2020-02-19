/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import robomus.util.Note;
import robomus.util.Notes;
import robomus.util.PrintOSCMessage;

        
/**
 *
 * @author Higor
 */
public class Instrument implements Serializable{
    
    protected String name; // nome do instrumento   
    protected int polyphony; // quantidade de notas
    protected String OscAddress; //endereÃ§o do OSC do instrumento
    protected int sendPort; // porta para envio msgOSC
    protected int receivePort; // porta pra receber msgOSC
    protected String typeFamily; //tipo do instrumento
    protected String specificProtocol; //procolo especifico do robo
    protected String ip;
    protected int threshold;
    protected List<Action> actions;
    protected Action lastAction;
    protected String lastInput;
    protected OSCPortOut sender = null;
    protected List<Delay> delays; 
    protected boolean waitingDelay;
    protected MultiLayerNetwork model;
    protected int maxInput;
    protected double maxValueIn;
    protected double maxValueOut;
    protected Boolean calculateDelay;
    protected int nDelays = 4; 
    public Instrument(){
        this.actions = new ArrayList<Action>();
        this.delays = new ArrayList<Delay>();
        this.lastInput = null;
    }

    public Instrument(String OscAddress) {
        this();
        this.OscAddress = OscAddress;
        
    }
    
    public Instrument(String name, int polyphony, String OscAddress,
            int sendPort, int receivePort,
            String typeFamily, String specificProtocol,
            String ip, int threshold) {
        
        this.name = name;
        this.polyphony = polyphony;
        this.OscAddress = OscAddress;
        
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.typeFamily = typeFamily;
        this.specificProtocol = specificProtocol;
        this.ip = ip;
        this.threshold = threshold;
        this.actions = new ArrayList<Action>();
        this.delays = new ArrayList<Delay>();
        this.lastInput = null;
        try {
            this.sender = new OSCPortOut(InetAddress.getByName(this.getIp()), this.getReceivePort());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {  
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setActions();
        this.maxInput = 0;
        
    }
    
    public void setActions(){
        String strActions[] = this.specificProtocol.split(">");
        List actionParametersName = new ArrayList<String>();
       
        for (String strAct : strActions){  
            
            Action action = new Action();
            //retira '<'
            strAct = strAct.substring(1, strAct.length());
           
            //separando os parametros da aÃ§Ã£o
            String strArgs[] = strAct.split(";");

            //tamanho de entrada da rede é definido pelo tamanho da maior msg
            if(strArgs.length > this.maxInput){
                this.maxInput = strArgs.length;
            }
            
            //System.out.println("args"+strArgs.length +strArgs[0]);
            //adicionando o nome da ação
            action.setActionAddress(strArgs[0]);
            
            //lista de argumentos
            List argsList = new ArrayList<>();
            
            for (int i  = 1; i < strArgs.length; i++) {
                 
                String name = strArgs[i].split("_")[0];
                String type = strArgs[i].split("_")[1];

                    
                if(type.equals("n")){ //se for do tipo note(n)
                    Argument argument = new Argument(name, 'n');
                    argsList.add(argument);
                }else if(type.equals("i")){ //se for do tipo inteiro(i)
                    Argument argument = new Argument(name, 'i');
                    argsList.add(argument);
                }
             
            }
            action.setArguments(argsList);
            this.actions.add(action);

        }
        
    }

    public boolean isWaitingDelay() {
        return waitingDelay;
    }

    public void setWaitingDelay(boolean waitingDelay) {
        this.waitingDelay = waitingDelay;
    }
    
    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }
    
    public String getIp() {
        return this.ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public void setPolyphony(int polyphony) {
        this.polyphony = polyphony;
    }

    public String getOscAddress() {
        return OscAddress;
    }

    public void setOscAddress(String OscAddress) {
        this.OscAddress = OscAddress;
    }

    public int getSendPort() {
        return sendPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public String getTypeFamily() {
        return typeFamily;
    }

    public void setTypeFamily(String typeFamily) {
        this.typeFamily = typeFamily;
    }

    public String getSpecificProtocol() {
        return specificProtocol;
    }

    public void setSpecificProtocol(String specificProtocol) {
        this.specificProtocol = specificProtocol;
        this.setActions();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + name +
                ", polyphony=" + polyphony +
                ", OscAddress=" + OscAddress +
                ", sendPort=" + sendPort +
                ", receivePort=" + receivePort +
                ", typeFamily=" + typeFamily +
                ", specificProtocol=" + specificProtocol +
                ", ip=" + ip +
                ", threshold=" + threshold + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(this.OscAddress, other.OscAddress)) {
            return false;
        }
        return true;
    }
    
    public OSCMessage createNewAction(Long id){
        Random rand = new Random();
        Integer index = rand.nextInt(this.actions.size());
        //escolhendo uma ação aleatoriamente
        Action act = this.actions.get(index);
        List<Argument> args = act.getArguments();
        String row = "";
        OSCMessage oscMessage = new OSCMessage(this.getOscAddress()+act.getActionAddress());
        oscMessage.addArgument(id);
        
        //adiciona index que representa aÃ§Ã£o
        row += index.toString();

        for (Argument arg : args) {
               
            if(arg.getType() == 'n'){
                Note note = Notes.generateNote();

                //adiciona valor midi que representa a nota
                row += ",";
                row += note.getMidiValue().toString();
                
                oscMessage.addArgument(note.getSymbol()+note.getOctavePitch());
            }else if(arg.getType() == 'i'){
                
                //provisorio
                //Gerar valores de 500 a 1024
                int value = rand.nextInt(524)+500;
                oscMessage.addArgument(value);
                row += ",";
                row += value;

            }
            //tem que criar os ifs para os outros tipos
        }
        PrintOSCMessage.printMsg(oscMessage);

        //verifica se Ã© primeira mensagen
        if(this.lastInput != null){
            Delay delay = new Delay(id, this.lastInput, row);          
            this.delays.add(delay);
            /*
            System.out.println("add");
            delays.forEach((d) -> {
                System.out.println(d);
            });
            */
        }else{
            //System.out.println("lastInput = null");
            this.lastInput = row;
        }
        
        return oscMessage;
       
    }
    
    public void send(OSCBundle oscBundle){
        if(this.sender == null){
            try {
                this.sender = new OSCPortOut(InetAddress.getByName(this.getIp()), this.getReceivePort());
            } catch (UnknownHostException ex) {
                Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {  
                Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            sender.send(oscBundle);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendTrainMessage(Long id){
        
        OSCMessage oscMessage = createNewAction(id);
        OSCBundle oscBundle =  new OSCBundle();
        oscBundle.addPacket(oscMessage);
        if(id == 0){
            oscBundle.setTimestamp(new Date(System.currentTimeMillis()+1000));
        }else{
            oscBundle.setTimestamp(new Date(System.currentTimeMillis()+600));
        }
        
        this.send(oscBundle);
        this.setWaitingDelay(true);
    }

    public void setLastDelay(Long id, Long delay) {        
        if(this.delays.size()>0){
            Delay aux = new Delay(id);
            int index = this.delays.indexOf(aux);
            if( index != -1){
                Delay d = this.delays.get(index);
               d.setDelay(delay);
               this.lastInput = d.getInput2();
            }else{
                System.out.println("setLastDelay:  id "+ id+" não encontrado");
            }
        }
    }
    
    public void removeDelay(Long id){
        this.lastInput = null;
        /*
        for (Delay delay : delays) {
            System.out.println(delay);
        }*/
        if(this.delays.size()>0){
            Delay aux = new Delay(id);
            int index = this.delays.indexOf(aux);
            if( index != -1){
                this.delays.remove(index);
                System.out.println("removeDelay:  Removeu id"+ id);
            }else{
                System.out.println("removeDelay:  id"+ id +"não encontrado");
            }
        }
        
    }

    public List getArgumentsType(OSCMessage oscMessage){
        Action a = new Action();
        String[] dividedAddress = divideAddress(oscMessage.getAddress());
        String actionAddress = '/'+dividedAddress[1];
        a.setActionAddress(actionAddress);

        List types = null;

        int index = this.actions.indexOf(a);

        if(index != -1){
            types = this.actions.get(index).getArgumentsType();
        }

        return types;
    }
    
    
    public void setLastInput(OSCMessage oscMessage){
        String[] adresses = divideAddress(oscMessage.getAddress());
        Action act = new Action("/"+adresses[1]);
        Integer index = this.actions.indexOf(act);
        if(index == -1){
            System.out.println("Indice da ação /" + adresses[1] +"não encontrado");
        }
        this.lastInput = "";
        this.lastInput += index.toString();
        
        List args = oscMessage.getArguments();
        
        for(int i = 1; i < args.size(); i++){
            this.lastInput += "," + args.get(i).toString();
        }
        
        //System.out.println(this.lastInput);
    }
    
    public Integer getDelay(OSCMessage oscMessage){
        //caso em que não se deseja usar delay
        if(!this.calculateDelay){
            System.out.println("return 0");
            return 0;
        }
        if(this.lastInput == null){
            System.out.println("lastInput null");
            return 0;
        }
        
        List args = oscMessage.getArguments();
        List argumentsType = this.getArgumentsType(oscMessage);
        //double[][] input = new double[1][this.maxInput*2+nDelays];
        double[][] input = new double[1][this.maxInput*2];
        
        int index = 0;

        //adicionando input da ultima mensagem
        String[] inp2 = this.lastInput.split(",");
        for (String s: inp2) {
            input[0][index] = (Double.parseDouble(s)/this.maxValueIn);
            index++;
        }
        
        String[] adresses = divideAddress(oscMessage.getAddress());
        Action act = new Action("/"+adresses[1]);
        Integer indexAct = this.actions.indexOf(act);
        if(index == -1){
            System.out.println("Indice da ação /" + adresses[1] +"não encontrado");
            return 0;
        }
        input[0][index] = (indexAct.doubleValue()/this.maxValueIn);
        index++;
        
        //adicionando inputs da nova msg
        for (int i = 1; i < args.size(); i++) {
            //System.out.println(args.get(i).toString() +" " + argumentsType.get(i-1).toString());
            if(argumentsType.get(i-1).equals('n')){
                Note note = new Note(args.get(i).toString());
                input[0][index] = note.getMidiValue();
            }else if(argumentsType.get(i-1).equals('c')){

            }else{
                input[0][index] =(((Integer)args.get(i)).doubleValue()/this.maxValueIn);
            }
            index++;
        }
        /*
        //adicionando delays anteriores
        for (int i = delays.size() - nDelays; i < delays.size(); i ++ ) {
            input[0][index] = (delays.get(i).getDelay()/this.maxValue);
            index++;
        }
        */
        System.out.println(Arrays.toString(input[0]));
        INDArray i = Nd4j.create(input);
        
        //INDArray i = Nd4j.zeros(1,4);
        INDArray output = this.model.output(i);
        System.out.println(output.getDouble(0)*this.maxValueOut);
        return (int)Math.round((output.getDouble(0)*this.maxValueOut));
    }
    
    public Integer getDelayFromPython(OSCMessage oscMessage){
        //caso em que não se deseja usar delay
        if(!this.calculateDelay){
            System.out.println("return 0");
            return 0;
        }
        if(this.lastInput == null){
            System.out.println("lastInput null");
            return 0;
        }
        
        List args = oscMessage.getArguments();
        List argumentsType = this.getArgumentsType(oscMessage);
        //double[][] input = new double[1][this.maxInput*2+nDelays];
        double[][] input = new double[1][this.maxInput*2];
        
        int index = 0;

        //adicionando input da ultima mensagem
        String[] inp2 = this.lastInput.split(",");
        for (String s: inp2) {
            input[0][index] = (Double.parseDouble(s));
            index++;
        }
        
        String[] adresses = divideAddress(oscMessage.getAddress());
        Action act = new Action("/"+adresses[1]);
        Integer indexAct = this.actions.indexOf(act);
        if(index == -1){
            System.out.println("Indice da ação /" + adresses[1] +"não encontrado");
            return 0;
        }
        input[0][index] = (indexAct.doubleValue());
        index++;
        
        //adicionando inputs da nova msg
        for (int i = 1; i < args.size(); i++) {
            //System.out.println(args.get(i).toString() +" " + argumentsType.get(i-1).toString());
            if(argumentsType.get(i-1).equals('n')){
                Note note = new Note(args.get(i).toString());
                input[0][index] = note.getMidiValue();
            }else if(argumentsType.get(i-1).equals('c')){

            }else{
                input[0][index] =(((Integer)args.get(i)).doubleValue());
            }
            index++;
        }
        String params = "";
        for(int i = 0; i < input[0].length; i++){
            params += Double.toString(input[0][i])+" ";
        }
        System.out.println("Params "+params);
        Process p;
        double ret = 0;
        try {
            p = Runtime.getRuntime().exec("python neuralNetwork.py "+this.name+" "+params);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ret = Double.parseDouble(in.readLine());
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        System.out.println(ret);
        return (int)Math.round(ret/1000);
    }
    
    public void loadModelFromKeras(){
        String simpleMlp = null;
        try {
            simpleMlp = new ClassPathResource("models\\"+this.name+"_keras_model.h5").getFile().getPath();
            MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKerasConfigurationException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedKerasConfigurationException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //load maxValue
        FileReader arq;
        try {
            arq = new FileReader("src\\main\\resources\\models\\"+this.name+"_max_values.txt");
            BufferedReader lerArq = new BufferedReader(arq);
 
            String linha = lerArq.readLine();
            System.out.println("maxValueX linha1="+linha);
            this.maxValueIn = Double.parseDouble(linha);
            linha = lerArq.readLine();
            System.out.println(" maxValueY linha2="+linha);
            this.maxValueOut = Double.parseDouble(linha);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void loadModel(){

        File file = new File("src\\main\\resources\\models\\"+this.name+".zip");

        try {
            this.model = MultiLayerNetwork.load(file, true);
        } catch (IOException e) {
            System.out.println("erro ao caregar o modelo!");
            e.printStackTrace();
        }
        
        //load maxValue
        FileReader arq;
        try {
            arq = new FileReader("src\\main\\resources\\models\\"+this.name+".txt");
            BufferedReader lerArq = new BufferedReader(arq);
 
            String linha = lerArq.readLine();
            System.out.println("maxValueX linha1="+linha);
            this.maxValueIn = Double.parseDouble(linha);
            linha = lerArq.readLine();
            System.out.println(" maxValueY linha2="+linha);
            this.maxValueOut = Double.parseDouble(linha);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      

    }
    public void fit2(){
        // create random object 
        Random ran = new Random(); 
        for(int i = 0; i < 500; i++){
            double nxt = ran.nextGaussian()*5000 + 20000; 
            delays.add(new Delay((long)i+1, "0", "0", (long) nxt));
        }
        this.maxInput = 1;
        fit();
    }
    public void fit(){
        System.out.println("fit(): "+this.delays.size());
        // 4 delays anteriores
        
        double[][] inputs = new double[this.delays.size()][this.maxInput*2 + nDelays];
        double[] outputs = new double[this.delays.size()];

        int index = 0;
        this.maxValueIn = 0;
        this.maxValueOut = 0;
        /*
        if(delays.size() < 5){
            System.out.println("Tamanho "+delays.size()+" do vetor delays insuficiente!");
            return;
        }
        */
        for (Delay delay : delays) {
            
            //se necessario completa a entrada da rede
            String fullInput = (delay.getInput1() + "," +delay.getInput2());
            System.out.println(fullInput);
            String row[] = fullInput.split(",");
            
            //convert string input para float
            System.out.println("maxinput "+this.maxInput);
            double[] aux = new double[this.maxInput*2];
            int indexAux = 0;
            for (String string : row) {
                aux[indexAux] = Double.parseDouble(string);
                if(aux[indexAux] > this.maxValueIn){
                    this.maxValueIn = aux[indexAux];
                }
                indexAux++;
            }
            
            
            inputs[index] = aux;
            //vetor de saída
            outputs[index] = delay.getDelay().doubleValue();
            if(outputs[index] > this.maxValueOut){
                this.maxValueOut = outputs[index];
            }
            index++;
        }
        
        FileWriter writer = null;
        try {
            writer = new FileWriter("src\\main\\resources\\models\\dados_"+this.name+".csv");
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //print dados e salva em arquivo
        for(int i = 0; i < inputs.length; i++){
            for(int j = 0; j < inputs[i].length; j++){
               System.out.print(inputs[i][j]+",");
                try {
                    writer.append(String.valueOf(inputs[i][j])+",");
                } catch (IOException ex) {
                    Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("");
            try {
                writer.append(String.valueOf(outputs[i]));
                writer.append("\n");
            } catch (IOException ex) {
                Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        //
        
        //normalizaÃ§Ã£o
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[i].length; j++) {
                inputs[i][j] = inputs[i][j]/this.maxValueIn;
            }
            outputs[i] = outputs[i]/this.maxValueOut;
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
        INDArray i = Nd4j.create(inputs);
        INDArray o = Nd4j.create(outputs, 'f').transpose();
        //System.out.println(o.shape()[0] +" " +o.shape()[1] );
        DataSet dataSet = new DataSet(i,o);

        System.out.println(dataSet);

        dataSet.shuffle();
        SplitTestAndTrain testAndTrain = dataSet.splitTestAndTrain(0.8);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        /*
        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerMinMaxScaler();
        normalizer.fitLabel(true);
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set
        */
        
        final int numInputs = maxInput*2;
        int outputNum = 1;
        long seed = 1;
            
        //log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(0.1))
                //.updater(Updater.NESTEROVS)
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(64)
                        .build())
                .layer(new DenseLayer.Builder().nIn(64).nOut(128)
                        .build())
                .layer(new DenseLayer.Builder().nIn(128).nOut(256)
                        .build())
                //.layer(new DenseLayer.Builder().nIn(2048).nOut(2048)
                //        .build())
                //.layer(new DenseLayer.Builder().nIn(2048).nOut(512)
                //        .build())
                // .layer(new DenseLayer.Builder().nIn(512).nOut(64)
                //        .build())
                .layer(new DenseLayer.Builder().nIn(256).nOut(32)
                        .build())
                .layer( new OutputLayer.Builder(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR)
                        .activation(Activation.SIGMOID)
                        .nIn(32).nOut(outputNum).build())
                .build();
        
        
        
        //run the model
        this.model = new MultiLayerNetwork(conf);
        

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
            arq = new FileWriter("src\\main\\resources\\models\\"+this.name+".txt");
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.printf(Double.toString(this.maxValueIn)+
                                "\n" + Double.toString(this.maxValueOut));
            arq.close();
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        File file = new File("src\\main\\resources\\models\\"+this.name+".zip");

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
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());
        
    }

    public String[] divideAddress(String address){
        String aux = address;
        if (aux.startsWith("/")) {
            aux = address.substring(1);
        }

        String[] split = aux.split("/", -1);

        return split;
    }

    public Boolean getCalculateDelay() {
        return calculateDelay;
    }

    public void setCalculateDelay(Boolean calculateDelay) {
        this.calculateDelay = calculateDelay;
    }

    
}
