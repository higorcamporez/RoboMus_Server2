/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import robomus.instrument.Instrument;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Higor
 */
public class SychTime extends Thread{
    private List<Instrument> instruments;
    private boolean firstTime = true;
    private int inteval;
     
    public SychTime(List instruments, int interval) {
        this.instruments = instruments;
        this.inteval = interval;
    }
    
    public void run(){
        //System.out.println("sych");
        while(true){
            for (Instrument instrument : instruments) {
                OSCPortOut sender = null;
                try {
                    sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()),
                            instrument.getReceivePort());
                } catch (UnknownHostException ex) {
                    Logger.getLogger(SychTime.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SocketException ex) {
                    Logger.getLogger(SychTime.class.getName()).log(Level.SEVERE, null, ex);
                }


                OSCMessage msg = null;
                if(this.firstTime){
                    msg = new OSCMessage(instrument.getOscAddress()+"/synchStart");       
                    msg.addArgument(System.currentTimeMillis());
                    msg.addArgument(instrument.getThreshold());

                }else{
                    msg = new OSCMessage(instrument.getOscAddress()+"/synch");       
                    msg.addArgument(System.currentTimeMillis()); 
                }



                try {
                    sender.send(msg);


                    //System.out.println("msg synch sent " + this.firstTime );

                } catch (IOException ex) {
                    Logger.getLogger(SychTime.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.firstTime = false;

            try {
                Thread.sleep(inteval);
            } catch (InterruptedException ex) {
                Logger.getLogger(SychTime.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
