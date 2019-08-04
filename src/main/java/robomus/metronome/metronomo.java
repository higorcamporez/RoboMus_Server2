/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.metronome;

import java.util.Timer;
import java.util.TimerTask;

import java.io.InputStream;

import java.io.FileInputStream;  
import java.io.File;

import  sun.audio.*;    //import the sun.audio package


		
//public class metronomo extends Thread{
public class metronomo{
    //atributos
    private int BPM;
    private int Tempos;
    private int Nota;
    
    private double sleep_timer;
    private int tempoatual;
    
    Timer timer = new Timer();
    private boolean Encerrar;
    
    public metronomo(int BPM, int Tempos, int Nota) {
        this.BPM = BPM;
	this.Tempos = Tempos;
	this.Nota = Nota;
	this.Encerrar = false;
    }
    
    //GET SETS
    public int getBPM() {
        return BPM;
    }

    /**
     * @param BPM the BPM to set
     */
    public void setBPM(int BPM) {
        this.BPM = BPM;
    }

    /**
     * @return the Tempos
     */
    public int getTempos() {
        return Tempos;
    }

    /**
     * @param Tempos the Tempos to set
     */
    public void setTempos(int Tempos) {
        this.Tempos = Tempos;
    }

    /**
     * @return the Nota
     */
    public int getNota() {
        return Nota;
    }

    /**
     * @param Nota the Nota to set
     */
    public void setNota(int Nota) {
        this.Nota = Nota;
    }

    /**
     * @return the Encerrar
     */
    public boolean isEncerrar() {
        return Encerrar;
    }

    /**
     * @param Encerrar the Encerrar to set
     */
    public void setEncerrar(boolean Encerrar) {
        this.Encerrar = Encerrar;
    }
   
    /**
     * @return the sleep_timer
     */
    public double getSleep_timer() {
        return sleep_timer;
    }

    /**
     * @param sleep_timer the sleep_timer to set
     */
    public void setSleep_timer(double sleep_timer) {
        this.sleep_timer = sleep_timer;
    }

    /**
     * @return the tempoatual
     */
    public int getTempoatual() {
        return tempoatual;
    }

    /**
     * @param tempoatual the tempoatual to set
     */
    public void setTempoatual(int tempoatual) {
        this.tempoatual = tempoatual;
    }
       
    
     
    
    //método principal, quando executar
    public void run() {
         
        //calculando tempo do timer
        setSleep_timer( 1.0 / getBPM() * 60000.0);
        
                //iniciando timer
        timer.schedule(new beep(),0, (long) getSleep_timer());
        
        
    }
    
    public class beep extends TimerTask {
        public void run() {
            
            
                if (getTempoatual() == 0) {
                    // agora vai passar um tempo
                    setTempoatual(getTempoatual() + 1);
                    return;
                }
                
                if (getTempoatual() == 1) {
                    BeepForte();
                }
                else {
                    BeepFraco();
                }
                
                if (tempoatual == getTempos()) {
                    // zerando o tempo
                    setTempoatual(0);
                }
                
                // agora vai passar um tempo
		setTempoatual(getTempoatual() + 1);
 
            
        }
    }

    //método para terminar a execução do programa
    public void encerrar() {
        timer.cancel();
    }
 
    public void BeepForte() {
        
        try{
            
            File diretorio = new File("beepforte.wav");
            
            InputStream in = new FileInputStream(diretorio);
            // Create an AudioStream object from the input stream.
            AudioStream as = new AudioStream(in);     
            // Use the static class member "player" from class AudioPlayer to play
            // clip.
            AudioPlayer.player.start(as);      
           
        }
            
       catch(java.io.IOException e) { System.out.println(e); }    
    }
    
    public void BeepFraco() {
        
        try{
            
            File diretorio = new File("beepfraco.wav");
            
            InputStream in = new FileInputStream(diretorio);
            // Create an AudioStream object from the input stream.
            AudioStream as = new AudioStream(in);     
            // Use the static class member "player" from class AudioPlayer to play
            // clip.
            AudioPlayer.player.start(as);      
           
        }
            
       catch(java.io.IOException e) { System.out.println(e); }    
    }
    
    
}