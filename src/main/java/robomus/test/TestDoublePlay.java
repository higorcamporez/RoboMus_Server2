/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import com.illposed.osc.OSCMessage;
import java.io.IOException;
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
public class TestDoublePlay {
    
    private Server server;
    
    public TestDoublePlay() {
        this.server =  new Server(1234);
        server.receiveMessages();
        
        System.out.println("TesteClient iniciado");
    }
    
    public void playHappyBirthday(int time){
        Instrument laplap, laplap2;
        List instruments = server.getInstruments();
       /* int index;
        index = instruments.indexOf(new Instrument("/laplap"));
        if(index != -1){
            laplap = instruments.get(index);
        }else{
            System.out.println("There is no laplap");
            return;
        }
           
        index = instruments.indexOf(new Instrument("/laplap2"));
        if(index != -1){
            laplap2 = instruments.get(index);
        }else{
            System.out.println("There is no laplap2");
            return;
        }*/
        
        //E|----0-0-2-0-5-4--0-0-2-0-7-5-5--9-9-12-9-5-4-2--10-10-9-5-7-5-5--------|
        //music notes E4-E4-F#4-E4-A4-G#4--
        //            E4-E4-F#4-E4-B4-A4-A4--
        //            C#5-C#5-E5-C#5-A4-G#4-F#4--
        //            D5-D5-C#5-A4-B4-A4-A4
        //int time = 500;
        String[] notes = {  "E4","E4","F#4","E4","A4","G#4",
                            "E4","E4","F#4","E4","B4","A4","A4",
                            "C#5","C#5","E5","C#5","A4","G#4","F#4",
                            "D5","D5","C#5","A4","B4","A4","A4" };
        /*List l1 = new ArrayList();
        l1.add("E4");
        l1.add("E4");
        l1.add("F#4");
        l1.add("E4");*/
        int[] duration = {  time,time,time,time,time,time,
                            time,time,time,time,time,time,time,
                            time,time,time,time,time,time,time,
                            time,time,time,time,time,time,time};
        List l = new ArrayList();

        for (int i = 0; i < notes.length; i++) {
            l.clear();
            l.add(i); //id
            l.add(i*time); //relative time
            l.add(time); //duration time
            l.add(notes[i]); // note
            OSCMessage oscMessage = null;
            if(i%2 == 0){
                oscMessage = new OSCMessage("/server/bongobot2/playNote", l);
                System.out.println("bongobot2->Note: "+notes[i]);
            }else{
                oscMessage = new OSCMessage("/server/bongobot/playNote", l);
                System.out.println("bongobot->Note: "+notes[i]);
            }     
            this.server.forwardMessage(oscMessage);
        }
        
    }
   
    
    
    
    public static void main(String[] args) {
        TestDoublePlay c = new TestDoublePlay();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("(2) play happy birthday");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            if(op.equals("0")){
                c.server.printInstruments();
            }else if(op.equals("1")){
                c.server.printClients();
            }else if(op.equals("2")){
                System.out.println("Entre com o tempo: ");
                c.playHappyBirthday(ler.nextInt());
            }else{
                System.out.println("option not found\n");
            }
            
        }
        
    }
}
