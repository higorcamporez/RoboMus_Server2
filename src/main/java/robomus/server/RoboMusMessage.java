package robomus.server;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import robomus.instrument.Instrument;

import java.util.Date;

public class RoboMusMessage implements Comparable<RoboMusMessage> {

    private Date originalTimestamp;
    private Date compensatedTimestamp;
    private OSCBundle oscBundle;
    private Instrument instrument;
    private long messageId;
    
    public RoboMusMessage() {
    }

    public RoboMusMessage(Date originalTimestamp, Date compensatedTimestamp, OSCBundle oscBundle, Instrument instrument, long messageId) {
        this.originalTimestamp = originalTimestamp;
        this.compensatedTimestamp = compensatedTimestamp;
        this.oscBundle = oscBundle;
        this.instrument = instrument;
        this.messageId = messageId;
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

    public OSCBundle getOscBundle() {
        return oscBundle;
    }

    public void setOscBundle(OSCBundle oscBundle) {
        this.oscBundle = oscBundle;
    }

    
    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
    
    
    public void send() {
        instrument.send(oscBundle);
    }

    @Override
    public String toString() {
        return "RoboMusMessage{"
                + "originalTimestamp=" + originalTimestamp
                + ", compensatedTimestamp=" + compensatedTimestamp
                + ", instrument=" + instrument
                + '}';
    }

    @Override
    public int compareTo(RoboMusMessage roboMusMessage) {
        if (this.compensatedTimestamp.getTime() < roboMusMessage.getCompensatedTimestamp().getTime()) {
            return -1;
        } else if (this.compensatedTimestamp.getTime() < roboMusMessage.getCompensatedTimestamp().getTime()) {
            return 1;
        } else {
            return 0;
        }

    }
}
