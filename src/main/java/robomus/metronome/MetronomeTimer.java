/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.metronome;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 *
 * @author higor
 */
public class MetronomeTimer extends TimerTask{
    
    private Synthesizer midiSynth;
    private MidiChannel[] mChannels;
    private int midiValue;
    private int duration;

    public MetronomeTimer(int midiValue, int duration) {
        this.midiValue = midiValue;
        this.duration = duration;
        
        try { 
            this.midiSynth = MidiSystem.getSynthesizer();
             midiSynth.open();
            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
            this.mChannels = midiSynth.getChannels();
            midiSynth.loadInstrument(instr[0]);//load an instrument
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(Metronome.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void run() {
        mChannels[0].noteOn(this.midiValue, 100);//On channel 0, play note number 60 with velocity 100 
        try {             
            Thread.sleep(this.duration); // wait time in milliseconds to control duration
        } catch (InterruptedException ex) {
            Logger.getLogger(Metronome.class.getName()).log(Level.SEVERE, null, ex);
        }
        mChannels[0].noteOff(this.midiValue);//turn of the note
    }
    
}
