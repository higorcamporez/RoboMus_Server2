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
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TestDelay {
    private Server server;
    
    public TestDelay() {
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
    
    public static void main(String[] args) {

        TestDelay c = new TestDelay();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("(2) Train instrument");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            if(op.equals("0")){
                c.server.printInstruments();
            }else if(op.equals("1")){
                c.server.printClients();
            }else if(op.equals("2")){
                System.out.println("============= menu ===============");
                System.out.println("(0) Train Model");
                System.out.println("(1) Load Model");
                System.out.println("(2) tst msgs sem delay");
                System.out.println("(2) tst msgs sem delay");
                System.out.println("==================================");
                
                String op2 = ler.nextLine();
                
                
                
                if( op2.equals("2")){
                    Instrument instrument1 = c.server.findInstrument("/Smartphone");
                    instrument1.setCalculateDelay(Boolean.FALSE);
                    Instrument instrument2 = c.server.findInstrument("/Smartphone2");
                    instrument2.setCalculateDelay(Boolean.FALSE);
                    
                    long timeRef = System.currentTimeMillis();
                    
                    for (int i = 0; i < 100; i++) {
                        
                        OSCMessage oscMessage1 = new OSCMessage(
                                instrument1.getOscAddress()+"/playNote"
                        );
                        oscMessage1.addArgument((long)i);
                        oscMessage1.addArgument("A4");
                        oscMessage1.addArgument(500);
                        
                        OSCMessage oscMessage2 = new OSCMessage(
                                instrument2.getOscAddress()+"/playNote"
                        );
                        oscMessage2.addArgument((long)i);
                        oscMessage2.addArgument("A4");
                        oscMessage2.addArgument(500);
                        
                        Date date = new Date(timeRef + (i+1)*2000);

                        OSCBundle oscBundle1 =  new OSCBundle();
                        oscBundle1.addPacket(oscMessage1);
                        oscBundle1.setTimestamp(date);

                        c.server.addMessage(oscBundle1);

                        OSCBundle oscBundle2 =  new OSCBundle();
                        oscBundle2.addPacket(oscMessage2);
                        oscBundle2.setTimestamp(date);

                        c.server.addMessage(oscBundle2);
                    }
                    
                            
                }
                System.out.println("Instrument osc address: ");
                String name = ler.nextLine();    
                Instrument instrument = c.server.findInstrument(name);
                    
                if(instrument == null){
                    System.out.println("Instrument not found!");
                }else{
                    switch (op2) {
                        case "0":
                            c.server.trainInstrumentDelay(instrument, 150);
                            break;
                        case "1":
                            instrument.loadModel();
                            break;
                        case "2":
                            
                            
                            //c.server.addMessage(new Date(System.currentTimeMillis() +90000), oscMessage);
                            //c.server.addMessage(new Date(System.currentTimeMillis() +80000), oscMessage);
                            break;
                    }
                }


                /*
                OSCMessage oscMessage = instrument.createNewAction((long)10);
                Date date = new Date(System.currentTimeMillis() + 10000);
                System.out.println(date.getTime());
                c.server.addMessage(date, oscMessage);
                */

            }else{
                System.out.println("Option not found\n");
            }
            
        }
        
    }
    
}
