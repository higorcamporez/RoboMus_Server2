/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

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
                Instrument instrument = c.server.findInstrument("/Smartphone");
                c.server.trainInstrumentDelay(instrument, 300);


                //instrument.loadModel();

                OSCMessage oscMessage = instrument.createNewAction((long)10);
                Date date = new Date(System.currentTimeMillis() + 10000);
                System.out.println(date.getTime());
                c.server.addMessage(date, oscMessage);


            }else{
                System.out.println("Option not found\n");
            }
            
        }
        
    }
    
}
