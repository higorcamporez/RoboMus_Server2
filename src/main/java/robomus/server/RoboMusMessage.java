package robomus.server;

import com.illposed.osc.OSCMessage;
import robomus.instrument.Instrument;

import java.util.Date;

public class RoboMusMessage {
    private Date originalTimestamp;
    private Date compensatedTimestamp;
    private OSCMessage oscMessage;
    private Instrument instrument;

    public RoboMusMessage() {
    }

    public RoboMusMessage(Date originalTimestamp, Date compensatedTimestamp, OSCMessage oscMessage, Instrument instrument) {
        this.originalTimestamp = originalTimestamp;
        this.compensatedTimestamp = compensatedTimestamp;
        this.oscMessage = oscMessage;
        this.instrument = instrument;
    }

    public Date getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(Date originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public Date getCompensatedTimestamp() {
        return compensatedTimestamp;
    }

    public void setCompensatedTimestamp(Date compensatedTimestamp) {
        this.compensatedTimestamp = compensatedTimestamp;
    }

    public OSCMessage getOscMessage() {
        return oscMessage;
    }

    public void setOscMessage(OSCMessage oscMessage) {
        this.oscMessage = oscMessage;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public void send(){
        instrument.send(oscMessage);
    }
}
