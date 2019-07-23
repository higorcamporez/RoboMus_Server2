/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 *
 * @author Higor
 */

public class TesteVelocidade {
    Server server;
    
    public TesteVelocidade() {
        this.server =  new Server(12345);
        server.receiveMessages();
    }
    
    
    public void playNoteTest(int fretNumber, int stringNumber){
        OSCPortOut sender = null;
        for (Instrument instrument : server.getInstruments()) {
            try {
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getSendPort());
                OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/playNoteTest");
                msg.addArgument((long)100);
                msg.addArgument(fretNumber);
                msg.addArgument(stringNumber);
                //msg.addArgument(id);
                try {
                    sender.send(msg);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }  
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        
        Server server = new Server(12345);
        
        while(true){
            System.out.println("Digite uma tecla pra enviar playNoteTest");
            Scanner ler = new Scanner(System.in);
            ler.nextLine();
            server.playNoteTest(4,4);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            server.playNoteTest(6,6);
            System.out.println("Enviou");
        }
    }
}
