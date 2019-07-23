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
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.server.Server;

/**
 *
 * @author Higor
 */
public class TestBongo {
  //                  "</playBongoDef1;relative_time_i;durationMillis_i>" +
   //             "</playBongoDef2;relative_time_i;durationMillis_i>" +
    private Server server;
    
    public TestBongo() {
        this.server =  new Server(1234);
        server.receiveMessages();
        
        System.out.println("TesteClient iniciado");
    }
    public void play(int time, int loops){
        //playBongoDefG grave
        //playBongoDefA agudo 
        
        String[] bongos = { 
                            "playBongoDefG",
                            "playBongoDefA",
                            "playBongoDefG",
                            "playBongoDefG",
                            "playBongoDefA"
                          };
        int[] durations = {
                            2*time,
                            1*time,
                            1*time,
                            2*time,
                            2*time
                          };
        
        String[] bongos2 = { 
                             "playBongoDefG",
                             "playBongoDefA",
                             "playBongoDefG",
                             "playBongoDefG",
                             "playBongoDefA",
                             "playBongoDefG",
                             "playBongoDefG",
                             "playBongoDefG",
                             "playBongoDefA",
                             "playBongoDefG",
                             "playBongoDefG",
                             "playBongoDefA",
                             "playBongoDefG"
                            };
        
        int[] durations2 = {
                            2*time,
                            2*time,
                            1*time,
                            1*time,
                            1*time,
                            1*time,
                            1*time,
                            1*time,
                            1*time,
                            2*time,
                            1*time,
                            1*time,
                            1*time,
                          };
        
        List l = new ArrayList();
        int relativeTime = 0;
        int id=0;
        for (int i = 0; i < loops; i++) {
            for(int j = 0; j < bongos.length; j++){
                id++;
                l.clear();
                l.add(id); //id
                l.add(relativeTime); //relative time
                l.add(0); //duration time
                
                OSCMessage oscMessage = null;
                oscMessage = new OSCMessage("/server/bongobot/"+bongos[j], l);
                  
                this.server.forwardMessage(oscMessage);
                System.out.println(bongos[j]+" [id="+id+", RT="+relativeTime+"]");
                relativeTime += durations[j]; 
            }
           
        }
    }
    public static void main(String[] args) {
        TestBongo c = new TestBongo();
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
                System.out.println("Entre com numero de loops: ");
                int loops = ler.nextInt();
                c.play(tempo, loops);
            }else{
                System.out.println("option not found\n");
            }
            
        }
        
    }
        
}
