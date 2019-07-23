/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.test;

import com.illposed.osc.OSCMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TesteBongoTempo {
    private Server server;
    
    public TesteBongoTempo() {
        this.server =  new Server(1234);
        server.receiveMessages();
        
        System.out.println("TesteClient iniciado");
    }
    public void play(int time, int loops){
        //playBongoDefG grave
        //playBongoDefA agudo 
        
        List l = new ArrayList();
        int relativeTime = 0;
        for (int i = 0; i < loops; i++) {
            
            l.clear();
            l.add(i); //id
            l.add(relativeTime); //relative time
            l.add(0); //duration time

            OSCMessage oscMessage = null;
            oscMessage = new OSCMessage("/server/bongobot/playBongoDefG", l);

            this.server.forwardMessage(oscMessage);
            System.out.println("playBongoDefG [id="+i+", RT="+relativeTime+"]");
            relativeTime += time; 
            
           
        }
    }
    public static void main(String[] args) {
        TesteBongoTempo c = new TesteBongoTempo();
        while(true){
            System.out.println("============= menu ===============");
            System.out.println("(0) print instruments");
            System.out.println("(1) print clients");
            System.out.println("(2) play");
            System.out.println("==================================");
            Scanner ler = new Scanner(System.in);
            String op = ler.nextLine();
            if(op.equals("0")){
                c.server.printInstruments();
            }else if(op.equals("1")){
                c.server.printClients();
            }else if(op.equals("2")){
                System.out.println("Entre com tempo: ");
                int tempo = ler.nextInt();
                System.out.println("Entre com a quantidade de batidas: ");
                int loops = ler.nextInt();
                c.play(tempo, loops);
            }else{
                System.out.println("option not found\n");
            }
            
        }
        
    }
}
