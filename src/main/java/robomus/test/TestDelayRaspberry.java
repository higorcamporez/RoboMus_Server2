/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import com.illposed.osc.OSCBundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import robomus.instrument.Instrument;
import robomus.metronome.Metronome;
import robomus.metronome.MetronomeTimer;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TestDelayRaspberry {
    private Server server;
    
    public TestDelayRaspberry() {
        /*teste metronome*/
        //Metronome m = new Metronome(60);
        //m.start();
        
        
        this.server = new Server(1234);
        server.receiveMessages();
//        Instrument i1 = new Instrument();
//        i1.setName("instr1");
//        i1.setOscAddress("/instr1");
//        i1.setSpecificProtocol("</play;time_i></stop;time_i></next;num_i>");
//        Instrument i2 = new Instrument();
//        i2.setName("instr2");
//        i2.setOscAddress("/instr2");
//        i2.setSpecificProtocol("</playNote;note_s;string_i></play;fret_i;sting1__i>");
//        Instrument i3 = new Instrument();
//        i3.setName("instr3");
//        i3.setOscAddress("/instr3");
//        i3.setSpecificProtocol("</act1;par_i>");
//        server.getInstruments().add(i1);
//        server.getInstruments().add(i2);
//        server.getInstruments().add(i3);

        //System.out.println("Teste Client iniciado");
    }
    
    public void msgsWithoutDelay(int nMsgs){
        Instrument instrument1 = server.findInstrument("/BongoBot");
        
        
        Instrument instrument2 = server.findInstrument("/BongoBot2");
        

        long timeRef = System.currentTimeMillis() + 3000;
        int tempo = 1000;
        Timer timer = new Timer();
        long t;
        for (int i = 0; i < nMsgs; i++) {
            
            t = timeRef + (i+1)*tempo;
            if(instrument1 != null) {
                instrument1.setCalculateDelay(Boolean.FALSE);
                OSCMessage oscMessage1 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage1.addArgument((long)i);
                //oscMessage1.addArgument(200);

                Date date = new Date(t);
                
                OSCBundle oscBundle1 =  new OSCBundle();
                oscBundle1.addPacket(oscMessage1);
                oscBundle1.setTimestamp(date);

                server.addMessage(oscBundle1,i);
            }
           if(instrument2 != null) {
       
                instrument2.setCalculateDelay(Boolean.FALSE);
                OSCMessage oscMessage2 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                
                oscMessage2.addArgument((long)i);
                //oscMessage2.addArgument(200);

                Date date2 = new Date(t);
                //System.out.println("t = " + date2.getTime());
                OSCBundle oscBundle2 =  new OSCBundle();
                oscBundle2.addPacket(oscMessage2);
                oscBundle2.setTimestamp(date2);

                server.addMessage(oscBundle2,i);
            }
            
        }
        
            
        t = timeRef + 10000 + (nMsgs)*500;
        if(instrument1 != null) {
            instrument1.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage1 = new OSCMessage(
                    instrument1.getOscAddress()+"/show"
            );
             
            Date date = new Date(t);

            OSCBundle oscBundle1 =  new OSCBundle();
            oscBundle1.addPacket(oscMessage1);
            oscBundle1.setTimestamp(date);

            server.addMessage(oscBundle1,1234);
        }
       if(instrument2 != null) {

            instrument2.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage2 = new OSCMessage(
                    instrument2.getOscAddress()+"/show"
            );

            Date date2 = new Date(t);

            OSCBundle oscBundle2 =  new OSCBundle();
            oscBundle2.addPacket(oscMessage2);
            oscBundle2.setTimestamp(date2);

            server.addMessage(oscBundle2,1234);
        }
            
    }
    
    public void msgsWithDelay(int nMsgs){
        
        Instrument instrument1 = server.findInstrument("/BongoBot");
        if(instrument1 != null) {
            instrument1.setCalculateDelay(Boolean.TRUE);
            instrument1.loadModel();
        }        
        
        Instrument instrument2 = server.findInstrument("/BongoBot2");
        if(instrument2 != null) {
            instrument2.setCalculateDelay(Boolean.TRUE);
            instrument2.loadModel();
        }
        
        long timeRef = System.currentTimeMillis() + 5000;
        int tempo = 500;
        Timer timer = new Timer();
        long t;
        for (int i = 0; i < nMsgs; i++) {
            
            t = timeRef + (i+1)*tempo;
            if(instrument1 != null) {
                
                OSCMessage oscMessage1 = new OSCMessage(
                        instrument1.getOscAddress()+"/playBongo"
                );
                oscMessage1.addArgument((long)i);
                //oscMessage1.addArgument(200);

                Date date = new Date(t);
                
                OSCBundle oscBundle1 =  new OSCBundle();
                oscBundle1.addPacket(oscMessage1);
                oscBundle1.setTimestamp(date);

                server.addMessage(oscBundle1,i);
            }
           if(instrument2 != null) {
       
                OSCMessage oscMessage2 = new OSCMessage(
                        instrument2.getOscAddress()+"/playBongo"
                );
                
                oscMessage2.addArgument((long)i);
                //oscMessage2.addArgument(200);

                Date date2 = new Date(t);
                //System.out.println("t = " + date2.getTime());
                OSCBundle oscBundle2 =  new OSCBundle();
                oscBundle2.addPacket(oscMessage2);
                oscBundle2.setTimestamp(date2);

                server.addMessage(oscBundle2,i);
            }
            
        }
        
            
        t = timeRef + 10000 + (nMsgs)*500;
        if(instrument1 != null) {
            instrument1.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage1 = new OSCMessage(
                    instrument1.getOscAddress()+"/show"
            );
             
            Date date = new Date(t);

            OSCBundle oscBundle1 =  new OSCBundle();
            oscBundle1.addPacket(oscMessage1);
            oscBundle1.setTimestamp(date);

            server.addMessage(oscBundle1,1234);
        }
       if(instrument2 != null) {

            instrument2.setCalculateDelay(Boolean.FALSE);
            OSCMessage oscMessage2 = new OSCMessage(
                    instrument2.getOscAddress()+"/show"
            );

            Date date2 = new Date(t);

            OSCBundle oscBundle2 =  new OSCBundle();
            oscBundle2.addPacket(oscMessage2);
            oscBundle2.setTimestamp(date2);

            server.addMessage(oscBundle2,1234);
        }
        
    }
 
    public static String menuTests(){
        System.out.println("============= menu ===============");
        System.out.println("(0) Teste sem delay mecânico");
        System.out.println("(1) Teste com delay mecânico");
        System.out.println("==================================");
        Scanner ler = new Scanner(System.in);
        String op = ler.nextLine();
        
        return op;
    }
    
    public static String menuTraining(){
        System.out.println("============= menu ===============");
        System.out.println("(0) Train Model");
        System.out.println("(1) Load Model");
        System.out.println("==================================");
        Scanner ler = new Scanner(System.in);
        String op = ler.nextLine();
        return op;
    }
    
    public static void main(String[] args) {
        
        TestDelayRaspberry c = new TestDelayRaspberry();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("(2) Train instrument");
            System.out.println("(3) Tests");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            
            switch(op){
                case "0":
                    c.server.printInstruments();
                    break;
                case "1":
                    c.server.printClients();
                    break;
                case "2":
                    String aux = TestDelayRaspberry.menuTraining();
                    System.out.println("Instrument osc address: ");
                    String name = ler.nextLine();    
                    Instrument instrument;
                    if(name.equals("")){
                        instrument = c.server.findInstrument("/BongoBot");
                    }else{
                        instrument = c.server.findInstrument(name);
                    }        
                    if(instrument == null){
                        System.out.println("Instrument not found!");
                    }else{
                        switch (aux) {
                            case "0":
                                c.server.trainInstrumentDelay(instrument, 300);
                                break;
                            case "1":
                                instrument.loadModel();
                                break;
                        }
                    }
                    break;
                case "3":
                    String aux2 = TestDelayRaspberry.menuTests();
                    
                    switch (aux2) {
                        case "0":
                            c.msgsWithoutDelay(10);
                            break;
                        case "1":
                            c.msgsWithDelay(10);
                            break;
                    }
                break;
                
                default:
                    System.out.println("Option not found\n");
            }
       
        }
        
    }
    
}
