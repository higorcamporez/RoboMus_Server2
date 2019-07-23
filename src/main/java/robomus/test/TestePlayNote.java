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

public class TestePlayNote {
    Server server;
    int idMessage;
    
    public TestePlayNote() {
        this.server =  new Server(1234);
        server.receiveMessages();
        idMessage = 100;
    }
    
    public int nextIdMessage(){
        idMessage++;
        return idMessage;
    }
    
    
    public void playNoteTest(int relative_time_l, int durationMillis_l , String noteSymbol ){

        OSCMessage msg = new OSCMessage("/server/laplap/playNote");
        msg.addArgument(nextIdMessage());
        msg.addArgument(relative_time_l);
        msg.addArgument(durationMillis_l);
        msg.addArgument(noteSymbol);
        
        this.server.forwardMessage(msg);
        
    }
    
    void testManyMsg(int nMsg, int rt, int dur){
        for (int i = 0; i < nMsg; i++) {
            playNoteTest(rt, dur, "A2");
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestePlayNote.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    void testeMsg(){
        
    }
    public static void main(String[] args) {
        
        TestePlayNote testePlayNote = new TestePlayNote();
        
        while(true){
            System.out.println("Digite uma tecla pra iniciar os envios playNoteTest");
            Scanner ler = new Scanner(System.in);
            ler.nextLine();
            testePlayNote.testManyMsg(500, 3000, 1500);
            
            
        }
    }
}
