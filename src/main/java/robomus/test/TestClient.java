/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TestClient {
    private Server server;
    
    public TestClient() {
        this.server =  new Server(1234);
        server.receiveMessages();
        Instrument i1 = new Instrument();
        i1.setName("instr1");
        i1.setOscAddress("/instr1");
        i1.setSpecificProtocol("</play;time_i></stop;time_i></next;num_i>");
        Instrument i2 = new Instrument();
        i2.setName("instr2");
        i2.setOscAddress("/instr2");
        i2.setSpecificProtocol("</playNote;note_s;string_i></play;fret_i;sting1__i>");
        Instrument i3 = new Instrument();
        i3.setName("instr3");
        i3.setOscAddress("/instr3");
        i3.setSpecificProtocol("</act1;par_i>");
        server.getInstruments().add(i1);
        server.getInstruments().add(i2);
        server.getInstruments().add(i3);
        
        System.out.println("TesteClient iniciado");
    }
    
    
    
    public static void main(String[] args) {
        TestClient c = new TestClient();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            if(op.equals("0")){
                c.server.printInstruments();
            }else if(op.equals("1")){
                c.server.printClients();
            }else{
                System.out.println("Option not found\n");
            }
            
        }
        
    }
    
}
