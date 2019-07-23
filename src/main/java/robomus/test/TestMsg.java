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
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.instrument.Instrument;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TestMsg {
    Server server;

    public TestMsg() {
        this.server =  new Server(1234);
        server.receiveMessages();
    }
    
    
    public void sendTestMsg(){
        OSCPortOut sender = null;
        for (Instrument instrument : server.getInstruments()) {
            try {
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getSendPort());
                OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/testeMsg");
                msg.addArgument((long)100);
                Random random = new Random();
                int id  = random.nextInt(6000);
                System.out.println("MsgID sent = "+id);
                msg.addArgument(id);
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
    public static void main(String[] args) throws SocketException {
        
        TestMsg testMsg = new TestMsg();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMsg.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
            System.out.println("Digite uma tecla pra enviar msgteste");
            Scanner ler = new Scanner(System.in);
            ler.nextLine();
            testMsg.sendTestMsg();

        }
        /*for (int i = 0; i < 3; i++) {
            testMsg.sendTestMsg();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestMsg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
    }
}

