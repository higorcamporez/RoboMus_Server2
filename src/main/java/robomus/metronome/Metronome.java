/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.metronome;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import robomus.util.Note;

/**
 *
 * @author higor
 */
public class Metronome{
    
    private int bpm;
    private Note note;
    private int duration;
    private int midiValue;
    private Timer timer;
             
    public Metronome(int bpm, Note note, int duration) {
        this.bpm = bpm;
        this.note = note;
        this.midiValue = note.getMidiValue();
        this.timer = new Timer();
    }
    
    public Metronome(int bpm) {
        this(
                bpm,
                new Note("A4"),
                100
            );
    }

    public void start(){
        long sleepTime = (long)(1.0 / this.bpm * 60000.0); 
        timer.schedule(new MetronomeTimer(midiValue, duration), 0, sleepTime);
    }
    
    public void stop(){
        timer.cancel();
    }

}
